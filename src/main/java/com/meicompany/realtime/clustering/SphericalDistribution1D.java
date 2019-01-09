/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.realtime.clustering;

import com.meicompany.realtime.Helper;
import java.util.ArrayList;

/**
 *
 * @author mpopescu
 */
public class SphericalDistribution1D extends Centroid{
    private double[] directionVector;
    private double weight;
    
    public SphericalDistribution1D() {
        super();
        this.location = new double[3];
        this.locationTemp = new double[3];
        this.stats = new double[1];
    }
    
    public double update(){
        this.locationTemp[0] /= n;
        this.locationTemp[1] /= n;
        this.locationTemp[2] /= n;
        double[] err = Helper.subtract(location, locationTemp);
        this.location = locationTemp;
        reset();
        return Helper.norm(err);
    }
    
    public double distance(double[] r) {
        return Helper.flatEarthDistance(location, r);
    }
    
    public void addPoint(double[] r, double d) {
        this.locationTemp[0] += r[0];
        this.locationTemp[1] += r[1];
        this.locationTemp[2] += r[2];
        this.stats[0] += d*d;
        this.n++;
    }
    
    public int getNumber() {
        return n;
    }
    
    public double[] calcStats(ArrayList<double[]> points) {
        stats[0] = Math.sqrt(stats[0])/(n-1);
        return stats;
    }
    
    public double getProbability(double[] r){
        double d = Helper.flatEarthDistance(location, r);
        return weight/(2*Math.PI*stats[0])*Math.exp(-d*d/stats[0]/2);
    }
}
