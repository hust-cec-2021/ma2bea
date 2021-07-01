
import java.util.ArrayList;
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
public class Model {

    double[] mean;
    double[] std;
    int dim;
    Random rand;

    public Model(int dim, Random rand) {
        this.rand = rand;
        this.dim = dim;
    }

    void init(ArrayList<Individual> subPop) {
        mean = new double[dim];
        std = new double[dim];
        for (int i = 0; i < dim; i++) {
            mean[i] = 0;
            std[i] =0;
        }
        int size = subPop.size();
        for (Individual ind : subPop) {
            for (int i = 0; i < dim; i++) {
                mean[i] += ind.genes[i];
            }
        }
        int m = (int) (size * 0.1);
        double[][] randMatrix = new double[m][dim];
        for (int k = 0; k < m; k++) {
            for (int i = 0; i < dim; i++) {
                randMatrix[k][i] = rand.nextDouble();
                mean[i] += randMatrix[k][i];
            }
        }
        for (int i = 0; i < dim; i++) {
            mean[i] = mean[i] / (size + m);
        }

        for (Individual ind : subPop) {
            for (int i = 0; i < dim; i++) {
                std[i] += (ind.genes[i] - mean[i]) * (ind.genes[i] - mean[i]);
            }
        }
        for (int k = 0; k < m; k++) {
            for (int i = 0; i < dim; i++) {
                std[i] += (randMatrix[k][i] - mean[i]) * (randMatrix[k][i] - mean[i]);
            }
        }

        for (int i = 0; i < dim; i++) {
            std[i] = Math.sqrt(std[i] / (size + m-1));
        }

    }

    double eval(double[] x, int dim) {
        double F = 1;
        for (int i = 0; i < dim; i++) {
            F = F * 1.0 / (std[i] * Math.sqrt(2 * Math.PI)) * Math.exp(-(x[i] - mean[i]) * (x[i] - mean[i]) / (2 * std[i] * std[i]));
        }
        
        return F;
    }

}
