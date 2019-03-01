/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.fragment;

import com.meicompany.pi.realtime.ode.ODEOptions;
import com.meicompany.pi.realtime.ode.util.OdeAtmosphere;

/**
 *
 * @author mpopescu
 */
public final class FragmentRK12 extends Fragment{
    
    final double[] xold = new double[3];
    final double[] dx = new double[3];
    
    final double[] xk2 = new double[3];
    final double[] xk3 = new double[3];
    
    final double[] vold = new double[3];
    final double[] dv = new double[3];
    
    final double[] vk1 = new double[3];
    final double[] vk2 = new double[3];
    final double[] vk3 = new double[3];

    public FragmentRK12(FragmentOptions fragOptions, OdeAtmosphere atm, ODEOptions odeOptions) {
        super(fragOptions,atm,odeOptions);
    }
    
    @Override
    public void run() {
        dt = 0.001;
        for(int iter = 0; iter < 40000; iter++) { 
            stepSize();
            if (h < 0) {
                groundImpact();
                break;
            } 
        }
    }
    
    @Override
    protected void stepSize() {
        double del = tol;
        calcA();
        System.arraycopy(x, 0, xold, 0, 3);
        System.arraycopy(v, 0, vold, 0, 3);
        System.arraycopy(a, 0, aprev, 0, 3);
        for(int y = 0; y < 8; y++) {
            del = dt*0.5; // small opportunity to pre multiply k1 ...
            for(int i = 0; i < 3; i++){
                x[i] = xold[i] + vold[i]*del;
                v[i] = vold[i] + aprev[i]*del;
            }
            calcA();
            System.arraycopy(v, 0, xk2, 0, 3);
            System.arraycopy(a, 0, vk2, 0, 3);
            del = dt*0.00390625;
            for(int i = 0; i < 3; i++){
                dx[i] = del*(vold[i]+xk2[i]*255);
                x[i] = xold[i] + dx[i];
                dv[i] = del*(aprev[i]+vk2[i]*255);
                v[i] = vold[i] + dv[i];
            }
            calcA();
            System.arraycopy(v, 0, xk3, 0, 3);
            System.arraycopy(a, 0, vk3, 0, 3);
            del = dt*0.001953125;
            for(int i = 0; i < 3; i++){
                dv[i] = del*(vk3[i]-aprev[i]);
            }
            del = (Math.abs(dv[0])+Math.abs(dv[1])+Math.abs(dv[2]))/airspeed;
            
            if (del < tol) {
                time += dt;
                break;
            }
            dt *= 0.9*Math.sqrt(tol/del);
            if (dt < minTimestep) {
                dt = minTimestep;
                break;
            }
        }
        dt *= 0.9*Math.sqrt(tol/del);
        if (dt > maxTimestep) {
            dt = maxTimestep;
        }
        
    }
    
    
    
}
