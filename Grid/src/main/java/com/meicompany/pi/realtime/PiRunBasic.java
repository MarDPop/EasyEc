/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

/**
 *
 * @author mpopescu
 */
public class PiRunBasic {
    final double time;
    final double[][] centroids;
    final double[][] centroidStatXtra; 
    
    public PiRunBasic(double time, double[][] centroids, double[][] centroidStatXtra) {
        this.time = time;
        int m = centroids.length;
        int n = centroids[0].length;
        int o = centroidStatXtra[0].length;
        this.centroids = new double[m][n];
        this.centroidStatXtra = new double[m][o];
        for(int i = 0; i < m; i++) {
            System.arraycopy(centroids[i], 0, this.centroids[i], 0, n);
            System.arraycopy(centroidStatXtra[i], 0, this.centroidStatXtra[i], 0, o);
        }
    }
    
    
}
