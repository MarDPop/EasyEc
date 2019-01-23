/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode;

import com.meicompany.pi.realtime.fragment.Fragment;
import static java.lang.Math.sqrt;
import static java.lang.Math.exp;

/**
 *
 * @author mpopescu
 */
public class FragmentOdeSimple extends FragmentOde {
    private double dragFactor;
    private double oldAirspeed;
    
    final double[] r_ = new double[3];
    final double[] e_ = new double[3];
    final double[] n_ = new double[3];
    
    public FragmentOdeSimple(double[] x, double[] v, Fragment frag, double time) {
        super(x,v,frag,time);
    }
    
    @Override
    public void calcA() {
        double R2 = x[0]*x[0]+x[1]*x[1]+x[2]*x[2];
        R = sqrt(R2);
        r_[0] = x[0]/R;
        r_[1] = x[1]/R;
        r_[2] = x[2]/R;

        h = (R-6371000)*6371000/R;
        
        double rho = 0.63*exp(-h*0.034167247386760/(280+tempOffset));
        
        double[] v_free = new double[3];
        v_free[0] = v[0] + x[1]*7.29211505392569e-05;
        v_free[1] = v[1] - x[0]*7.29211505392569e-05;
        v_free[2] = v[2];
        
        double airspeed = norm(v_free);
        double drag = rho*airspeed/frag.bcFast(airspeed);
        dragFactor = drag*airspeed;
        
        double lift = frag.l2d()*dragFactor;
        lift -= EARTH_MU/R2;
        a[0] = -drag*v_free[0]+lift*r_[0];
        a[1] = -drag*v_free[1]+lift*r_[1];
        a[2] = -drag*v_free[2]+lift*r_[2];
        
        oldAirspeed = airspeed;
    }

    @Override
    public void stepSize() {
        dt2 = tol*9.8/(dragFactor+0.1);
        dt = Math.sqrt(2*dt2);
        double d = 2*norm(a);
        if (d*dt > oldAirspeed) {
            dt = oldAirspeed/d;
        }
    }
}
