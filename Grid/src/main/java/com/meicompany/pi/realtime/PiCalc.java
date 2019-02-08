/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.realtime.ode.util.OdeAtmosphere;
import com.meicompany.pi.realtime.artifacts.BoundingBox;
import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.coordinates.Earth;
import com.meicompany.pi.realtime.clustering.KMeans;
import com.meicompany.pi.realtime.fragment.FragmentWithOde;
import com.meicompany.pi.realtime.map.util.Node;
import com.meicompany.pi.realtime.map.util.NodeMap;
import static com.meicompany.pi.realtime.Helper.*;
import com.meicompany.pi.realtime.clustering.CentroidPi;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mpopescu
 */
public class PiCalc {
    private final OdeAtmosphere atm;
    private final ArrayList<FragmentWithOde> fragments;
    private final double maxQ = 101000;
    
    private final boolean testing = true;
    private final boolean recordFragments = false;
    
    // Parameters
    final int numberFragments;
    final int numberTurns;
    
    private int numberCentroids;
    
    // Initial
    private final double[] x0 = new double[3];
    private final double[] v0 = new double[3];
    private double time;
    
    // Variance
    private float sigma_pos;
    private float sigma_speed;
    private float sigma_heading;
    private float sigma_pitch;
    private float sigma_temperature;
    
    private double[][] centroidStatXtra;
    
    //Impacts
    private final double[][] impacts;
    private final double[][] impacts2D;
    private double[][] centroids;
    private HashMap<Double,CentroidPi[]> runs = new HashMap<>();
    private double weight;
    private ArrayList<double[][]> testImpacts = new ArrayList<>();
    private ArrayList<ArrayList<double[]>> testFragments = new ArrayList<>();
    
    
    public PiCalc(double[] x0, double[] v0, double time) {
        this.atm = new OdeAtmosphere("src/main/resources/altitudes2.csv",0,1);
        setState(x0,v0, time);
        this.numberFragments = 200;
        this.fragments = new ArrayList<>(numberFragments);
        for(int i = 0; i < numberFragments; i++) {
            fragments.add(new FragmentWithOde(atm));
        }
        this.numberTurns = 5;
        this.impacts = new double[numberFragments*numberTurns][4]; // The indexes are : x y z time
        this.impacts2D = new double[numberFragments*numberTurns][2];
    }
    
    public final void setState(double[] x0, double[] v0, double time) {
        System.arraycopy(x0, 0, this.x0, 0, 3);
        System.arraycopy(v0, 0, this.v0, 0, 3);
        this.time = time;
    }
    
    
    public void run(int nCentroids) {
        // Get Random Generator
        Random rand = new Random();
                
        // Position Variables 
        float speed0 = (float)norm(v0);
        double radius = norm(x0);
        double[] radialVector = divide(x0, radius);
        double lat_ecef = Math.asin(radialVector[2]);
        double long_ecef = atan2(radialVector[1],radialVector[0]);
        
        // Local Coordinate Frame Vectors (East, North, [Up is already done])
        double ct = cos(long_ecef);
        double st = sin(long_ecef);
        double cp = cos(lat_ecef);
        double sp = radialVector[2];
        double[] eastVector = new double[] {-st, ct, 0};
        double[] northVector = new double[] {-sp*ct, -sp*st, cp};
        // Flying Values
        float speedVertical = (float)dot(v0,radialVector);
        float speedNorth = (float)dot(v0,northVector);
        float speedEast = (float)(v0[0]*eastVector[0]+v0[1]*eastVector[1]); 
        float pitch = Helper.asin(speedVertical/speed0);
        float heading = Helper.atan2(speedNorth,speedEast);
        // Repeated Parameters
        double[] g0 = multiply(radialVector,-9.8);
        double dynamicPressureApprox = 0.63*exp(-(radius-6371000)/8500)*speed0*speed0;
        float speedFactor = (float)sqrt(dynamicPressureApprox/maxQ); // exp(-1000/dynamicPressureApprox)
        // Initialize Loop Variables
        double[] v = new double[3];
        double[] x = new double[3];
        int count = 0;
        // Impact Integration
        for(int turn = 0; turn < numberTurns; turn++) {
            // Heading and Pitch and Speed Variability
            float headingAngle = heading + speedFactor*sigma_heading*(float)rand.nextGaussian();
            float pitchAngle = pitch + speedFactor*sigma_pitch*(float)rand.nextGaussian();
            float speed = speed0 + speedFactor*sigma_speed*(float)rand.nextGaussian();
            
            // Velocity Temp Calc
            x[2] = speed*Helper.sin(pitchAngle);
            pitchAngle = Helper.cos(pitchAngle);
            x[0] = speed*pitchAngle*Helper.cos(headingAngle);
            x[1] = speed*pitchAngle*Helper.sin(headingAngle);
            // Multiply by direction (convert to ECI coordinates)
            v[0] = x[0]*eastVector[0]+x[1]*northVector[0]+x[2]*radialVector[0];
            v[1] = x[0]*eastVector[1]+x[1]*northVector[1]+x[2]*radialVector[1];
            v[2] = x[0]*eastVector[2]+x[1]*northVector[2]+x[2]*radialVector[2];
            
            // Position Calc
            double r = speedFactor*sigma_pos;
            x[0] = x0[0] + r*rand.nextGaussian();
            x[1] = x0[1] + r*rand.nextGaussian();
            x[2] = x0[2] + r*rand.nextGaussian();
            
            // Other Variability per turn
            double dTemp = sigma_temperature*rand.nextGaussian();
            this.atm.setOffsetTemp(dTemp);
            
            if(!recordFragments) {
                fragments.parallelStream().forEach(frag -> frag.run(x,v,g0,time));

                for(FragmentWithOde frag : fragments) {
                    System.arraycopy(frag.impact(), 0, impacts[count], 0, 4);
                    count++;
                }
            } else {
                fragments.parallelStream().forEach(frag -> frag.runRecord(x,v,g0,time));

                for(FragmentWithOde frag : fragments) {
                    System.arraycopy(frag.impact(), 0, impacts[count], 0, 4);
                    testFragments.add(frag.recording);
                    count++;
                }
            }
        }

        for(int i = 0; i < impacts.length; i++) {
            impacts2D[i] =  Helper.impact2xy(impacts[i]); 
        }
        
        if(testing) {
            for (double[] impact : impacts) {
                double[] geo = CoordinateFrame.ecef2geo(impact);
                geo[0] -= impact[3]*Earth.EARTH_ROT;
                System.arraycopy(geo, 0, impact, 0, 3);
            }            
            testImpacts.add(Helper.copy(impacts));
        }
        
        this.centroids = KMeans.cluster(impacts2D,nCentroids);
        this.numberCentroids = centroids.length;
        
        // Precompute 2d distribution details
        centroidStatXtra = new double[numberCentroids][3];
        for(int i = 0; i < numberCentroids; i++) {
            centroidStatXtra[i][0] = Math.sqrt(centroids[i][6]*centroids[i][7]);
            centroidStatXtra[i][1] = centroids[i][8]/centroidStatXtra[i][0];
            centroidStatXtra[i][2] = Math.sqrt(1-centroidStatXtra[i][1]*centroidStatXtra[i][1]);
        }
    }
    
