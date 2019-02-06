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
public class Earth {

    public static final double EARTH_MU = 3.986004418e14; // m3 s-2
    public static final double EARTH_AVG_R = 6371000; // m
    public static final double EARTH_AVG_D = 12742000; // m
    public static final double EARTH_POLAR_R = 6356752.314; // m
    public static final double EARTH_SIDEREAL = 86164.1; // m
    public static final double EARTH_FLATTENING = 0.003352810664747;  
    public static final double EARTH_EQUATOR_R = 6378137; // m
    public static final double EARTH_ROT = 7.29211505392569E-5; // rad/s
    
    static final double A1 = 42697.67270715754; //a1 = a*E2
    static final double A2 = 1.8230912546075456E9; //a2 = A1*A1
    static final double A3 = 142.91722289812412; //a3 = A1*E2/2
    static final double A4 = 4.557728136518864E9; //a4 = 2.5*A2
    static final double A5 = 42840.589930055656; //a5 = A1+A3
    static final double A6 = 0.9933056200098622; //a6 = 1-E2
    public static final double E2 = 0.006694380066765; //WGS-84 first eccentricity squared
    public static final double ESECOND2 = 0.006739496819936; // second eccentricity
    
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
