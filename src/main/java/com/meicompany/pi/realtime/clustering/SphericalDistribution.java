/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.clustering;

import com.meicompany.pi.coordinates.CoordinateException;
import com.meicompany.pi.realtime.Helper;
import java.util.ArrayList;

/**
 *
 * @author mpopescu
 */
public class SphericalDistribution extends Centroid{
    private double[] directionVector;
    private double weight;
    
    public SphericalDistribution() {
        super();
        this.location = new double[3];
        this.locationTemp = new double[3];
        this.stats = new double[2];
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
        return CoordinateException.flatEarthDistance(location, r);
    }
    
    public void addPoint(double[] r, double d, int i) {
        this.locationTemp[0] += r[0];
        this.locationTemp[1] += r[1];
        this.locationTemp[2] += r[2];
        this.stats[0] += d*d;
        this.n++;
        this.ids.add(i);
    }
    
    public double[] calcStats(ArrayList<double[]> points) {
        directionVector = new double[3];
        if (n > 1) {
            stats[0] = Math.sqrt(stats[0])/(n-1);
            stats[1] = 0;
            for(double[] point : points) {
                double[] dr = Helper.subtract(point, location);
                double[] N = Helper.cross(location, dr);
                this.directionVector[0] += N[0];
                this.directionVector[1] += N[1];
                this.directionVector[2] += N[2];
            }
            this.directionVector[0] /= n;
            this.directionVector[1] /= n;
            this.directionVector[2] /= n;
            double mag = Helper.norm(directionVector);
            for(double[] point : points) {
                double[] dr = Helper.subtract(point, location);
                double d = mag-Helper.dot(location, dr);
                stats[1] += d*d;
            }
            stats[1] = Math.sqrt(stats[1])/(n-1);
        }
        return stats;
    }
    
    public double getProbability(double[] point){
        return weight*Math.exp(1);
    }
}
