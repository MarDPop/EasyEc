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
public class ControlSurface {
    
    private LiftingSurface surf;
    private Controller control;
    
    public ControlSurface(LiftingSurface surf, Controller control) {
        this.surf = surf;
        this.control = control;
    }
}
