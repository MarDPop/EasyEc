/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.grid.util.SparseFloat;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mpopescu
 */
public final class Helper {
    
    public static final double EARTH_SIDEREAL = 86164.1; 
    public static final double EARTH_ROT = 7.292115053925690e-05;
    public static final double TWOPI = 2*Math.PI;
    public static final double EARTH_AVG_R = 6371000;
    public static final double EARTH_AVG_D = 12742000;
    public static final double EARTH_F = 0.003352810664747;
    public static final double EARTH_POLAR_R = 6356752.314;
    public static final double EARTH_EQUATOR_R = 6378137;
    public static final double DEG2RAD = 180/Math.PI;

    private static final double e2 = 6.694380066765e-3;  //WGS-84 first eccentricity squared
    private static final double e2prime = 6.739496819936e-3;
    private static final double a1 = 4.2697672707157535e+4;  //a1 = a*e2
    private static final double a2 = 1.8230912546075455e+9;  //a2 = a1*a1
    private static final double a3 = 1.4291722289812413e+2;  //a3 = a1*e2/2
    private static final double a4 = 4.5577281365188637e+9;  //a4 = 2.5*a2
    private static final double a5 = 4.2840589930055659e+4;  //a5 = a1+a3
    private static final double a6 = 9.9330562000986220e-1;  //a6 = 1-e2
    
    private Helper(){}
    
    public static double norm(double[] v) {
        return Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
    }
    
    public static double[] normalize(double[] v) {
        return divide(v,norm(v));
    }
    
    public static double[] divide(double[] v, double a) {
        double[] out = new double[3];
        out[0] = v[0]/a;
        out[1] = v[1]/a;
        out[2] = v[2]/a;
        return out;
    }
    
    public static double[] multiply(double[] v, double a) {
        double[] out = new double[3];
        out[0] = v[0]*a;
        out[1] = v[1]*a;
        out[2] = v[2]*a;
        return out;
    }
    
    public static double[] add(double[] v, double[] u) {
        double[] out = new double[3];
        out[0] = v[0] + u[0];
        out[1] = v[1] + u[1];
        out[2] = v[2] + u[2];
        return out;
    }
    
    public static double[] subtract(double[] v, double[] u) {
        double[] out = new double[3];
        out[0] = v[0] - u[0];
        out[1] = v[1] - u[1];
        out[2] = v[2] - u[2];
        return out;
    }
    
    public static double dot(double[] u, double[] v) {
        return u[0]*v[0]+u[1]*v[1]+u[2]*v[2];
    }
    
    /*
    float InvSqrt(float x){
        float xhalf = 0.5f * x;
        int i = *(int*)&x;            // store floating-point bits in integer
        i = 0x5f3759df - (i >> 1);    // initial guess for Newton's method
        x = *(float*)&i;              // convert new bits into float
        x = x*(1.5f - xhalf*x*x);     // One round of Newton's method
        return x;
    }
    */
    
    public static double[] cross(double[] u, double[] v) {
        double[] out = new double[3];
        out[0] = u[1]*v[2] - u[2]*v[1];
        out[1] = u[2]*v[0] - u[0]*v[2];
        out[2] = u[0]*v[1] - u[1]*v[0];
        return out;
    }
    
    public static double seaLevel(double latitude) {
        double a = cos(latitude)/6378137;
        double b = sin(latitude)/6356752.3;
        return 1/sqrt(a*a+b*b);
    }
    
    public static double[][] convert2Rlonglat(double[] x, double[] y, double[] z, double[] time) {
        int n = x.length;
        double[][] out = new double[n][3];
        for(int i = 1; i < n; i++) {
            out[i] = convert2Rlonglat(x[i],y[i],z[i],time[i]);
        }
        return out;
    }
    
    public static double[] convert2Rlonglat(double x, double y, double z, double time) {
        double[] out = new double[3];
        out[0] = sqrt(x*x+y*y+z*z);
        out[1] = atan2(y,x) + Helper.EARTH_ROT*time;
        out[2] = asin(z/out[0]);
        return out;
    }
    
