/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.vehicle;

/**
 *
 * @author mpopescu
 */
public abstract class Tank extends Structure{
    
    private double[] fluidCOG;
    private double fluidMass;
    private double flowrate;

    /**
     * @return the flowrate
     */
    public double getFlowrate() {
        return flowrate;
    }

    /**
     * @param flowrate the flowrate to set
     */
    public void setFlowrate(double flowrate) {
        this.flowrate = flowrate;
    }

    /**
     * @return the fluidCOG
     */
    public double[] getFluidCOG() {
        return fluidCOG;
    }

    /**
     * @param fluidCOG the fluidCOG to set
     */
    public void setFluidCOG(double[] fluidCOG) {
        this.fluidCOG = fluidCOG;
    }
    
    
}
