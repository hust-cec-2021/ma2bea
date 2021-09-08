
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
public class MaTDE extends Algorithm {

    public int SUBPOPSIZE = 100;//Population size for each  sub-population

    public int MAX_GEN = 1000;//Maximum generation
    public int RECORD_FRE = 1000;//Interval of recording

    public int MAXEVALS = 1000000;//Mximum number of evaluations
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
    //---------parameters for DE---------------------
    double UF = 2, LF = 0.1, UCR = 0.9, LCR = 0.1;
    Individual[][] population;
    int generation;
    double fbest[];

    public MaTDE(Problem prob) {
        this.prob = prob;
    }

    public MaTDE(Problem prob, int SIZE, int maxEvals) {
        this.prob = prob;

        this.SUBPOPSIZE = SIZE / prob.list_task.size();
        this.MAXEVALS = maxEvals;

    }

    public ArrayList<Individual> run(int seed, double[][] data) {
        Parameter.countFitness = new int[prob.list_task.size()];
        Parameter.num_fitness = 0;
        System.out.println("MaTDE is running on seed = " + seed);
        rand = new Random(seed);
        ArrayList<Individual> best = new ArrayList<Individual>();
        task_num = prob.list_task.size();
        possibility = new double[task_num][task_num];
        reward = new double[task_num][task_num];
        MAX_NVARS = -1;
        for (Task t : prob.list_task) {
            MAX_NVARS = Math.max(MAX_NVARS, t.dims);
        }

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
        return best;
    }

