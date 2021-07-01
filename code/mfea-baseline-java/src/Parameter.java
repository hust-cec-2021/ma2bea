                                                                                                                                                                                                                                                                                                                                    
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
public class Parameter {
    static int reps = 30;
    static int maxFEs = 1000000;
    static int numRecords = 1000;
    static int SUBPOPULATION =100;
    static int SIZE_POPULATION = 1000;
    
    public static double mum =5;
    public static double mu =2;
   
    public static double rmp =0.3;
    public static double pc =0.8;
    public static double pm =0.1;

    public static double num_fitness=0;
    public static   int [] countFitness;
    static ArrayList<Double> o_rmp[][][];
    
    
    
}
