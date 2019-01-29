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
public abstract class LiftingSurface extends Structure{

    protected double[] areaNormal; // normal vector with magnitude equal to area
 
    public abstract double getLift();
    
    
}
