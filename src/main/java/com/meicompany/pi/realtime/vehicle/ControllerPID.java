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
public class ControllerPID implements Controller{
    private double proportionalCoef;
    private double dampingCoef;
    private double integralCoef;
    
    private double measuredProcessVariable;
    private double sum;
    private double prev;
    private double rate;
    
    public ControllerPID(double proportionalCoef, double dampingCoef, double integralCoef) {
        this.proportionalCoef = proportionalCoef;
        this.dampingCoef = dampingCoef;
        this.integralCoef = integralCoef;
    }
    
    @Override
    public double controlOutput(double setPoint, double dt) {
        double err = setPoint-measuredProcessVariable;
        sum += err*dt;
        rate = (err-prev)/dt; 
        prev = err;
        return proportionalCoef*err+dampingCoef*rate+integralCoef*sum;
    }

 
}
