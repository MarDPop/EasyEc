/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.coordinates;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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
    public static final float EARTH_ROT_F = 7.29211505392569E-5f; // rad/s
    
    
    public static final double EARTH_POLAR_R_2 = 4.040829998408706e+13; //m2
    public static final double EARTH_EQUATOR_R_2 = 4.068063159076900e+13; //m2
    
    static final double A1 = 42697.67270715754; //a1 = a*E2
    static final double A2 = 1.8230912546075456E9; //a2 = A1*A1
    static final double A3 = 142.91722289812412; //a3 = A1*E2/2
    static final double A4 = 4.557728136518864E9; //a4 = 2.5*A2
    static final double A5 = 42840.589930055656; //a5 = A1+A3
    static final double A6 = 0.9933056200098622; //a6 = 1-E2
    public static final double E2 = 0.006694380066765; //WGS-84 first eccentricity squared
    public static final double ESECOND2 = 0.006739496819936; // second eccentricity
    
    static final float A1_F = 42697.67270715754f; //a1 = a*E2
    static final float A2_F = 1.8230912546075456E9f; //a2 = A1*A1
    static final float A3_F = 142.91722289812412f; //a3 = A1*E2/2
    static final float A4_F = 4.557728136518864E9f; //a4 = 2.5*A2
    static final float A5_F = 42840.589930055656f; //a5 = A1+A3
    static final float A6_F = 0.9933056200098622f; //a6 = 1-E2
    public static final float E2_F = 0.006694380066765f; //WGS-84 first eccentricity squared

    /**
     * Gets sea level at latitude assuming ellipsoid earth
     * @param latitude
     * @return
     */
    public static double seaLevel(double latitude) {
        double a = cos(latitude) * EARTH_EQUATOR_R;
        double b = sin(latitude) * EARTH_POLAR_R;
        a /= b;
        a *= a;
        return Math.sqrt( (a*EARTH_EQUATOR_R_2 + EARTH_POLAR_R_2)/(a+1));
    }
    
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
                
    public static double getWGS84Gravity(double latitude) {
        double s = Math.sin(latitude);
        s *= s;
        return 9.7803253359*(1+0.00193185265241*s)/Math.sqrt(1-E2*s);
    }


}
