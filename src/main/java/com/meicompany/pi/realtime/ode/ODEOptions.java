/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode;

/**
 *
 * @author mpopescu
 */
public class ODEOptions {

    public final double startTime;
    public final double endTime;
    
    private double absoluteTolerance;
    private double relativeTolerance;
    private double tolerance;
    
    
    public ODEOptions(double startTime, double endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * @return the absoluteTolerance
     */
    public double getAbsoluteTolerance() {
        return absoluteTolerance;
    }

    /**
     * @param absoluteTolerance the absoluteTolerance to set
     */
    public void setAbsoluteTolerance(double absoluteTolerance) {
        this.absoluteTolerance = absoluteTolerance;
    }

    /**
     * @return the relativeTolerance
     */
    public double getRelativeTolerance() {
        return relativeTolerance;
    }

    /**
     * @param relativeTolerance the relativeTolerance to set
     */
    public void setRelativeTolerance(double relativeTolerance) {
        this.relativeTolerance = relativeTolerance;
    }

    /**
     * @return the tolerance
     */
    public double getTolerance() {
        return tolerance;
    }

    /**
     * @param tolerance the tolerance to set
     */
    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }
    
    
    
}
