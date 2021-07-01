
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
public class LMFEA extends Algorithm {

    Random rand;
    Problem prob;
    int POPSIZE = 1000;
    int maxEvals;
    int MAX_GEN = 1000;
    double[][][] M_rmp;

    ArrayList<Individual> A;
    int H;
    int memory_pos[][];
    double success_rmp[][];
    double sum_rmp[][];
    boolean change[][];
   int generation;
 
    public LMFEA(Problem prob, int POPSIZE, int maxEvals, int H) {
        this.prob = prob;
        this.POPSIZE = POPSIZE;
        this.maxEvals = maxEvals;
        this.MAX_GEN = maxEvals / POPSIZE;
        this.H = H;
        Parameter.o_rmp = new ArrayList[prob.list_task.size()][prob.list_task.size()][Parameter.numRecords];
        for (int i = 0; i < prob.list_task.size(); i++) {
            for (int j = i + 1; j < prob.list_task.size(); j++) {
                for(int k=0;k<Parameter.numRecords; k++){
                    Parameter.o_rmp[i][j][k] = new ArrayList<>();
                }
                Parameter.o_rmp[i][j][0].add(0.5);
            }
        }
    }

    public ArrayList<Individual> run(int seed, double[][] data) {
        Parameter.countFitness = new int[prob.list_task.size()];
        A = new ArrayList<Individual>();
      //  rmp = new ArrayList[Parameter.numRecords][prob.list_task.size()][prob.list_task.size()];
        
        M_rmp = new double[prob.list_task.size()][prob.list_task.size()][H];
        memory_pos = new int[prob.list_task.size()][prob.list_task.size()];
        for (int i = 0; i < prob.list_task.size(); i++) {
            for (int j = i + 1; j < prob.list_task.size(); j++) {
               
                for (int k = 0; k < H; k++) {
                    M_rmp[i][j][k] = M_rmp[j][i][k] = 0.5;
                }
                memory_pos[i][j] = memory_pos[j][i] = 0;
            }
        }

        Parameter.num_fitness = 0;
        System.out.println("Adaptive LMFEA 1 is running on seed = " + seed);
        rand = new Random(seed);
        ArrayList<Individual> best = new ArrayList<Individual>();
        Population pop = new Population(POPSIZE, prob, rand);
        pop.init();
        pop.update_scalar_fitness();
        pop.selection();

        generation = 1;

        for (int i = 1; i <= prob.list_task.size(); i++) {
            for (Individual ind : pop.pop) {
                if (ind.skill_factor == i) {
                    data[generation - 1][i - 1] = ind.fitness[i - 1];
                    best.add(ind);
                    break;
                }
            }
        }

        while (generation < MAX_GEN) {
            generation++;
            // repopulation
            ArrayList<Individual> offs = reproduction(pop, POPSIZE);
            pop.pop.addAll(offs);
            pop.update_scalar_fitness();
            pop.selection();
           
            for (int i = 1; i <= prob.list_task.size(); i++) {
                for (Individual ind : pop.pop) {
                    if (ind.skill_factor == i) {
                        if (best.get(i - 1).fitness[i - 1] > ind.fitness[i - 1]) {
                            best.set(i - 1, ind);
                        }
                        data[generation - 1][i - 1] = best.get(i - 1).fitness[i - 1];

                        break;
                    }
                }
            }
//            System.out.println("---------------------------------------");
//            for (int i = 1; i <= prob.list_task.size(); i++) {
//                System.out.println(best.get(i - 1).skill_factor + " : " + best.get(i - 1).fitness[i - 1]);
//
//            }
        }
        System.out.println("Seed " + seed + " NUM FITNESS: " + Parameter.num_fitness);
        for (int i = 1; i <= prob.list_task.size(); i++) {
            System.out.println(best.get(i - 1).skill_factor + " : " + best.get(i - 1).fitness[i - 1]);

        }
        return best;

    }

