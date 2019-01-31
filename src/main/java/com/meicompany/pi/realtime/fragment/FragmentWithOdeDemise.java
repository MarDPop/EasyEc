/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.fragment;

import static com.meicompany.pi.realtime.Helper.atan2Fast;
import com.meicompany.pi.realtime.OdeAtmosphere;
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
public final class FragmentWithOdeDemise {
    private final Random rand = new Random();
    
    
    private final Material material;
    private double tempWall;
    
    private final double cD;
    private final double bc_initial;
    private double pseudoRadius;
    private double pseudoRadius_dot;
    private final double density;
    private double bc;
    private double bc_dot;
    private final double explosionSpeed;
    private final double lift2drag;
    
    private final double[] machTable = new double[]{0.3, 0.5, 0.8, 0.9, 1, 1.4, 2, 4, 5, 10};
    private final double[] drag2mach = new double[]{1.0000000,0.971428592,0.886956541,0.85955058,0.711627922,0.528497421,0.488038288,0.525773207,0.512562825,0.506622527};
    private final double[] dBdM = new double[] {-0.14285704,-0.281573503,-0.27405961,-1.47922658,-0.457826253,-0.067431888,0.01886746,-0.013210382,-0.00118806};
    
    // Initial
    private final double[] x = new double[3];
    private final double[] v = new double[3]; 
    private final double[] a = new double[3];
    private final double[] aprev = new double[3];
    
    // Time
    private double dt;
    private double time;
    
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
    
    private final double[] densities;
    private final double[] soundSpeed;
    private final double[][] winds;
    
    private final double[] wind = new double[3];
    private double rho = 0;
    
    final double[] r_ = new double[3];
    final double[] e_ = new double[3];
    final double[] n_ = new double[3];
    
    public FragmentWithOdeDemise(FragmentOptions options, OdeAtmosphere atm) {
        this.densities = atm.densities;
        this.soundSpeed = atm.speedSound;
        this.winds = atm.winds;
        // Time Defaults
        this.dt = 2;
        this.maxTimestep = 10;
        this.minTimestep = bc*1e-6;
        this.tol = 3e-4;
        // Atm defaults
        this.temp_low = 287;
        this.temp_high = 216.7;
        this.speedSound_high = sqrt(401.37*temp_high);
        this.speedSound_low = sqrt(401.37*temp_low);
        // Demise Properties
        this.cD = options.getDragCoefficient();
        this.pseudoRadius = options.getCharacteristicLength();
        this.bc_initial = options.getBallisticCoefficient();
        this.material = options.getMaterial();
        this.density = material.getDensity()*options.getHollownessFactor();
        //Random 
        this.explosionSpeed = options.getExplosionSpeed();
        this.lift2drag = options.getLift2DragRatio();
    }
    
    /**
     * 
     * @param offsetTemp 
     */
    public void setOffsetTemp(double offsetTemp) {
        this.temp_low = 287+offsetTemp;
        this.temp_high = 216.7+offsetTemp;
        this.speedSound_high = sqrt(401.37*temp_high);
        this.speedSound_low = sqrt(401.37*temp_low);
    }
    
    /**
     * quicker implementation of getting ballistic coefficient
     * @param speed airspeed of fragment
     * @return 
     */
    public double bcFast(double speed) {
        if (speed > 500) {
            return 0.5*bc;
        } else { 
            return bc/(1 + speed/1000);
        }
    }
    
    /**
     * 
     * @param mach mach number 
     * @return the ballistic coefficient for given mach number from table values
     */
    @SuppressWarnings("empty-statement")
    public double bc(double mach) {
        if (mach > 10) {
            return 0.506622527*bc;
        } else {
            if (mach < 0.3) {
                return bc;
            } else {
                int count = 10; //BCs.length
                while(machTable[--count] > mach);
                return drag2mach[count] + (mach - machTable[count])*dBdM[count];
            }
        }
    }
    
    /**
     * Gets demise of the fragment. Calculates mass lass and approximate cross area reduction
     * @param rho
     * @param airspeed 
     */
    private void demise() {
        // Look for opportunity to precompute
        double qw = Math.sqrt(rho/pseudoRadius)*airspeed*airspeed*airspeed;
        if(qw > 1e8) {
            qw *= 1.7415e-4; //  W / m2  heat to wall Sutton Graves
            tempWall = Math.sqrt(Math.sqrt(qw/material.getEmmissivity()/5.67e-8)); 
            if(tempWall > material.getMeltingPoint()) {
                double m_dot = qw/material.getDensity();
                bc_dot = m_dot/(3*cD);
                pseudoRadius_dot = m_dot/(12.566370614359172*pseudoRadius*pseudoRadius/density);
            } else {
                bc_dot = 0;
                pseudoRadius_dot = 0;
            }
        } else {
            bc_dot = 0;
            pseudoRadius_dot = 0;
        }
    }
    
    
    
    /**
     * 
     * @param x0
     * @param v0
     * @param a0
     * @param time 
     * @return  if the impact hit ground
     */
    public boolean run(double[] x0, double[] v0, double[] a0, double time) {
        this.time = time;
        
        System.arraycopy(x0, 0, this.x, 0, 3);
        System.arraycopy(v0, 0, this.v, 0, 3);
        System.arraycopy(a0, 0, this.a, 0, 3);
        
        this.bc = bc_initial;
        // Explosion
        double angle1 = rand.nextFloat()*TWOPI;
        double angle2 = rand.nextFloat()*TWOPI;
        //double[] dv = new double[]{frag.explosionSpeed()*cos(angle1)*cos(angle2), frag.explosionSpeed()*cos(angle1)*sin(angle2), frag.explosionSpeed()*sin(angle1)};
        // Add explosion to velocity
        double dv = explosionSpeed*cos(angle1);
        v[1] += dv*sin(angle2);
        v[2] += dv*tan(angle1);
        v[0] += dv*cos(angle2);
        
        for(int iter = 0; iter < 100000; iter++) {
            System.arraycopy(a, 0, aprev, 0, 3);
            calcA();
            stepSize();
            bc += bc_dot*dt;
            pseudoRadius += pseudoRadius_dot*dt;
            if (bc < 0.1 || pseudoRadius < 1e-3) {
                return false;
            } else {
                if (h > 0) {
                    for (int i = 0; i < 3; i++) {
                        x[i] += dt*(v[i]+ (dt/6)*(4*a[i]-aprev[i]));
                        v[i] += (dt/2)*(3*a[i] - aprev[i]);
                    }
                } else {
                    groundImpact();
                    return true;
                }
            }
            this.time += dt;
        }
        return false;
    }
    
    
    private void calcA() {
        double R2 = x[0]*x[0]+x[1]*x[1]+x[2]*x[2];
        R = sqrt(R2);

        // Get unit vectors
        r_[0] = x[0]/R;
        r_[1] = x[1]/R;
        r_[2] = x[2]/R;
        h = atan2Fast(x[1],x[0]); // Major POINT OF SLOWDOWN
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
        
        demise();
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
        double time_past_impact = h/airspeed; // speed is approximately vertical
        x[0] -= v[0]*time_past_impact;
        x[1] -= v[1]*time_past_impact;
        x[2] -= v[2]*time_past_impact;
    }
    
    public double[] impact() {
        return new double[]{x[0],x[1],x[2],time};
    }
    
    private static double norm(double[] v) {
        return Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
    }
}
