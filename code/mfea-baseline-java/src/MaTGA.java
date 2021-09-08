
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thang.tb153544
 */
public class MaTGA extends Algorithm {

    public int SUBPOPSIZE = 100;//Population size for each  sub-population

    public int MAX_GEN = 1000;//Maximum generation
    public int RECORD_FRE = 100;//Interval of recording

    public int K_ARCHIVE_SIZE = 300;//Archive size

    public double alpha = 0.1;//The rate of Transfer Learning Crossover
    public double replace_rate = 0.2;//Archive update rate (UR)
    public double ro = 0.8;//The attenuation coefficient
    public double shrink_rate = 0.8;//shrink rate of the local refined process

    double possibility[][];	//Possibility table
    double reward[][];	//Reward table
    int successful_transfer[][][];
    //--------------------variables for result reporting-----------------
    double fbest_value[][];
    int TLC_record[][][];

    Problem prob;
    Random rand;
    int MAX_NVARS;
    int task_num;
    double Cov[][][];		//Covariance matrixs
    double Cov_Inv[][][];	//Inverse covariance matrixs
    double Cov_Det[];							//Deteminents for covariance matrixs
    double Cov_Mean[][];				//Mean vector for covariance matrixs	
    double KLD[];			//Similarities between a certain task to other tasks based on KL divergence
    double avg_value[][];			//Average values for each dimension
    int k_archive_size[];						//Current size of archive
    Individual K_archive[][];	//Archive
    int trans_target;

    Individual[][] population;
    int generation;
    double fbest[];
    double MIN_V[];
    double MAX_V[];

    public MaTGA(Problem prob) {
        this.prob = prob;
        System.out.println("MaTGA is running");

    }


    public ArrayList<Individual> run(int seed, double[][] data) {
        long start =  System.currentTimeMillis();
        Parameter.countFitness = new int[prob.list_task.size()];
        Parameter.num_fitness = 0;
//        System.out.println("MaTGA is running on seed = " + seed);
//        System.out.println("MAX GENERATIONS "+ MAX_GEN);
        rand = new Random(seed);
        ArrayList<Individual> best = new ArrayList<Individual>();
        task_num = prob.list_task.size();
        possibility = new double[task_num][task_num];
        reward = new double[task_num][task_num];
        MAX_NVARS = -1;
        for (Task t : prob.list_task) {
            MAX_NVARS = Math.max(MAX_NVARS, t.dims);
        }
        MIN_V = new double[MAX_NVARS];
        MAX_V = new double[MAX_NVARS];

        Cov = new double[task_num][MAX_NVARS][MAX_NVARS];
        Cov_Inv = new double[task_num][MAX_NVARS][MAX_NVARS];
        Cov_Det = new double[task_num];
        Cov_Mean = new double[task_num][MAX_NVARS];
        KLD = new double[task_num];
        avg_value = new double[task_num][MAX_NVARS];
        k_archive_size = new int[task_num];
        K_archive = new Individual[task_num][K_ARCHIVE_SIZE];
        fbest = new double[prob.list_task.size()];
        successful_transfer = new int[task_num][task_num][MAX_GEN / RECORD_FRE];
        fbest_value = new double[task_num][MAX_GEN / RECORD_FRE + 1];
        TLC_record = new int[task_num][MAX_GEN / RECORD_FRE][task_num];
        initPopulation();

        generation = 1;
        for (int i = 1; i <= prob.list_task.size(); i++) {
            data[generation - 1][i - 1] = fbest[i - 1];
        }

        while (generation < MAX_GEN) {

            //generate offspring based on the two populations, knowledge transfer will happen here.
            for (int i = 0; i < task_num; i++) {
                reproduction(i);
            }
            //Update archive
            for (int i = 0; i < task_num; i++) {
                for (int j = 0; j < SUBPOPSIZE; j++) {
                    if (rand.nextDouble() < replace_rate) {
                        put_k_archive(population[i][j], i);
                    }
                }
            }
            generation++;
            for (int i = 1; i <= prob.list_task.size(); i++) {
                data[generation - 1][i - 1] = fbest[i - 1];
            }

        }
        for (int i = 1; i <= prob.list_task.size(); i++) {
            best.add(new Individual(MAX_NVARS, task_num));
            best.get(i - 1).fitness[i - 1] = fbest[i - 1];
            best.get(i - 1).skill_factor = i;
        }
        System.out.println("Seed " + seed + " NUM FITNESS: " + Parameter.num_fitness);
        for (int i = 1; i <= prob.list_task.size(); i++) {
            System.out.println(best.get(i - 1).skill_factor + " : " + best.get(i - 1).fitness[i - 1]);

        }
        long end =  System.currentTimeMillis();
        System.out.println(end-start);
        return best;
    }

