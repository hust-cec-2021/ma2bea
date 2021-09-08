
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
public class MFEA extends Algorithm {

    Random rand;
    Problem prob;
    int POPSIZE = 1000;
    int maxEvals;
    int MAX_GEN = 1000;
    double rmp = 0.3;



    public MFEA(Problem prob, int POPSIZE, int maxEvals, double rmp) {
         System.out.println("MFEA is running");
        this.prob = prob;
        this.rmp = rmp;
        this.POPSIZE = POPSIZE;
        this.maxEvals = maxEvals;
        this.MAX_GEN = maxEvals / POPSIZE;
        
    }

    public ArrayList<Individual> run(int seed, double[][] data) {
        long start =  System.currentTimeMillis();
        Parameter.countFitness = new int[prob.list_task.size()];
        Parameter.num_fitness =0;
//        System.out.println("MFEA is running on seed = " + seed);
//        System.out.println("MAX GENERATIONS "+ MAX_GEN);
        
        rand = new Random(seed);
        ArrayList<Individual> best = new ArrayList<Individual>();
        Population pop = new Population(POPSIZE, prob, rand);
        pop.init();
        pop.update_scalar_fitness();
        pop.selection();
        int generation = 1;
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
        long end =  System.currentTimeMillis();
        System.out.println(end-start);
        return best;

    }

    public ArrayList<Individual> reproduction(Population pop, int SIZE) {
        ArrayList<Individual> offs = new ArrayList<Individual>();
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i=0;i< pop.pop.size(); i++){
            list.add(i);
        }
        Collections.shuffle(list, rand);
        while (offs.size() < SIZE) {
            int a = list.get(rand.nextInt(pop.pop.size()/2));
            int b = list.get(rand.nextInt(pop.pop.size()/2)+pop.pop.size()/2)  ;

            Individual parent1 = pop.pop.get(a);
            Individual parent2 = pop.pop.get(b);
            ArrayList<Individual> child;
            if (parent1.skill_factor == parent2.skill_factor || rand.nextDouble() < rmp) {
                child = pop.crossover(parent1, parent2);
                for (Individual ind : child) {
                    if (rand.nextDouble() > 0.5) {
                        ind.skill_factor = parent1.skill_factor;

                    } else {
                        ind.skill_factor = parent2.skill_factor;
                    }
                }
            } else {
                child = new ArrayList<Individual>();
                Individual ind1 = pop.mutation(parent1);
                ind1.skill_factor = parent1.skill_factor;
                Individual ind2 = pop.mutation(parent2);
                ind2.skill_factor = parent2.skill_factor;
                child.add(ind2);
                child.add(ind1);
            }
            offs.addAll(child);
        }
        for (Individual ind : offs) {
            for (int i = 1; i <= prob.list_task.size(); i++) {
                if (i == ind.skill_factor) {
                    ind.fitness[i - 1] = prob.list_task.get(i - 1).eval(ind.genes);
                } else {
                    ind.fitness[i - 1] = Double.MAX_VALUE;
                }
            }
        }
        return offs;

    }
}
