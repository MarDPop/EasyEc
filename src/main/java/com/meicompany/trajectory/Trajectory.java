/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.trajectory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author mpopescu
 */
public class Trajectory {
    
    public final String version;
    
    public final String name;
    
    public String DBObjectUsed = null;
    
    public String TRajectoryEditXML = null;
    
    public String sourceReference = "";
    
    public String siteReference = "";
    
    public double originLatitude = 0;
    
    public double originLongitude = 0;
    
    public double originAzimuth = 0;
    
    public double originAltitude = 0;
    
    public String tspi = null;
    
    public String extraTspi = null;
    
    public String vehicle = null;
    
    public String attitude = null;
    
    public String extrattitude = null;
    
    public String site = null;
    
    public String eva = null;
    
    public String trajImportDetail = null;
    
    public String trajInputControl = null;
    
    public String TrajExcerpt = null;
    
    public String trajBulkImport = null;
    
    ArrayList<Double[]> position = new ArrayList<>();
    
    ArrayList<Double[]> velocity = new ArrayList<>(); 
        
    ArrayList<Double> times = new ArrayList<>();
    
    public int coordinateFrame; // Default ECEF
    
    
    public Trajectory() {
        version = "";
        name = "";
    }
    
    public void load(String filename) {
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            double[] numbers = new double[7];
            while ((line = br.readLine()) != null) {
                String[] state = line.split(cvsSplitBy);
                for(int i = 0; i < 7; i++){
                    numbers[i] = Double.parseDouble(state[i]);
                }
                position.add(new Double[]{numbers[1],numbers[2],numbers[3]});
                velocity.add(new Double[]{numbers[4],numbers[5],numbers[6]});
                times.add(numbers[0]);
            }
        } catch (IOException e) {
        }
    }
    
    public double[][] getState(double time) {
        double[][] out = new double[2][3];
        int i = 1;
        while(times.get(i) < time) {
            i++;
        }
        double dt = (time-times.get(i-1))/(times.get(i)-times.get(i-1));
        Double[] pos2 = position.get(i);
        Double[] pos1 = position.get(i-1);
        Double[] vel2 = velocity.get(i);
        Double[] vel1 = velocity.get(i-1);
        for(int j = 0; j < 3; j++) {
            out[0][j] = pos1[j] + dt*(pos2[j]-pos1[j]); 
            out[1][j] = vel1[j] + dt*(vel2[j]-vel1[j]); 
        }
        return out;
    }
    
    
}
