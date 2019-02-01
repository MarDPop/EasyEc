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
    
}
