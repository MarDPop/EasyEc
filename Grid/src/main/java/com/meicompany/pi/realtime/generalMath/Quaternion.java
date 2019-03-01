/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.generalMath;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 *
 * @author mpopescu
 */
public class Quaternion {
    public double a;
    public double b; //i
    public double c; //j
    public double d; //k
    
    public Quaternion(){};
    
    public Quaternion(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
    
    public Quaternion(Quaternion x) {
        this.a = x.a;
        this.b = x.b;
        this.c = x.c;
        this.d = x.d;
    }
    
    public Quaternion(double[] x) {
        this.a = x[0];
        this.b = x[1];
        this.c = x[2];
        this.d = x[3];
    }
    
    public Quaternion(double angle, Vector3 axis) {
        double s = angle/2;
        this.a = cos(s);
        s = sin(s);
        this.b = s*axis._1;
        this.c = s*axis._2;
        this.d = s*axis._3;
    }
    
    public Quaternion(double yaw, double pitch, double roll) {
        // Roll Pitch Yaw
        double cy = cos(yaw*0.5);
        double sy = sin(yaw*0.5);
        double cp = cos(pitch*0.5);
        double sp = sin(pitch*0.5);
        double cr = cos(roll*0.5);
        double sr = sin(roll*0.5);
        
        this.a = cy*cp*cr + sy*sp*sr;
        this.b = cy*cp*sr - sy*sp*cr;
        this.c = sy*cp*sr + cy*sp*cr;
        this.d = sy*cp*cr - cy*sp*sr;
    }
    
    public void add(Quaternion x) {
        this.a += x.a;
        this.b += x.b;
        this.c += x.c;
        this.d += x.d;
    }
    
    public void subtract(Quaternion x) {
        this.a -= x.a;
        this.b -= x.b;
        this.c -= x.c;
        this.d -= x.d;
    }
    
    public void multiply(double lambda){
        this.a *= lambda;
        this.b *= lambda;
        this.c *= lambda;
        this.d *= lambda;
    }
    
    public void divide(double lambda){
        this.a /= lambda;
        this.b /= lambda;
        this.c /= lambda;
        this.d /= lambda;
    }
    
    public Quaternion product(Quaternion x) {
        // hamiltoniion product
        Quaternion out = new Quaternion();
        out.a = a*x.a-b*x.b-c*x.c-d*x.d;
        out.b = a*x.b+b*x.a+c*x.d-d*x.c;
        out.c = a*x.c-b*x.d+c*x.a+d*x.b;
        out.d = a*x.d+b*x.c-c*x.b+d*x.a;
        return out;
    }
    
    public double mag(){
        return a*a+b*b+c*c+d*d;
    }
    
    public double norm() {
        return Math.sqrt(mag());
    }
    
    public Quaternion reciprocal() {
        Quaternion out = conjugate();
        out.divide(this.mag());
        return out;
    }
    
    public Quaternion unit() {
        Quaternion out = new Quaternion(this);
        out.divide(this.norm());
        return out;
    }
    
    public Quaternion conjugate() {
        Quaternion out = new Quaternion();
        out.a = a;
        out.b = -b;
        out.c = -c;
        out.d = -d;
        return out;
    }
    
    public double dot(Quaternion x){
        return a*x.a+b*x.b+c*x.c+d*x.d;
    }
    
    public double scalar(){
        return a;
    }
    
    public Vector3 vector() {
        return new Vector3(b,c,d);
    }
    
    public Vector3 toEulerAngles() {
        Vector3 x = new Vector3();
        x._1 = Math.atan2(2*(a*b+c*d), (1-2*(b*b+c*c))); // X (Roll)
        x._2 = Math.asin(2*(a*c-d*b)); // Y (Pitch) Not gimble lock safe
        x._3 = Math.atan2(2*(a*d+b*c), (1-2*(d*d+c*c))); // Z (Yaw) 
        return x;
    }
}
