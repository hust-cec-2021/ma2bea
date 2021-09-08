


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thang.tb153544
 */
public class Task {
    int id;
    int dims;
    int func_id;
    Function func;
    double UB[];
    double LB[];
    double global_optimize[];

    public Task(int id, int dims, Function func, double UB[], double LB[]) {
        this.id = id;
        this.dims = dims;
        this.func = func;
        this.UB = UB;
        this.LB = LB;
    }

    public Task(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public double[] getGlobal_optimize() {
        return global_optimize;
    }

    public int getDims() {
        return dims;
    }

    public void setDims(int dims) {
        this.dims = dims;
    }

    public Function getFunc() {
        return func;
    }

    public void setFunc(Function func) {
        this.func = func;
    }
    
    public double eval(double[] genes){
        if (Parameter.num_fitness >= Parameter.maxFEs) return Double.MAX_VALUE;
        Parameter.countFitness[id-1]++;
        double [] y= new double [dims];
        for(int i=0; i<dims; i++){
            y[i] = genes[i]*(UB[i] - LB[i]) + LB[i] ;
        }
        Parameter.num_fitness++;
        double v = func.eval(y);
        if(v<0) v =0;
        return v;
        
    }

    

}
