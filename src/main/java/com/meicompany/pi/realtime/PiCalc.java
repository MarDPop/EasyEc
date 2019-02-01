/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.realtime.ode.util.OdeAtmosphere;
import com.meicompany.pi.realtime.artifacts.BoundingBox;
import com.meicompany.pi.coordinates.CoordinateException;
import com.meicompany.pi.coordinates.Coordinates;
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

/**
 *
 * @author mpopescu
 */
public class PiCalc {
    private final OdeAtmosphere atm;
    private final ArrayList<FragmentWithOde> fragments;
    
    // Parameters
    final boolean pseudofragments = true;
    final int numberFragments;
    final int numberTurns;
    
    private int numberCentroids;
    
    // Initial
    private final double[] x0 = new double[3];
    private final double[] v0 = new double[3];
    private double time;
    
    // Variance
    private double sigma_pos;
    private double sigma_speed;
    private double sigma_heading;
    private double sigma_pitch;
    private double sigma_temperature;
    
    private double[][] centroidStatXtra;
    
    //Impacts
    private final double[][] impacts;
    private final double[][] impacts2D;
    private double[][] centroids;
    private HashMap<Double,CentroidPi[]> runs = new HashMap<>();
    private double weight;
    private ArrayList<double[][]> testImpacts = new ArrayList<>();
    private ArrayList<double[]> testTraj = new ArrayList<>();
    
    
    public PiCalc(double[] x0, double[] v0, double time) {
        this.atm = new OdeAtmosphere("src/main/resources/altitudes2.csv",0,1);
        setState(x0,v0, time);
        this.numberFragments = 150;
        this.fragments = new ArrayList<>(numberFragments);
        for(int i = 0; i < numberFragments; i++) {
            fragments.add(new FragmentWithOde(atm));
        }
        this.numberTurns = 6;
        this.impacts = new double[numberFragments*numberTurns][4]; // x, y , z, time;
        this.impacts2D = new double[numberFragments*numberTurns][2];
    }
    
    public final void setState(double[] x0, double[] v0, double time) {
        System.arraycopy(x0, 0, this.x0, 0, 3);
        System.arraycopy(v0, 0, this.v0, 0, 3);
        this.time = time;
        testTraj.add(Helper.ecef2xy(x0)); 
    }
    
    
    public void run(int nCentroids) {
        // Get Random Generator
        Random rand = new Random();
        
        // Position Variables 
        double speed0 = norm(v0);
        double R = norm(x0);
        double[] r_ = divide(x0, R);
        double lat_ecef = Math.asin(r_[2]);
        double long_ecef = atan2(r_[1],r_[0]);
        
        // Local Coordinate Frame Vectors (East, North, [Up is already done])
        double ct = cos(long_ecef);
        double st = sin(long_ecef);
        double cp = cos(lat_ecef);
        double sp = sin(lat_ecef);
        double[] e_ = new double[] {-st, ct, 0};
        double[] n_ = new double[] {-sp*ct, -sp*st, cp};
        // Flying Values
        double speed_vertical = dot(v0,r_);
        double speed_north = dot(v0,n_);
        double speed_east = v0[0]*e_[0]+v0[1]*e_[1]; // this might be microptimizing but saves 2 operations 
        double pitch = Helper.asin(speed_vertical/speed0);
        double heading = atan2(speed_north,speed_east);
        // Repeated Parameters
        double[] g0 = multiply(r_,-9.8);
        double q_approx = 0.63*exp(-(R-6371000)/8500)*speed0*speed0;
        double speedFactor = q_approx/6000;
        // Initialize Loop Variables
        double[] v = new double[3];
        double[] x = new double[3];
        int count = 0;
        System.out.println("Integrating Impacts");
        for(int turn = 0; turn < numberTurns; turn++) {
            // Heading and Pitch and Speed Variability
            double headingAngle = heading + speedFactor*sigma_heading*rand.nextGaussian();
            double pitchAngle = pitch + speedFactor*sigma_pitch*rand.nextGaussian();
            double speed = speed0 + speedFactor*sigma_speed*rand.nextGaussian();
            
            // Velocity Temp Calc
            x[2] = speed*sin(pitchAngle);
            pitchAngle = cos(pitchAngle);
            x[0] = speed*pitchAngle*cos(headingAngle);
            x[1] = speed*pitchAngle*sin(headingAngle);
            // Multiply by direction (convert to ECI coordinates)
            v[0] = x[0]*e_[0]+x[1]*n_[0]+x[2]*r_[0];
            v[1] = x[0]*e_[1]+x[1]*n_[1]+x[2]*r_[1];
            v[2] = x[0]*e_[2]+x[1]*n_[2]+x[2]*r_[2];
            
            // Position Calc
            double r = speedFactor*sigma_pos;
            x[0] = x0[0] + r*rand.nextGaussian();
            x[1] = x0[1] + r*rand.nextGaussian();
            x[2] = x0[2] + r*rand.nextGaussian();
            
            // Other Variability per turn
            double dTemp = sigma_temperature*rand.nextGaussian();
            this.atm.setOffsetTemp(dTemp);
            
            fragments.parallelStream().forEach((frag) -> frag.run(x,v,g0,time));
            
            for(FragmentWithOde frag : fragments) {
                System.arraycopy(frag.impact(), 0, impacts[count], 0, 3);
                count++;
            }
        }
        System.out.println("Converting Impacts to 2D");
        for(int i = 0; i < impacts.length; i++) {
            impacts2D[i] = Helper.impact2xy(impacts[i]); //impactLatLong
        }
        testImpacts.add(Helper.copy(impacts2D));
        System.out.println("Running Kmeans");
        this.centroids = KMeans.cluster(impacts2D,nCentroids);
        this.numberCentroids = centroids.length;
        
        // Precompute 2d distribution details
        centroidStatXtra = new double[numberCentroids][3];
        for(int i = 0; i < numberCentroids; i++) {
            centroidStatXtra[i][0] = Math.sqrt(centroids[i][6]*centroids[i][7]);
            centroidStatXtra[i][1] = centroids[i][8]/centroidStatXtra[i][0];
            centroidStatXtra[i][2] = Math.sqrt(1-centroidStatXtra[i][1]*centroidStatXtra[i][1]);
        }
        Helper.printCsv(impacts2D,"data.csv");
    }
    