    public ArrayList<Individual> reproduction(Population pop, int SIZE) {
        ArrayList<Individual> offs = new ArrayList<Individual>();
        ArrayList<Integer> list = new ArrayList<Integer>();
        success_rmp = new double[prob.list_task.size()][prob.list_task.size()];
        sum_rmp = new double[prob.list_task.size()][prob.list_task.size()];
        change = new boolean[prob.list_task.size()][prob.list_task.size()];
        for (int i = 0; i < prob.list_task.size(); i++) {
            for (int j = i+1; j < prob.list_task.size(); j++) {
                sum_rmp[i][j] = sum_rmp[j][i] =0;
                success_rmp[i][j] = success_rmp[j][i] =0;
                change[i][j] = change[j][i] =false;
            }
        }
        for (int i = 0; i < pop.pop.size(); i++) {
            list.add(i);
        }
        Collections.shuffle(list, rand);

        ArrayList<Double> success_df = new ArrayList<>();
        while (offs.size() < SIZE) {
            int a = list.get(rand.nextInt(pop.pop.size() / 2));
            int b = list.get(rand.nextInt(pop.pop.size() / 2) + pop.pop.size() / 2);
            Individual parent1 = pop.pop.get(a);
            Individual parent2 = pop.pop.get(b);
            ArrayList<Individual> child;
            double m_rmp = M_rmp[parent1.skill_factor - 1][parent2.skill_factor - 1][rand.nextInt(H)];
            double pop_rmp;
            do {
                pop_rmp = gauss(m_rmp, 0.1);
            } while (pop_rmp <= 0);
            if (pop_rmp > 1) {
                pop_rmp = 1;
            }
            int k1;
            int k2;
            if(parent1.skill_factor <parent2.skill_factor ){
                k1 = parent1.skill_factor;
                k2 = parent2.skill_factor;
                Parameter.o_rmp[k1-1][k2-1][generation -1].add(pop_rmp);
            }else if(parent2.skill_factor <parent1.skill_factor){
                k1 = parent2.skill_factor;
                k2 = parent1.skill_factor;
                Parameter.o_rmp[k1-1][k2-1][generation -1].add(pop_rmp);
            }
            double df = 0;
            
            if (parent1.skill_factor == parent2.skill_factor || rand.nextDouble() < pop_rmp) {
                child = pop.crossover(parent1, parent2);

                for (Individual ind : child) {
                    if (rand.nextDouble() > 0.5) {
                        ind.skill_factor = parent1.skill_factor;
                        for (int i = 1; i <= prob.list_task.size(); i++) {
                            if (i == ind.skill_factor) {
                                ind.fitness[i - 1] = prob.list_task.get(i - 1).eval(ind.genes);
                            } else {
                                ind.fitness[i - 1] = Double.MAX_VALUE;
                            }
                        }
                        df = Math.max(df, (parent1.fitness[parent1.skill_factor - 1] - ind.fitness[ind.skill_factor - 1]) / parent1.fitness[parent1.skill_factor - 1]);
                    } else {
                        ind.skill_factor = parent2.skill_factor;
                        for (int i = 1; i <= prob.list_task.size(); i++) {
                            if (i == ind.skill_factor) {
                                ind.fitness[i - 1] = prob.list_task.get(i - 1).eval(ind.genes);
                            } else {
                                ind.fitness[i - 1] = Double.MAX_VALUE;
                            }
                        }
                        df = Math.max(df, (parent2.fitness[parent2.skill_factor - 1] - ind.fitness[ind.skill_factor - 1]) / parent2.fitness[parent2.skill_factor - 1]);
                    }

                }
            } else {
                child = new ArrayList<Individual>();
                Individual ind1 = pop.mutation(parent1);
                ind1.skill_factor = parent1.skill_factor;
                Individual ind2 = pop.mutation(parent2);
                ind2.skill_factor = parent2.skill_factor;
                child.add(ind1);
                child.add(ind2);
                for (Individual ind : child) {
                    for (int i = 1; i <= prob.list_task.size(); i++) {
                        if (i == ind.skill_factor) {
                            ind.fitness[i - 1] = prob.list_task.get(i - 1).eval(ind.genes);
                        } else {
                            ind.fitness[i - 1] = Double.MAX_VALUE;
                        }
                    }
                }
                df = Math.max(df, (parent2.fitness[parent2.skill_factor - 1] - ind2.fitness[ind2.skill_factor - 1]) / parent2.fitness[parent2.skill_factor - 1]);
                df = Math.max(df, (parent1.fitness[parent1.skill_factor - 1] - ind1.fitness[ind1.skill_factor - 1]) / parent1.fitness[parent1.skill_factor - 1]);
            }
            if (df > 0 && parent2.skill_factor != parent1.skill_factor) {
                change[parent2.skill_factor -1][parent1.skill_factor -1] = change[parent1.skill_factor -1][parent2.skill_factor -1] = true;
                success_rmp[parent2.skill_factor - 1][parent1.skill_factor - 1] += df * pop_rmp * pop_rmp;
                success_rmp[parent1.skill_factor - 1][parent2.skill_factor - 1] = success_rmp[parent2.skill_factor - 1][parent1.skill_factor - 1];

                sum_rmp[parent1.skill_factor - 1][parent2.skill_factor - 1] += df * pop_rmp;
                sum_rmp[parent2.skill_factor - 1][parent1.skill_factor - 1] = sum_rmp[parent1.skill_factor - 1][parent2.skill_factor - 1];
            }

            offs.addAll(child);
        }
        for (int i = 0; i < prob.list_task.size(); i++) {
            for (int j = i + 1; j < prob.list_task.size(); j++) {
                if (change[i][j]) {
                    M_rmp[i][j][memory_pos[i][j]] = success_rmp[i][j]/sum_rmp[i][j];
                    M_rmp[j][i][memory_pos[j][i]]= M_rmp[i][j][memory_pos[i][j]];
                    memory_pos[i][j]= (memory_pos[i][j]+1)%H;
                    memory_pos[j][i] =memory_pos[i][j];   
                }
            }
        }

        return offs;

    }

    double cauchy_g(double mu, double gamma) {
        return mu + gamma * Math.tan(Math.PI * (rand.nextDouble() - 0.5));
    }

    double gauss(double mu, double sigma) {
        return mu + sigma * Math.sqrt(-2.0 * Math.log(rand.nextDouble())) * Math.sin(2.0 * Math.PI * rand.nextDouble());
    }
}
