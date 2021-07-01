
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
public class Benchmark2 {

    ArrayList<Problem> getManyTask10() {
        ArrayList<Problem> list_prob = new ArrayList<Problem>();
        Problem prob = new Problem();
        int task_size = 10;

        for (int task_id = 1; task_id <= task_size; task_id++) {
            Task t = new Task(task_id);
            double bias[];
            double matrix[][];

            switch (task_id) {
                case 1: // sphere 1
                    t.dims = 50;
                    t.LB = ones(t.dims, -100);
                    t.UB = ones(t.dims, 100);
                    bias = new double[t.dims];
                    matrix = i_matrix(t.dims);
                    for (int i = 0; i < t.dims; i++) {
                        bias[i] = 0;
                    }
                    t.func = new Sphere(matrix, bias);
                    prob.add_task(t);
                    break;
                case 2:
                    t.dims = 50;
                    t.LB = ones(t.dims, -100);
                    t.UB = ones(t.dims, 100);
                    bias = new double[t.dims];
                    matrix = i_matrix(t.dims);
                    for (int i = 0; i < t.dims; i++) {
                        bias[i] = 80; //80
                    }
                    t.func = new Sphere(matrix, bias);
                    prob.add_task(t);
                    break;
                case 3:
                    t.dims = 50;
                    t.LB = ones(t.dims, -100);
                    t.UB = ones(t.dims, 100);
                    bias = new double[t.dims];
                    matrix = i_matrix(t.dims);
                    for (int i = 0; i < t.dims; i++) {
                        bias[i] = -80;
                    }
                    t.func = new Sphere(matrix, bias);
                    prob.add_task(t);
                    break;
                case 4:
                    t.dims = 25;
                    t.LB = ones(t.dims, -0.5);
                    t.UB = ones(t.dims, 0.5);
                    bias = new double[t.dims];
                    matrix = i_matrix(t.dims);
                    for (int i = 0; i < t.dims; i++) {
                        bias[i] = -0.4;
                    }
                    t.func = new Weierstrass(matrix, bias);
                    prob.add_task(t);
                    break;
                case 5:
                    t.dims = 50;
                    t.LB = ones(t.dims, -50);
                    t.UB = ones(t.dims, 50);
                    bias = new double[t.dims];
                    matrix = i_matrix(t.dims);
                    for (int i = 0; i < t.dims; i++) {
                        bias[i] = -1; //0
                    }
                    t.func = new Rosenbrock(matrix, bias);
                    prob.add_task(t);
                    break;
                case 6:
                    t.dims = 50;
                    t.LB = ones(t.dims, -50);
                    t.UB = ones(t.dims, 50);
                    bias = new double[t.dims];
                    matrix = i_matrix(t.dims);
                    for (int i = 0; i < t.dims; i++) {
                        bias[i] = 40;
                    }
                    t.func = new Ackley(matrix, bias);
                    prob.add_task(t);
                    break;
                case 7:
                    t.dims = 50;
                    t.LB = ones(t.dims, -0.5);
                    t.UB = ones(t.dims, 0.5);
                    bias = new double[t.dims];
                    matrix = i_matrix(t.dims);
                    for (int i = 0; i < t.dims; i++) {
                        bias[i] = -0.4;
                    }
                    t.func = new Weierstrass(matrix, bias);
                    prob.add_task(t);
                    break;
                case 8:
                    t.dims = 50;
                    t.LB = ones(t.dims, -500);
                    t.UB = ones(t.dims, 500);
                    bias = new double[t.dims];
                    matrix = i_matrix(t.dims);
                    for (int i = 0; i < t.dims; i++) {
                        bias[i] = 0; //420.9687
                    }
                    t.func = new Schwefel(matrix, bias);
                    prob.add_task(t);
                    break;
                case 9:
                    t.dims = 50;
                    t.LB = ones(t.dims, -100);
                    t.UB = ones(t.dims, 100);
                    bias = new double[t.dims];
                    matrix = i_matrix(t.dims);
                    for (int i = 0; i < t.dims / 2; i++) {
                        bias[i] = -80;
                    }
                    for (int i = t.dims / 2; i < t.dims; i++) {
                        bias[i] = 80;
                    }
                    t.func = new Griewank(matrix, bias);
                    prob.add_task(t);
                    break;
                case 10:
                    t.dims = 50;
                    t.LB = ones(t.dims, -50);
                    t.UB = ones(t.dims, 50);
                    bias = new double[t.dims];
                    matrix = i_matrix(t.dims);
                    for (int i = 0; i < t.dims / 2; i++) {
                        bias[i] = 40;
                    }
                    for (int i = t.dims / 2; i < t.dims; i++) {
                        bias[i] = -40;
                    }
                    t.func = new Rastrigin(matrix, bias);
                    prob.add_task(t);
                    break;
                default:
                    System.out.println("Invalid function");
            };
        }
//        for (int i = 0; i < prob.list_task.size(); i++) {
//            prob.list_task.get(i).id = i + 1;
//        }
        list_prob.add(prob);
        return list_prob;
    }

