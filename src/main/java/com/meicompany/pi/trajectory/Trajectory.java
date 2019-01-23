/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.trajectory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
    
    public final String DBObjectUsed = null;
    
    public final String TRajectoryEditXML = null;
    
    public final String sourceReference = "";
    
    public final String siteReference = "";
    
    public final double originLatitude = 0;
    
    public final double originLongitude = 0;
    
    public final double originAzimuth = 0;
    
    public final double originAltitude = 0;
    
    public final String tspi = null;
    
    public final String extraTspi = null;
    
    public final String vehicle = null;
    
    public final String attitude = null;
    
    public final String extrattitude = null;
    
    public final String site = null;
    
    public final String eva = null;
    
    public final String trajImportDetail = null;
    
    public final String trajInputControl = null;
    
    public final String TrajExcerpt = null;
    
    public final String trajBulkImport = null;
    
    private ArrayList<Double[]> position = new ArrayList<>();
    
    private ArrayList<Double[]> velocity = new ArrayList<>(); 
        
    private ArrayList<Double> times = new ArrayList<>();
    
    public final int coordinateFrame = 1; // Default ECEF
    
    
    public Trajectory() {
        version = "";
        name = "";
    }
    
    public void load(String filename) throws FileNotFoundException, IOException {
        String line;
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
