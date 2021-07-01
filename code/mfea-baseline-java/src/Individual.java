
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
public class Individual  implements Comparable<Individual>{
    int MAX_NVARS;
    int MAX_OBJS;
    public double []genes;
    public double[] fitness;
    public int skill_factor;
    public double scalar_fitness;
    public int rank[];
    
    public Individual(int MAX_NVARS,int  MAX_OBJS ){
        this.MAX_NVARS = MAX_NVARS;
        this.MAX_OBJS = this.MAX_OBJS;
        genes = new double[MAX_NVARS];
        fitness = new double[MAX_OBJS];
        rank = new int[MAX_OBJS];
        for(int i=0;i<MAX_OBJS; i++) fitness[i] = Double.MAX_VALUE; 
    }

    
    
    public void init(Random rand){
        for(int i=0;i<MAX_NVARS; i++) genes[i] = rand.nextDouble();
    }
    @Override
    public int compareTo(Individual o) {
        if (scalar_fitness > o.scalar_fitness) {
            return -1;
        } else if (scalar_fitness < o.scalar_fitness) {
            return 1;
        } else {
            return 0;
        }
    }
}
