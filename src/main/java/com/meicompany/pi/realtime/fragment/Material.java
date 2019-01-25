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
    private double emmissivity;
    public final String name;
    
    public Material(double density, double meltingPoint, double specificHeatCapacity, double heatOfFusion, double emmissivity, String name){
        this.density = density;
        this.meltingPoint = meltingPoint;
        this.specificHeatCapacity = specificHeatCapacity;
        this.heatOfFusion = heatOfFusion;
        this.emmissivity = emmissivity;
        this.name = name;
    }
    
    public Material() {
        // default is aluminum
        this.density = 2700; // kg/m3 approx aluminum
        this.meltingPoint = 870; // K ... varies with pressure, but only slightly, also slightly lower value (-50) chosen since some components will likely vaporize long before aluminum
        this.specificHeatCapacity = 896; // J/kg
        this.heatOfFusion = 310e+3; // J/kg
        this.emmissivity = 0.19; // emmissivity varies with temperature and oxidation etc, but this is a good "default" value
        this.name = "default";
    }

    /**
     * @return the emmissivity
     */
    public double getEmmissivity() {
        return emmissivity;
    }

    /**
     * @param emmissivity the emmissivity to set
     */
    public void setEmmissivity(double emmissivity) {
        this.emmissivity = emmissivity;
    }
}
