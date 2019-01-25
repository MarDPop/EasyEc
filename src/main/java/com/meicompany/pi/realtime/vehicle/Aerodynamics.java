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
public abstract class Aerodynamics {
    protected final Vehicle vehicle;
    
    protected final EarthModel earth;
    
    public Aerodynamics(Vehicle vehicle, EarthModel earth) {
        this.vehicle = vehicle;
        this.earth = earth;
    }
    
    public abstract double[] getAxisForces();
    
    public abstract double[] getAxisTorques();
}