    void reproduction(int task) {
        int i, j, k, l;
        int r1;

        if (rand.nextDouble() > alpha) {   //perform the crossover and mutation within the subpopulation
            for (i = 0; i < SUBPOPSIZE; i++) {
                do {
                    r1 = rand.nextInt(SUBPOPSIZE);
                } while (i == r1);
                double F = rand.nextDouble() * (UF - LF) + LF;
                double CR = rand.nextDouble() * (UCR - LCR) + LCR;

                k = rand.nextInt(prob.list_task.get(task).dims);
                double genes[] = new double[MAX_NVARS];
                for (j = 0; j < MAX_NVARS; j++) {
                    genes[j] = population[task][i].genes[j] + F * (population[task][r1].genes[j] - population[task][i].genes[j]);
                    if (genes[j] > 1) {
                        genes[j] = rand.nextDouble() * (1 - population[task][i].genes[j]) + population[task][i].genes[j];
                    }
                    if (genes[j] < 0) {
                        genes[j] = rand.nextDouble() * population[task][i].genes[j];
                    }
                    if (k == j || rand.nextDouble() < CR) {
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
                double CR = rand.nextDouble() * (UCR - LCR) + LCR;
                k = rand.nextInt(NVARS);
                double genes[] = new double[MAX_NVARS];
                for (j = 0; j < MAX_NVARS; j++) {
                    if (k == j || rand.nextDouble() < CR) {            //at least one dimension is replaced
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

    void get_Cov(int trans_target, int NVARS) {
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

    double get_Trace(int t1, int t2, int NVARS) {
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

    public ArrayList<Individual> reproduction(Individual[] pop, Individual[] pop2) {
        ArrayList<Individual> offs = new ArrayList<Individual>();
        while (offs.size() < pop.length) {
            if (rand.nextDouble() < Parameter.pc) {
                int a = rand.nextInt(pop.length);

                int b = rand.nextInt(Math.min(K_ARCHIVE_SIZE, k_archive_size[pop2[0].skill_factor - 1]) + pop2.length);
                Individual parent1 = pop[a];
                Individual parent2;
                if (b < pop2.length) {
                    parent2 = pop2[b];
                } else {
                    parent2 = K_archive[pop2[0].skill_factor - 1][b - pop2.length];
                }

                ArrayList<Individual> child = crossover(parent1, parent2);
                for (Individual ind : child) {
                    if (rand.nextDouble() < Parameter.pm) {
                        direct_mutation(ind);
                    }
                }
                variable_swap(child.get(0), child.get(1));
                for (Individual ind : child) {
                    ind.skill_factor = parent1.skill_factor;
                    for (int i = 1; i <= prob.list_task.size(); i++) {
                        if (i == ind.skill_factor) {
                            ind.fitness[i - 1] = prob.list_task.get(i - 1).eval(ind.genes);
                        } else {
                            ind.fitness[i - 1] = Double.MAX_VALUE;
                        }
                    }

                }
                offs.addAll(child);

            }
        }
        return offs;

    }

    public ArrayList<Individual> crossover(Individual parent1, Individual parent2) {
        ArrayList<Individual> off_spring = new ArrayList<Individual>();
        double cf[] = new double[MAX_NVARS];
        for (int i = 0; i < MAX_NVARS; i++) {
            cf[i] = 1;
            double u = rand.nextDouble();
            if (u <= 0.5) {
                cf[i] = Math.pow((2 * u), 1.0 / (Parameter.mu + 1));
            } else {
                cf[i] = Math.pow(2 * (1 - u), -1.0 / (Parameter.mu + 1));
            }
        }
        Individual child1 = new Individual(MAX_NVARS, task_num);
        Individual child2 = new Individual(MAX_NVARS, task_num);
        for (int i = 0; i < MAX_NVARS; i++) {
            double v = 0.5 * ((1 + cf[i]) * parent1.genes[i] + (1 - cf[i]) * parent2.genes[i]);
            if (v > 1) {
                v = 1;
            } else if (v < 0) {
                v = 0;
            }
            child1.genes[i] = v;

            double v2 = 0.5 * ((1 - cf[i]) * parent1.genes[i] + (1 + cf[i]) * parent2.genes[i]);
            if (v2 > 1) {
                v2 = 1;
            } else if (v2 < 0) {
                v2 = 0;
            }
            child2.genes[i] = v2;
        }
        off_spring.add(child1);
        off_spring.add(child2);
        return off_spring;
    }

    public Individual mutation(Individual parent) {
        Individual ind = new Individual(MAX_NVARS, task_num);
        for (int i = 0; i < MAX_NVARS; i++) {
            ind.genes[i] = parent.genes[i];
        }

        for (int i = 1; i <= MAX_NVARS; i++) {
            if (rand.nextDouble() < 1.0 / MAX_NVARS) {
                double u = rand.nextDouble();
                if (u <= 0.5) {
                    double del = Math.pow((2 * u), 1.0 / (1 + Parameter.mum)) - 1;
                    ind.genes[i - 1] = ind.genes[i - 1] * (del + 1);
                } else {
                    double del = 1 - Math.pow(2 * (1 - u), 1.0 / (1 + Parameter.mum));
                    ind.genes[i - 1] = ind.genes[i - 1] + del * (1 - ind.genes[i - 1]);
                }
            }
            if (ind.genes[i - 1] > 1) {
                ind.genes[i - 1] = 1;
            } else if (ind.genes[i - 1] < 0) {
                ind.genes[i - 1] = 0;
            }
        }
        return ind;

    }

    public void direct_mutation(Individual parent) {
        Individual ind = parent;
        for (int i = 1; i <= MAX_NVARS; i++) {
            if (rand.nextDouble() < 1.0 / MAX_NVARS) {
                double u = rand.nextDouble();
                if (u <= 0.5) {
                    double del = Math.pow((2 * u), 1.0 / (1 + Parameter.mum)) - 1;
                    ind.genes[i - 1] = ind.genes[i - 1] * (del + 1);
                } else {
                    double del = 1 - Math.pow(2 * (1 - u), 1.0 / (1 + Parameter.mum));
                    ind.genes[i - 1] = ind.genes[i - 1] + del * (1 - ind.genes[i - 1]);
                }
            }
            if (ind.genes[i - 1] > 1) {
                ind.genes[i - 1] = 1;
            } else if (ind.genes[i - 1] < 0) {
                ind.genes[i - 1] = 0;
            }
        }
    }

    public void variable_swap(Individual p1, Individual p2) {
        Individual ind1 = p1;
        Individual ind2 = p2;

        for (int i = 1; i <= MAX_NVARS; i++) {
            if (rand.nextDouble() > 0.5) {
                double temp1 = p1.genes[i - 1];
                double temp2 = p2.genes[i - 1];
                ind1.genes[i - 1] = temp2;
                ind2.genes[i - 1] = temp1;
            }
        }
    }
   

}
