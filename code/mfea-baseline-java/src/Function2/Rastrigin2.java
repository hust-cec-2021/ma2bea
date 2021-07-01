


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thang.tb153544
 */
public class Rastrigin2 extends Function{
    double [][] M;
    double [] opt;
    public Rastrigin2(double[][] M, double[] opt) {
        this.M = M;
        this.opt = opt;
    }

    @Override
    public double eval(double[] x) {
         double vars[] = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            vars[i] = x[i] - opt[i];
        }
        double v[] = new double[x.length];
        for (int row = 0; row < x.length; row++) {
            v[row] =0.0;
            for (int col = 0; col < x.length; col++) {
                    v[row]+= M[row][col]*vars[col];
            }
        }
        int dim = x.length;
        double obj = dim*10;
        for(int i=0;i<dim; i++){
            obj=obj+(v[i]*v[i] - 10*(Math.cos(2*Math.PI*v[i])));
        }
        return obj;
    }
    
}
