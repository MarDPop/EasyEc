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
public abstract class Structure {
    
    private double[] position = new double[3]; // position of center of gravity
    private double mass = 0;

    /**
     * @return the mass
     */
    public double getMass() {
        return mass;
    }

    /**
     * @param mass the mass to set
     */
    public void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * @return the position
     */
    public double[] getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(double[] position) {
        this.position = position;
    }
    
}
