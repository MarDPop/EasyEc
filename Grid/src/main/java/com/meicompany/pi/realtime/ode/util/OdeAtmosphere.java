/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode.util;

import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.coordinates.Earth;
import com.meicompany.pi.realtime.generalMath.Math2;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author mpopescu
 */
public class OdeAtmosphere {   
    private double tempOffset = 0;
    
    private final ArrayList<Double[]> alt = new ArrayList<>();
    public final double[] temperatures;
    public final double[] densities;
    public final double[] speedSound;
    public final double[][] winds;
    
    
    public OdeAtmosphere() {
        densities = null;
        temperatures = null;
        winds = null;
        speedSound = null;
    }
    
    public OdeAtmosphere(String filename, double tempOffset, double windStrengthMultiplier)  {
        String line = "";
        String cvsSplitBy = ",";
        boolean readfail = false;
         try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            
            while ((line = br.readLine()) != null) {
                Double[] numbers = new Double[7];
                String[] state = line.split(cvsSplitBy);
                for(int i = 0; i < 7; i++){
                    numbers[i] = Double.parseDouble(state[i]);
                }
                alt.add(numbers);
            }
            
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(OdeAtmosphere.class.getName()).log(Level.SEVERE, null, ex);
            readfail = true;
        } 

        if (readfail) {
            densities = null;
            temperatures = null;
            winds = null;
            speedSound = null;
        } else { 
            densities = new double[alt.size()];
            temperatures = new double[alt.size()];
            winds = new double[alt.size()][2];
            speedSound = new double[alt.size()];
            for(int i = 0; i < alt.size(); i++) {
                Double[] row = alt.get(i);
                temperatures[i] = row[2] + tempOffset;
                double delta = row[2]/temperatures[i];
                densities[i] = row[1]/2000*(delta);
                speedSound[i] = row[6]/Math.sqrt(delta);
                delta = row[3]*windStrengthMultiplier;
                double windDirection = row[4];
                winds[i][0] = delta*cos(windDirection);
                winds[i][1] = delta*sin(windDirection);
            }
        }
        
    }
    
    /**
     * Sets offset to nominal temperature
     * @param tempOffset 
     */
    public void setOffsetTemp(double tempOffset){
        this.tempOffset = tempOffset;
        for(int i = 0; i < alt.size(); i++) {
            Double[] row = alt.get(i);
            temperatures[i] = row[2] + this.tempOffset;
            double delta = row[2]/temperatures[i];
            densities[i] = row[1]/2000*(delta);
            speedSound[i] = row[6]/Math.sqrt(delta);
        }
    }
    
    /**
     * Adds offset to wind strength
     * @param addWind 
     */
    public void changeWindStrength(double[] addWind){
        for(int i = 0; i < alt.size(); i++) {
            winds[i][0] += addWind[0];
            winds[i][1] += addWind[1];
        }
    }
    
    /**
     * Get the geopotential altitude
     * @param r
     * @return 
     */
    public static double geometricAlt(double[] r) {
        double radius = Math2.norm(r);
        double lat = Math.asin(r[2]/radius);
        double seaLevel = Earth.seaLevel(lat);
        return (radius-seaLevel)*seaLevel/radius;
    }
    
}
