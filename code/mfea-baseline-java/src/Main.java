
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thang.tb153544
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Algorithm alg = null;
        String nameAlg = "LMFEA_3";
        int H = 30;
        int N_min = 50;
        int benchmark = 10;
        if (args.length >= 2) {
            nameAlg = args[0];
            benchmark = Integer.parseInt(args[1]);
            if (args.length >= 4) {
                H = Integer.parseInt(args[2]);
                N_min = Integer.parseInt(args[3]);
            }
        }

        Benchmark2 bechmark = new Benchmark2();
        ArrayList<Problem> list_prob = null;
        if (benchmark == 10) {
            Parameter.maxFEs = 1000000;
            
            list_prob = bechmark.getManyTask10();
        } else if (benchmark == 50) {
            Parameter.maxFEs = 5000000;
            list_prob = bechmark.getManyTask50();
        }
        
        System.out.println("Benchmark "+ benchmark);
        int count =1;
        for (Problem prob : list_prob) {
            Parameter.SIZE_POPULATION =  Parameter.SUBPOPULATION*prob.list_task.size();
            System.out.println("Prob "+ count);
            System.out.println("SIZE POPULATION "+ Parameter.SIZE_POPULATION);
            count++;
            if (nameAlg.equals("MFEA")) {
                alg = new MFEA(prob, Parameter.SIZE_POPULATION, Parameter.maxFEs, 0.3);
            } else if (nameAlg.equals("LMFEA")) {
                alg = new LMFEA(prob, Parameter.SIZE_POPULATION, Parameter.maxFEs, H);
            } else if (nameAlg.equals("LMFEA_2")) {
                alg = new LMFEA_2(prob, Parameter.SIZE_POPULATION, Parameter.maxFEs, H);
            } else if (nameAlg.equals("LMFEA_3")) {
                alg = new LMFEA_3(prob, Parameter.SIZE_POPULATION, Parameter.maxFEs, H, N_min);
            } else if (nameAlg.equals("MaTGA")) {
                alg = new MaTGA(prob);
            } else if (nameAlg.equals("MaTDE")) {
                alg = new MaTDE(prob);
            }else if (nameAlg.equals("SBS_GA")) {
                alg = new SBS_GA(prob,Parameter.SIZE_POPULATION, Parameter.maxFEs );
            }else if (nameAlg.equals("EBS_GA")) {
                alg = new EBS_GA(prob,Parameter.SIZE_POPULATION, Parameter.maxFEs );
            }else if (nameAlg.equals("SBS_GA_poly")) {
                alg = new SBS_GA_poly(prob,Parameter.SIZE_POPULATION, Parameter.maxFEs );
            }

            double sum[] = new double[prob.list_task.size()];
            double data[][][] = new double[Parameter.reps][Parameter.numRecords][prob.list_task.size()];
            for (int i = 0; i < Parameter.reps; i++) {
                alg.run(i, data[i]);
                for (int k = 0; k < prob.list_task.size(); k++) {
                    sum[k] += data[i][Parameter.numRecords - 1][k] / Parameter.reps;
                }
            }
//            for (int k = 0; k < prob.list_task.size(); k++) {
//                System.out.println(sum[k]);
//            }
//
//            String dirType = nameAlg + "_Results_benchmark_"+benchmark;
//            File dir = new File(dirType);
//            if (!dir.exists()) {
//                dir.mkdir();
//            }
//            dirType = dirType+"/Benchmark_"+count;
//            dir = new File(dirType);
//            if (!dir.exists()) {
//                dir.mkdir();
//            }
//            
//            if (nameAlg.equals("LMFEA_2")) {
//                dirType = dirType + "/" + "H=" + H;
//            } else if (nameAlg.equals("LMFEA_3")) {
//                dirType = dirType + "/" + "H=" + H + " Nmin = " + N_min;
//            }
//            dir = new File(dirType);
//            if (!dir.exists()) {
//                dir.mkdir();
//            }
//            String filename = "Result_" + nameAlg + ".txt";
//
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream(dirType + "/" + filename);
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            PrintWriter pw0 = new PrintWriter(fos);
//            for (int k = 0; k < Parameter.numRecords; k++) {
//                pw0.print((k + 1) * (Parameter.maxFEs / Parameter.numRecords) + ", ");
//                for (int j = 0; j < Parameter.reps; j++) {
//                    for (int i = 0; i < prob.list_task.size(); i++) {
//                        if (j == 0 && i == 0) {
//                            pw0.print(data[j][k][i]);
//                        } else {
//                            pw0.print(", " + data[j][k][i]);
//                        }
//                    }
//                }
//                pw0.println();
//
//            }
//            pw0.close();
//            if (nameAlg.equals("LMFEA_3") || nameAlg.equals("LMFEA_2")) {
//                for (int i = 0; i < prob.list_task.size(); i++) {
//                    for (int k = i + 1; k < prob.list_task.size(); k++) {
//                        FileOutputStream fos1 = null;
//                        try {
//                            fos1 = new FileOutputStream(dirType + "/" + "rmp_" + (i + 1) + "_" + (k + 1) + ".txt");
//                        } catch (FileNotFoundException ex) {
//                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                        PrintWriter pw1 = new PrintWriter(fos1);
//                        for (int h = 0; h < Parameter.numRecords; h++) {
//
//                            for (int m = 0; m < Parameter.o_rmp[i][k][h].size(); m++) {
//                                if (m == 0) {
//                                    pw1.print(Parameter.o_rmp[i][k][h].get(m));
//                                } else {
//                                    pw1.print(", " + Parameter.o_rmp[i][k][h].get(m));
//                                }
//                            }
//                            pw1.println();
//
//                        }
//                        pw1.close();
//                    }
//                }
//            }
        }

    }
}
