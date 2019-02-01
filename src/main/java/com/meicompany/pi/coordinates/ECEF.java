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
public final class ECEF extends CoordinateFrame {
    
    private double[] spherical;
    
    public ECEF(double[] coordinates) {
        System.arraycopy(coordinates, 0, this.x, 0, 2);
        this.epoch = Epoch.EPOCH_J2000;
    }
    
    public ECEF(double[] coordinates, double time) {
        this(coordinates);
        this.time = time;
    }
    
    public ECEF(double radius, double polarAngle, double azimuthAngle) {
        this(spherical2cartesian(radius,polarAngle,azimuthAngle));
        this.spherical[0] = radius;
        this.spherical[1] = polarAngle;
        this.spherical[2] = azimuthAngle;
    }
    
    public ECEF(double radius, double polarAngle, double azimuthAngle, double time) {
        this(radius,polarAngle,azimuthAngle);
        this.time = time;
    }
    
    public double[] getSpherical() {
        double[] s = cartesian2spherical(x[0], x[1], x[2]);
        System.arraycopy(s, 0, this.spherical, 0, 3);
        return s;
    }
    
    public ECI toECI() {
        double angle = spherical[2] + Epoch.getEarthRotationAngle(time);
        double[] x = new double[3];
        x[0] = spherical[0]*Math.cos(angle)*Math.sin(spherical[1]);
        x[1] = spherical[0]*Math.sin(angle)*Math.sin(spherical[1]);
        x[2] = spherical[0]*Math.cos(spherical[1]);
        return new ECI(x);
    }
    
    
    public Geodesy toGeodesy() {
        return new Geodesy(0,0,0);
    }
    
}
