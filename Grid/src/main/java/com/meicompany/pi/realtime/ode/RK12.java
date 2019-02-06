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
public class RK12 extends ODE {
    final double[] xa;
    final double[] xb;
    final double[] k1;
    final double[] k2;
    final double[] k3;
    final double[] e;
    final int n;
    
    public RK12(Dynamics dynamics, double[] x, ODEOptions options) {
        super(dynamics,x,options);
        n = x.length;
        xa = new double[n];
        xb = new double[n];
        k1 = new double[n];
        k2 = new double[n];
        k3 = new double[n];  
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
            h = dt*0.00390625;
            for(int i = 0; i < n; i++){
                xb[i] = h*(k1[i]+k2[i]*255);
                xa[i] = x[i] + xb[i];
            }
            System.arraycopy(dynamics.calc(xa, time+dt), 0, k3, 0, n);
            h = dt*0.001953125;
            for(int i = 0; i < n; i++){
                e[i] = h*(k3[i]-k1[i]);
                xb[i] = x[i] + xb[i]+e[i];
            }
            double err = Helper.norm(e);
            if (err > options.getTolerance()*dt) {
                dt *= 0.7071*Math.sqrt(options.getTolerance()*dt/err);
            } else {
                System.arraycopy(xa, 0, x, 0, n);
                System.arraycopy(k3, 0, k1, 0, n);
                time +=dt;
                break;
            }
        }
    }
}