    public static double[] impactLatLong(double[] impact) {
        //http://www.oc.nps.edu/oc2902w/coord/coordcvt.pdf
        double[] out = new double[2];
        double p = sqrt(impact[0]*impact[0]+impact[1]*impact[1]);
        double lambda = atan(impact[2]*EARTH_EQUATOR_R/(p*EARTH_POLAR_R));
        lambda *= 3;
        out[0] = atan((impact[2]+e2prime*EARTH_POLAR_R*sin(lambda))/(p*e2*EARTH_EQUATOR_R*cos(lambda)));
        out[1] = atan2(impact[1],impact[0])-impact[3]*EARTH_ROT;
        return out;
    }
    
    public static double era(double UT1) {
        return TWOPI*(0.779057273264+1.00273781191135448*(UT1-2451545));
    }
    
    public static double[] flatEarthXY(double longitude, double latitude) {
        double[] out = new double[2];
        double R = seaLevel(latitude);
        out[1] = R*longitude;
        out[0] = R*cos(latitude)*longitude;
        return out;
    }
    
    public static double[] geodetic2ecef(double longitude, double latitude, double h) {
        double s = sin(latitude);
        double n = EARTH_EQUATOR_R/sqrt(1-e2*s*s);
        double[] ecef = new double[3];
        ecef[0] = (n+h)*cos(latitude)*cos(longitude);
        ecef[1] = ecef[0]*tan(longitude);
        ecef[2] = s*(n*(1-e2)+h);
        return ecef;
    }
    
    public static double lengthDegreeLat(double latitude) {
        return 111132.92 - 559.82*cos(2*latitude) + 1.175*cos(4*latitude) - 0.0023*cos(6*latitude);
    }
    
    public static double lengthDegreeLong(double latitude) {
        return 111412.84*cos(latitude) - 93.5*cos(3*latitude) + 0.118*cos(5*latitude);
    }
    
    public static double[] ll2xy(double[] ll){
        double[] out = new double[2];
        out[0] = (6.383485515566318e+06*cos(ll[0]) - 5.357155384473197e+03*cos(3*ll[0]) + 6.760901982543714*cos(5*ll[0]))*ll[1];
        out[1] = (6.367447280965017e+06*ll[0] - 1.603766164350688e+04*sin(2*ll[0]) + 16.830635231967932*sin(4*ll[0]) - 0.021963382146682*sin(6*ll[0]));
        return out;
    }
    
    public static double[] xy2ll(double[] xy) {
        double[] out = new double[2];
        out[0] = xy[1]/6371000;
        out[1] = xy[0]/(6.383485515566318e+06*cos(out[0]) - 5.357155384473197e+03*cos(3*out[0]) + 6.760901982543714*cos(5*out[0]));
        return out;
    }
    
    public static double[] impact2xy(double[] impact) {
        double[] ll = ecef2geo(impact);
        ll[1] -= impact[3]*EARTH_ROT;
        return ll2xy(ll);
    }
    
    public static double[] impactECEF2XY(double[] ecef) {
        double[] geo = ecef2geo(ecef);
        double[] out = new double[2];
        out[0] = geo[1]*lengthDegreeLong(geo[0])*DEG2RAD;
        out[1] = geo[0]*lengthDegreeLat(geo[0])*DEG2RAD;
        return out;
    }
    
    public static double[] ecef2geo( double[] ecef ){
        double g,rg,rf,u,v,m,f,p,x,y,z,zp,w2,w,r2,r,s2,c2,s,c,ss;
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
        rg = EARTH_EQUATOR_R/Math.sqrt( g );
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
        return( geo );    //Return Lat, Lon, Altitude in that order
    }
    
