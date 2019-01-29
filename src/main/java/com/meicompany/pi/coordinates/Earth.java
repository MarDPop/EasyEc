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

    public static final double EARTH_AVG_R = 6371000;
    public static final double EARTH_POLAR_R = 6356752.314;
    public static final double e2prime = 0.006739496819936; // second eccentricity
    static final double a5 = 42840.589930055656; //a5 = a1+a3
    public static final double EARTH_SIDEREAL = 86164.1;
    static final double a2 = 1.8230912546075456E9; //a2 = a1*a1
    public static final double EARTH_F = 0.003352810664747;
    public static final double e2 = 0.006694380066765; //WGS-84 first eccentricity squared
    public static final double EARTH_EQUATOR_R = 6378137;
    public static final double EARTH_ROT = 7.29211505392569E-5;
    static final double a6 = 0.9933056200098622; //a6 = 1-e2
    static final double a4 = 4.557728136518864E9; //a4 = 2.5*a2
    static final double a3 = 142.91722289812412; //a3 = a1*e2/2
    static final double a1 = 42697.67270715754; //a1 = a*e2
    public static final double EARTH_AVG_D = 12742000;
    
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
}
