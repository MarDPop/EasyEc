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
public abstract class Propulsion extends Structure {
    
    protected double[] thrust; // vector in direction and magnitude of thrust
    
    public abstract double[] getThrust();
    
}