    public void collectRun() {
        CentroidPi[] C = new CentroidPi[centroids.length];
        for(int i = 0; i < numberCentroids; i++) {
            C[i] = new CentroidPi(centroids[i],centroidStatXtra[i]);
        }
        runs.put(time, C);
    }
    
    public double calcAtLatLong2d(double latitude, double longitude) {
        double sum = 0;
        double[] xy = Coordinates.ll2xySpherical(latitude,longitude);
        for(int i = 0; i < numberCentroids; i++) {
            double dx = xy[0] - centroids[i][0];
            double dy = xy[1] - centroids[i][1];
            double tmp = Math.sqrt(centroids[i][6]*centroids[i][7]);
            double p = centroids[i][8]/tmp;
            double p2 = 1-p*p;
            sum += centroids[i][4]*Math.exp(-(dx*dx/centroids[i][6]+dy*(dy/centroids[i][7]-2*p*dx/tmp))/(2*p2))/(TWOPI*tmp*Math.sqrt(p2));
        }
        return sum/impacts2D.length;
    }
    
    public double calcAtLatLong1d(double longitude, double latitude){
        double sum = 0;
        double x = longitude*Math.cos(latitude)*6371000;
        double y = latitude*6371000;
        for(int i = 0; i < numberCentroids; i++) {
            double dx = x-centroids[i][0];
            double dy = y-centroids[i][1];
            double tmp = centroids[i][5]*2;
            sum += 1/sqrt(Math.PI*tmp)*Math.exp(-(dx*dx+dy*dy)/tmp);
        }
        return sum/impacts2D.length;
    }
    
    public double calcAtXY(double x, double y){
        double sum = 0;
        for(int i = 0; i < numberCentroids; i++) {
            double dx = x-centroids[i][0];
            double dy = y-centroids[i][1];
            double tmp = centroids[i][5]*2;
            sum += 1/sqrt(Math.PI*tmp)*Math.exp(-(dx*dx+dy*dy)/tmp);
        }
        return sum/impacts2D.length;
    }
    
    public double calcAtXY2d(double x, double y) {
        double sum = 0;
        for(int i = 0; i < numberCentroids; i++) {
            double dx = x - centroids[i][0];
            double dy = y - centroids[i][1];            
            sum += centroids[i][4]*Math.exp(-(dx*dx/centroids[i][6]+dy*(dy/centroids[i][7]-2*centroidStatXtra[i][1]*dx/centroidStatXtra[i][0]))/(2*centroidStatXtra[i][2]*centroidStatXtra[i][2]))/(TWOPI*centroidStatXtra[i][0]*centroidStatXtra[i][2]);
        }
        return sum/impacts2D.length;
    }
    
    public NodeMap mapQuick() {
        System.out.println("Generating Pi Map");
        double maxSigma = 0;
        double xC = 0;
        double yC = 0;
        double xMax = -1e100;
        double xMin = 1e100;
        double yMax = -1e100;
        double yMin = 1e100;
        for(double[] c : centroids) {
            if (c[5] > maxSigma) {
                maxSigma = c[5];
            }
            if (c[0] > xMax) {
                xMax = c[0];
            }
            if (c[0] < xMin) {
                xMin = c[0];
            }
            if (c[1] > yMax) {
                yMax = c[1];
            }
            if (c[1] < yMin) {
                yMin = c[1];
            }
            xC += c[0];
            yC += c[1];
        }
        xC /= centroids.length;
        yC /= centroids.length;
        
        xMax -= xMin;
        yMax -= yMin;
        double d = 5*Math.sqrt(maxSigma)+Math.sqrt(xMax*xMax+yMax*yMax);
        d = Math.ceil(d/1000)*1000;
        NodeMap map = new NodeMap(xC,yC,20,20,d/20);
        for(Node[] row : map.nodes){
            for(Node col : row) {
                testNode(col,1e-12);
            }
        }
        return map;
    }
    
    public NodeMap mapAll() {
        System.out.println("Generating Pi Map");
        double[] stats = getCentroidStats();
        double d = 3*Math.sqrt(stats[2] + stats[3]);
        int delta = 20;
        NodeMap map = new NodeMap(stats[0],stats[1],delta,delta,d/delta);
        for(Node[] row : map.nodes){
            for(Node col : row) {
                testNodeMultiple(col,1e-12);
            }
        }
        BoundingBox box = new BoundingBox(stats);
        Helper.printCsv(box.pointsXY,"initial.csv");
        Helper.printCsv(testImpacts,"data.csv");
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
        double prob = calcAtXY2d(n.x,n.y);
        n.setValue(prob);
        if (prob > tol) {
            n.divide();
            for(Node c : n.getChildren()) {
                testNode(c,tol*5);
            }
        }
    }
    
    private void testNodeMultiple(Node n, double tol) {
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
        if (prob > tol) {
            n.divide();
            for(Node c : n.getChildren()) {
                testNodeMultiple(c,tol*5);
            }
        }
    }
    
}
