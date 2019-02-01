/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.coordinates;

import com.meicompany.pi.realtime.Helper;

/**
 *
 * @author mpopescu
 */
public class Earth {

    public static final double EARTH_MU = 3.986004418e14; // m3 s-2
    public static final double EARTH_AVG_R = 6371000; // m
    public static final double EARTH_AVG_D = 12742000; // m
    public static final double EARTH_POLAR_R = 6356752.314; // m
    public static final double EARTH_SIDEREAL = 86164.1; // m
    public static final double EARTH_FLATTENING = 0.003352810664747;  
    public static final double EARTH_EQUATOR_R = 6378137; // m
    public static final double EARTH_ROT = 7.29211505392569E-5; // rad/s
    
    static final double a1 = 42697.67270715754; //a1 = a*e2
    static final double a2 = 1.8230912546075456E9; //a2 = a1*a1
    static final double a3 = 142.91722289812412; //a3 = a1*e2/2
    static final double a4 = 4.557728136518864E9; //a4 = 2.5*a2
    static final double a5 = 42840.589930055656; //a5 = a1+a3
    static final double a6 = 0.9933056200098622; //a6 = 1-e2
    public static final double e2 = 0.006694380066765; //WGS-84 first eccentricity squared
    public static final double e2prime = 0.006739496819936; // second eccentricity
    
    private double time;
    
    public Earth(double time) {
        this.time = time;
    }
    
    public double changeTime(double dt) {
        this.time += dt;
        return time;
    }
    
    public double getTime() {
        return time;
    }
    
    public void setTime(double time) {
        this.time = time;
    }
    
    public static double getObliquity(double UT1time) {
        double t = UT1time/Epoch.JULIAN_YEAR/10000;
        double out = 0.409092804222329 - 0.022693789043161*t;
        t *= t;
        out -= 7.514612057197808e-06*t;
        t *= t;
        out += 0.009692637519582*t;
        t *= t;
        out -= 2.490972693540796e-04*t;
        t *= t;
        out -= 0.001210434317626*t;
        t *= t;
        out -= 1.893197424732738e-04*t;
        t *= t;
        out += 3.451873409499897e-05*t;
        t *= t;
        out += 1.351175729252277e-04*t;
        t *= t;
        out += 2.807071213624213e-05*t;
        t *= t;
        out += 1.187793518718363e-05*t;
        return out;
    }
    
    public static double getIGF67Gravity(double latitude) {
        double c = Math.cos(2*latitude);
        return 9.780327*(1.0026454-0.0026512*c+0.0000058*c*c);
    }        
            
    public static double getWGS84Gravity(double latitude) {
        double s = Math.sin(latitude);
        s *= s;
        return 9.7803253359*(1+0.00193185265241*s)/Math.sqrt(1-e2*s);
    }
    
    public static double freeAirCorrection(double h) {
        return 3.086e-6*h;
    }
    
    public static double getWGS84Gravity(double[] ecef) {
        return 0;
    }
    
    public static double[] getEGM96Gravity(double[] ecef) {
        return new double[]{};
    }
    
    /**
     * Gets the magnetic strength of the earth's dipole magnetism. 
     * @param r radial distance to center of earth in meters
     * @param angle angle between magnetic north pole
     * @return 
     */
    public static double getEarthDipoleMagneticStrength(double r, double angle) {
        double R_E = EARTH_AVG_R/r;
        double c = Math.cos(angle);
        return 3.12e-5*R_E*R_E*R_E*Math.sqrt(1+3*c*c);
    }
    
    /** 
     * the Geomagnetic dipole in ecef coordinates with magnitude equal to dipole. 
     * 
     * @param UT1time
     * @return 
     */
    public static double[] getEarthDipoleVector(double UT1time) {
        int[] ymd = Epoch.getGregorianYMDFromJulianDay((int) UT1time);
        return new double[]{0.015227442576372e-4,  -0.049191918858826e-4,   0.307721107679616e-4};
        
    }
    
    /** 
     * Returns the Magnetic Dipole of earth at time set
     * @return 
     */
    public double[] getEarthDipoleVector() {
        return getEarthDipoleVector(time);
    }
    
    /**
     * Gets the magnetic vector of the earth's dipole magnetism. 
     * @param ecef
     * @return 
     */
    public double[] getEarthMagneticStrength(double[] ecef) {
        double angle = Helper.angleQuick(ecef,getEarthDipoleVector()); // quick angle used here since accuracy isn't crucial
        double r = EARTH_AVG_R/Helper.norm(ecef);
        double B_r = -6.24e-5*r*r*r*Math.cos(angle);
        return new double[]{};
    }
}
