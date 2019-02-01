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
public final class Geodesy extends Coordinates {
    
    public Geodesy(double latitude, double longitude, double height) {
        this.x[0] = latitude;
        this.x[1] = longitude;
        this.x[2] = height;
        this.epoch = Epoch.EPOCH_J2000;
    }
    
    public Geodesy(double latitude, double longitude, double height, double time) {
        this(latitude,longitude,height);
        this.time = time;
    }
    
    public ECEF toEcef() {
        return new ECEF(geodetic2ecef(x[0],x[1],x[2]));
    }
    
    
}
