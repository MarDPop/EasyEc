/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode;

import com.meicompany.pi.realtime.fragment.Fragment;

/**
 *
 * @author mpopescu
 */
public class FragmentOde {
    // Fragment
    Fragment frag;
    
    // Initial
    protected final double[] x = new double[3];
    protected final double[] xold = new double[3];
    protected final double[] v = new double[3]; 
    protected final double[] a = new double[3];
    
    // Time
    protected double dt;
    protected double dt2;
    protected double time;
    
    // Options
    protected double tol;
    protected double minTimestep;
    protected double maxTimestep;
    protected double tempOffset;
    
    // Parameters
    protected double h;
    protected double R;
    
    public static final int ITER_MAX = 50000;
    public static final double EARTH_MU = 3.986004418e14;
    
    public FragmentOde(){}
    
    public FragmentOde(double[] x, double[] v, Fragment frag, double time) {
        System.arraycopy(x, 0, this.x, 0, 3);
        System.arraycopy(v, 0, this.v, 0, 3);
        this.frag = frag;
        this.time = time;
        this.tol = 0.5;
    }
    
    public void setXV(double[] x, double[] v, double time) {
        System.arraycopy(x, 0, this.x, 0, 3);
        System.arraycopy(v, 0, this.v, 0, 3);
        this.time = time;
    }
    
    public double[] run() {
        for(int iter = 0; iter < ITER_MAX; iter++) {
            System.arraycopy(x, 0, xold, 0, 3);
            calcA();
            stepSize();
            x[0] += v[0]*dt+a[0]*dt2;
            x[1] += v[1]*dt+a[1]*dt2;
            x[2] += v[2]*dt+a[2]*dt2;
            if (stop()) {
                return groundImpact();
            } else {
                v[0] += a[0]*dt;
                v[1] += a[1]*dt;
                v[2] += a[2]*dt;
            }
            time += dt;
        }
        return new double[]{0, 0, 0, 0};
    }
    
    public void calcA() {
        this.R = norm(x);
        this.h = R-6371000;
        double g = -EARTH_MU/(R*R*R);
        a[0] = g*x[0];
        a[1] = g*x[1];
        a[2] = g*x[2];
    }
    
    public void setFrag(Fragment frag){
        this.frag = frag;
    }
    
    public void setA(double[] a) {
        System.arraycopy(a, 0, this.a, 0, 3);
    }
    
    public void setTempOffset(double tempOffset) {
        this.tempOffset = tempOffset;
    }
    
    public final boolean stop() {
        return (h < 0);
    }
    
    public void stepSize() {
        dt2 = tol*9.8;
        dt = Math.sqrt(2*dt2);
    }
    
    public double[] groundImpact() {
        x[0] -= xold[0];
        x[1] -= xold[1];
        x[2] -= xold[2];
        double delta = h/norm(x); // fair approx
        xold[0] += delta*x[0];
        xold[1] += delta*x[1];
        xold[2] += delta*x[2];
        return new double[] {xold[0],xold[1],xold[2],time+dt*delta} ;
    }
    
    protected static double norm(double[] v) {
        return Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
    }
}
