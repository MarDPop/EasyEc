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
public final class Fragment2ndOrder extends Fragment{

    public Fragment2ndOrder(FragmentOptions fragOptions, OdeAtmosphere atm, ODEOptions odeOptions) {
        super(fragOptions,atm,odeOptions);
    }
    
    @Override
    public void run() {
        calcA();
        for(int iter = 0; iter < 120000; iter++) {
            stepSize();
            if(airspeed*dt > h) {
                dt = h/airspeed;
                x[0] += dt*v[0];
                x[1] += dt*v[1];
                x[2] += dt*v[2];
                this.time += dt;
                break;
            } else {
                double dt2 = dt*0.5;
                x[0] += dt*(v[0]+ dt2*a[0]);
                x[1] += dt*(v[1]+ dt2*a[1]);
                x[2] += dt*(v[2]+ dt2*a[2]);
                System.arraycopy(a, 0, aprev, 0, 3);
                calcA();
                v[0] += dt2*(a[0]+aprev[0]);
                v[1] += dt2*(a[1]+aprev[1]);
                v[2] += dt2*(a[2]+aprev[2]); 
                this.time += dt;
            }
        }
    }
    /**
     * Gets step size
     */
    @Override
    protected void stepSize() {
        //dt = Math.pow(tol/(a[0]*a[0]+a[1]*a[1]+a[2]*a[2]),0.25); // tolerance is tol^2
        dt = tol*airspeed/Math.sqrt(a[0]*a[0]+a[1]*a[1]+a[2]*a[2]);
        if (dt > maxTimestep) {
            dt = maxTimestep;
        }
        if (dt < minTimestep) {
            dt = minTimestep;
        }
        
    }
    
}
