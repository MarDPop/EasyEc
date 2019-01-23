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
public class BackwardsEuler extends GeneralOde {
    final int n;
    final double[] x_next;
    
    public BackwardsEuler(Dynamics dynamics, double[] x, double time_start, double time_final) {
        super(dynamics,x,time_start,time_final);
        n = x.length;
        x_next = new double[n];
    }
    
    @Override
    void step() {    
        
        for(int iter = 0; iter < 10; iter++) {
            System.arraycopy(dynamics.calc(x, time), 0, x_dot, 0, n);
            for(int i = 0; i < n; i++){
                x_next[i] = x[i] + dt*x_dot[i];
            }
            double max_err = 0;
            for(int iter2 = 0; iter2 < 10; iter2++) {
                System.arraycopy(dynamics.calc(x_next, time), 0, x_dot, 0, n);
                max_err = 0;
                for(int i = 0; i < n; i++){
                    double old = x_next[i];
                    x_next[i] = x[i] + dt*x_dot[i];
                    old = Math.abs((old-x_next[i])/x[i]);
                    if (old > max_err) {
                        max_err = old;
                    }
                }
                
            }

            if (max_err < tol) {
                break;
            }
        }
    }
    
    
}
