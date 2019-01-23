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
public class Epoch {
    
    public static final double EARTH_ROT_RATE = 7.2921150e-5; // rad/s
    public static final double EARTH_STELLAR_DAY = 86164.098903691; // s
    public static final double EARTH_SIDEREAL_DAY = 86164.09053083288; //s
    public static final int J2000_JULIAN_TIME = 2451545;
    public static final int EPOCH_J2000 = 1;
    public static final int EPOCH_M50 = 2;
    public static final int EPOCH_GCRF = 3;
    public static final int EPOCH_MATLAB = 4;
    
    public static double time2Angle(double time) {
        return 2*Math.PI*time/84600.0;
    }
    
    public static double earthRotationAngle(double julianUT1) {
        return 2*Math.PI*(0.7790572732640+1.00273781191135448*(julianUT1-2451545.0));
    }
    
    public static double GMST(double julianUT1) {
        //http://aa.usno.navy.mil/faq/docs/GAST.php
        int JD0 = (int)julianUT1;
        double H = (julianUT1-JD0)*24;
        double T = (julianUT1-J2000_JULIAN_TIME)/36525.0;
        return 6.697374558 + 0.06570982441908*(JD0-J2000_JULIAN_TIME) + 1.00273790935*H + 0.000026*T*T ;
    }
    
    public static double GAST(double julianUT1) {
        double D = julianUT1-J2000_JULIAN_TIME;
        double o = 125.04 - 0.052954*D;
        double L = 280.47 + 0.98565*D;
        double e = 23.4393 - 0.0000004*D;
        double d = -0.000319*Math.sin(Math.toRadians(o)) - 0.000024*Math.sin(Math.toRadians(2*L)); 
        double eqeq = d*Math.cos(Math.toRadians(e));
        return GMST(julianUT1)+eqeq;
    }
    
}
