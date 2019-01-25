/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode;

/**
 *
 * @author mpopescu
 * 
 */
@FunctionalInterface
public interface OdeDynamics {
    
    public abstract double[] calc(double[] state, double time);
}
