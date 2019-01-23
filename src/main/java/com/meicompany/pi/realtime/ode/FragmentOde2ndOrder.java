/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode;

import com.meicompany.pi.realtime.Helper;
import com.meicompany.pi.realtime.OdeAtmosphere;
import com.meicompany.pi.realtime.fragment.Fragment;
import static com.meicompany.pi.realtime.ode.FragmentOde.norm;
import static com.meicompany.pi.realtime.Helper.dot;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 *
 * @author mpopescu
 */
public class FragmentOde2ndOrder extends FragmentOde {
    private double airspeed;
    
    private final double speedSound_high;
    private final double speedSound_low;
    
    private final double[] densities;
    private final double[] temperatures;
    
    final double[] r_ = new double[3];
    final double[] e_ = new double[3];
    final double[] n_ = new double[3];
    
    private final double[] wind = new double[3];
    private final double[][] winds;
    private double rho = 0;
    
    final double[] a0 = new double[3];
    final double[] x0 = new double[3];
    final double[] v0 = new double[3];
    final double[] aprev = new double[3];
    
    private double small_dt;
    
    private double minTol;
    
    public FragmentOde2ndOrder(double[] x, double[] v, Fragment frag, double time) {
        super(x,v,frag,time);
        tempOffset = 0;
        OdeAtmosphere atm = new OdeAtmosphere("src/main/resources/altitudes2.csv",tempOffset,1);
        this.densities = atm.densities;
        this.temperatures = atm.temperatures;
        this.winds = atm.winds;
        this.dt = 2;
        this.maxTimestep = 10;
        this.minTimestep = 1e-6;
        this.tol = 1e-6;
        // Atm defaults
        this.speedSound_high = sqrt(401.37*(250+tempOffset));
        this.speedSound_low = sqrt(401.37*(280+tempOffset));
    }
    
    
    @Override
    public void setA(double[] a) {
        System.arraycopy(a, 0, this.aprev, 0, 3);
    }
    
    @Override 
    public double[] run() {
        for(int iter = 0; iter < ITER_MAX; iter++) {
            System.arraycopy(x, 0, xold, 0, 3);
            calcA();
            stepSize();
            for (int i = 0; i < 3; i++) {
                double da = (a[i]-aprev[i])*small_dt;
                x[i] = x0[i] + dt*(v0[i]+ a0[i]*small_dt + da/3);
                v[i] = v0[i] + a0[i]*dt + da;
            }
            if (stop()) {
                return groundImpact();
            } 
            System.arraycopy(a, 0, aprev, 0, 3);
            time += dt;
        }
        return new double[]{0, 0, 0, 0};
    }
    
    @Override
    public void calcA() {
        double R2 = x[0]*x[0]+x[1]*x[1]+x[2]*x[2];
        R = sqrt(R2);
        
        // Get unit vectors
        r_[0] = x[0]/R;
        r_[1] = x[1]/R;
        r_[2] = x[2]/R;
        h = atan2(x[1],x[0]);
        double cl = cos(h);
        double sl = sin(h);
        double b = Math.sqrt(1-r_[2]*r_[2]);
        e_[0] = -sl;
        e_[1] = cl;
        n_[0] = r_[2]*cl;
        n_[1] = r_[2]*sl;
        n_[2] = b;
        
        // Calculate Geopot Height
        b /= 6378137;
        h = r_[2]/6356752.3;
        h = 1/sqrt(b*b+h*h);
        
        h = (R-h)*h/R;
        
        // Atmospheric density and wind
        if (h > 1.2e5) {
            // rho is less than 1e-6
            double g = -EARTH_MU/R2;
            a[0] = g*r_[0];
            a[1] = g*r_[1];
            a[2] = g*r_[2];
            return;
        } else {
            if (h > 34080) {
                rho = 0.63*exp(-h*0.034167247386760/tempOffset); //already divided by 2 and 9.806/287
                wind[0] = 10*e_[0]+19*n_[0];
                wind[1] = 10*e_[1]+19*n_[1];
                wind[2] = 10*e_[2]+19*n_[2];
                b = speedSound_high;
            } else {
                if (h < 1.022401e3) {
                    rho = 0.63*exp(-h*0.034167247386760/tempOffset);
                    wind[0] = 1.5*e_[0]+2*n_[0];
                    wind[1] = 1.5*e_[1]+2*n_[1];
                    wind[2] = 1.5*e_[2]+2*n_[2];
                    b = speedSound_low;
                } else {
                    h /= 340.8;
                    int i = (int) h;
                    double delta = h - i;
                    i-=2;
                    rho = densities[i]+delta*(densities[i+1]-densities[i]);
                    b = winds[i][0];
                    h = winds[i][1];
                    wind[0] = b*e_[0]+h*n_[0];
                    wind[1] = b*e_[1]+h*n_[1];
                    wind[2] = b*e_[2]+h*n_[2];
                    b = temperatures[i];
                }
            }
        }
        
        double[] v_free = new double[3];
        v_free[0] = -v[0] - x[1]*7.29211505392569e-05 + wind[0];
        v_free[1] = -v[1] + x[0]*7.29211505392569e-05 + wind[1];
        v_free[2] = -v[2] + wind[2];
        
        airspeed = norm(v_free);
        double mach = airspeed/b;
        
        double drag = rho*airspeed/frag.bc(mach);
        double lift = drag*frag.l2d()*airspeed;
        lift -= EARTH_MU/R2;
        a[0] = drag*v_free[0]+lift*r_[0];
        a[1] = drag*v_free[1]+lift*r_[1];
        a[2] = drag*v_free[2]+lift*r_[2];
    }

    
    @Override
    public void stepSize() {
        short stepIteration = 0;
        // get initial state
        System.arraycopy(x, 0, x0, 0, 3);
        System.arraycopy(v, 0, v0, 0, 3);
        System.arraycopy(a, 0, a0, 0, 3);
        
        while(++stepIteration < 10) {
            small_dt = dt/2;
            // full step on velocity first
            double[] v_test  = Helper.add(v0, Helper.multiply(a0, dt));
            // half step
            for (int i = 0; i < 3; i++) {
                x[i] = x0[i] + small_dt*(v0[i]+ a0[i]*small_dt/2);
                v[i] = v0[i] + a0[i]*small_dt;
            }
            // recalc acceleration
            calcA();
            // half step
            double[] temp = Helper.multiply(a, small_dt);  
            v[0] += temp[0];
            v[1] += temp[1];
            v[2] += temp[2];
            // compare velocity
            double err = (Math.abs(v_test[0]-v[0])+Math.abs(v_test[1]-v[1])+Math.abs(v_test[2]-v[2]));
            if (err < tol) {
                if (err < minTol) {
                    dt *= 1.4;
                    if (dt > maxTimestep) {
                        dt = maxTimestep;
                        small_dt = dt/2;
                        break;
                    }
                } else {
                    break;
                }
            } else {
                dt *= 0.75;
                if (dt < minTimestep) {
                    dt = minTimestep;
                    small_dt = dt/2;
                    break;
                }
            }

        }
        
        
    }
}
