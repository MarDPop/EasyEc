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
        this.coordinates[0] = latitude;
        this.coordinates[1] = longitude;
        this.coordinates[2] = height;
        this.epoch = Epoch.EPOCH_J2000;
    }
    
    public Geodesy(double latitude, double longitude, double height, double time) {
        this(latitude,longitude,height);
        this.time = time;
    }
    
    public static ECEF geodetic2ecef(double latitude, double longitude, double height) {
        double N = primeVerticalRadiusCurvature(latitude);
        double[] x = new double[3];
        x[0] = (N+height)*Math.cos(latitude)*Math.cos(longitude);
        x[1] = (N+height)*Math.cos(latitude)*Math.sin(longitude);
        x[2] = ((1-ECCENTRICITY_SQ)*N+height)*Math.sin(latitude);
        return new ECEF(x);
    }
    
    public ECEF toEcef() {
        return geodetic2ecef(coordinates[0],coordinates[1],coordinates[2]);
    }
    
    public static double primeVerticalRadiusCurvature(double phi){
        return EQUATORIAL_RADIUS/Math.sqrt(1-ECCENTRICITY_SQ*Math.sin(phi)*Math.sin(phi));
    }
}
