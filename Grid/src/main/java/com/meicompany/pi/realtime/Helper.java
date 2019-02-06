/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.coordinates.Earth;
import com.meicompany.pi.realtime.clustering.CentroidPi;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    public static final float PI_2_F = (float) Math.PI/2;
    public static final float TWOPI_F = (float) Math.PI*2;
    public static final float PI_F = (float) Math.PI;
    
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
        return (1-Math.cos(A))/2;
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
    
    
    /* TRIGNOMETRIC FUNCTIONS */
    // faster implementation 
    public static double acos(double x) {
        return PI_2-asin(x);
    }
    
    // faster implementation
    public static float sin(float x) {
        if(x < 0){
            return -sin(-x);
        }
        if(x > PI_2_F) {
            if(x > PI_F) {
                if(x > TWOPI_F) {
                    return sin(x % TWOPI_F);
                }
                return -sin(x-PI_F);
            }
            return sin(PI_F-x);
        }
        float res=x;
        float x2 = -x*x;
        x *= x2;
        res += x/6;
        if (x2 < -0.01) {
            x *= x2;
            res += x/120f;
            if (x2 < -0.25f) {
                x *= x2;
                res += x/5040f;
                if (x < -0.8f) {
                    x *= x2;
                    res += x/362880f;
                }
            }
        }
        return res;
    }
    
    // faster implementation
    public static float cos(float x) {
        x = abs(x);
        if(x > PI_2_F) {
            if(x > PI_F) {
                if(x > TWOPI_F) {
                    return cos(x % TWOPI_F);
                }
                return -cos(x-PI_F);
            }
            return -cos(PI_F-x);
        }
        float x2 = -x*x; 
        x = x2;
        float res=1+x2/2;
        if (x2 < -0.01f) {
            x *= x2;
            res += x/24f;
            if (x2 < -0.16f) {
                x *= x2;
                res += x/720f;
                if (x2 < -0.5f) {
                    x *= x2;
                    res += x/40320f;
                    if (x2 < -1f) {
                        x *= x2;
                        res += x/403200f;
                    }
                }     
            }
        }
        return res;
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
    
    // fast implementations with accuracy of 0.00084 rad
    public static float atan(float x){
        float xx = x*x;
        return ((0.0776509570923569f*xx - 0.287434475393028f)*xx + 0.995181681698119f)*x;
    }
    
    // super fast implementations with accuracy of 1% 
    public static float atan2(float y, float x) {
        if (x != 0.0f){
            if (abs(x) > abs(y)) {
                float z = y / x;
                if (x > 0.0){
                    return atan(z);
                } else if (y >= 0.0){
                    return atan(z) + (float) PI;
                } else {
                    return atan(z) - (float) PI;
                }
            } else {
                float z = x / y;
                if (y > 0.0){
                    return -atan(z) + (float) PI_2;
                } else {
                    return -atan(z) - (float) PI_2;
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
     * Gets the xy coordinates from impact which contains a time difference
     * @param impact
     * @return 
     */
    public static double[] impact2xy(double[] impact) {
        double[] ll = CoordinateFrame.ecef2geo(impact);
        ll[0] -= impact[3]*Earth.EARTH_ROT;
        return CoordinateFrame.ll2xy(ll);
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
     * @param data
     * @param file 
     */
    public static void printCsv(Collection<CentroidPi[]> data, String file){
        // ',' divides the word into columns
        try (FileWriter fw = new FileWriter(file); PrintWriter out = new PrintWriter(fw)) {
            // ',' divides the word into columns
            for (CentroidPi[] data2: data) {
                for (CentroidPi data1 : data2) {
                    out.print(data1.x_Center);
                    out.print(",");
                    out.print(data1.y_Center);
                    out.print(",");
                    out.print(data1.number);
                    out.print(",");
                    out.print(data1.sigma_x);
                    out.print(",");
                    out.print(data1.sigma_y);
                    out.println();
                }
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
    public static void printCsv(List<double[][]> runs, String file){
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
    public static void printCsv2(List<double[]> runs, String file){
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
    public static List<Integer> primeFactors(int number) {
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
