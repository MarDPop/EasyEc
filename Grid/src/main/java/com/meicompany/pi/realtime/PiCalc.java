/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.realtime.generalMath.Math2;
import com.meicompany.pi.realtime.ode.util.OdeAtmosphere;
import com.meicompany.pi.realtime.artifacts.BoundingBox;
import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.coordinates.Earth;
import com.meicompany.pi.realtime.clustering.KMeans;
import com.meicompany.pi.realtime.fragment.Fragment;
import com.meicompany.pi.realtime.map.util.Node;
import com.meicompany.pi.realtime.map.util.NodeMap;
import static com.meicompany.pi.realtime.generalMath.Math2.*;
import com.meicompany.pi.realtime.clustering.CentroidPi;
import com.meicompany.pi.realtime.fragment.Fragment2ndOrder;
import com.meicompany.pi.realtime.fragment.FragmentAdaptive;
import com.meicompany.pi.realtime.fragment.FragmentAdaptiveHigher;
import com.meicompany.pi.realtime.fragment.FragmentDefault;
import com.meicompany.pi.realtime.fragment.FragmentLeapfrog;
import com.meicompany.pi.realtime.fragment.FragmentOptions;
import com.meicompany.pi.realtime.fragment.FragmentRK12;
import com.meicompany.pi.realtime.ode.ODEOptions;
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
    private final ArrayList<Fragment> fragments;

    private final boolean testing = true;
    private final double arcSecinRadians = 1.45444104333e-4; // 30 arcseconds in radians for pop scan
    
    // Parameters
    final int numberFragments;
    final int numberTurns;
    final double maxQ = 101000;
    final double fragsPerEvent = 50;
    final short maxLevels = 6;
    final float levelTolMultiplier = 7.5f;
    float maxTol;
    
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
    
    //Impacts
    private final float[][] impacts2D;
    private float[][] centroids;
    private final HashMap<Double,CentroidPi[]> runs = new HashMap<>();
    private double weight;
    private final ArrayList<float[][]> testImpacts = new ArrayList<>();
    private final ArrayList<double[]> testImpactsSpherical = new ArrayList<>();
    
    
    public PiCalc(double[] x0, double[] v0, double time) {
        this.atm = new OdeAtmosphere("src/main/resources/altitudes2.csv",0,1);
        setState(x0,v0, time);
        this.numberFragments = 120;
        this.fragments = new ArrayList<>(numberFragments);
        FragmentOptions fOptions = new FragmentOptions();
        ODEOptions oOptions = new ODEOptions();
        oOptions.setMaxTimestep(8);
        oOptions.setTolerance(5e-9);
        oOptions.setInitialStep(sqrt(0.63*exp(-(norm(x0)-6371000)*1.577086431315691e-04)*(v0[0]*v0[0]+v0[1]*v0[1]+v0[2]*v0[2])/1e5));
        for(int i = 0; i < numberFragments; i++) {
            fOptions.generatePseudo();
            fragments.add(new FragmentRK12(fOptions,atm,oOptions));
        }
        this.numberTurns = 5;
        // this.impacts = new double[numberFragments*numberTurns][4]; // The indexes are : x y z time
        this.impacts2D = new float[numberFragments*numberTurns][2];
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
        float pitch = Math2.asin(speedVertical/speed0);
        float heading = Math2.atan2(speedNorth,speedEast);
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
            x[2] = speed*Math2.sin(pitchAngle);
            pitchAngle = Math2.cos(pitchAngle);
            x[0] = speed*pitchAngle*Math2.cos(headingAngle);
            x[1] = speed*pitchAngle*Math2.sin(headingAngle);
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
            
            // Integrate 
            fragments.parallelStream().forEach(frag -> {
                frag.setup(x,v,g0,time);
                frag.run();
            });
            
            // Record Impacts
            for(Fragment frag : fragments) {
                System.arraycopy(frag.impact2xy(), 0, impacts2D[count], 0, 2);
                count++;
                if(testing) {
                    double[] nj = new double[4];
                    System.arraycopy(frag.impact(),0,nj,0,4);
                    if( nj[0] == Double.NaN || nj[1] == Double.NaN ) {
                        System.out.println(frag.hashCode());
                    }
                    if (nj[3] > 4000) {
                        System.out.println(time+" to "+nj[3]+" bc: "+frag.bcFast(0));
                    }
                    testImpactsSpherical.add(CoordinateFrame.rotateZ(nj, (float)(-nj[3]*Earth.EARTH_ROT)));
                }
            }
        }
        
        if(testing) {         
            testImpacts.add(Math2.copy(impacts2D));
        }
        
        this.centroids = KMeans.cluster(impacts2D,nCentroids);    
    }
    
    public void collectRun() {
        int numberCentroids = centroids.length;
        CentroidPi[] c = new CentroidPi[numberCentroids];
        for(int i = 0; i < numberCentroids; i++) {
            c[i] = new CentroidPi(centroids[i],time);
        }
        runs.put(time, c);
    }
    
    public NodeMap map(float tol) {
        this.maxTol = (float) (1.01*Math.pow(levelTolMultiplier, maxLevels));
        
        double[] stats = getCentroidStats();
        
        BoundingBox box = new BoundingBox(stats);
        
        if(testing) {
            Logger.getLogger(PiCalc.class.getName()).log(Level.INFO, "Printing data ");
            // IOHelper.printCsv(box.pointsXY,"traj.csv");
            IOHelper.printCsv(runs.values(), "centroids.csv");
            
            IOHelper.printCsv2(testImpactsSpherical,"impacts.csv");
            
            IOHelper.printCsv3(testImpacts,"data.csv");
        }
        
        double[] center = CoordinateFrame.xy2ll(stats);
        double approxLong = Math.sqrt(stats[2])/6371000;
        double approxLat = Math.sqrt(stats[3])/6371000;
        if(approxLong > Math.PI*0.33) {
            approxLong = Math.PI*0.33;
        }
        
        if(approxLat > Math.PI*0.166666) {
            approxLat = Math.PI*0.166666;
        }
        double sdNumber = 2;
        double initialCellSize = arcSecinRadians*32;
        int m = (int) (sdNumber*approxLong/initialCellSize); // divisions in even number of 30 arc seconds
        int n = (int) (sdNumber*approxLat/initialCellSize);
        
        NodeMap map = new NodeMap(center[0],center[1],m,n,initialCellSize); // need to output a spherical too
        int i = 0;
        for(Node[] row : map.nodes){
            for(Node col : row) {
                testNode(col,tol);
            }
            Logger.getLogger(PiCalc.class.getName()).log(Level.INFO, "processed row " + (i++));
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
    
    private void testNode(Node n, float tol) {
        float prob = 0;
        for(CentroidPi[] list : runs.values()){
            for(CentroidPi centroid : list) {
                prob += centroid.calcAtLongLat((float)n.longitude, (float)n.latitude);
            }
        }
        
        if (prob < 1e-25f) {
            prob = 1e-25f;
        } else {
            prob /= weight;
        }
        
        n.setValue(prob);
        
        if (tol < maxTol) {
            if (prob > tol) {
                n.divide();
                for(Node c : n.getChildren()) {
                    testNode(c,tol*levelTolMultiplier);
                }
            }
        }
    }
    
}
