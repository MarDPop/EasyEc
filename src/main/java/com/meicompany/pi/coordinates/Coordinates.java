/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.coordinates;

/**
 *
 * @author mpopescu
 */
public abstract class Coordinates {
    
    protected int epoch;
    protected double time;
    protected final double[] coordinates = new double[3];
    
    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }
    
    public int getEpoch() {
        return epoch;
    }
    
    public void setTime(double time) {
        this.time = time;
    }
    
    public double getTime() {
        return time;
    }
    
    public void set(double[] coordinates){
        if(coordinates.length != 3) {
            throw new InstantiationError("must provide x y z");
        }
        System.arraycopy(coordinates, 0, this.coordinates, 0, 3);
    }
    
    public static double[] spherical2cartesian(double radius, double polarAngle, double azimuthAngle) {
        double[] out = new double[3];
        out[0] = radius*Math.cos(azimuthAngle)*Math.sin(polarAngle);
        out[1] = radius*Math.sin(azimuthAngle)*Math.sin(polarAngle);
        out[2] = radius*Math.cos(polarAngle);
        return out;
    }
    
    public static double[] cartesian2spherical(double x, double y, double z) {
        double[] out = new double[3];
        out[0] = Math.sqrt(x*x+y*y+z*z);
        out[1] = Math.acos(z/out[0]);
        out[2] = Math.atan2(y,x);
        return out;
    }
    
}
