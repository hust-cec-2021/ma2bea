



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thang.tb153544
 */
public abstract class Function {
    int func_id;

    
    public Function() {
        
    }

    public Function(int func_id) {
        this.func_id = func_id;
    }
    
    
    
    
    public abstract double eval(double[] x);
}
