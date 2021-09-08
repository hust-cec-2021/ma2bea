

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author thang.tb153544
 */
public class Ackley extends Function {
    double [][] M;
    double [] opt;
    public Ackley(double [][] M, double [] opt) {
        this.M = M;
        this.opt = opt;
    }

    @Override
    public double eval(double[] x) {
        double v[] = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            v[i] = x[i] - opt[i];
        }
//        double v[] = new double[x.length];
//        for (int row = 0; row < x.length; row++) {
//            v[row] =(int)0;
//            for (int col = 0; col < x.length; col++) {
//                    v[row]+= M[row][col]*vars[col];
//            }
//        }
        double sum1 =0;
        double sum2=0;
        for (int i = 0; i < x.length; i++) {
            sum1 += v[i]*v[i];
            sum2 += Math.cos(2*Math.PI*v[i]);
        }
        double avgsum1  = sum1/ x.length;
        double avgsum2  = sum2/x.length;
        return -20*Math.exp(-0.2*Math.sqrt(avgsum1)) - Math.exp(avgsum2) + 20 + Math.exp(1);  
        
    }

   
    
}
