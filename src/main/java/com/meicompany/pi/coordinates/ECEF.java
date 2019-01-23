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
public final class ECEF extends Coordinates {
    
    //    radius; // from inertial center
    //    polarAngle; // from true north
    //    azimuthAngle; // from equator and prime meridian intersection
    private double[] spherical;
    
    private double time;
    
    
    public ECEF(double[] coordinates) {
        System.arraycopy(coordinates, 0, this.coordinates, 0, 2);
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
        return this.spherical = cartesian2spherical(coordinates[0], coordinates[1], coordinates[2]);
    }
    
    public ECI toECI() {
        double angle = spherical[2] + Epoch.earthRotationAngle(time);
        double[] x = new double[3];
        x[0] = spherical[0]*Math.cos(angle)*Math.sin(spherical[1]);
        x[1] = spherical[0]*Math.sin(angle)*Math.sin(spherical[1]);
        x[2] = spherical[0]*Math.cos(spherical[1]);
        return new ECI(x);
    }
    
    public static double[] ecefToGeodesy( double[] ecef ){
        double a = 6378137.0;              //WGS-84 semi-major axis
        double e2 = 6.6943799901377997e-3;  //WGS-84 first eccentricity squared
        double a1 = 4.2697672707157535e+4;  //a1 = a*e2
        double a2 = 1.8230912546075455e+9;  //a2 = a1*a1
        double a3 = 1.4291722289812413e+2;  //a3 = a1*e2/2
        double a4 = 4.5577281365188637e+9;  //a4 = 2.5*a2
        double a5 = 4.2840589930055659e+4;  //a5 = a1+a3
        double a6 = 9.9330562000986220e-1;  //a6 = 1-e2
        double zp,w2,w,r2,r,s2,c2,s,c,ss;
        double g,rg,rf,u,v,m,f,p,x,y,z;
        double[] geo = new double[3];   //Results go here (Lat, Lon, Altitude)
        x = ecef[0];
        y = ecef[1];
        z = ecef[2];
        zp = Math.abs( z );
        w2 = x*x + y*y;
        w = Math.sqrt( w2 );
        r2 = w2 + z*z;
        r = Math.sqrt( r2 );
        geo[1] = Math.atan2( y, x );       //Lon (final)
        s2 = z*z/r2;
        c2 = w2/r2;
        u = a2/r;
        v = a3 - a4/r;
        if( c2 > 0.3 ){
            s = ( zp/r )*( 1.0 + c2*( a1 + u + s2*v )/r );
            geo[0] = Math.asin( s );      //Lat
            ss = s*s;
            c = Math.sqrt( 1.0 - ss );
        }
        else{
            c = ( w/r )*( 1.0 - s2*( a5 - u - c2*v )/r );
            geo[0] = Math.acos( c );      //Lat
            ss = 1.0 - c*c;
            s = Math.sqrt( ss );
        }
        g = 1.0 - e2*ss;
        rg = a/Math.sqrt( g );
        rf = a6*rg;
        u = w - rg*c;
        v = zp - rf*s;
        f = c*u + s*v;
        m = c*v - s*u;
        p = m/( rf/g + f );
        geo[0] = geo[0] + p;      //Lat
        geo[2] = f + m*p/2.0;     //Altitude
        if( z < 0.0 ){
            geo[0] *= -1.0;     //Lat
        }
        return geo;    //Return Lat, Lon, Altitude in that order
    }
    
    public Geodesy toGeodesy() {
        return new Geodesy(0,0,0);
    }
    
}
