/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.vehicle;

import com.meicompany.pi.coordinates.Earth;
import com.meicompany.pi.realtime.Helper;

/**
 *
 * @author mpopescu
 */
public class Vehicle {
    // coordinates measured from COG
    
    protected final double[] position = new double[3]; // xyz in ECI frame
    protected final double[] rotation = new double[3]; // pitch, roll, yaw [ie. rotation respect to xyz axis in ECI FRAME]
    protected final double[] velocity = new double[3]; //uvw in ECI frame
    protected final double[] rotationRate = new double[3]; // pitch, roll, yaw rates in ECI frame
    
    protected final double[] forces = new double[3]; // force in xyz axis in ECI (inertial) frame
    protected final double[] moments = new double[3]; // torques about xyz axis in ECI (inertial) frame
    
    protected final double[] control_forces = new double[3]; // currently just simple control vector instead of control surfaces / rcs
    protected final double[] control_forques = new double[3]; // in Body frame
    
    protected double[] moments_body_frame; // roll moment, pitch moment, yaw moment
    protected double[] forces_body_frame; // 
    
    protected double[][] inertia = new double[3][3]; // Ixx, Iyy, Izz, Ixy, Ixz, Iyz from center of mass 3 more memory but faster implementation
    protected double[][] inertiaInv = new double[3][3];
    
    protected double[][] rotationMatrixBody2ECI = new double[3][3]; 
    protected double[][] rotationMatrixECI2Body = new double[3][3];
    
    protected double mass;
    
    protected double time;
    
    protected Earth earth;
    
    protected double[] wind;
    
    protected double[] velocity_body_frame;
    protected double[] rpw_body_frame;
    
    protected double angle_of_attack;
    protected double side_slip;
    
    protected double lift;
    protected double drag;
    protected double grav;
    protected double thrust;
    protected double sideForce;
    
    public Vehicle(double[][] inertia){
        this.inertia = inertia;
        calcInertiaInverse();
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
        double[] temp = new double[3];
        for(int i = 0; i < 3; i++) {
            forces[i] += control_forces[i];
            moments[i] += control_forques[i];
            temp[i] = inertia[i][0]*rotationRate[0]+inertia[i][1]*rotationRate[1]+inertia[i][2]*rotationRate[2];
        }
        double[] tumble = Helper.cross(rotationRate, temp);
        for(int i = 0; i < 3; i++) {
            moments[i] -= tumble[i];
        }
        for(int i = 0; i < 3; i++) {
            temp[i] = moments[0]*inertiaInv[i][0]+moments[1]*inertiaInv[i][1]+moments[2]*inertiaInv[i][2];
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
    
    private void calcRotationMatrixes(double roll, double pitch, double yaw) {
        double c1 = Math.cos(roll);
        double c2 = Math.cos(pitch);
        double c3 = Math.cos(yaw);
        double s1 = Math.sin(roll);
        double s2 = Math.sin(pitch);
        double s3 = Math.sin(yaw);
        double a,b;
        this.rotationMatrixECI2Body[0][0] = c2*c3;
        this.rotationMatrixBody2ECI[0][0] = this.rotationMatrixECI2Body[2][2];
        
        this.rotationMatrixECI2Body[0][1] = -c2*s3;
        this.rotationMatrixBody2ECI[2][1] = -this.rotationMatrixECI2Body[0][1];
        
        this.rotationMatrixECI2Body[0][2] = s2;
        this.rotationMatrixBody2ECI[2][0] = -this.rotationMatrixECI2Body[0][2];
        
        b = c3*s1*s2;
        a = c1*s3;
        this.rotationMatrixECI2Body[1][0] = a+b;
        this.rotationMatrixBody2ECI[1][2] = b-a;
        
        a = s1*s2*s3;
        b = c1*c3;
        this.rotationMatrixECI2Body[1][1] = b-a;
        this.rotationMatrixBody2ECI[1][1] = a+b;
        
        this.rotationMatrixECI2Body[1][2] = -c2*s1;
        this.rotationMatrixBody2ECI[1][0] = -this.rotationMatrixECI2Body[1][2];
        
        a = c1*c3*s2;
        b = s1*s3;
        this.rotationMatrixECI2Body[2][0] = b-a;
        this.rotationMatrixBody2ECI[0][2] = a+b;
        
        a = c1*s2*s3;
        b = c3*s1;
        this.rotationMatrixECI2Body[2][1] = a+b;
        this.rotationMatrixBody2ECI[1][2] = b-a;
        
        this.rotationMatrixECI2Body[2][2] = c1*c2;
        this.rotationMatrixBody2ECI[2][2] = this.rotationMatrixECI2Body[0][0];
        
    }

}
