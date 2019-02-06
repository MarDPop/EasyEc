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
    public static final int J2000_JULIAN_TIME = 2451545; // days after 4763 BC
    public static final int JULIAN_DAY = 86400; //  julian day in seconds
    public static final int JULIAN_YEAR = 31557600; // julian year in seconds
    public static final int EPOCH_J2000 = 1;
    public static final int EPOCH_M50 = 2;
    public static final int EPOCH_GCRF = 3;
    public static final int EPOCH_MATLAB = 4;
    
    private Epoch(){}
    
    /**
     * 
     * @param julianUT1
     * @return 
     */
    public static double getGMST(double julianUT1) {
        //http://aa.usno.navy.mil/faq/docs/GAST.php
        int JD0 = (int)julianUT1;
        double H = (julianUT1-JD0)*24;
        double T = (julianUT1-J2000_JULIAN_TIME)/36525.0;
        return 6.697374558 + 0.06570982441908*(JD0-J2000_JULIAN_TIME) + 1.00273790935*H + 0.000026*T*T ;
    }
    
    /**
     * 
     * @param julianUT1
     * @return 
     */
    public static double getGAST(double julianUT1) {
        double D = julianUT1-J2000_JULIAN_TIME;
        double o = 125.04 - 0.052954*D;
        double L = 280.47 + 0.98565*D;
        double e = 23.4393 - 0.0000004*D;
        double d = -0.000319*Math.sin(Math.toRadians(o)) - 0.000024*Math.sin(Math.toRadians(2*L)); 
        double eqeq = d*Math.cos(Math.toRadians(e));
        return getGMST(julianUT1)+eqeq;
    }
    
    /**
     * 
     * @param j
     * @return 
     */
    public static int[] getGregorianYMDFromJulianDay(int j) {
        // All of these must be integer divisions
        int[] ymd = new int[3];
        int f = j+1401+(((4*j+274277)/146097)*3)/4-38;
        int e = 4*f+3;
        int g = (e % 1461)/4;
        int h = 5*g+2;
        ymd[2] = (h % 153)/5+1;
        ymd[1] = (h/153+2) % 12 + 1;
        ymd[0] = (e/1461)-4716+(12+2-ymd[1])/12;
        return ymd;
    }
    
    /**
     * 
     * @param y
     * @param m
     * @param d
     * @return 
     */
    public static int getJulianDayFromGregorianYMD(int y, int m, int d) {
        // All of these must be integer divisions
        int M2 = (m-14)/12;
        return (1461*(y+4800+M2))/4+(367*(m-2-12*M2))/4 - (3*(y+4900+M2/100))/4 + d - 32075;
    }
    
}
