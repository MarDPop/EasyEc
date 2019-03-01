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
    
    private double maxTimestep;
    private double minTimestep;
    private double initialStep;
    
    public ODEOptions() {
        this(new double[]{0,1e5,1e-3,1e-4,1e-3,10,1e-6,1e-3});
    }
    
    public ODEOptions(double startTime, double endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public ODEOptions(double[] in) {
        this.startTime = in[0];
        this.endTime = in[1];
        this.absoluteTolerance = in[2];
        this.relativeTolerance = in[3];
        this.tolerance = in[4];
        this.maxTimestep = in[5];
        this.minTimestep = in[6];
        this.initialStep = in[7];
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
    
    /**
     * @return the maxTimestep
     */
    public double getMaxTimestep() {
        return maxTimestep;
    }

    /**
     * @param maxTimestep the absoluteTolerance to set
     */
    public void setMaxTimestep(double maxTimestep) {
        this.maxTimestep = maxTimestep;
    }
    
    /**
     * @return the minTimestep
     */
    public double getMinTimestep() {
        return minTimestep;
    }

    /**
     * @param minTimestep the minTimestep to set
     */
    public void setMinTimestep(double minTimestep) {
        this.minTimestep = minTimestep;
    }

    /**
     * @return the initialStep
     */
    public double getInitialStep() {
        return initialStep;
    }

    /**
     * @param initialStep the initialStep to set
     */
    public void setInitialStep(double initialStep) {
        this.initialStep = initialStep;
    }
    
}
