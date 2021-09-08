
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thang.tb153544
 */
public class Problem {
    ArrayList<Task> list_task;

    public Problem() {
        list_task = new ArrayList<>();
    }
    
    public Problem(Task t) {
        list_task = new ArrayList<>();
        list_task.add(t);
    }
    public void add_task(Task t){
        list_task.add(t);
    }
    public Task get_task_by_id(int id){
        return list_task.get(id -1);
    }
    public ArrayList<Task> get_all_tasks(){
        return list_task;
    }
    
}
