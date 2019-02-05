/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.fragment;

import com.meicompany.pi.realtime.ode.util.OdeAtmosphere;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import java.util.Random;

/**
 *
 * @author mpopescu
 */
public final class FragmentWithOde {
    private final Random rand = new Random();
            
    private double BC;
    private double explosionVelocity;
    private double lift2drag;
    
    private final double[] machTable = new double[]{0.3, 0.5, 0.8, 0.9, 1, 1.4, 2, 4, 5, 10};
    private final double[] bc2mach = new double[]{1.0000000,0.971428592,0.886956541,0.85955058,0.711627922,0.528497421,0.488038288,0.525773207,0.512562825,0.506622527};
    private final double[] dBdM = new double[] {-0.14285704,-0.281573503,-0.27405961,-1.47922658,-0.457826253,-0.067431888,0.01886746,-0.013210382,-0.00118806};

    private final double[] sigma_l2d = new double[] {0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.8};
    
    // Initial
    private final double[] x = new double[3];
    private final double[] xold = new double[3];
    private final double[] v = new double[3]; 
    private final double[] a = new double[3];
    private final double[] aprev = new double[3];
    
    // Time
    private double dt;
    private double time;
    private double initialTime;
    
    // Options
    private double tol;
    private double minTimestep;
    private double maxTimestep;
    
    // Parameters
    private double h;
    private double R;
    private double airspeed;
    
    public static final double EARTH_MU = 3.986004418e14;
    public static final double TWOPI = 2*Math.PI;
    
    private double temp_high;
    private double temp_low;
    private double speedSound_high;
    private double speedSound_low;
    
    private double[] densities;
    private double[] soundSpeed;
    private double[][] winds;
    
    private final double[] wind = new double[3];
    private double rho = 0;
    
    final double[] r_ = new double[3];
    final double[] e_ = new double[3];
    final double[] n_ = new double[3];
    
    public FragmentWithOde(double BC, double explosionVelocity, double lift2drag, OdeAtmosphere atm) {
        this(atm);
        this.BC = BC;
        this.explosionVelocity = explosionVelocity;
        this.lift2drag = lift2drag;
    }
 
    public FragmentWithOde(OdeAtmosphere atm) {
        generatePseudo();
        // Atm
        this.densities = atm.densities;
        this.soundSpeed = atm.speedSound;
        this.winds = atm.winds;
        // Time Defaults
        this.dt = 0.01;
        this.maxTimestep = 10;
        this.minTimestep = BC*1e-6;
        this.tol = 3e-4;
        // Atm defaults
        this.temp_low = 287;
        this.temp_high = 216.7;
        this.speedSound_high = sqrt(401.37*temp_high);
        this.speedSound_low = sqrt(401.37*temp_low);
    }
    
    public void setOffsetTemp(double offsetTemp) {
        this.temp_low = 287+offsetTemp;
        this.temp_high = 216.7+offsetTemp;
        this.speedSound_high = sqrt(401.37*temp_high);
        this.speedSound_low = sqrt(401.37*temp_low);
    }
    
    public double bcFast(double speed) {
        if (speed > 500) {
            return 0.5*BC;
        } else { 
            return BC/(1 + speed/1000);
        }
    }
    
    @SuppressWarnings("empty-statement")
    public double bc(double mach) {
        if (mach > 10) {
            return 0.506622527*BC;
        } else {
            if (mach < 0.3) {
                return BC;
            } else {
                int count = 10; //BCs.length
                while(machTable[--count] > mach);
                return BC*(bc2mach[count] + (mach - machTable[count])*dBdM[count]);
            }
        }
    }
    
    public void generatePseudo() {
        this.BC = Math.pow(10,rand.nextFloat()*3)+2;
        this.lift2drag = sigma_l2d[rand.nextInt(6)];
        this.explosionVelocity = Math.pow(15,rand.nextFloat()*2);
    }
    
    public void run(double[] x0, double[] v0, double[] a0, double time) {
        this.time = time;
        this.initialTime = time;
        System.arraycopy(x0, 0, this.x, 0, 3);
        System.arraycopy(v0, 0, this.v, 0, 3);
        System.arraycopy(a0, 0, this.a, 0, 3);
        
        // Explosion
        double angle1 = rand.nextFloat()*TWOPI;
        double angle2 = rand.nextFloat()*TWOPI;
        //double[] dv = new double[]{frag.explosionVelocity()*cos(angle1)*cos(angle2), frag.explosionVelocity()*cos(angle1)*sin(angle2), frag.explosionVelocity()*sin(angle1)};
        // Add explosion to velocity
        double dv = explosionVelocity*cos(angle1);
        v[1] += dv*sin(angle2);
        v[2] += dv*tan(angle1);
        v[0] += dv*cos(angle2);
        
        for(int iter = 0; iter < 100000; iter++) {
            System.arraycopy(a, 0, aprev, 0, 3);
            System.arraycopy(x, 0, xold, 0, 3);
            calcA();
            stepSize();
            for (int i = 0; i < 3; i++) {
                x[i] += dt*(v[i]+ (dt/6)*(4*a[i]-aprev[i]));
                v[i] += (dt/2)*(3*a[i] - aprev[i]);
            }
            if (h < 0) {
                groundImpact();
                break;
            } 
            this.time += dt;
        }
    }
    
    
    private void calcA() {
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
        
        double drag = rho*airspeed/bc(mach);
        double lift = drag*lift2drag*airspeed;
        lift -= EARTH_MU/R2;
        a[0] = drag*v_free[0]+lift*r_[0];
        a[1] = drag*v_free[1]+lift*r_[1];
        a[2] = drag*v_free[2]+lift*r_[2];
    }
    
    private void stepSize() {
        dt = Math.sqrt(tol/(Math.abs(a[0]-aprev[0])+Math.abs(a[1]-aprev[1])+Math.abs(a[2]-aprev[2])));
        if (dt > maxTimestep) {
            dt = maxTimestep;
        }
        if (dt < minTimestep) {
            dt = minTimestep;
        }
    }
    
    private void groundImpact() {
        x[0] -= xold[0];
        x[1] -= xold[1];
        x[2] -= xold[2];
        double delta = h/norm(x); // fair approx
        x[0] = xold[0] + delta*x[0];
        x[1] = xold[1] + delta*x[1];
        x[2] = xold[2] + delta*x[2];
    }
    
    public double[] getX() {
        return x;
    }
    
    public double[] impact() {
        return new double[]{x[0],x[1],x[2],time};
    }
    
    private static double norm(double[] v) {
        return Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
    }
}
