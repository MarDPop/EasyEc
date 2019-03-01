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
public final class FragmentRK23 extends Fragment{
    
    final double[] xold = new double[3];
    final double[] dx = new double[3];
    
    final double[] xk2 = new double[3];
    final double[] xk3 = new double[3];
    final double[] xk4 = new double[3];
    
    final double[] vold = new double[3];
    final double[] dv = new double[3];
    
    final double[] vk2 = new double[3];
    final double[] vk3 = new double[3];
    final double[] vk4 = new double[3];

    public FragmentRK23(FragmentOptions fragOptions, OdeAtmosphere atm, ODEOptions odeOptions) {
        super(fragOptions,atm,odeOptions);
    }
    
    @Override
    public void run() {
        // initialize k1
        dt = Math.abs(v[0])+Math.abs(v[1])+Math.abs(v[2])/5000;
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
            del = dt*0.75;
            for(int i = 0; i < 3; i++){
                x[i] = xold[i] + xk2[i]*del;
                v[i] = vold[i] + vk2[i]*del;
            }
            calcA();
            System.arraycopy(v, 0, xk3, 0, 3);
            System.arraycopy(a, 0, vk3, 0, 3);
            for(int i = 0; i < 3; i++){
                dx[i] = dt*(vold[i]*0.2222222+xk2[i]*0.3333333+xk3[i]*0.444444);
                x[i] = xold[i] + dx[i];
                dv[i] = dt*(aprev[i]*0.2222222+vk2[i]*0.3333333+vk3[i]*0.444444);
                v[i] = vold[i] + dv[i];
            }
            
            calcA();
            System.arraycopy(v, 0, xk4, 0, 3);
            System.arraycopy(a, 0, vk4, 0, 3);
            for(int i = 0; i < 3; i++){
                dv[i] -= dt*(aprev[i]*0.29166666+vk2[i]*0.25+vk3[i]*0.333333+vk4[i]*0.125);
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
