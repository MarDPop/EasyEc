/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode;

import com.meicompany.pi.realtime.Helper;

/**
 *
 * @author mpopescu
 */
public class RK23 extends GeneralOde {
    //https://en.wikipedia.org/wiki/Bogacki–Shampine_method
    final double[] xa;
    final double[] xb;
    final double[] k1;
    final double[] k2;
    final double[] k3;
    final double[] k4;
    final double[] e;
    final int n;
    
    public RK23(Dynamics dynamics, double[] x, double time_start, double time_final) {
        super(dynamics,x,time_start,time_final);
        n = x.length;
        xa = new double[n];
        xb = new double[n];
        k1 = new double[n];
        k2 = new double[n];
        k3 = new double[n];  
        k4 = new double[n];  
        e = new double[n];
        System.arraycopy(dynamics.calc(x, time), 0, k1, 0, n);
    }
    
    @Override
    void step() {
        for(int iter = 0; iter < 10; iter++) {
            double h = dt*0.5;
            for(int i = 0; i < n; i++){
                xa[i] = x[i] + k1[i]*h;
            }
            System.arraycopy(dynamics.calc(xa, time+h), 0, k2, 0, n);
            h = dt*0.75;
            for(int i = 0; i < n; i++){
                xa[i] = x[i] + h*k2[i];
            }
            System.arraycopy(dynamics.calc(xa, time+h), 0, k3, 0, n);
            h = dt*0.222222222222222;
            for(int i = 0; i < n; i++){
                xa[i] = x[i] + h*(k1[i] + 1.5*k2[i] + 2*k3[i]);
            }
            System.arraycopy(dynamics.calc(xa, time+dt), 0, k4, 0, n);
            for(int i = 0; i < n; i++){
                xb[i] = x[i] + dt*(k1[i]*0.29166666666666 + k2[i]*0.25 + k3[i]*0.333333333 +k4[i]*0.125);
            }
            for(int i = 0; i < n; i++){
                e[i] = xb[i] - xa[i];
            }
            double err = Helper.norm(e);
            if (err > tol) {
                dt *= 0.7071*Math.sqrt(tol*dt/err);
            } else {
                System.arraycopy(xa, 0, x, 0, n);
                System.arraycopy(k4, 0, k1, 0, n);
                time +=dt;
                break;
            }
        }
    }
}
