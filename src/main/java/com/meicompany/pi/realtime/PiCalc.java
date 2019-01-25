/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.realtime.clustering.KMeans;
import com.meicompany.pi.realtime.ode.FragmentOdeQuickerStep;
import com.meicompany.pi.realtime.ode.FragmentOde;
import com.meicompany.pi.realtime.fragment.Fragment;
import com.meicompany.pi.grid.util.NodeFlat;
import com.meicompany.pi.grid.util.NodeMap;
import static com.meicompany.pi.realtime.Helper.*;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author mpopescu
 */
public class PiCalc {
    
    private final ArrayList<Fragment> fragments;
    
    final double twopi = Math.PI*2;
    
    // Parameters
    final boolean pseudofragments = true;
    final int numberFragments;
    final int numberTurns;
    
    private int numberCentroids;
    
    // Initial
    private final double[] x0;
    private final double[] v0;
    
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
    
    public PiCalc(double[] x0, double[] v0) {
        this.x0 = x0;
        this.v0 = v0;
        this.numberFragments = 120;
        this.fragments = new ArrayList<>(numberFragments);
        for(int i = 0; i < numberFragments; i++) {
            fragments.add(new Fragment());
        }
        this.numberTurns = 6;
        this.impacts = new double[numberFragments*numberTurns][4]; // [x, y , z, time];
        this.impacts2D = new double[numberFragments*numberTurns][2];
    }
    
    /*
    public void setFragments(ArrayList<Fragment> fragments) {
        this.fragments = fragments;
        this.numberFragments = fragments.size();
        impacts = new double[numberFragments*numberTurns][4];
        this.pseudofragments = false;
    }
    */
    
    public double[][] getCentroids(int nCentroids) {
        // Get Random Generator
        Random rand = new Random();
        // Position Variables 
        double speed0 = norm(v0);
        double R = norm(x0);
        double[] r_ = divide(x0, R);
        double lat_ecef = asin(r_[2]);
        double long_ecef = atan2(r_[1],r_[0]);
        // Local Coordinate Frame Vectors (East, North, [Up is already done])
        double ct = cos(long_ecef);
        double st = sin(long_ecef);
        double cp = cos(lat_ecef);
        double sp = sin(lat_ecef);
        double[] e_ = new double[] {-st, ct, 0};
        double[] n_ = new double[] {sp*ct, sp*st, cp};
        // Flying Values
        double speed_vertical = dot(v0,r_);
        double speed_north = dot(v0,n_);
        double speed_east = v0[0]*e_[0]+v0[1]*e_[1]; // this might be microptimizing but saves 2 operations 
        double pitch = asin(speed_vertical/speed0);
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
            
            fragments.parallelStream().forEach((frag) -> {
                // Explosion Angles
                double angle1 = rand.nextFloat()*twopi;
                double angle2 = rand.nextFloat()*twopi;
                //double[] dv = new double[]{frag.explosionVelocity()*cos(angle1)*cos(angle2), frag.explosionVelocity()*cos(angle1)*sin(angle2), frag.explosionVelocity()*sin(angle1)};
                // Add explosion to velocity
                double[] dv = new double[3];
                dv[0] = frag.explosionVelocity()*cos(angle1);
                dv[1] = dv[0]*sin(angle2)+v[1];
                dv[2] = dv[0]*tan(angle1)+v[2];
                dv[0] = dv[0]*cos(angle2)+v[0];
                // Integrate Fragment
                FragmentOde ode = new FragmentOdeQuickerStep(x,dv,frag,0);
                ode.setA(g0);
                ode.setTempOffset(dTemp);
                frag.setImpact(ode.run());
            });
            
            for(Fragment frag : fragments) {
                System.arraycopy(frag.getImpact(), 0, impacts[count], 0, 3);
                count++;
            }
        }
        System.out.println("Converting Impacts to 2D");
        for(int i = 0; i < impacts.length; i++) {
            impacts2D[i] = Helper.impact2xy(impacts[i]); //impactLatLong
        }
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
        return this.centroids; // may need to look into this
    }
    
    public double calcAtLatLong2d(double latitude, double longitude) {
        double sum = 0;
        double[] xy = flatEarthXY(latitude,longitude);
        for(int i = 0; i < numberCentroids; i++) {
            double dx = xy[0] - centroids[i][0];
            double dy = xy[1] - centroids[i][1];
            double tmp = Math.sqrt(centroids[i][6]*centroids[i][7]);
            double p = centroids[i][8]/tmp;
            double p2 = 1-p*p;
            sum += centroids[i][4]*Math.exp((dx*dx/centroids[i][6]+dy*(dy/centroids[i][7]-2*p*dx/tmp))/(2*p2))/(TWOPI*tmp*Math.sqrt(p2));
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
    
    public NodeMap map() {
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
        double d = 3*Math.sqrt(maxSigma)+Math.sqrt(xMax*xMax+yMax*yMax);
        d = Math.ceil(d/1000)*1000;
        NodeMap map = new NodeMap(xC,yC,20,20,d/20);
        for(NodeFlat[] row : map.nodes){
            for(NodeFlat col : row) {
                testNode(col,1e-12);
            }
        }
        return map;
    }
    
    private void testNode(NodeFlat n, double tol) {
        double prob = calcAtXY2d(n.x,n.y);
        if (prob < 1e-100) {
            prob = 0;
        }
        n.setValue(prob);
        if (prob > tol) {
            n.divide();
            for(NodeFlat c : n.getChildren()) {
                testNode(c,tol*5);
            }
        }
    }
    
}
