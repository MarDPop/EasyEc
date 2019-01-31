/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.coordinates.Coordinates;
import com.meicompany.pi.coordinates.Earth;
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
    
    public static final double PI_2 = Math.PI/2;
    public static final double TWOPI = 2*Math.PI;
    public static final double DEG2RAD = 180/Math.PI;

    
    private Helper(){}
    
    /**
     * Performs euclidean norm of v
     * @param v
     * @return 
     */
    public static double norm(double[] v) {
        return Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
    }
    
    /**
     * Unit vector of v
     * @param v
     * @return 
     */
    public static double[] normalize(double[] v) {
        return divide(v,norm(v));
    }
    
    /**
     * Divide v by scalar a
     * @param v
     * @param a
     * @return 
     */
    public static double[] divide(double[] v, double a) {
        double[] out = new double[3];
        out[0] = v[0]/a;
        out[1] = v[1]/a;
        out[2] = v[2]/a;
        return out;
    }
    
    /**
     * Multiply v by scalar a
     * @param v
     * @param a
     * @return 
     */
    public static double[] multiply(double[] v, double a) {
        double[] out = new double[3];
        out[0] = v[0]*a;
        out[1] = v[1]*a;
        out[2] = v[2]*a;
        return out;
    }
    
    /**
     * New double array of v + u
     * @param v
     * @param u
     * @return 
     */
    public static double[] add(double[] v, double[] u) {
        double[] out = new double[3];
        out[0] = v[0] + u[0];
        out[1] = v[1] + u[1];
        out[2] = v[2] + u[2];
        return out;
    }
    
    /**
     * New double array of v - u
     * @param v
     * @param u
     * @return 
     */
    public static double[] subtract(double[] v, double[] u) {
        double[] out = new double[3];
        out[0] = v[0] - u[0];
        out[1] = v[1] - u[1];
        out[2] = v[2] - u[2];
        return out;
    }
    
    /** 
     * performs dot product
     * @param u
     * @param v
     * @return 
     */
    public static double dot(double[] u, double[] v) {
        return u[0]*v[0]+u[1]*v[1]+u[2]*v[2];
    }
    
    /**
     * performs cross product
     * @param u
     * @param v
     * @return 
     */
    public static double[] cross(double[] u, double[] v) {
        double[] out = new double[3];
        out[0] = u[1]*v[2] - u[2]*v[1];
        out[1] = u[2]*v[0] - u[0]*v[2];
        out[2] = u[0]*v[1] - u[1]*v[0];
        return out;
    }
    
    /**
     * Gets the angle between u and v, with faster implementation accuracy is about 8e-5
     * @param u
     * @param v
     * @return 
     */
    public static double angleQuick(double[] u, double[] v) {
        return acos(dot(u,v)/Math.sqrt(dot(u,u)*dot(v,v)));
    }
    
    /**
     * performs haversin function
     * @param A
     * @return 
     */
    public static double haversin(double A) {
        return (1-cos(A))/2;
    }
    
    /**
     * Gets the angle between u and v
     * @param u
     * @param v
     * @return 
     */
    public static double angle(double[] u, double[] v) {
        return Math.atan2(norm(cross(u,v)),dot(u,v));
    }
    
    // faster implementation 
    public static double acos(double x) {
        return PI_2-asin(x);
    }
    
    // Absolute error <= 6.7e-5
    public static double asin(double x) {
        double negate = 1;
        if (x < 0) { 
            negate = -1;
        }
        x = abs(x);
        float ret = -0.0187293f;
        ret *= x;
        ret += 0.0742610f;
        ret *= x;
        ret -= 0.2121144f;
        ret *= x;
        ret += 1.5707288f;
        ret = (float) (PI_2 - sqrt(1.0 - x)*ret);
        return  ret*negate;
    }
    
    // Approximate acos(a) with relative error < 5.15e-3
    // This uses an idea from Robert Harley's posting in comp.arch.arithmetic on 1996/07/12
    // https://groups.google.com/forum/#!original/comp.arch.arithmetic/wqCPkCCXqWs/T9qCkHtGE2YJ
    public static float acosFast(float x){
        float r, s, t, u;
        t = (x < 0) ? (-x) : x;  // handle negative arguments
        u = 1.0f - t;
        s = (float) Math.sqrt (u + u);
        r = 0.10501094f * u * s + s;  // or fmaf (C * u, s, s) if FMA support in hardware
        if (x < 0) {
            return 3.14159265f - r;
        } else {
            return r;
        }
    }
    
    // super fast implementations with accuracy of 0.18 rad
    public static double acosFast2(double x) {
        return (-0.69813170079773212 * x * x - 0.87266462599716477) * x + 1.5707963267948966;
    }
    
    // fast implementations with accuracy of 0.00084 rad
    public static double atanQuick(double x){
        double xx = x*x;
        return ((0.0776509570923569*xx - 0.287434475393028)*xx + 0.995181681698119)*x;
    }
    
    // super fast implementations with accuracy of 0.0047 rad
    public static float atanFast(float x){
        return x/(1+0.2808f*x*x);
    }
    
    // super fast implementations with accuracy of 1% 
    public static float atan2Fast(float y, float x) {
        if (x != 0.0f){
            if (abs(x) > abs(y)) {
                float z = y / x;
                if (x > 0.0){
                    return atanFast(z);
                } else if (y >= 0.0){
                    return atanFast(z) + (float) PI;
                } else {
                    return atanFast(z) - (float) PI;
                }
            } else {
                float z = x / y;
                if (y > 0.0){
                    return -atanFast(z) + (float) PI_2;
                } else {
                    return -atanFast(z) - (float) PI_2;
                }
            }
        } else {
            if (y > 0.0f) {
                return (float)PI_2;
            } else if (y < 0.0f) {
                return (float) -PI_2;
            }
        }
        return 0.0f; 
    }
    
    /**
     * Gets the latitude and longitude from impact array produced by PiCalc
     * @param impact
     * @return 
     */
    public static double[] impactLatLong(double[] impact) {
        //http://www.oc.nps.edu/oc2902w/coord/coordcvt.pdf
        double[] out = new double[2];
        double p = sqrt(impact[0]*impact[0]+impact[1]*impact[1]);
        double lambda = atan(impact[2]*Earth.EARTH_EQUATOR_R/(p*Earth.EARTH_POLAR_R));
        lambda *= 3.0f;
        out[0] = atan((float) ((impact[2]+Earth.e2prime*Earth.EARTH_POLAR_R*sin(lambda))/(p*Earth.e2*Earth.EARTH_EQUATOR_R*cos(lambda))));
        out[1] = atan2(impact[1],impact[0])-impact[3]*Earth.EARTH_ROT;
        return out;
    }
    
    /**
     * Gets the 
     * @param impact
     * @return 
     */
    public static double[] impact2xy(double[] impact) {
        double[] ll = Coordinates.ecef2geo(impact);
        ll[1] -= impact[3]*Earth.EARTH_ROT;
        return Coordinates.ll2xy(ll);
    }
    
    public static double[] impactECEF2XY(double[] ecef) {
        double[] geo = Coordinates.ecef2geo(ecef);
        double[] out = new double[2];
        out[0] = geo[1]*Coordinates.lengthDegreeLong(geo[0])*DEG2RAD;
        out[1] = geo[0]*Coordinates.lengthDegreeLat(geo[0])*DEG2RAD;
        return out;
    }
    
    /**
     * prints array to csv
     * @param data
     * @param file 
     */
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
    
    /**
     * prints array to csv
     * @param runs
     * @param file 
     */
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
    
    /**
     * prints array to csv
     * @param runs
     * @param file 
     */
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
    
    /**
     * 
     * @param arr
     * @return 
     */
    public static double[][] copy(double[][] arr){
        double[][] out = new double[arr.length][arr[0].length];
        for(int i = 0; i < arr.length; i++){
            System.arraycopy(arr[i], 0, out[i], 0, arr[i].length);
        }
        return out;
    }
    
    /**
     * gets greatest common denominator of two integers
     * @param a
     * @param b
     * @return 
     */
    public static int gcd(int a, int b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }
    
    /**
     * Gets prime factors of number
     * @param number
     * @return 
     */
    public static ArrayList<Integer> primeFactors(int number) {
        int n = number;
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
