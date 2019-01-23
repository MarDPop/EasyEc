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
public class Vehicle {
    
    protected final double[] position = new double[3];
    protected final double[] orientation = new double[3];
    protected final double[] velocity = new double[3];
    protected final double[] rotationRate = new double[3];
    
    protected double[] normalizedInertia = new double[6];
    protected double mass;
    
    protected double time;
    
    protected Aerodynamics aero;
    
    public void setState(double[] state, double time){
        
    }
}
