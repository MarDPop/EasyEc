/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.clustering;

import java.util.ArrayList;

/**
 *
 * @author mpopescu
 */
public class Centroid {
    protected double[] location;
    protected double[] locationTemp;
    
    ArrayList<Integer> ids;
    protected int n;
    protected double[] stats;
    
    public Centroid(){}
    
    public void reset(){
        ids.clear();
        this.n = 0;
        this.locationTemp = new double[locationTemp.length];
        this.stats = new double[stats.length];
    }
    
    public void setLocation(double[] location) {
        System.arraycopy(location, 0, this.location, 0, location.length);  ;
    }
    
    public int getNumber() {
        return n;
    }
}
