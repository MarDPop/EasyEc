/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode;

import com.meicompany.pi.realtime.OdeAtmosphere;
import com.meicompany.pi.realtime.fragment.Fragment;
import static com.meicompany.pi.realtime.ode.FragmentOde.norm;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 *
 * @author mpopescu
 */
public class FragmentOdeQuickerStep extends FragmentOde {
    private double airspeed;
    
    private final double temp_high;
    private final double temp_low;
    private final double speedSound_high;
    private final double speedSound_low;
    
    private final double[] densities;
    private final double[] soundSpeed;
    private final double[][] winds;
    
    private final double[] wind = new double[3];
    private double rho = 0;
    
    final double[] r_ = new double[3];
    final double[] e_ = new double[3];
    final double[] n_ = new double[3];
    
    final double[] aprev = new double[3];
    //final double[] da = new double[3];
    
    public FragmentOdeQuickerStep(double[] x, double[] v, Fragment frag, double time) {
        super(x,v,frag,time);
        tempOffset = 0;
        OdeAtmosphere atm = new OdeAtmosphere("src/main/resources/altitudes2.csv",tempOffset,1);
        this.densities = atm.densities;
        this.soundSpeed = atm.speedSound;
        this.winds = atm.winds;
        this.dt = 2;
        this.maxTimestep = 10;
        this.minTimestep = 1e-5;
        this.tol = 4e-4;
        // Atm defaults
        this.temp_low = 287+tempOffset;
        this.temp_high = 216.7+tempOffset;
        this.speedSound_high = sqrt(401.37*temp_high);
        this.speedSound_low = sqrt(401.37*temp_low);
    }
    
    @Override
    public void setA(double[] a) {
        System.arraycopy(a, 0, this.aprev, 0, 3);
    }
    
    @Override 
    public double[] run() {
        for(int iter = 0; iter < ITER_MAX; iter++) {
            System.arraycopy(a, 0, aprev, 0, 3);
            System.arraycopy(x, 0, xold, 0, 3);
            calcA();
            stepSize();
            for (int i = 0; i < 3; i++) {
                x[i] += dt*(v[i]+ (dt/6)*(4*a[i]-aprev[i]));
                v[i] += (dt/2)*(3*a[i] - aprev[i]);
            }
            if (h < 0) {
                return groundImpact();
            } 
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
                rho = 0.63*exp(-h*0.034167247386760/temp_high); //already divided by 2 and 9.806/287
                wind[0] = 10*e_[0]+19*n_[0];
                wind[1] = 10*e_[1]+19*n_[1];
                wind[2] = 10*e_[2]+19*n_[2];
                b = speedSound_high;
            } else {
                if (h < 1.022401e3) {
                    rho = 0.63*exp(-h*0.034167247386760/temp_low);
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
                    b = soundSpeed[i];
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
        dt = Math.sqrt(tol/(Math.abs(a[0]-aprev[0])+Math.abs(a[1]-aprev[1])+Math.abs(a[2]-aprev[2])));
        if (dt > maxTimestep) {
            dt = maxTimestep;
        }
        if (dt < minTimestep) {
            dt = minTimestep;
        }
    }
}
