



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thang.tb153544
 */
public class Sphere extends Function {
    double [][] M;
    double [] opt;
    public Sphere(double[][] M, double[] opt) {
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
        double sum = 0;
        for(double d : v){
            sum += d*d;
        }
        return sum;

    }

   

}
