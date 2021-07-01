//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Random;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
///**
// *
// * @author thang.tb153544
// */
//public class GA extends Algorithm{
//
//    Random rand;
//    Problem prob;
//    int POPSIZE=100;
//    int maxEvals;
//    int MAX_GEN = 1000;
//    double pm =0.1;
//    double pc =0.9;
//    public GA(Problem prob){
//        this.prob = prob;
//    }
//    public GA(Problem prob, int POPSIZE, int maxEvals, double pm, double pc) {
//        this.prob = prob;
//        this.pm = pm;
//        this.pc = pc;
//        this.POPSIZE = POPSIZE;
//        this.maxEvals = maxEvals;
//        this.MAX_GEN = maxEvals / POPSIZE;
//    }
//
//    public ArrayList<Individual> run(int seed, double[][] data) {
//        Parameter.num_fitness =0;
//        System.out.println("GA is running on seed = "+seed);
//        rand = new Random(seed);
//        ArrayList<Individual> best = new ArrayList<Individual>();
//        Population pop = new Population(POPSIZE, prob, rand);
//        pop.init();
//        pop.update_scalar_fitness();
//        pop.selection();
//        int generation = 1;
//        for (int i = 1; i <= prob.list_task.size(); i++) {
//            for (Individual ind : pop.pop) {
//                if (ind.skill_factor == i) {
//                    data[generation - 1][i - 1] = ind.fitness[i - 1];
//                    best.add(ind);
//                    break;
//                }
//            }
//        }
//        while (generation < MAX_GEN) {
//            generation++;
//            ArrayList<Individual> offs = reproduction(pop, POPSIZE);
//            pop.pop.addAll(offs);
//            pop.update_scalar_fitness();
//            pop.selection();
//            for (int i = 1; i <= prob.list_task.size(); i++) {
//                for (Individual ind : pop.pop) {
//                    if (ind.skill_factor == i) {
//                        if (best.get(i - 1).fitness[i - 1] > ind.fitness[i - 1]) {
//                            best.set(i - 1, ind);
//                        }
//                        data[generation - 1][i - 1] = best.get(i - 1).fitness[i - 1];
//
//                        break;
//                    }
//                }
//            }
//        }
////        System.out.println("Seed " + seed + " NUM FITNESS: " + Parameter.num_fitness);
////        for (int i = 1; i <= prob.list_task.size(); i++) {
////            System.out.println(best.get(i - 1).skill_factor + " : " + best.get(i - 1).fitness[i-1]);
////
////        }
//        return best;
//    }
//
//    public ArrayList<Individual> reproduction(Population pop, int SIZE) {
//        ArrayList<Individual> offs = new ArrayList<Individual>();
//        while (offs.size() < SIZE) {
//            if (rand.nextDouble() < pc) {
//                int a = rand.nextInt(pop.pop.size());
//                int b = rand.nextInt(pop.pop.size());
//                while (a == b) {
//                    b = rand.nextInt(pop.pop.size());
//                }
//                Individual parent1 = pop.pop.get(a);
//                Individual parent2 = pop.pop.get(b);
//                if (rand.nextDouble() < Parameter.pc) {
//                    ArrayList<Individual> child = pop.crossover(parent1, parent2);
//                    for (Individual ind : child) {
//                        if (rand.nextDouble() < pm) {
//                            pop.direct_mutation(ind);
//                        }
//                    }
//                    for (Individual ind : child) {
//                        ind.skill_factor = parent1.skill_factor;
//                        for (int i = 1; i <= prob.list_task.size(); i++) {
//                            if (i == ind.skill_factor) {
//                                ind.fitness[i - 1] = prob.list_task.get(i - 1).eval(ind.genes);
//                            } else {
//                                ind.fitness[i - 1] = Double.MAX_VALUE;
//                            }
//                        }
//
//                    }
//                    offs.addAll(child);
//                }
//
//            }
//        }
//        return offs;
//
//    }
//
//}