    ArrayList<Problem> getManyTask50() {
        ArrayList<Problem> list_prob = new ArrayList<Problem>();
        for (int index = 1; index <= 10; index++) {
            Problem prob = new Problem();
            int task_size = 50;
            int dim = 50;
            int choice_functions[] = null;
            switch (index) {
                case 1:
                    choice_functions = new int[]{1};
                    break;
                case 2:
                    choice_functions = new int[]{2};
                    break;
                case 3:
                    choice_functions = new int[]{4};
                    break;
                case 4:
                    choice_functions = new int[]{1, 2, 3};
                    break;
                case 5:
                    choice_functions = new int[]{4, 5, 6};
                    break;
                case 6:
                    choice_functions = new int[]{2, 5, 7};
                    break;
                case 7:
                    choice_functions = new int[]{3, 4, 6};
                    break;
                case 8:
                    choice_functions = new int[]{2, 3, 4, 5, 6};
                    break;
                case 9:
                    choice_functions = new int[]{2, 3, 4, 5, 6, 7};
                    break;
                case 10:
                    choice_functions = new int[]{3, 4, 5, 6, 7};
                    break;
                default:
                    System.out.println("Invalid input: ID should be in [1,10]");
            };
            for (int task_id = 1; task_id <= task_size; task_id++) {
                int func_id = choice_functions[(task_id - 1) % choice_functions.length];
                String file_dir = "./SO-Manytask-Benchmarks/Tasks/benchmark_" + index;
                String file_matrix = file_dir + "/matrix_" + task_id;
                String file_bias = file_dir + "/bias_" + task_id;
                double matrix[][] = read_matrix(file_matrix, dim);
                double bias[] = read_bias(file_bias, dim);
                Task t = new Task(task_id);
                switch (func_id) {
                    case 1:
                        t.dims = dim;
                        t.LB = ones(dim, -100);
                        t.UB = ones(dim, 100);
                        for (int i = 0; i < dim; i++) {
                            bias[i] = bias[i];
                        }
                        t.func = new Sphere2(matrix, bias);
                        prob.add_task(t);
                        break;
                    case 2:
                        t.dims = dim;
                        t.LB = ones(dim, -50);
                        t.UB = ones(dim, 50);
                        for (int i = 0; i < dim; i++) {
                            bias[i] = bias[i];
                        }
                        t.func = new Rosenbrock2(matrix, bias);
                        prob.add_task(t);
                        break;
                    case 3:
                        t.dims = dim;
                        t.LB = ones(dim, -50);
                        t.UB = ones(dim, 50);
                        for (int i = 0; i < dim; i++) {
                            bias[i] = bias[i];
                        }
                        t.func = new Ackley2(matrix, bias);
                        prob.add_task(t);
                        break;
                    case 4:
                        t.dims = dim;
                        t.LB = ones(dim, -50);
                        t.UB = ones(dim, 50);
                        for (int i = 0; i < dim; i++) {
                            bias[i] = bias[i];
                        }
                        t.func = new Rastrigin2(matrix, bias);
                        prob.add_task(t);
                        break;
                    case 5:
                        t.dims = dim;
                        t.LB = ones(dim, -100);
                        t.UB = ones(dim, 100);
                        for (int i = 0; i < dim; i++) {
                            bias[i] = bias[i];
                        }
                        t.func = new Griewank2(matrix, bias);
                        prob.add_task(t);
                        break;
                    case 6:
                        t.dims = dim;
                        t.LB = ones(dim, -0.5);
                        t.UB = ones(dim, 0.5);
                        for (int i = 0; i < dim; i++) {
                            bias[i] = bias[i];
                        }
                        t.func = new Weierstrass2(matrix, bias);
                        prob.add_task(t);
                        break;
                    case 7:
                        t.dims = dim;
                        t.LB = ones(dim, -500);
                        t.UB = ones(dim, 500);
                        for (int i = 0; i < dim; i++) {
                            bias[i] = bias[i];
                        }
                        t.func = new Schwefel2(matrix, bias);
                        prob.add_task(t);
                        break;
                    default:
                        System.out.println("Invalid function");
                };
            }
            list_prob.add(prob);
        }
        return list_prob;
    }
    double[][] read_matrix(String file_name, int dim) {
        double[][] matrix = new double[dim][dim];
        BufferedReader br = null;

        String sCurrentLine = null;
        try {
            br = new BufferedReader(new FileReader(file_name));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Benchmark2.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sCurrentLine = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Benchmark2.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] str = null;
        int i = 0;
        while (sCurrentLine != null) {
            int j = 0;
            str = sCurrentLine.split("\\s+");
            for (String s : str) {
                if(s.isEmpty()){
                    continue;
                }
                matrix[i][j] = Double.parseDouble(s);
                j++;
            }
            try {
                sCurrentLine = br.readLine();
                i++;
            } catch (IOException ex) {
                Logger.getLogger(Benchmark2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Benchmark2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return matrix;
    }

    double[] read_bias(String file_name, int dim) {
        double[] bias = new double[dim];

        BufferedReader br = null;

        String sCurrentLine = null;
        try {
            br = new BufferedReader(new FileReader(file_name));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Benchmark2.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sCurrentLine = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Benchmark2.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] str = null;
        str = sCurrentLine.split("\\s+");
        int i = 0;
        for (String s : str) {
            if(s.isEmpty()){
                continue;
            }
            bias[i] = Double.parseDouble(s);
            i++;
        }
        
        try {
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Benchmark2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bias;
    }

    double[] ones(int n, double scale) {
        double[] matrix = new double[n];
        for (int i = 0; i < n; i++) {
            matrix[i] = 1 * scale;
        }
        return matrix;
    }

    double[][] i_matrix(int n) {
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                if (i == j) {
                    matrix[i][j] = (int) 1;
                } else {
                    matrix[i][j] = matrix[j][i] = (int) 0;
                }
            }

        }
        return matrix;
    }
}
