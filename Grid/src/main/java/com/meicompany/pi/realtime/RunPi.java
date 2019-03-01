/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.coordinates.Earth;
import com.meicompany.pi.realtime.map.util.NodeMap;
import com.meicompany.pi.trajectory.Trajectory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        NodeMap map = testMultiple(traj);
        long finish = System.nanoTime();
        Logger.getLogger(RunPi.class.getName()).log(Level.INFO, "runtime approx: {0} ms", (finish-start)/1000000);
        Logger.getLogger(RunPi.class.getName()).log(Level.INFO, "Printing Map to Csv");
        map.printCsv();
        Logger.getLogger(RunPi.class.getName()).log(Level.INFO, "Printing Map to Json");
        JSONObject jmap = map.toJson(true);
        try (FileWriter file = new FileWriter("map.json")) {
            jmap.write(file);
        } catch (IOException ex) {
            Logger.getLogger(RunPi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    private static NodeMap testMultiple(Trajectory traj) {
        double time = 0;
        double[][] state = traj.getStateCountup(0);
        PiCalc pi = new PiCalc(state[0],state[1],1);
        ArrayList<double[]> testTraj = new ArrayList<>();
        while(time < 2025){
            state = traj.getStateCountup(time);
            testTraj.add(CoordinateFrame.ecef2geo(state[0])); 
            if (traj.getCoordinateFrame() == 1) {
                state[0] = CoordinateFrame.rotateZ(state[0], (float)(time*Earth.EARTH_ROT));                
                state[1][0] -= Earth.EARTH_ROT*state[0][1];
                state[1][1] += Earth.EARTH_ROT*state[0][0];
            }
            pi.setState(state[0],state[1], time);
            pi.run(7);
            pi.collectRun();
            time += 10;
            Logger.getLogger(RunPi.class.getName()).log(Level.INFO, "time: {0} s", time);
        }
        Logger.getLogger(PiCalc.class.getName()).log(Level.INFO, "Printing Traj ");
        IOHelper.printCsv2(testTraj,"traj.csv");
        return pi.map(1e-16f);
    }
    
    
}
