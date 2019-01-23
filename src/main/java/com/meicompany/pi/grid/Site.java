/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.grid;

/**
 *
 * @author mpopescu
 */
public class Site {
    
    public final String name;
    
    public final double latitude;
    
    public final double longitude;
    
    public final double height;
    
    public final double azimuth;
    
    public double meridianDeflection = 0;
    
    public double verticalDeflection = 0;
    
    public double[] efg0;
    
    public double[][] DC;
    
    public Site(String name, double latitude, double longitude, double height, double azimuth) {
        this.name = name;
        this.latitude = latitude;
        this.longitude= longitude;
        this.height = height; 
        this.azimuth = azimuth;
    }
    
    public Site(double latitude, double longitude) {
        this("",latitude,longitude,0,Math.PI/2);
    }
    
    
}
