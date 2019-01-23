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
public class EarthModel {
    
    private double time;
    
    public EarthModel(double time) {
        this.time = time;
    }
    
    public double changeTime(double dt) {
        this.time += dt;
        return time;
    }
    
    public double getTime() {
        return time;
    }
    
    public void setTime(double time) {
        this.time = time;
    }
}
