
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
public class Population {
    int SIZEPOP;
    int NUM_TASKS;
    int SIZE_GENES;
    ArrayList<Individual> pop;
    Problem  prob;
    Random rand;
    public Population(int SIZEPOP, Problem prob, Random rand) {
        this.SIZEPOP = SIZEPOP;
        this.rand = rand;
        this.NUM_TASKS = prob.list_task.size();
        this.SIZE_GENES =-1;
        for(Task t: prob.list_task){
            this.SIZE_GENES = Math.max(this.SIZE_GENES, t.dims);
        }
        pop= new ArrayList<Individual>();
        this.prob = prob;
    }
    public void init(){
       for(int i=0;i<SIZEPOP; i++ ){
           Individual ind = new Individual( this.SIZE_GENES, this.NUM_TASKS);
           ind.init(rand);
           for(int t=1;t<= NUM_TASKS; t++){
               ind.fitness[t-1] = prob.list_task.get(t-1).eval(ind.genes);
           }
           pop.add(ind);
       } 
    }
//    public void init2(){
//       for(int i=0;i<SIZEPOP; i++ ){
//           Individual ind = new Individual( this.SIZE_GENES, this.NUM_TASKS);
//           
//           ind.init(rand);
//           int skill = i %prob.list_task.size()+1;
//           for(int t=1;t<= NUM_TASKS; t++){
//               if(t== skill) ind.fitness[t-1] = prob.list_task.get(t-1).eval(ind.genes);
//               else ind.fitness[t-1] = Double.MAX_VALUE;
//           }
//           pop.add(ind);
//       } 
//    }
    public void update_scalar_fitness(){
        // tinh rank tren k task
        for(int  task = prob.list_task.size(); task >=1; task--){
            final  int t = task;
            Collections.sort(pop, new Comparator<Individual>() {
                @Override
                public int compare(Individual o1, Individual o2) {
                    if(o1.fitness[t-1] < o2.fitness[t-1]) return -1;
                    else if(o1.fitness[t-1] > o2.fitness[t-1]) return 1;
                    else return 0;
                }
            });
            for(int i=0;i<pop.size(); i++){
                pop.get(i).rank[task -1] = i;
            }
        }
        
        // update scalarfitness theo task co rank min
        // re-assigned skill factor
        for(Individual ind : pop){
            int _min = Integer.MAX_VALUE;
            int _task = 0;
            for(int task=1;task<=prob.list_task.size(); task++){
                if(_min > ind.rank[task -1] ){
                    _min = ind.rank[task -1] ;
                    _task = task;
                }else if(_min == ind.rank[task -1] ){
                    if(rand.nextDouble() <0.5){
                         _task = task;
                    }
                }
            }
            ind.skill_factor = _task;
            ind.scalar_fitness = (1.0/(_min+1));
        }   
    }
    
    public ArrayList<Individual> crossover(Individual parent1, Individual parent2){
        ArrayList<Individual> off_spring = new ArrayList<Individual>();
         double cf[] = new double[SIZE_GENES];
        for(int i=0; i<SIZE_GENES; i++){
            cf[i] = 1;
            double u = rand.nextDouble();
            if(u<=0.5){
                cf[i]= Math.pow((2*u), 1.0/(Parameter.mu +1));
            }else{
                cf[i]= Math.pow(2*(1-u), -1.0/(Parameter.mu +1));
            }
        }
        Individual child1 = new Individual(SIZE_GENES, NUM_TASKS);
        Individual child2 = new Individual(SIZE_GENES, NUM_TASKS);
        for(int i=0;i<SIZE_GENES; i++){
            double v = 0.5*((1+cf[i])*parent1.genes[i] + (1-cf[i])*parent2.genes[i]);
            if(v>1) v=1;
            else if(v<0) v= 0;
            child1.genes[i]= v;
            
            double v2 = 0.5*((1-cf[i])*parent1.genes[i] + (1+cf[i])*parent2.genes[i]);
            if(v2>1) v2= 1;
            else if(v2<0) v2= 0;
            child2.genes[i]= v2;
        } 
       off_spring.add(child1);
       off_spring.add(child2);
       return off_spring;
    }
    
    public Individual mutation(Individual parent){
        Individual ind = new Individual(SIZE_GENES, NUM_TASKS);
        for(int i=0;i<SIZE_GENES; i++){
            ind.genes[i] = parent.genes[i];
        }
        
        for(int i=1; i<=SIZE_GENES; i++){
            if(rand.nextDouble() < 1.0/SIZE_GENES){
                double u = rand.nextDouble();
                if(u<=0.5){
                    double del=Math.pow((2*u), 1.0/(1+Parameter.mum)) - 1;
                    ind.genes[i-1] = ind.genes[i-1]*(del+1);
                }else{
                   double del= 1 - Math.pow(2*(1-u), 1.0/(1+Parameter.mum));
                   ind.genes[i-1] = ind.genes[i-1] +del*(1-ind.genes[i-1]);
                }
            }
            if(ind.genes[i-1]>1) ind.genes[i-1] =parent.genes[i] + rand.nextDouble()*(1-parent.genes[i] );
            else if(ind.genes[i-1]<0) ind.genes[i-1] = parent.genes[i]*rand.nextDouble();
        }
        return ind;

    }
    
    public void direct_mutation(Individual parent){
        Individual ind = parent; 
        for(int i=1; i<=SIZE_GENES; i++){
            if(rand.nextDouble() < 1.0/SIZE_GENES){
                double u = rand.nextDouble();
                double v =0;
                if(u<=0.5){
                    double del=Math.pow((2*u), 1.0/(1+Parameter.mum)) - 1;
                    v = ind.genes[i-1]*(del+1);
                }else{
                   double del= 1 - Math.pow(2*(1-u), 1.0/(1+Parameter.mum));
                   v = ind.genes[i-1] +del*(1-ind.genes[i-1]);
                }
                if(v>1) ind.genes[i-1] = ind.genes[i-1] + rand.nextDouble()*(1-ind.genes[i-1]);
                else if(v<0) ind.genes[i-1] = ind.genes[i-1]*rand.nextDouble();
            }
            
        }
        
        
    }
    
    public void selection(){
        // sort theo scala fitness giam dan
        Collections.sort(pop);
        int size = pop.size();
        // lay du size quan the
        if(pop.size()> Parameter.SIZE_POPULATION ) pop.subList(Parameter.SIZE_POPULATION, size).clear();  
    }
    public void variable_swap(Individual p1, Individual p2){
        Individual ind1 = p1;
        Individual ind2 = p2;
       
        
        for(int i=1; i<=SIZE_GENES; i++){
            if(rand.nextDouble() >0.5){
                double temp1 = p1.genes[i-1];
                double temp2 = p2.genes[i-1];
                ind1.genes[i-1] = temp2;
                ind2.genes[i-1] =  temp1;
            }
        }
    }
    
    public void GaussMutation(Individual ind) {
        double p = 0.01;
        for (int i = 0; i < SIZE_GENES; i++) {
            if (rand.nextDouble() < 1.0 / SIZE_GENES) {
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
