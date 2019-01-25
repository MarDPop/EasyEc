/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.vehicle;

import com.meicompany.pi.realtime.Helper;

/**
 *
 * @author mpopescu
 */
public class Vehicle {
    
    
    protected final double[] position = new double[3]; // xyz in ECI frame
    protected final double[] rotation = new double[3]; // pitch, roll, yaw [ie. rotation respect to xyz axis in ECI FRAME]
    protected final double[] velocity = new double[3]; //uvw in ECI frame
    protected final double[] rotationRate = new double[3]; // pitch, roll, yaw rates
    
    protected final double[] forces = new double[3]; // force in xyz axis in ECI frame
    protected final double[] torques = new double[3]; // moments about xyz axis in ECI frame
    
    protected double[][] inertia = new double[3][3]; // Ixx, Iyy, Izz, Ixy, Ixz, Iyz from center of mass 3 more memory but faster implementation
    protected double[][] inertiaInv = new double[3][3];
    protected double mass;
    
    protected double time;
    
    protected final VehicleDynamics dynamics;
    
    protected double[] controlForces = new double[3]; // currently just simple control vector instead of control surfaces / rcs
    protected double[] controlTorques = new double[3];
    
    public Vehicle(double[][] inertia, VehicleDynamics dynamics){
        this.inertia = inertia;
        calcInertiaInverse();
        this.dynamics = dynamics;
    }
    
    public void setState(double[] state, double time){
        this.position[0] = state[0];
        this.position[1] = state[1];
        this.position[2] = state[2];
        this.velocity[0] = state[3];
        this.velocity[1] = state[4];
        this.velocity[2] = state[5];
        this.rotation[0] = state[6];
        this.rotation[1] = state[7];
        this.rotation[2] = state[8];
        this.rotationRate[0] = state[9];
        this.rotationRate[1] = state[10];
        this.rotationRate[2] = state[11];
        this.time = time;
    }
    
    public double[] getStateRate() {
        System.arraycopy(dynamics.getAxisForces(), 0, forces, 0, 3);
        System.arraycopy(dynamics.getAxisTorques(), 0, torques, 0, 3);
        double[] temp = new double[3];
        for(int i = 0; i < 3; i++) {
            forces[i] += controlForces[i];
            torques[i] += controlTorques[i];
            temp[i] = inertia[i][0]*rotationRate[0]+inertia[i][1]*rotationRate[1]+inertia[i][2]*rotationRate[2];
        }
        double[] tumble = Helper.cross(rotationRate, temp);
        for(int i = 0; i < 3; i++) {
            torques[i] -= tumble[i];
        }
        for(int i = 0; i < 3; i++) {
            temp[i] = torques[0]*inertiaInv[i][0]+torques[1]*inertiaInv[i][1]+torques[2]*inertiaInv[i][2];
        }
        return new double[] {velocity[0], velocity[1], velocity[2], forces[0]/mass, forces[1]/mass, forces[2]/mass, rotationRate[0], rotationRate[1], rotationRate[2], temp[0], temp[1], temp[2]};
    }
  
    private void calcInertiaInverse() {
        double[] cofactors = new double[6];
        cofactors[0] = inertia[1][1]*inertia[2][2] - inertia[1][2]*inertia[2][1]; // A11
        cofactors[1] = inertia[1][0]*inertia[2][2] - inertia[1][2]*inertia[2][0]; // A12
        cofactors[2] = inertia[1][0]*inertia[2][1] - inertia[2][0]*inertia[1][1]; // A13
        cofactors[3] = inertia[0][0]*inertia[2][2] - inertia[2][0]*inertia[0][2]; // A22
        cofactors[4] = inertia[0][1]*inertia[2][2] - inertia[2][1]*inertia[0][2]; // A23 
        cofactors[5] = inertia[0][0]*inertia[1][1] - inertia[1][0]*inertia[0][1]; // A33
        double det =  1/(inertia[0][0]*cofactors[0]+inertia[0][1]*cofactors[1]+inertia[0][2]*cofactors[2]);
        inertiaInv[0][0] = cofactors[0]*det;
        inertiaInv[0][1] = inertiaInv[1][0] = cofactors[1]*det;
        inertiaInv[0][2] = inertiaInv[2][0] = cofactors[2]*det;
        inertiaInv[1][2] = inertiaInv[2][1] = cofactors[4]*det;
        inertiaInv[1][1] = cofactors[3]*det;
        inertiaInv[2][2] = cofactors[5]*det;
    }
    
    public double[] getState(){
        return new double[] {position[0], position[1], position[2], velocity[0], velocity[1], velocity[2], rotation[0], rotation[1], rotation[2], rotationRate[0], rotationRate[1], rotationRate[2]};
    }
}
