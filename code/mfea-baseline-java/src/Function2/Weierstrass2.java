

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thang.tb153544
 */
public class Weierstrass2 extends Function{
    double [][] M;
    double [] opt;
    public Weierstrass2(double[][] M, double[] opt) {
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
        double a = 0.5;
        double b = 3;
        int kmax = 20;
        double obj = 0;
        int D = x.length;
        for(int i=1; i<= D; i++){
            for(int k=0; k<=kmax; k++){
                obj = obj + Math.pow(a,k)*Math.cos(2*Math.PI*Math.pow(b,k)*(v[i-1]+0.5));
            }
        }
        for(int k=0;k<=kmax; k++){
            obj = obj - D*Math.pow(a,k)*Math.cos(2*Math.PI*Math.pow(b,k)*0.5);
        }
        return obj;
    }
    
}
