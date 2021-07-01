

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thang.tb153544
 */
public class Schwefel2 extends Function{
    double [][] M;
    double [] opt;
    public Schwefel2(double[][] M, double[] opt) {
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
        int dim =x.length;
        double sum =0;
        for(int i=0;i<x.length; i++){
            sum = sum + v[i]*Math.sin(Math.sqrt(Math.abs(v[i])));
        }
        return 418.9829*dim-sum;
        
    }
    
}
