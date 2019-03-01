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
public final class FragmentAdaptive extends Fragment{
    
    // Initial
    private final double[] xold = new double[3];
    private final double[] vold = new double[3];
    
    private final double[] err = new double[3];
    private final double smallTol;
 
    public FragmentAdaptive(FragmentOptions fragOptions, OdeAtmosphere atm, ODEOptions odeOptions) {
        super(fragOptions,atm,odeOptions);
        smallTol = tol*5e-2;
    }
    
    @Override
    public void run() {
        for(int iter = 0; iter < 100000; iter++) {
            calcA();
            System.arraycopy(a, 0, aprev, 0, 3);
            System.arraycopy(x, 0, xold, 0, 3);
            System.arraycopy(v, 0, vold, 0, 3);
            stepSize();
            if (h < 0) {
                groundImpact();
                break;
            } 
        }
    }
    
    /**
     * Gets step size
     */
    @Override
    protected void stepSize() {
        
        for(int j = 0; j < 10; j++) {
            
            double dt2 = dt*0.5;
            double dt4 = dt*0.25;
            
            for (int i = 0; i < 3; i++) {
                // wind[i] = x[i]+ dt*(v[i] + dt2*aprev[i]);
                err[i] = vold[i] + dt*aprev[i];
                x[i] += dt2*(v[i] + dt4*aprev[i]);
                v[i] += dt2*aprev[i];
            }

            calcA();
            for (int i = 0; i < 3; i++) {
                v[i] += dt2*a[i];
                err[i] -= v[i];
            }
            
            // error
            rho = (Math.abs(err[0])+Math.abs(err[1])+Math.abs(err[2]))/airspeed;
            if (rho < tol) {
                dt4 *= 0.5;
                x[0] += dt2*(v[0] - dt4*a[0]);
                x[1] += dt2*(v[1] - dt4*a[1]);
                x[2] += dt2*(v[2] - dt4*a[2]);
                this.time += dt;
                if (dt < maxTimestep) {
                    if(rho < smallTol) {
                        dt *= 2.5; // smallTol 1/20 dt^2 = 16 so should be safe
                    } else {
                        dt *= 1.2;
                    }
                }
                break;
            } else {
                if (dt > minTimestep) {
                    System.arraycopy(xold, 0, x, 0, 3);
                    System.arraycopy(vold, 0, v, 0, 3);
                    dt *= 0.76;
                } else {
                    dt4 *= 0.5;
                    x[0] += dt2*(v[0] - dt4*a[0]);
                    x[1] += dt2*(v[1] - dt4*a[1]);
                    x[2] += dt2*(v[2] - dt4*a[2]);
                    this.time += dt;
                    break;
                }
            }
        }       
    }
    
}
