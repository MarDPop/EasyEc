/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.coordinates.CoordinateException;
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
    
    public static final double TWOPI = 2*Math.PI;
    public static final double DEG2RAD = 180/Math.PI;

    
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
    
    public static double[] cross(double[] u, double[] v) {
        double[] out = new double[3];
        out[0] = u[1]*v[2] - u[2]*v[1];
        out[1] = u[2]*v[0] - u[0]*v[2];
        out[2] = u[0]*v[1] - u[1]*v[0];
        return out;
    }
    
    
    public static double[] impactLatLong(double[] impact) {
        //http://www.oc.nps.edu/oc2902w/coord/coordcvt.pdf
        double[] out = new double[2];
        double p = sqrt(impact[0]*impact[0]+impact[1]*impact[1]);
        double lambda = atan(impact[2]*CoordinateException.EARTH_EQUATOR_R/(p*CoordinateException.EARTH_POLAR_R));
        lambda *= 3;
        out[0] = atan((impact[2]+CoordinateException.e2prime*CoordinateException.EARTH_POLAR_R*sin(lambda))/(p*CoordinateException.e2*CoordinateException.EARTH_EQUATOR_R*cos(lambda)));
        out[1] = atan2(impact[1],impact[0])-impact[3]*CoordinateException.EARTH_ROT;
        return out;
    }
    
    
    public static double[] impact2xy(double[] impact) {
        double[] ll = CoordinateException.ecef2geo(impact);
        ll[1] -= impact[3]*CoordinateException.EARTH_ROT;
        return CoordinateException.ll2xy(ll);
    }
    
    public static double[] impactECEF2XY(double[] ecef) {
        double[] geo = CoordinateException.ecef2geo(ecef);
        double[] out = new double[2];
        out[0] = geo[1]*CoordinateException.lengthDegreeLong(geo[0])*DEG2RAD;
        out[1] = geo[0]*CoordinateException.lengthDegreeLat(geo[0])*DEG2RAD;
        return out;
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
