/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode;

import com.meicompany.pi.realtime.vehicle.Vehicle;

/**
 *
 * @author mpopescu
 */
public class VehicleDynamics implements Dynamics{
    Vehicle vehicle;
    
    public VehicleDynamics(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public double[] calc(double[] state, double time) {
        vehicle.setState(state, time);
        return vehicle.getStateRate();
    }
    
}
