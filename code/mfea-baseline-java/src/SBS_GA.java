
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
public class SBS_GA extends Algorithm {

    Random rand;
    Problem prob;
    int SUBPOPSIZE = 100;
    int maxEvals;
    int MAX_GEN = 1000;
    double rmp = 0.3;
    int MAX_NVARS;
    double H = 0.5;
    double B = 0.25;

    public SBS_GA(Problem prob, int POPSIZE, int maxEvals) {
        System.out.println("SBS_GA is running");
        this.prob = prob;
        this.SUBPOPSIZE = POPSIZE / prob.list_task.size();
        this.maxEvals = maxEvals;
        this.MAX_GEN = maxEvals / POPSIZE;
    }

    public ArrayList<Individual> run(int seed, double[][] data) {
        long start =  System.currentTimeMillis();
        Parameter.countFitness = new int[prob.list_task.size()];
        Parameter.num_fitness = 0;
//        System.out.println("SBS-GA is running on seed = " + seed);
//        System.out.println("MAX GENERATIONS " + MAX_GEN);
        MAX_NVARS = -1;
        for (Task t : prob.list_task) {
            MAX_NVARS = Math.max(MAX_NVARS, t.dims);
        }
        rand = new Random(seed);
        ArrayList<Individual> best = new ArrayList<Individual>();
        ArrayList<ArrayList<Individual>> subPops = new ArrayList<ArrayList<Individual>>();
        for (int i = 0; i < prob.list_task.size(); i++) {
            subPops.add(new ArrayList<Individual>());
            for (int k = 0; k < SUBPOPSIZE; k++) {
                Individual ind = new Individual(MAX_NVARS, prob.list_task.size());
                ind.init(rand);
                ind.fitness[i] = prob.list_task.get(i).eval(ind.genes);
                ind.skill_factor = i+1;
                subPops.get(i).add(ind);
                
            }
            final int x = i;

            Collections.sort(subPops.get(i), new Comparator<Individual>() {
                @Override
                public int compare(Individual o1, Individual o2) {
                    if (o1.fitness[x] < o2.fitness[x]) {
                        return -1;
                    } else if (o1.fitness[x] > o2.fitness[x]) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            best.add(subPops.get(i).get(0));
            data[0][i] = best.get(i).fitness[i];
        }
        double[][] M = ones(prob.list_task.size(), prob.list_task.size());
        double[][] N = ones(prob.list_task.size(), prob.list_task.size());
        double[][] C = ones(prob.list_task.size(), prob.list_task.size());
        double[][] O = ones(prob.list_task.size(), prob.list_task.size());
        double[][] P = ones(prob.list_task.size(), prob.list_task.size());
        double[][] A = ones(prob.list_task.size(), prob.list_task.size());
        double[][] R = ones(prob.list_task.size(), prob.list_task.size());
        updateRate(M, N, C, O, P, A, R);
        int generation = 1;

        while (generation < MAX_GEN) {
            generation++;
            ArrayList<ArrayList<Individual>> Off_pops = new ArrayList<ArrayList<Individual>>();
            ArrayList<ArrayList<Individual>> copy_Off_pops = new ArrayList<ArrayList<Individual>>();

            for (int i = 0; i < prob.list_task.size(); i++) {
                ArrayList<Individual>  offs = reproduction(i + 1, subPops.get(i), SUBPOPSIZE);
                Off_pops.add(offs);
                copy_Off_pops.add(offs);
            }
            for (int i = 0; i < prob.list_task.size(); i++) {
                double max = -1;
                int task_j = i;
                for (int k = 0; k < prob.list_task.size(); k++) {
                    if (k != i && max < R[i][k]) {
                        max = R[i][k];
                        task_j = k;
                    }
                }
                double Ri = max;
                if (rand.nextDouble() < Ri) {
                    int S_i = (int) Math.floor(Ri * SUBPOPSIZE);
                    if (S_i > SUBPOPSIZE) {
                        S_i = SUBPOPSIZE;
                    }
                    for (int h = SUBPOPSIZE - S_i; h < SUBPOPSIZE; h++) {
                        int m = SUBPOPSIZE - h;
                        Off_pops.get(i).set(h, copy_Off_pops.get(task_j).get(m));
                    }
                }
                for (Individual ind : Off_pops.get(i)) {
                    ind.fitness[i] = prob.list_task.get(i).eval(ind.genes);
                }
                subPops.get(i).addAll(Off_pops.get(i));
                final int x = i;
                Collections.sort(subPops.get(i), new Comparator<Individual>() {
                    @Override
                    public int compare(Individual o1, Individual o2) {
                        if (o1.fitness[x] < o2.fitness[x]) {
                            return -1;
                        } else if (o1.fitness[x] > o2.fitness[x]) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
                subPops.get(i).subList(SUBPOPSIZE, subPops.get(i).size()).clear();
                best.set(i, subPops.get(i).get(0));
                data[generation -1][i] =  best.get(i).fitness[i];
                
            }
            updateSymbiosis(Off_pops, subPops, M, N, C, O, P, A);
            updateRate(M, N, C, O, P, A, R);

        }
        System.out.println("Seed " + seed + " NUM FITNESS: " + Parameter.num_fitness);
        for (int i = 1; i <= prob.list_task.size(); i++) {
            System.out.println(best.get(i - 1).skill_factor + " : " + best.get(i - 1).fitness[i - 1]);

        }
        long end =  System.currentTimeMillis();
        System.out.println(end-start);
        return best;

    }

    public void updateRate(double[][] M, double[][] N, double[][] C, double[][] O, double[][] P, double[][] A, double[][] R) {
        for (int i = 0; i < prob.list_task.size(); i++) {
            for (int j = 0; j < prob.list_task.size(); j++) {
                if (i != j) {
                    double T_pos = M[i][j] + O[i][j] + P[i][j];
                    double T_neg = A[i][j] + C[i][j];
                    double T_neu = N[i][j];
                    R[i][j] = T_pos / (T_pos + T_neg + T_neu);
                }
            }
        }
    }

    public void updateSymbiosis(ArrayList<ArrayList<Individual>> offs_pop, ArrayList<ArrayList<Individual>> sub_pop, double[][] M, double[][] N, double[][] C, double[][] O, double[][] P, double[][] A) {
        for (int i = 1; i <= prob.list_task.size(); i++) {
            for (Individual ind : offs_pop.get(i - 1)) {
                int j = ind.skill_factor;
                if (j != i) {
                    double r_i = rank(sub_pop.get(i - 1), ind, i);
                    double r_j = rank(sub_pop.get(j - 1), ind, j);
                    if (isBenefit(r_i) && isBenefit(r_j)) {
                        M[i - 1][j - 1] += 1;
                    } else if (isNeural(r_i) && isNeural(r_j)) {
                        N[i - 1][j - 1] += 1;
                    } else if (isHarmful(r_i) && isHarmful(r_j)) {
                        C[i - 1][j - 1] += 1;
                    } else if (isBenefit(r_i) && isNeural(r_j)) {
                        O[i - 1][j - 1] += 1;
                    } else if (isBenefit(r_i) && isHarmful(r_j)) {
                        P[i - 1][j - 1] += 1;
                    } else if (isNeural(r_i) && isHarmful(r_j)) {
                        A[i - 1][j - 1] += 1;
                    }
                }
            }
        }
    }
    
    public double rank(ArrayList<Individual> pop, Individual ind, int task) {
        int rank = pop.size();
        double fitness = ind.fitness[task -1];
        for (int i = 0; i < pop.size(); i++) {
            if (fitness <= pop.get(i).fitness[task -1]) {
                rank = i;
                break;
            }
        }
        return rank * 1.0 / pop.size();
    }
    

    public boolean isBenefit(double r) {
        if (r <= B) {
            return true;
        }
        return false;
    }

    public boolean isHarmful(double r) {
        if (r > H) {
            return true;
        }
        return false;
    }

    public boolean isNeural(double r) {
        return (r > B && r <= H);
    }

    public double[][] ones(int a, int b) {
        double[][] matrix = new double[a][b];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                matrix[i][j] = matrix[j][i] = 1;
            }
        }
        return matrix;
    }

    public ArrayList<Individual> reproduction(int task, ArrayList<Individual> subpop, int SIZE) {
        ArrayList<Individual> offs = new ArrayList<Individual>();
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < subpop.size(); i++) {
            list.add(i);
        }
        Collections.shuffle(list, rand);
        while (offs.size() < SIZE) {
            int a = list.get(rand.nextInt(subpop.size() / 2));
            int b = list.get(rand.nextInt(subpop.size() / 2) + subpop.size() / 2);

            Individual parent1 = subpop.get(a);
            Individual parent2 = subpop.get(b);
            ArrayList<Individual> child = crossover(parent1, parent2);
            for (Individual ind : child) {
                GaussMutation(ind);
                ind.skill_factor = task;
            }
            variable_swap(child.get(0),child.get(1) );
            offs.addAll(child);
        }
        return offs;

    }
    public void variable_swap(Individual p1, Individual p2){
        Individual ind1 = p1;
        Individual ind2 = p2;
       
        
        for(int i=1; i<=MAX_NVARS; i++){
            if(rand.nextDouble() >0.5){
                double temp1 = p1.genes[i-1];
                double temp2 = p2.genes[i-1];
                ind1.genes[i-1] = temp2;
                ind2.genes[i-1] =  temp1;
            }
        }
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
        Individual child1 = new Individual(MAX_NVARS, MAX_NVARS);
        Individual child2 = new Individual(MAX_NVARS, MAX_NVARS);
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

    public void direct_mutation(Individual parent) {
        Individual ind = parent;
        for (int i = 1; i <= MAX_NVARS; i++) {
            if (rand.nextDouble() < 1.0 / MAX_NVARS) {
                double u = rand.nextDouble();
                double v = 0;
                if (u <= 0.5) {
                    double del = Math.pow((2 * u), 1.0 / (1 + Parameter.mum)) - 1;
                    v = ind.genes[i - 1] * (del + 1);
                } else {
                    double del = 1 - Math.pow(2 * (1 - u), 1.0 / (1 + Parameter.mum));
                    v = ind.genes[i - 1] + del * (1 - ind.genes[i - 1]);
                }
                if (v > 1) {
                    ind.genes[i - 1] = ind.genes[i - 1] + rand.nextDouble() * (1 - ind.genes[i - 1]);
                } else if (v < 0) {
                    ind.genes[i - 1] = ind.genes[i - 1] * rand.nextDouble();
                }
            }
        }
    }
    public void GaussMutation(Individual ind) {
        double p = 0.01;
        for (int i = 0; i < MAX_NVARS; i++) {
            if (rand.nextDouble() < 1.0 / MAX_NVARS) {
                double t = ind.genes[i] + rand.nextGaussian();
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
