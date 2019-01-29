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
    
    public static final double EQUATORIAL_RADIUS = 6378137.0;
    public static final double POLAR_RADIUS =  6356752.314245;
    public static final double FLATTENING = 298.257223563;
    public static final double ECCENTRICITY_SQ = 6.694379990198e-3;
    
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
    
    public static double primeVerticalRadiusCurvature(double phi){
        return EQUATORIAL_RADIUS/Math.sqrt(1-ECCENTRICITY_SQ*Math.sin(phi)*Math.sin(phi));
    }
}
