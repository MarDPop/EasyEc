/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.fragment;

import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.coordinates.Earth;
import com.meicompany.pi.realtime.generalMath.Math2;
import com.meicompany.pi.realtime.ode.ODEOptions;
import com.meicompany.pi.realtime.ode.util.OdeAtmosphere;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import java.util.Random;

/**
 *
 * @author mpopescu
 */
public class Fragment {

    protected final Random rand = new Random();
            
    protected float ballisticCoefficient;
    protected float explosionSpeed;
    protected float lift2drag;
    
    protected static final double[] MACH_TABLE = new double[]{0.3, 0.5, 0.8, 0.9, 1, 1.4, 2, 4, 5, 10};
    protected static final double[] DRAG_TABLE = new double[]{1.0000000,0.971428592,0.886956541,0.85955058,0.711627922,0.528497421,0.488038288,0.525773207,0.512562825,0.506622527};
    protected static final double[] DBCDM_TABLE = new double[] {-0.14285704,-0.281573503,-0.27405961,-1.47922658,-0.457826253,-0.067431888,0.01886746,-0.013210382,-0.00118806};
    
    // Initial
    protected final double[] x = new double[3];
    protected final double[] v = new double[3]; 
    protected final double[] a = new double[3];
    protected final double[] aprev = new double[3];
    
    // Time
    protected double dt;
    protected double time;
    
    // Options
    protected double tol;
    protected double minTimestep;
    protected double maxTimestep;
    
    // Parameters
    protected double h; // height
    protected double radius;
    protected double airspeed;
    
    protected double temp_high;
    protected double temp_low;
    protected double speedSound_high;
    protected double speedSound_low;
    
    protected double[] densities;
    protected double[] soundSpeed;
    protected double[][] winds;
    
    protected final double[] wind = new double[3];
    protected double rho = 0;
    
    final double[] radialVector = new double[3];
    final double[] eastVector = new double[3];
    final double[] northVector = new double[3];
    
    final double[] freestreamVelocity = new double[3];
    
    public Fragment(FragmentOptions fragOptions, OdeAtmosphere atm, ODEOptions odeOptions) {
        this.densities = atm.densities;
        this.soundSpeed = atm.speedSound;
        this.winds = atm.winds;
        //Random 
        this.explosionSpeed = (float)fragOptions.getExplosionSpeed();
        this.lift2drag = (float)fragOptions.getLift2DragRatio();
        this.ballisticCoefficient = (float) fragOptions.getBallisticCoefficient();
        // Time Defaults
        this.dt = odeOptions.getInitialStep();
        this.maxTimestep = odeOptions.getMaxTimestep();
        this.minTimestep = ballisticCoefficient*1e-6;
        this.tol = odeOptions.getTolerance();
        // Atm defaults
        setOffsetTemp(fragOptions.getOffsetTemp());
    }
    
