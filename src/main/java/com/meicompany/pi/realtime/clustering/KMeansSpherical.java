/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.clustering;

import com.meicompany.pi.coordinates.CoordinateException;
import com.meicompany.pi.realtime.Helper;
import java.util.Random;

/**
 *
 * @author mpopescu
 */
public class KMeansSpherical {
    public static final int MAX_ITER = 100;
    
    private KMeansSpherical(){}
    
    public static SphericalDistribution1D[] cluster(double[][] data, double[][] initial, int nScatter, double tol) {
        int nPresets = initial.length;
        int nTotal = nPresets+nScatter;
        
        SphericalDistribution1D[] centroids = new SphericalDistribution1D[nTotal];
        // Set Initial Locations
        for(int i = 0; i < nPresets; i++){
            centroids[i].setLocation(initial[i]);
        }
        
        for(int iter = 0; iter < MAX_ITER; iter++) {
            // Reset Centroids
            for(SphericalDistribution1D centroid : centroids) {
                centroid.reset();
            }
            // Identify Grouping from New Locations by min distance
            for(double[] point : data) {
                int i_found  = 0;
                double d_min = 1e10;
                // Find closest centroid
                for(int i = 0; i < nTotal; i++) {
                    double d = centroids[i].distance(point);
                    if (d < d_min) {
                        d_min = d;
                        i_found = i;
                    }
                }
                // Add to Centroid
                centroids[i_found].addPoint(point, d_min);
            }
            // Update location and identify err
            double err = 0;
            for(SphericalDistribution1D centroid : centroids) {
                err += centroid.update();
            }
            
            if (err < tol) {
                break;
            }
        }
        
        return centroids;
    }
    
    public static SphericalDistribution1D[] cluster(double[][] data, int nScatter) {
        double[] s = stats(data);
        
        double[][] initial = new double[nScatter][3];
        Random rand = new Random();
        double R = 6371000;
        for(int i = 0; i < nScatter; i++){
            double azimuth = rand.nextGaussian()*s[1];
            double zenith = rand.nextGaussian()*s[2];
            initial[i] = new double[] {R*Math.cos(azimuth)*Math.cos(zenith),R*Math.sin(azimuth)*Math.cos(zenith),R*Math.sin(zenith) };
        }
        
        return cluster(data, initial, nScatter,1000);
    }
    
    public static double[][] clusterv2(double[][] data, double[][] initial, int nScatter, double tol) {
        int nPresets = initial.length;
        int nTotal = nPresets+nScatter;
        double[] rc = new double[3];
        
        double[][] centroids = new double[nTotal][11];
        for(int i = 0; i < nPresets; i++){
            System.arraycopy(initial[i], 0, centroids[i], 0, 3); 
        }
        
        for(int iter = 0; iter < 200; iter++) {
            
            for(double[] centroid : centroids) {
                for(int j = 3; j < 10;j++) {
                    centroid[j] = 0;
                }
            }
            
            for(double[] point : data) {
                int i_found  = 0;
                double d_min = 7;
                for(int i = 0; i < nTotal; i++) {
                    double d = CoordinateException.flatEarthDistance(point[0],centroids[i][0],point[1],centroids[i][1]);
                    if (d < d_min) {
                        i_found = i;
                        d_min = d;
                    }
                }
                rc[0] = centroids[i_found][0];
                rc[1] = centroids[i_found][1];
                rc[2] = centroids[i_found][2];
                double[] dr = Helper.subtract(point, rc);
                double[] n = Helper.cross(rc, dr);
                centroids[i_found][3] += point[0]; 
                centroids[i_found][4] += point[1]; 
                centroids[i_found][5] += point[2];
                centroids[i_found][6] += 1;
                centroids[i_found][7] += d_min;
                centroids[i_found][8] += n[0];
                centroids[i_found][9] += n[1];
                centroids[i_found][10] += n[2];
            }
            
            double err = 0;
            for(double[] centroid : centroids) {
                double oldx = centroid[0];
                double oldy = centroid[1];
                double oldz = centroid[2];
                centroid[0] = centroid[3]/centroid[6];
                centroid[1] = centroid[4]/centroid[6];
                centroid[2] = centroid[5]/centroid[6];
                err += Math.abs(oldx-centroid[0]) + Math.abs(oldy-centroid[1]) + Math.abs(oldz-centroid[2]);
            }
            
            if (err < tol) {
                break;
            }
        }
        
        return centroids;
    }
    
    
    private static double[] stats(double[][] data) {
        int m = data.length;
        
        double[] azimuth = new double[m];
        double[] zenith = new double[m];
        double[] stats = new double[7];
        for (int i = 0; i < m; i++) {
            azimuth[i] = Math.atan2(data[i][1],data[i][0]);
            double r = Helper.norm(data[i]);
            zenith[i] = Math.asin(data[i][2]/r);
            stats[0] += azimuth[i];
            stats[1] += zenith[i];
        }
        stats[0] /= m;
        stats[1] /= m;
        for (int i = 0; i < m; i++) {
            double d = stats[0]-azimuth[i];
            stats[2] += d*d;
            d = stats[1]-zenith[i];
            stats[3] += d*d;
        }
        stats[3] /= (m-1);
        stats[4] /= (m-1); 
        return stats;
    }
   
}
