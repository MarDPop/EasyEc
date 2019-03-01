/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.generalMath;

import static java.lang.Math.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mpopescu
 */
public final class Math2 {
    
    public static final double PI_2 = Math.PI/2;
    public static final double TWOPI = 2*Math.PI;
    public static final double RAD2DEG = 180/Math.PI;
    public static final double DEG2RAD = Math.PI/180;
    public static final float PI_2_F = (float) Math.PI/2;
    public static final float TWOPI_F = (float) Math.PI*2;
    public static final float PI_F = (float) Math.PI;
    
    private Math2(){}
    
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
    public static float angleQuick(double[] u, double[] v) {
        return acos((float)(dot(u,v)/Math.sqrt(dot(u,u)*dot(v,v))));
    }
    
    /**
     * performs haversin function
     * @param A
     * @return 
     */
    public static double haversin(double A) {
        return (1-Math.cos(A))*0.5;
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
    public static float acos(float x) {
        return PI_2_F-asin(x);
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
        res += x*0.166666666f;
        if (x2 < -0.01) {
            x *= x2;
            res += x*8.333333333e-3f;
            if (x2 < -0.25f) {
                x *= x2;
                res += x*1.984126984126984e-04f;
                if (x < -0.8f) {
                    x *= x2;
                    res += x*2.755731922398589e-06f;
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
        float res=1+x2*0.5f;
        if (x2 < -0.01f) {
            x *= x2;
            res += x*4.166666666666e-2f;
            if (x2 < -0.16f) {
                x *= x2;
                res += x*1.38888888888e-3f;
                if (x2 < -0.5f) {
                    x *= x2;
                    res += x*2.480158730158730e-05f;
                    if (x2 < -1f) {
                        x *= x2;
                        res += x*2.480158730158730e-06f;
                    }
                }     
            }
        }
        return res;
    }
    
    // Don't use for angle close to pi/2 works well for angle < 0.6
    public static float tanFast(float x) {
        float x2 = x*x;
        float res = x;
        x *= x2;
        res += 0.334961658*x;
        x *= x2;
        res += 0.118066350*x;
        x *= x2;
        res += 0.092151584*x;
        return res;
    }
    
    // http://www2.mae.ufl.edu/~uhk/IEEETrigpaper8.pdf 4th order
    public static float tan(float x) {
        float x2 = x*x;
        return x*(1-0.095238095238095f*x2)/(1-0.428571428571429f*x2+0.009523809523810f*(x2*x2));
    }
    
    // Absolute error <= 6.7e-5
    public static float asin(float x) {
        float negate = 1;
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
     * 
     * @param arr
     * @return 
     */
    public static float[][] copy(float[][] arr){
        float[][] out = new float[arr.length][arr[0].length];
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
    
    /**
     * Attempt at a faster sqrt
     * @param S
     * @param guess
     * @return 
     */
    public static double halleySqrt(double S, double guess) {
        return 0.3333*(guess+8/(1/guess+3*guess/S));
    }
    
    /**
     * 
     * @param x
     * @return 
     */
    public static float rsqrt(float x){
        int temp = Float.floatToRawIntBits(x);
        temp = 0x5F1FFFF9 - (temp >> 1); // 0x4ebe6c0f  0x5F3759DF
        float newX = Float.intBitsToFloat(temp);
        return  0.703952253f * newX * (2.38924456f - x * newX * newX); // from http://rrrola.wz.cz/inv_sqrt.html
    }
    
    
}
