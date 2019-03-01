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
public final class FragmentDefault extends Fragment {
            
    // Initial
    private final double[] xold = new double[3];
    private final double[] deltav = new double[3];
    
    
    public FragmentDefault(FragmentOptions fragOptions, OdeAtmosphere atm, ODEOptions odeOptions) {
        super(fragOptions,atm,odeOptions);
    }
    
    @Override
    public void run() {
        for(int iter = 0; iter < 120000; iter++) {
            System.arraycopy(a, 0, aprev, 0, 3);
            System.arraycopy(x, 0, xold, 0, 3);
            calcA();
            stepSize();
            if(airspeed*dt > h) {
                dt = h/airspeed;
                x[0] += dt*v[0];
                x[1] += dt*v[1];
                x[2] += dt*v[2];
                this.time += dt;
                break;
            } else {
                for (int i = 0; i < 3; i++) {
                    x[i] += dt*(v[i] + (dt*0.16666666666666)*(deltav[i]+a[i]));
                    v[i] += (dt*0.5)*deltav[i];
                }
                this.time += dt;
            }
        }
    }
    
    @Override
    protected void stepSize() {
        deltav[0] = 3*a[0]-aprev[0];
        deltav[1] = 3*a[1]-aprev[1];
        deltav[2] = 3*a[2]-aprev[2];
        dt = tol*airspeed/(Math.abs(deltav[0])+Math.abs(deltav[1])+Math.abs(deltav[2])); // normally tolerance multiplied by airspeed but this saves a multiplication and error more sensitive at high speeds anyway
        if (dt > maxTimestep) {
            dt = maxTimestep;
        }
        if (dt < minTimestep) {
            dt = minTimestep;
        }
        
    } 
}
