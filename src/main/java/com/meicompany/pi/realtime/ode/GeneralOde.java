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
public abstract class GeneralOde {
    protected final OdeDynamics dynamics;
    
    protected final double[] x;
    protected final double[] x_dot;
    
    protected double time;
    protected double dt;    
    protected final double time_final;
    
    protected double tol;
    
    public GeneralOde(OdeDynamics dynamics, double[] x, double time_start, double time_final) {
        this.x = x;
        this.time = time_start;
        this.time_final = time_final;
        this.x_dot = new double[x.length];
        this.dynamics = dynamics;
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
