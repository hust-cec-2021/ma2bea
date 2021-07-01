
import java.util.ArrayList;
import java.util.Arrays;
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
public class EBS_GA extends Algorithm {

    Random rand;
    Problem prob;
    int SUBPOPSIZE = 100;
    int maxEvals;
    int MAX_GEN = 1000;
    double rmp = 0.3;
    int MAX_NVARS;

    public EBS_GA(Problem prob, int POPSIZE, int maxEvals) {
        System.out.println("EBS_GA is running");
        this.prob = prob;
        this.SUBPOPSIZE = POPSIZE / prob.list_task.size();
        this.maxEvals = maxEvals;
        this.MAX_GEN = maxEvals / POPSIZE;
    }

    public ArrayList<Individual> run(int seed, double[][] data) {
        long start =  System.currentTimeMillis();
        Parameter.countFitness = new int[prob.list_task.size()];
        Parameter.num_fitness = 0;
//        System.out.println("EBS-GA is running on seed = " + seed);
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
                ind.skill_factor = i + 1;
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

        int generation = 1;
        double R_o[] = new double[prob.list_task.size()];
        double R_s[] = new double[prob.list_task.size()];
        double E_o[] = new double[prob.list_task.size()];
        double E_s[] = new double[prob.list_task.size()];
        for (int i = 0; i < prob.list_task.size(); i++) {
            R_o[i] =0;
            R_s[i] =0;
            E_o[i] =0;
            E_s[i] = 0;   
        }
        while (generation < MAX_GEN) {
            generation++;
            ArrayList<ArrayList<Individual>> Off_pops = new ArrayList<ArrayList<Individual>>();
            ArrayList<Individual> pop = new ArrayList<Individual>();
            for (int i = 0; i < prob.list_task.size(); i++) {
                pop.addAll(subPops.get(i));
            }

            for (int i = 0; i < prob.list_task.size(); i++) {
                ArrayList<Individual> offs = new ArrayList<Individual>();
                double lamda_i = (R_o[i]/(E_o[i] +0.0000001)) / (R_s[i]/(E_s[i] +0.0000001) + R_o[i]/(E_o[i] +0.0000001) +0.000000001);
                if (rand.nextDouble() < lamda_i) {
                    E_o[i] += SUBPOPSIZE;
                    boolean visited[] = new boolean[pop.size()];
                    Arrays.fill(visited, false);
                    while (offs.size() < SUBPOPSIZE) {
                        int a = rand.nextInt(pop.size());
                        while (visited[a]) {
                            a = rand.nextInt(pop.size());
                        }
                        offs.add(pop.get(a));
                        visited[a] = true;
                    }
                    for (Individual ind : offs) {
                        ind.fitness[i] = prob.list_task.get(i).eval(ind.genes);
                        if (ind.fitness[i] < best.get(i).fitness[i]) {
                                R_o[i] ++;
                        }
                    }
                } else {
                    E_s[i] += SUBPOPSIZE;
                    offs.addAll(reproduction(i + 1, subPops.get(i), SUBPOPSIZE));
                    for (Individual ind : offs) {
                        ind.fitness[i] = prob.list_task.get(i).eval(ind.genes);
                        if (ind.fitness[i] < best.get(i).fitness[i]) {
                                R_s[i] ++;
                        }
                    }
                }
                Off_pops.add(offs);

            }
            for (int i = 0; i < prob.list_task.size(); i++) {
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
                data[generation - 1][i] = best.get(i).fitness[i];

            }

        }
        System.out.println("Seed " + seed + " NUM FITNESS: " + Parameter.num_fitness);
        for (int i = 1; i <= prob.list_task.size(); i++) {
            System.out.println(best.get(i - 1).skill_factor + " : " + best.get(i - 1).fitness[i - 1]);

        }
        long end =  System.currentTimeMillis();
        System.out.println(end-start);
        return best;

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
            variable_swap(child.get(0), child.get(1));
            offs.addAll(child);
        }
        return offs;

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
