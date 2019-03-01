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
public final class FragmentAdaptiveHigher extends Fragment{
    
    // Initial
    private final double[] xold = new double[3];
    private final double[] vold = new double[3];
    private final double[] atemp = new double[3];
    
    private final double[] err = new double[3];
    private double dt2;
    private final double smallTol;
 
    public FragmentAdaptiveHigher(FragmentOptions fragOptions, OdeAtmosphere atm, ODEOptions odeOptions) {
        super(fragOptions,atm,odeOptions);
        this.smallTol = tol*0.05;
        dt2 = dt*0.5;
    }
    
    @Override
    public void run() {
        for(int iter = 0; iter < 120000; iter++) {
            System.arraycopy(a, 0, aprev, 0, 3);
            calcA();
            aprev[0] = (a[0]-aprev[0])/dt2;
            aprev[1] = (a[1]-aprev[1])/dt2;
            aprev[2] = (a[2]-aprev[2])/dt2;
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
        boolean finalStep = false;
        System.arraycopy(a, 0, atemp, 0, 3);
        for(int iter = 0; iter < 10; iter++) {
            dt2 = dt*0.5;
            double dt3 = dt*0.33333333;
            double dt4 = dt*0.25;
            for (int i = 0; i < 3; i++) {
                // wind[i] = x[i]+ dt*(v[i] + dt6*(rho+a[i]));
                err[i] = v[i] + dt*(atemp[i]+dt2*aprev[i]);
                x[i] += dt2*(v[i] + dt4*(atemp[i] + aprev[i]*dt3));
                v[i] += dt2*(atemp[i]+dt4*aprev[i]);
            }

            calcA();
            for (int i = 0; i < 3; i++) {
                wind[i] = 3*a[i]-atemp[i];
                v[i] += dt4*wind[i];
                err[i] -= v[i];
            }
            
            dt4 = (Math.abs(err[0])+Math.abs(err[1])+Math.abs(err[2]))/airspeed;
            if (dt4 < tol || finalStep == true) {
                double dt12 = dt*0.08333333333333;
                x[0] += dt2*(v[0] - dt12*(wind[0]+a[0]));
                x[1] += dt2*(v[1] - dt12*(wind[1]+a[1]));
                x[2] += dt2*(v[2] - dt12*(wind[2]+a[2]));
                this.time += dt;
                if(dt < maxTimestep) {
                    if(dt4 < smallTol) {
                        dt *= 4; 
                    } else {
                        dt *= 1.25;
                    }
                }
                break;
            } else {
                System.arraycopy(xold, 0, x, 0, 3);
                System.arraycopy(vold, 0, v, 0, 3);
                dt *= 0.82;
            }
            if (dt < minTimestep) {
                dt = minTimestep;
                finalStep = true;
            }
        }
        
        
    }
    
}
