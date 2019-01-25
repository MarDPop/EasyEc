/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.fragment;

import static java.lang.Math.random;
import java.util.Random;

/**
 *
 * @author mpopescu
 */
public final class Fragment {
    
    private double BC;
    private double explosionVelocity;
    private double lift2drag;
    
    private final double[] machTable = new double[]{0.3, 0.5, 0.8, 0.9, 1, 1.4, 2, 4, 5, 10};
    private final double[] BCs = new double[]{1.0000000,0.971428592,0.886956541,0.85955058,0.711627922,0.528497421,0.488038288,0.525773207,0.512562825,0.506622527};
    private final double[] dBdM = new double[] {-0.14285704,-0.281573503,-0.27405961,-1.47922658,-0.457826253,-0.067431888,0.01886746,-0.013210382,-0.00118806};

    private final double[] sigma_l2d = new double[] {0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.8};
    
    private double[] impact ;
    
    public Fragment(double BC, double explosionVelocity, double lift2drag) {
        this.BC = BC;
        this.explosionVelocity = explosionVelocity;
        this.lift2drag = lift2drag;
    }
    
    public Fragment(){
        generatePseudo();
    }
    
    public double bcFast(double speed) {
        if (speed > 500) {
            return 0.5*BC;
        } else { 
            return BC/(1 + speed/1000);
        }
    }
    
    public double bc(double mach) {
        if (mach > 10) {
            return 0.506622527*BC;
        } else {
            if (mach < 0.3) {
                return BC;
            } else {
                int count = 10; //BCs.length
                while(machTable[--count] > mach);
                return BCs[count] + (mach - machTable[count])*dBdM[count];
            }
        }
    }
    
    public double l2d() {
        return lift2drag;
    }
    
    public double explosionVelocity() {
        return this.explosionVelocity;
    }
    
    public void generatePseudo() {
        Random rand = new Random();
        this.BC = Math.pow(10,random()*3)+2;
        this.lift2drag = sigma_l2d[rand.nextInt(6)];
        this.explosionVelocity = Math.pow(15,random()*2);
    }
    
    public void setImpact(double[] impact){
        this.impact = impact;
    }
    
    public double[] getImpact() {
        return impact;
    }
    
}
