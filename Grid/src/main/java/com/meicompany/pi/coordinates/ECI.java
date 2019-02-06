/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.coordinates;

/**
 *
 * @author mpopescu
 */
public final class ECI extends CoordinateFrame {
    
    public ECI(double[] cartesian) {
        set(cartesian);
        this.epoch = 1;
    }
    
    public ECI(double[] cartesian, int epoch) {
        set(cartesian);
        this.epoch = epoch;
    }
    
    public ECI(double x, double y, double z, int epoch) {
        set(new double[]{x,y,z});
        this.epoch = epoch;
    }
    
    public double[] toSpherical() {
        return cartesian2spherical(x[0], x[1], x[2]);
    }
     
}
