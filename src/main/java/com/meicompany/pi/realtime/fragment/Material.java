/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.fragment;

/**
 *
 * @author mpopescu
 */
public class Material {
    public final double density;
    public final double meltingPoint;
    public final double specificHeatCapacity;
    public final double heatOfFusion;
    public final String name;
    
    public Material(double density, double meltingPoint, double specificHeatCapacity, double heatOfFusion, String name){
        this.density = density;
        this.meltingPoint = meltingPoint;
        this.specificHeatCapacity = specificHeatCapacity;
        this.heatOfFusion = heatOfFusion;
        this.name = name;
    }
}