    public void collectRun() {
        CentroidPi[] c = new CentroidPi[numberCentroids];
        for(int i = 0; i < numberCentroids; i++) {
            c[i] = new CentroidPi(centroids[i],centroidStatXtra[i]);
        }
        runs.put(time, c);
    }
    
    public NodeMap map(double tol) {

        double[] stats = getCentroidStats();
        double d = 2.5*Math.sqrt(stats[2] + stats[3]);
        int delta = 20;
        NodeMap map = new NodeMap(stats[0],stats[1],delta,delta,d/delta);
        int i = 0;
        for(Node[] row : map.nodes){
            for(Node col : row) {
                testNode(col,tol);
            }
            Logger.getLogger(PiCalc.class.getName()).log(Level.INFO, "processed row " + (i++));
        }
        BoundingBox box = new BoundingBox(stats);
        if(testing) {
            // Helper.printCsv(box.pointsXY,"traj.csv");
            Helper.printCsv(runs.values(), "centroids.csv");
            Logger.getLogger(PiCalc.class.getName()).log(Level.INFO, "Printing data ");
            Helper.printCsv(testImpacts,"data.csv");
            if (recordFragments) {
                Logger.getLogger(PiCalc.class.getName()).log(Level.INFO, "Printing Frag ");
                Helper.printFrags(testFragments);
            }
        }
        Logger.getLogger(PiCalc.class.getName()).log(Level.INFO, "Get map ");
        return map;
    }
    
    private double[] getCentroidStats() {
        double[] stats = new double[5]; // x center, y center, sigma x, sigma y, sigma xy
        weight = 0;
        for(CentroidPi[] list : runs.values()){
            for(CentroidPi c : list) {
                stats[0] += c.number*c.x_Center;
                stats[1] += c.number*c.y_Center;
                weight += c.number;
            }
        }
        stats[0] /= weight;
        stats[1] /= weight;
        for(CentroidPi[] list : runs.values()){
            for(CentroidPi c : list)  {
                if (!Double.isNaN(c.x_Center)) {
                    double dx = c.x_Center-stats[0];
                    double dy = c.y_Center-stats[1];
                    stats[2] += c.number*dx*dx;
                    stats[3] += c.number*dy*dy;
                    stats[4] += c.number*dx*dy;
                }
            }
        }
        stats[2] /= weight;
        stats[3] /= weight;
        stats[4] /= weight;
        return stats;
    }
    
    private void testNode(Node n, double tol) {
        double prob = 0;
        for(CentroidPi[] list : runs.values()){
            for(CentroidPi c : list) {
                prob += c.calcAt(n.x, n.y);
            }
        }
        if (prob < 1e-50) {
            prob = 0;
        } else {
            prob /= weight;
        }
        n.setValue(prob);
        if (tol < 2e-6) {
            if (prob > tol) {
                n.divide();
                for(Node c : n.getChildren()) {
                    testNode(c,tol*10);
                }
            }
        }
    }
    
}
