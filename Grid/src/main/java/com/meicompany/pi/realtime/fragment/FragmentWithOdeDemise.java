package com.meicompany.pi.realtime.fragment;

import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.coordinates.Earth;
import com.meicompany.pi.realtime.ode.ODEOptions;
import com.meicompany.pi.realtime.ode.util.Material;
import com.meicompany.pi.realtime.ode.util.OdeAtmosphere;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import java.util.Random;

/**
 *
 * @author mpopescu
 */
public final class FragmentWithOdeDemise extends Fragment{;
    
    private final Material material;
    private double tempWall;
    
    private final double cD;
    private final double bc_initial;
    private double pseudoRadius;
    private double pseudoRadius_dot;
    private final double density;
    private double bc;
    private double bc_dot;
    
    // Initial
    private final double[] deltav = new double[3];
    
    public static final float TWOPI_F = 6.283185307179586f;

    public FragmentWithOdeDemise(FragmentOptions fragOptions, OdeAtmosphere atm, ODEOptions odeOptions) {
        super(fragOptions,atm,odeOptions);
        // Demise Properties
        this.cD = fragOptions.getDragCoefficient();
        this.pseudoRadius = fragOptions.getCharacteristicLength();
        this.bc_initial = fragOptions.getBallisticCoefficient();
        this.material = fragOptions.getMaterial();
        this.density = material.getDensity()*fragOptions.getHollownessFactor();
        this.bc = this.bc_initial;
    }
    
    /**
     * Gets demise of the fragment. Calculates mass lass and approximate cross area reduction
     * @param rho
     * @param airspeed 
     */
    private void demise() {
        // Assumptions... many. Most important : empirical formula that assumes
        // round geometry, heating only on one side. Wall reaches radiative equilibrium only 
        // 
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
    
    @Override
    public void run() {
        this.bc = bc_initial;        
        for(int iter = 0; iter < 100000; iter++) {
            System.arraycopy(a, 0, aprev, 0, 3);
            calcA();
            stepSize();
            bc += bc_dot*dt;
            pseudoRadius += pseudoRadius_dot*dt;
            if (bc < 1 || pseudoRadius < 1e-3) {
                this.bc = 0;
                break;
            } else {
                if(airspeed*dt > h) {
                    dt = h/airspeed;
                    x[0] += dt*v[0];
                    x[1] += dt*v[1];
                    x[2] += dt*v[2];
                    this.time += dt;
                    break;
                } else {
                    for (int i = 0; i < 3; i++) {
                        x[i] += dt*(v[i]+ (dt*0.16666666666666666666)*(deltav[i]+a[i]));
                        v[i] += (dt*0.5)*deltav[i];
                    } 
                    this.time += dt;
                }
            }
               
        }
    }
    
    @Override
    protected void calcA() {
        h = x[0]*x[0]+x[1]*x[1];
        double R2 = 1/(h+x[2]*x[2]);
        h = sqrt(h);
        radius = sqrt(R2);

        // Get unit vectors
        radialVector[0] = x[0]*radius;
        radialVector[1] = x[1]*radius;
        radialVector[2] = x[2]*radius;
        
        double cl = x[0]/h;
        double sl = x[1]/h;
        double c = Math.sqrt(1-radialVector[2]*radialVector[2]);
        eastVector[0] = -sl;
        eastVector[1] = cl;
        northVector[0] = -radialVector[2]*cl;
        northVector[1] = -radialVector[2]*sl;
        northVector[2] = c;
        
        // Calculate Geopot Height
        h = radialVector[2];
        h = 6378137/sqrt(c*c+(h*h)*1.006739501254387);
        
        h = (1-h*radius)*h;
        // Atmospheric density and wind        
        if (h > 1.25e5) {
            // rho is less than 1e-6
            double g = -Earth.EARTH_MU*R2;
            a[0] = g*radialVector[0];
            a[1] = g*radialVector[1];
            a[2] = g*radialVector[2];
            return;
        } else {
            if (h < 45000) {
                if (h > 34080) {
                    rho = 0.63*exp(-h*1.577086431315691e-04); 
                    if ( h > 45000) {
                        wind[0] = 0;
                        wind[1] = 0;
                        wind[2] = 0;
                    } else {
                        wind[0] = 10*eastVector[0]+19*northVector[0];
                        wind[1] = 10*eastVector[1]+19*northVector[1];
                        wind[2] = 10*eastVector[2]+19*northVector[2];
                    }
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
        
        demise();
        double drag = rho*airspeed/bc(airspeed/c);
        double lift = drag*lift2drag*airspeed;
        lift -= Earth.EARTH_MU*R2;
        a[0] = drag*freestreamVelocity[0]+lift*radialVector[0];
        a[1] = drag*freestreamVelocity[1]+lift*radialVector[1];
        a[2] = drag*freestreamVelocity[2]+lift*radialVector[2];
    }
    
    @Override
    protected void stepSize() {
        deltav[0] = 3*a[0]-aprev[0];
        deltav[1] = 3*a[1]-aprev[1];
        deltav[2] = 3*a[2]-aprev[2];
        dt = tol/(Math.abs(deltav[0])+Math.abs(deltav[1])+Math.abs(deltav[2])); // normally tolerance multiplied by airspeed but this saves a multiplication and error more sensitive at high speeds anyway
        if (dt > maxTimestep) {
            dt = maxTimestep;
        }
        if (dt < minTimestep) {
            dt = minTimestep;
        }
    }
    
    /**
     * Gets the xy coordinates from impact which contains a time difference
     * @return
     */
    @Override
    public float[] impact2xy() {
        if(isGone()) {
            return null;
        }
        return super.impact2xy();
    }
    
    /**
     * Checks if fragment is demised
     * @return 
     */
    public boolean isGone(){
        return (bc == 0);
    }
    
}
