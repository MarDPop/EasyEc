/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.vehicle;

/**
 *
 * @author mpopescu
 */
public abstract class VehicleDynamics implements Dynamics6Dof {
    protected final Vehicle vehicle;
    
    protected final EarthModel earth;
    
    protected double[] velocity_body_frame;
    protected double[] rpw_body_frame;
    
    protected double angle_of_attack;
    protected double side_slip;
    
    protected double lift;
    protected double drag;
    protected double grav;
    protected double thrust;
    protected double sideForce;
            
    protected double[] moments_body_frame; // roll moment, pitch moment, yaw moment
    protected double[] forces_body_frame; // 
    
    public VehicleDynamics(Vehicle vehicle, EarthModel earth) {
        this.vehicle = vehicle;
        this.earth = earth;
    }
    
    
}