    public final void setOffsetTemp(double offsetTemp) {
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
            return 0.5*ballisticCoefficient;
        } else { 
            return ballisticCoefficient/(1 + speed/1000);
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
            return 0.506622527*ballisticCoefficient;
        } else {
            if (mach < 0.3) {
                return ballisticCoefficient;
            } else {
                int count = 10; //BCs.length
                while(MACH_TABLE[--count] > mach);
                return ballisticCoefficient*(DRAG_TABLE[count] + (mach - MACH_TABLE[count])*DBCDM_TABLE[count]);
            }
        }
    }
    
    /**
     * Integration
     */
    public void run() {
        h = 1e6;
        int outer = 0;
        for(int iter = 0; iter < 40000; iter++) {
            if(airspeed*dt > h) {
                dt = h/airspeed;
                x[0] += dt*v[0];
                x[1] += dt*v[1];
                x[2] += dt*v[2];
                break;
            } else {
                calcA();
                stepSize();
                for(int i = 0; i < 3; i++) {
                    x[i] += dt*v[i];
                    v[i] += dt*a[i];
                }
                this.time += dt;
                outer = iter;
            }
        } 
    }
    
    /**
     * Sets up state
     * @param x0
     * @param v0
     * @param a0
     * @param time 
     */
    public void setup(double[] x0, double[] v0, double[] a0, double time) {
        this.time = time;
        System.arraycopy(x0, 0, this.x, 0, 3);
        System.arraycopy(v0, 0, this.v, 0, 3);
        System.arraycopy(a0, 0, this.a, 0, 3);
        
        // Explosion
        float angle1 = (rand.nextFloat()-0.5f)*6.28318530718f;
        float angle2 = (rand.nextFloat()-0.5f)*6.28318530718f;
        
        // Add explosion to velocity
        float dv = explosionSpeed*Math2.cos(angle1);
        v[1] += dv*Math2.sin(angle2);
        v[2] += dv*Math2.tan(angle1); // this microoptimization that has potential mathematical instability is dangerous, should evaluate
        v[0] += dv*Math2.cos(angle2);
    }
    
    /** 
     * Calculates Dynamics
     */
    protected void calcA() {
        h = x[0]*x[0]+x[1]*x[1];
        double R2 = h+x[2]*x[2];
        h = 1/sqrt(h);
        radius = 1/sqrt(R2); // encourage JIT to use fast inverse square root

        // Get unit vectors
        radialVector[0] = x[0]*radius;
        radialVector[1] = x[1]*radius;
        radialVector[2] = x[2]*radius;
        
        double cl = x[0]*h;
        double sl = x[1]*h;
        h = radialVector[2]*radialVector[2];
        double c = 1-h;
        eastVector[0] = -sl;
        eastVector[1] = cl;
        northVector[0] = -radialVector[2]*cl;
        northVector[1] = -radialVector[2]*sl;
        northVector[2] = Math.sqrt(c);
        
        // Calculate Geopot Height
        h = 6378174/sqrt(1+h*0.006739501254387); // Quick calc maximum off by +/- 35 meters, no big deal (saves 3 multiplications)
        
        h = (1-h*radius)*h; 
        // Atmospheric density and wind        
        if (h > 1.25e5) {
            // rho is less than 1e-6
            double g = -Earth.EARTH_MU/R2;
            a[0] = g*radialVector[0];
            a[1] = g*radialVector[1];
            a[2] = g*radialVector[2];
            return;
        } else {
            if (h < 45000) {
                if (h > 34080) {
                    rho = 0.63*exp(-h*1.577086431315691e-04); 
                    wind[0] = 10*eastVector[0]+19*northVector[0];
                    wind[1] = 10*eastVector[1]+19*northVector[1];
                    wind[2] = 10*eastVector[2]+19*northVector[2];
                    c = speedSound_high;
                } else {
                    if (h < 1.022401e3) {
                        rho = 0.63*exp(-h*1.190617829523243e-04); // 0.034167247386760/temp_low
                        wind[0] = 1.5*eastVector[0]+2*northVector[0];
                        wind[1] = 1.5*eastVector[1]+2*northVector[1];
                        wind[2] = 1.5*eastVector[2]+2*northVector[2];
                        c = speedSound_low;
                    } else {
                        h /= 340.8;
                        int i = (int) h;
                        double delta = h - i;
                        i-=2;
                        rho = densities[i]+delta*(densities[i+1]-densities[i]);
                        sl = winds[i][0];
                        cl = winds[i][1];
                        wind[0] = sl*eastVector[0]+cl*northVector[0];
                        wind[1] = sl*eastVector[1]+cl*northVector[1];
                        wind[2] = sl*eastVector[2]+cl*northVector[2];
                        c = soundSpeed[i];
                    }
                }
                freestreamVelocity[0] = -v[0] - x[1]*Earth.EARTH_ROT + wind[0];
                freestreamVelocity[1] = -v[1] + x[0]*Earth.EARTH_ROT + wind[1];
                freestreamVelocity[2] = -v[2] + wind[2];
            } else {
                rho = 0.63*exp(-h*1.577086431315691e-04); 
                c = speedSound_high;
                freestreamVelocity[0] = -v[0] - x[1]*Earth.EARTH_ROT;
                freestreamVelocity[1] = -v[1] + x[0]*Earth.EARTH_ROT;
                freestreamVelocity[2] = -v[2];
            }
        }
        
        airspeed = sqrt(freestreamVelocity[0]*freestreamVelocity[0]+freestreamVelocity[1]*freestreamVelocity[1]+freestreamVelocity[2]*freestreamVelocity[2]);
        
        double drag = rho*airspeed/bc(airspeed/c);
        double lift = drag*lift2drag*airspeed - Earth.EARTH_MU/R2;
        a[0] = drag*freestreamVelocity[0]+lift*radialVector[0];
        a[1] = drag*freestreamVelocity[1]+lift*radialVector[1];
        a[2] = drag*freestreamVelocity[2]+lift*radialVector[2];
    }
    
    /**
     * Gets step size
     */
    protected void stepSize() {
        dt = tol*airspeed/sqrt(a[0]*a[0]+a[1]*a[1]+a[2]*a[2]);
        //dt = sqrt(tol/(a[0]*a[0]+a[1]*a[1]+a[2]*a[2])); // normally tolerance multiplied by airspeed but this saves a multiplication and error more sensitive at high speeds anyway
        if (dt > maxTimestep) {
            dt = maxTimestep;
        }
        if (dt < minTimestep) {
            dt = minTimestep;
        }   
    }
    
    /**
     * Returns impact of
     * @return 
     */
    public double[] impact() {
        return new double[]{x[0],x[1],x[2],time};
    }
    
    /**
     * Gets the xy coordinates from impact which contains a time difference
     * @param impact
     * @return
     */
    public static double[] impact2xy(double[] impact) {
        double[] ll = CoordinateFrame.ecef2geo(impact);
        ll[0] -= impact[3] * Earth.EARTH_ROT;
        while (ll[0] < -Math.PI) {
            ll[0] += Math2.TWOPI;
        }
        return CoordinateFrame.ll2xy(ll);
    }
    
    /**
     * Gets the xy coordinates from impact which contains a time difference
     * @return
     */
    public float[] impact2xy() {
        
        float[] ecef = CoordinateFrame.rotateZ(new float[]{(float)x[0],(float)x[1],(float)x[2]}, (float)-time*Earth.EARTH_ROT_F);
        float[] ll = CoordinateFrame.ecef2geo(ecef[0],ecef[1],ecef[2]);
        
        /*
        float[] ll = CoordinateFrame.ecef2geo((float)x[0],(float)x[1],(float)x[2]);
        ll[0] -= time * Earth.EARTH_ROT_F;
        while (ll[0] < -Math2.PI_F) {
            ll[0] += Math2.TWOPI_F;
        }
        */
        return CoordinateFrame.ll2xy(ll);
    }
    
    /**
     * Approximates ground impact
     */
    protected void groundImpact() {
        double timePastImpact = h/airspeed; // speed is approximately vertical
        x[0] -= v[0]*timePastImpact;
        x[1] -= v[1]*timePastImpact;
        x[2] -= v[2]*timePastImpact;
    }
    
}
