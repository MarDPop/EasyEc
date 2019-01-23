/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.realtime.clustering.Centroid;

/**
 *
 * @author mpopescu
 */
public class PiRun {
    final double time;
    final Centroid[] centroids;
    private int weight;
    
    public PiRun(double time, Centroid[] centroids) {
        this.time = time;
        this.centroids = centroids;
        weight = 0;
        for (Centroid centroid : centroids) {
            weight += centroid.getNumber();
        }
    }
    
    public int weight() {
        return this.weight;
    }
}
