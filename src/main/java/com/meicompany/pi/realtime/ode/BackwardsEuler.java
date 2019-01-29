/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode;

/**
 *
 * @author mpopescu
 */
public class BackwardsEuler extends ODE {
    private double dt_small;
    final int n;
    final double[] x_dot;
    final double[] x_next;
    final double[] x_temp;
    final double[] e;
    
    private static final int MAXINNERLOOP = 10;
    
    public BackwardsEuler(Dynamics dynamics, double[] x, ODEOptions options) {
        super(dynamics,x,options);
        n = x.length;
        x_dot = new double[n];
        x_next = new double[n];
        x_temp = new double[n];
        e = new double[n];
        dt = 1;
    }
    
    @Override
    void step() {    
        
        for(int iter = 0; iter < 10; iter++) {
            System.arraycopy(dynamics.calc(x, time), 0, x_dot, 0, n);
            for(int i = 0; i < n; i++){
                x_next[i] = x[i] + dt*x_dot[i];
            }
            double max_err = 0;
            int iter2 = 0;
            while(iter2 < MAXINNERLOOP) {
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
                iter2++;
            }
            if (iter2 < MAXINNERLOOP) {
                dt_small = dt/2;
                
                System.arraycopy(dynamics.calc(x, time), 0, x_dot, 0, n);
                for(int i = 0; i < n; i++){
                    x_temp[i] = x[i] + dt_small*x_dot[i];
                }

                iter2 = 0;
                while(iter2 < MAXINNERLOOP) {
                    System.arraycopy(dynamics.calc(x_temp, time), 0, x_dot, 0, n);
                    max_err = 0;
                    for(int i = 0; i < n; i++){
                        double old = x_next[i];
                        x_temp[i] = x[i] + dt_small*x_dot[i];
                        old = Math.abs((old-x_temp[i])/x[i]);
                        if (old > max_err) {
                            max_err = old;
                        }
                    }
                    iter2++;
                }
                
                System.arraycopy(dynamics.calc(x_temp, time), 0, x_dot, 0, n);
                for(int i = 0; i < n; i++){
                    x_temp[i] += dt_small*x_dot[i];
                }

                iter2 = 0;
                while(iter2 < MAXINNERLOOP) {
                    System.arraycopy(dynamics.calc(x_temp, time), 0, x_dot, 0, n);
                    max_err = 0;
                    for(int i = 0; i < n; i++){
                        double old = x_temp[i];
                        e[i] = x_temp[i] + dt*x_dot[i];
                        old = Math.abs((old-e[i])/x_temp[i]);
                        if (old > max_err) {
                            max_err = old;
                        }
                    }
                    iter2++;
                }
                
                if (iter2 >= MAXINNERLOOP) {
                    dt/=2;
                }
            } else {
                dt /= 2;
            }
            
            max_err = 0;
            for(int i = 0; i < n; i++) {
                e[i] -= x_next[i];
                max_err += Math.abs(e[i]/x[i]);
            }
            if (max_err < options.getRelativeTolerance()) {
                break;
            }
        }
    }
    
    
}