    void reproduction(int task) {
        int i, j, k, l;
        int r1;

        if (rand.nextDouble() > alpha) {   //perform the crossover and mutation within the subpopulation
            for (int h = 0; h < MAX_NVARS; h++) {
                double d_min = 1;
                double d_max = 0;
                for (Individual ind : population[task]) {
                    d_max = Math.max(d_max, ind.genes[h]);
                    d_min = Math.min(d_min, ind.genes[h]);
                }
                MAX_V[h] = d_max;
                MIN_V[h] = d_min;
            }

            ArrayList<Individual> offs = new ArrayList();
            for (i = 0; i < SUBPOPSIZE / 2; i++) {
                int r2 = rand.nextInt(SUBPOPSIZE);;
                do {
                    r1 = rand.nextInt(SUBPOPSIZE);
                } while (r2 == r1);

                ArrayList<Individual> child = SBXcrossover2(population[task][i], population[task][r1]);
                for (Individual ind : child) {
                    GaussMutation(task, ind);
                    double fitness = prob.list_task.get(task).eval(ind.genes);
                    fbest[task] = Math.min(fbest[task], fitness);
                    ind.fitness[task] = fitness;
                    offs.add(ind);
                }

            }

            for (Individual ind : population[task]) {
                offs.add(ind);
            }
            Collections.sort(offs, new Comparator<Individual>() {
                @Override
                public int compare(Individual o1, Individual o2) {
                    if (o1.fitness[task] < o2.fitness[task]) {
                        return -1;
                    } else if (o1.fitness[task] > o2.fitness[task]) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            int size = offs.size();
            offs.subList(SUBPOPSIZE, size).clear();
            //   System.out.println("SIZE SUBPOP "+task+" : "+offs.size());
            for (int h = 0; h < SUBPOPSIZE; h++) {
                population[task][h] = offs.get(h);
            }
        } else {
            //knowledge transferring£º 
            //perform the crossover and mutation cross different subpopulations		
            l = adaptive_choose(task);
            double s = 0;
            s = fbest[task];
            TLC_record[task][generation / RECORD_FRE][l]++;
            int NVARS = prob.list_task.get(task).dims;
            for (i = 0; i < SUBPOPSIZE; i++) {
                r1 = rand.nextInt(SUBPOPSIZE);
                k = rand.nextInt(NVARS);
                double genes[] = new double[MAX_NVARS];
                for (j = 0; j < MAX_NVARS; j++) {
                    if (k == j || rand.nextDouble() < 0.9) {            //at least one dimension is replaced
                        genes[j] = population[l][r1].genes[j];
                    } else {
                        genes[j] = population[task][i].genes[j];
                    }
                }
                double fitness = prob.list_task.get(task).eval(genes);
                fbest[task] = Math.min(fbest[task], fitness);
                if (fitness < population[task][i].fitness[task]) {
                    population[task][i].genes = genes;
                    population[task][i].fitness[task] = fitness;
                }
            }

            if (fbest[task] < s) {
                reward[task][l] /= shrink_rate;
                successful_transfer[task][l][generation / RECORD_FRE]++;
            } else {
                reward[task][l] *= shrink_rate;
            }
        }

    }

    int adaptive_choose(int task) {
        int i;
        double sum = 0;
        int max = 0;
        double max_p = -1e10;
        cal_KLD(task);
        //Update possibility table
        for (i = 0; i < task_num; i++) {
            if (i == task) {
                continue;
            }
            possibility[task][i] = ro * possibility[task][i] + reward[task][i] / (1 + Math.log(1 + KLD[i]));
            sum += possibility[task][i];
        }

        //roulette wheel selection
        double p = rand.nextDouble();
        double s = 0;
        for (i = 0; i < task_num; i++) {
            if (i == task) {
                continue;
            }
            s += possibility[task][i] / sum;
            if (s >= p) {
                break;
            }
        }
        return i;
    }
    //Calculate the KLD between "task" task and the other tasks

    void cal_KLD(int task) {
        int i, j;
        double tr, u;
        double s1, s2;
        for (i = 0; i < task_num; i++) {
            if (task == i) {
                continue;
            }
            int NVARS = (prob.list_task.get(task).dims > prob.list_task.get(i).dims ? prob.list_task.get(i).dims : prob.list_task.get(task).dims);	//Pick the smaller dimension number to calculate KLD
            get_Cov(task, NVARS);
            get_Cov(i, NVARS);
            get_Cov_Det(task, NVARS);
            get_Cov_Inv(task, NVARS);
            get_Cov_Det(i, NVARS);
            get_Cov_Inv(i, NVARS);
            tr = get_Trace(task, i, NVARS);
            u = get_Mul(task, i, NVARS);
            if (Cov_Det[i] < 1e-3) {
                Cov_Det[i] = 0.001;
            }
            if (Cov_Det[task] < 1e-3) {
                Cov_Det[task] = 0.001;
            }
            s1 = Math.abs(0.5 * (tr + u - NVARS + Math.log(Cov_Det[task] / Cov_Det[i])));

            tr = get_Trace(i, task, NVARS);
            u = get_Mul(i, task, NVARS);

            s2 = Math.abs(0.5 * (tr + u - NVARS + Math.log(Cov_Det[i] / Cov_Det[task])));

            KLD[i] = 0.5 * (s1 + s2);
        }
        //NVARS = NVARS_t[task];
    }

    void get_Cov(int trans_target, int NVARS
    ) {
        int i, j, l;
        for (i = 0; i < NVARS; i++) {
            double s = 0;
            for (j = 0; j < k_archive_size[trans_target]; j++) {
                s += K_archive[trans_target][j].genes[i];
            }
            avg_value[trans_target][i] = s / k_archive_size[trans_target];
        }

        for (i = 0; i < NVARS; i++) {
            for (j = 0; j <= i; j++) {
                double s = 0;
                for (l = 0; l < k_archive_size[trans_target]; l++) {
                    s += (K_archive[trans_target][l].genes[i] - avg_value[trans_target][i]) * (K_archive[trans_target][l].genes[j] - avg_value[trans_target][j]);
                }
                Cov[trans_target][i][j] = Cov[trans_target][j][i] = s / k_archive_size[trans_target];
            }
        }
    }

    void get_Cov_Det(int trans_target, int NVARS) {
        int i, j, k, is = 0, js = 0, l, u, v;
        double f, det, q, d;
        double a[][] = new double[MAX_NVARS][MAX_NVARS];
        for (i = 0; i < NVARS; i++) {
            for (j = 0; j < NVARS; j++) {
                a[i][j] = Cov[trans_target][i][j];
            }
        }

        f = 1.0;
        det = 1.0;
        for (k = 0; k <= NVARS - 2; k++) {
            q = 0.0;

            for (i = k; i <= NVARS - 1; i++) {
                for (j = k; j <= NVARS - 1; j++) {
                    d = Math.abs(a[i][j]);
                    if (d > q) {
                        q = d;
                        is = i;
                        js = j;
                    }
                }
            }

            if (q + 1.0 == 1.0) {
                det = 0.0;
                //	printf("error!");
                return;
            }

            if (is != k) {
                f = -f;
                for (j = k; j <= NVARS - 1; j++) {
                    d = a[k][j];
                    a[k][j] = a[is][j];
                    a[is][j] = d;
                }
            }

            if (js != k) {
                f = -f;
                for (i = k; i <= NVARS - 1; i++) {
                    d = a[i][js];
                    a[i][js] = a[i][k];
                    a[i][k] = d;
                }
            }

            det = det * a[k][k];
            for (i = k + 1; i <= NVARS - 1; i++) {
                d = a[i][k] / a[k][k];
                for (j = k + 1; j <= NVARS - 1; j++) {
                    a[i][j] = a[i][j] - d * a[k][j];
                }
            }

        }

        det = f * det * a[NVARS - 1][NVARS - 1];

        Cov_Det[trans_target] = det;
    }

    void get_Cov_Inv(int trans_target, int NVARS) {
        int is[] = new int[MAX_NVARS];
        int js[] = new int[MAX_NVARS];
        int i, j, k, l, u, v;
        double d, p;
        for (i = 0; i < NVARS; i++) {
            for (j = 0; j < NVARS; j++) {
                Cov_Inv[trans_target][i][j] = Cov[trans_target][i][j];
            }
        }

        for (i = 0; i < NVARS; i++) {
            is[i] = 0;
            js[i] = 0;
        }

        for (k = 0; k <= NVARS - 1; k++) {
            d = 0.0;
            for (i = k; i <= NVARS - 1; i++) {
                for (j = k; j <= NVARS - 1; j++) {
                    p = Math.abs(Cov_Inv[trans_target][i][j]);
                    if (p > d) {
                        d = p;
                        is[k] = i;
                        js[k] = j;
                    }
                }
            }

            if (d + 1.0 == 1.0) {
                //	printf("error!");
                return;
            }

            if (is[k] != k) {
                for (j = 0; j <= NVARS - 1; j++) {
                    p = Cov_Inv[trans_target][k][j];
                    Cov_Inv[trans_target][k][j] = Cov_Inv[trans_target][is[k]][j];
                    Cov_Inv[trans_target][is[k]][j] = p;
                }
            }

            if (js[k] != k) {
                for (i = 0; i <= NVARS - 1; i++) {
                    p = Cov_Inv[trans_target][i][k];
                    Cov_Inv[trans_target][i][k] = Cov_Inv[trans_target][i][js[k]];
                    Cov_Inv[trans_target][i][js[k]] = p;
                }
            }

            Cov_Inv[trans_target][k][k] = 1.0 / Cov_Inv[trans_target][k][k];

            for (j = 0; j <= NVARS - 1; j++) {
                if (j != k) {
                    Cov_Inv[trans_target][k][j] *= Cov_Inv[trans_target][k][k];
                }
            }
            for (i = 0; i <= NVARS - 1; i++) {
                if (i != k) {
                    for (j = 0; j <= NVARS - 1; j++) {
                        if (j != k) {
                            Cov_Inv[trans_target][i][j] = Cov_Inv[trans_target][i][j] - Cov_Inv[trans_target][i][k] * Cov_Inv[trans_target][k][j];
                        }
                    }
                }
            }

            for (i = 0; i <= NVARS - 1; i++) {
                if (i != k) {
                    Cov_Inv[trans_target][i][k] = -Cov_Inv[trans_target][i][k] * Cov_Inv[trans_target][k][k];
                }
            }

        }

        for (k = NVARS - 1; k >= 0; k--) {
            if (js[k] != k) {
                for (j = 0; j <= NVARS - 1; j++) {
                    p = Cov_Inv[trans_target][k][j];
                    Cov_Inv[trans_target][k][j] = Cov_Inv[trans_target][js[k]][j];
                    Cov_Inv[trans_target][js[k]][j] = p;
                }
            }

            if (is[k] != k) {
                for (i = 0; i < NVARS; i++) {
                    p = Cov_Inv[trans_target][i][k];
                    Cov_Inv[trans_target][i][k] = Cov_Inv[trans_target][i][is[k]];
                    Cov_Inv[trans_target][i][is[k]] = p;
                }
            }
        }

    }

    double get_Trace(int t1, int t2, int NVARS
    ) {
        int i, j, l;
        double sum;
        double result[][] = new double[MAX_NVARS][MAX_NVARS];
        for (i = 0; i < NVARS; i++) {
            for (j = 0; j < NVARS; j++) {
                sum = 0;
                for (l = 0; l < NVARS; l++) {
                    sum += Cov_Inv[t1][i][l] * Cov[t2][l][j];
                }
                result[i][j] = sum;
            }
        }

        sum = 0;
        for (i = 0; i < NVARS; i++) {
            sum += result[i][i];
        }

        return sum;
    }

    double get_Mul(int t1, int t2, int NVARS) {
        double a[] = new double[MAX_NVARS];
        double sum;
        int i, j;

        for (i = 0; i < NVARS; i++) {
            sum = 0;
            for (j = 0; j < NVARS; j++) {
                sum += (avg_value[t1][j] - avg_value[t2][j]) * Cov_Inv[t1][j][i];
            }
            a[i] = sum;
        }

        sum = 0;
        for (i = 0; i < NVARS; i++) {
            sum += (avg_value[t1][i] - avg_value[t2][i]) * a[i];
        }

        return sum;
    }

    public void initPopulation() {
        population = new Individual[task_num][SUBPOPSIZE];
        for (int l = 0; l < task_num; l++) {
            fbest[l] = Double.MAX_VALUE;
            for (int i = 0; i < SUBPOPSIZE; i++) {
                population[l][i] = new Individual(MAX_NVARS, task_num);
                for (int j = 0; j < MAX_NVARS; j++) {
                    population[l][i].genes[j] = rand.nextDouble();
                }
                population[l][i].skill_factor = l + 1;
                population[l][i].fitness[l] = prob.list_task.get(l).eval(population[l][i].genes);
                fbest[l] = Math.min(fbest[l], population[l][i].fitness[l]);
                put_k_archive(population[l][i], l);
            }
        }
        for (int i = 0; i < task_num; i++) {
            for (int j = 0; j < task_num; j++) {
                possibility[i][j] = 0;
                reward[i][j] = 1;
            }
        }

    }
    //Insert individual p into "task" archive

    void put_k_archive(Individual p, int task) {
        if (k_archive_size[task] < K_ARCHIVE_SIZE - 1) {
            K_archive[task][k_archive_size[task]++] = p;
        } else {
            int l = rand.nextInt(K_ARCHIVE_SIZE);
            K_archive[task][l] = p;
        }
    }

    public ArrayList<Individual> SBXcrossover2(Individual parent1, Individual parent2) {
        Individual child1 = new Individual(MAX_NVARS, task_num);
        Individual child2 = new Individual(MAX_NVARS, task_num);
        double y1, y2, yl, yu;
        for (int i = 0; i < MAX_NVARS; i++) {
            if (rand.nextDouble() < 0.9) //crossover rate
            {
                if (Math.abs(parent1.genes[i] - parent2.genes[i]) > 0) //crossover if different
                {
                    if (parent1.genes[i] < parent2.genes[i]) {
                        y1 = parent1.genes[i];
                        y2 = parent2.genes[i];
                    } else {
                        y1 = parent2.genes[i];
                        y2 = parent1.genes[i];
                    }
                    yl = MIN_V[i];
                    yu = MAX_V[i];
                    double r = rand.nextDouble();
                    double beta = 1.0 + (2.0 * (y1 - yl) / (y2 - y1));
                    double Xalpha = 2.0 - Math.pow(beta, -(Parameter.mu + 1.0));
                    double betaq;
                    if (r <= (1.0 / Xalpha)) {
                        betaq = Math.pow((r * Xalpha), (1.0 / (Parameter.mu + 1.0)));
                    } else {
                        betaq = Math.pow((1.0 / (2.0 - r * Xalpha)), (1.0 / (Parameter.mu + 1.0)));
                    }
                    double c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));

                    beta = 1.0 + (2.0 * (yu - y2) / (y2 - y1));
                    Xalpha = 2.0 - Math.pow(beta, -(Parameter.mu + 1.0));
                    if (r <= (1.0 / Xalpha)) {
                        betaq = Math.pow((r * Xalpha), (1.0 / (Parameter.mu + 1.0)));
                    } else {
                        betaq = Math.pow((1.0 / (2.0 - r * Xalpha)), (1.0 / (Parameter.mu + 1.0)));
                    }
                    double c2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));
                    if (c1 < yl) {
                        c1 = yl;
                    }
                    if (c2 < yl) {
                        c2 = yl;
                    }
                    if (c1 > yu) {
                        c1 = yu;
                    }
                    if (c2 > yu) {
                        c2 = yu;
                    }
                    if (rand.nextDouble() <= 0.5) {
                        child1.genes[i] = c2;
                        child2.genes[i] = c1;
                    } else {
                        child1.genes[i] = c1;
                        child2.genes[i] = c2;
                    }
                } else {
                    child1.genes[i] = parent1.genes[i];
                    child2.genes[i] = parent2.genes[i];
                }
            } else {
                child1.genes[i] = parent1.genes[i];
                child2.genes[i] = parent2.genes[i];
            }
        }
        ArrayList<Individual> offs = new ArrayList();
        offs.add(child1);
        offs.add(child2);
        return offs;
    }

    double gauss(double mu, double sigma) {
        return mu + sigma * Math.sqrt(-2.0 * Math.log(rand.nextDouble())) * Math.sin(2.0 * Math.PI * rand.nextDouble());
    }

    public void GaussMutation(int task, Individual ind) {
        double p = 0.01;
        for (int i = 0; i < MAX_NVARS; i++) {
            if (rand.nextDouble() < 1.0 / MAX_NVARS) {
                double t = ind.genes[i] + gauss(0, 0.1 * MAX_V[i]);
                if (t > 1) {
                    t = ind.genes[i] + rand.nextDouble() * (1 - ind.genes[i]);
                } else if (t < 0) {
                    t = rand.nextDouble() * ind.genes[i];
                }

                ind.genes[i] = t;
            }
        }
    }

  

}
