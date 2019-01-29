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
public abstract class ODE {
    protected final Dynamics dynamics;
    
    protected final double[] x;
    
    protected double time;
    protected double dt;    
    protected final double time_final;
   
    protected ODEOptions options;
    
    public ODE(Dynamics dynamics, double[] x, ODEOptions options) {
        this.dynamics = dynamics;
        this.x = x;
        this.time = options.startTime;
        this.time_final = options.endTime;
    }
    
    public void run() {
        while(nextStep()) {
            step();
            time += dt;
        }
    }
    
    protected boolean nextStep() {
        return time < time_final;
    }
    
    abstract void step();
    
}
