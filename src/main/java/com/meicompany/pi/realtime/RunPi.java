/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.realtime.map.util.NodeMap;
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
     */
    public static void main(String[] args) {
        Trajectory traj = new Trajectory();
        try {
            traj.load("src/main/resources/trajplot_i28_asc_m100_ksc15.csv");
        } catch (IOException e) {
            Logger.getLogger(RunPi.class.getName()).log(Level.SEVERE, null, e);
        }
        long start = System.nanoTime();
        //NodeMap map = testSingle(traj);
        NodeMap map = testMultiple(traj);
        long finish = System.nanoTime();
        Logger.getLogger(RunPi.class.getName()).log(Level.INFO, (finish-start)/1e6+" ms run time");
        map.printCsv();
        JSONObject jmap = map.toJson(true);
        try (FileWriter file = new FileWriter("map.json")) {
                jmap.write(file);
            } catch (IOException ex) {
                Logger.getLogger(RunPi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static NodeMap testSingle(Trajectory traj) {
        double[][] state = traj.getState(1);
        PiCalc pi = new PiCalc(state[0],state[1],1);
        pi.run(12);
        return pi.mapQuick();
    }
   
    private static NodeMap testMultiple(Trajectory traj) {
        double time = 0;
        double[][] state = traj.getState(0);
        PiCalc pi = new PiCalc(state[0],state[1],1);
        while(time < 5){
            state = traj.getState(time);
            pi.setState(state[0],state[1], time);
            pi.run(12);
            pi.collectRun();
            time += 5;
        }
        return pi.mapAll();
    }
    
    
}
