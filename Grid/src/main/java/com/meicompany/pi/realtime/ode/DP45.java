/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode;

import com.meicompany.pi.realtime.generalMath.Math2;

/**
 *
 * @author mpopescu
 */
public class DP45 extends ODE {
    //https://en.wikipedia.org/wiki/Bogacki–Shampine_method
    final double[] xa;
    final double[] xb;
    final double[] k1;
    final double[] k2;
    final double[] k3;
    final double[] k4;
    final double[] k5;
    final double[] k6;
    final double[] k7;
    final double[] e;
    final int n;
    
    public DP45(Dynamics dynamics, double[] x, ODEOptions options) {
        super(dynamics,x,options);
        n = x.length;
        xa = new double[n];
        xb = new double[n];
        k1 = new double[n];
        k2 = new double[n];
        k3 = new double[n];  
        k4 = new double[n];  
        k5 = new double[n]; 
        k6 = new double[n]; 
        k7 = new double[n]; 
        e = new double[n];
        System.arraycopy(dynamics.calc(x, time), 0, k1, 0, n);
    }
    
    @Override
    void step() {
        for(int iter = 0; iter < 10; iter++) {
            double h = dt*0.2;
            for(int i = 0; i < n; i++){
                xa[i] = x[i] + k1[i]*h;
            }
            System.arraycopy(dynamics.calc(xa, time+h), 0, k2, 0, n);
            h = dt*0.075;
            for(int i = 0; i < n; i++){
                xa[i] = x[i] + h*(k1[i]+3*k2[i]);
            }
            System.arraycopy(dynamics.calc(xa, time+dt*0.3), 0, k3, 0, n);
            h = dt*0.022222222222222;
            for(int i = 0; i < n; i++){
                xa[i] = x[i] + h*(44*k1[i] - 168*k2[i] + 160*k3[i]);
            }
            System.arraycopy(dynamics.calc(xa, time+dt*0.8), 0, k4, 0, n);
            h = dt/6561;
            for(int i = 0; i < n; i++){
                xa[i] = x[i] + h*(k1[i]*19372 - k2[i]*76080 + k3[i]*64448 - k4[i]*1908);
            }
            System.arraycopy(dynamics.calc(xa, time+dt*0.888888888), 0, k5, 0, n);
            for(int i = 0; i < n; i++){
                xa[i] = x[i] + dt*(k1[i]*2.846275252525253 - k2[i]*10.757575757575758 + k3[i]*8.906422717743473 + k4[i]*0.278409090909091 - k5[i]*0.273531303602058);
            }
            System.arraycopy(dynamics.calc(xa, time+dt), 0, k6, 0, n);
            for(int i = 0; i < n; i++){
                xa[i] = x[i] + dt*(k1[i]*0.091145833333333 + k3[i]*0.449236298292902 + k4[i]*0.651041666666667 - k5[i]*0.322376179245283 + k6[i]*0.130952380952381);
            }
            System.arraycopy(dynamics.calc(xa, time+dt), 0, k7, 0, n);
            for(int i = 0; i < n; i++){
                xb[i] = x[i] + dt*(k1[i]*0.089913194444444 + k3[i]*0.453489068583408 + k4[i]*0.6140625 - k5[i]*0.271512382075472 + k6[i]*0.089047619047619 + k7[i]*0.025);
            }
            for(int i = 0; i < n; i++){
                e[i] = xb[i] - xa[i];
            }
            double err = Math2.norm(e);
            if (err > options.getTolerance()) {
                dt *= 0.84*Math.sqrt(Math.sqrt(options.getTolerance()*dt/err));
            } else {
                System.arraycopy(xa, 0, x, 0, n);
                System.arraycopy(k7, 0, k1, 0, n);
                time +=dt;
                dt *= 1.2;
                break;
            }
        }
    }
}

