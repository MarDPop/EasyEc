/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.grid.util.NodeMap;
import com.meicompany.pi.trajectory.Trajectory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;


/**
 *
 * @author mpopescu
 */
public class RunPi {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Trajectory traj = new Trajectory();
        traj.load("src/main/resources/trajplot_i28_asc_m100_ksc15.csv");
        long start = System.nanoTime();
        //NodeMap map = testSingle(traj);
        NodeMap map = testMultiple(traj);
        long finish = System.nanoTime();
        map.printCsv();
        JSONObject jmap = map.toJson(true);
        try (FileWriter file = new FileWriter("map.json")) {
                jmap.write(file);
            } catch (IOException ex) {
                Logger.getLogger(RunPi.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println((finish-start)/1e6+" ms run time");
    }
    
    private static NodeMap testSingle(Trajectory traj) {
        double[][] state = traj.getState(1);
        PiCalc2 pi = new PiCalc2(state[0],state[1],1);
        pi.run(12);
        return pi.mapQuick();
    }
   
    private static NodeMap testMultiple(Trajectory traj) {
        double time = 0;
        double[][] state = traj.getState(0);
        PiCalc2 pi = new PiCalc2(state[0],state[1],1);
        while(time < 100){
            state = traj.getState(time);
            pi.setState(state[0],state[1], time);
            pi.run(12);
            pi.collectRun();
            time += 5;
        }
        return pi.mapAll();
    }
    
    
}