    public static double flatEarthDistance(double long1, double lat1,double long2, double lat2) {
        return EARTH_AVG_D*asin(sqrt(haversin(lat2-lat1)-cos(lat1)*cos(lat2)*haversin(long2-long1)));
    }
    
    public static double flatEarthDistance(double[] xyz1, double[] xyz2) {
        return EARTH_AVG_D*asin(sqrt((1-dot(xyz1,xyz2)))/EARTH_AVG_D*EARTH_AVG_R);
    }
    
    
    public static double vincentyFormulae(double long1, double lat1,double long2, double lat2) {
        double U1 = atan((1-EARTH_F)*tan(lat1));
        double U2 = atan((1-EARTH_F)*tan(lat2));
        double L = long2-long1;
        double l = L;
        double calpha,st,ct,d,a,b;
        calpha = st = ct = d = a = b = 0;
        for(int i = 0; i < 20; i++) {
            double su2 = sin(U2);
            double su1 = sin(U1);
            double cu2 = cos(U2);
            double cu1 = cos(U1);
            a = cu2*sin(l);
            b = cu1*su2-su1*cu2*cos(l);
            st = sqrt(a*a+b*b);
            ct = su2*su1+cu1*cu2*cos(l);
            a = atan2(st,ct);
            b = cu2*cu1*sin(l)/st;
            calpha = 1-b*b;
            d = ct-2*su1*su2/calpha;
            double C = EARTH_F/16*calpha*(4+EARTH_F*(4-3*calpha));
            l = L + (1-C)*EARTH_F*b*(a+C*st*(d+C*ct*(2*d-1)));
        }
        double u2 = calpha*(EARTH_EQUATOR_R*EARTH_EQUATOR_R/(EARTH_POLAR_R*EARTH_POLAR_R)-1);
        double A = 1-u2/16384*(4096+u2*(-768+u2*(320-175*u2)));
        double B = u2/1024*(256+u2*(-128+u2*(74-47*u2)));
        L = B*st*(d+0.25*B*(ct*(2*d-1)) - 0.1666*B*d*(-3+4*b*b)*(-3+4*d) );
        return EARTH_POLAR_R*A*(a-L);
    }
    
    public static double haversin(double A) {
        return (1-cos(A))/2;
    }
    
    public static void printCsv(double[][] data, String file){
        // ',' divides the word into columns
        try (FileWriter fw = new FileWriter(file); PrintWriter out = new PrintWriter(fw)) {
            // ',' divides the word into columns
            for (double[] data1 : data) {
                for (double data2 : data1) {
                    out.print(data2);
                    out.print(",");
                }
                out.println();
            }
            //Flush the output to the file
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void printCsv(ArrayList<double[][]> runs, String file){
        try (FileWriter fw = new FileWriter(file); PrintWriter out = new PrintWriter(fw)) {
            for(double[][] data : runs) {
                for (double[] data1 : data) {
                    for (double data2 : data1) {
                        out.print(data2);
                        out.print(",");
                    }
                    out.println();
                }
            }
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void printCsv2(ArrayList<double[]> runs, String file){
        try (FileWriter fw = new FileWriter(file); PrintWriter out = new PrintWriter(fw)) {
            for (double[] data1 : runs) {
                for (double data2 : data1) {
                    out.print(data2);
                    out.print(",");
                }
                out.println();
            }
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static double[][] copy(double[][] arr){
        double[][] out = new double[arr.length][arr[0].length];
        for(int i = 0; i < arr.length; i++){
            System.arraycopy(arr[i], 0, out[i], 0, arr[i].length);
        }
        return out;
    }
    
    public static int gcd(int a, int b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }
    
    public static ArrayList<Integer> primeFactors(int numbers) {
        int n = numbers;
        ArrayList<Integer> factors = new ArrayList<>();
        for (int i = 2; i <= n / i; i++) {
            while (n % i == 0) {
                factors.add(i);
                n /= i;
            }
        }
        if (n > 1) {
            factors.add(n);
        }
        return factors;
    }
    
}
