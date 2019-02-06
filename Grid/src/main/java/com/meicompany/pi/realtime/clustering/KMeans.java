/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.clustering;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author mpopescu
 */
public class KMeans {
    
    private KMeans(){}
    
    public static double[][] cluster(double[][] data, double[][] initial, double tol) {
        int nTotal = initial.length;
        double[][] centroids = new double[nTotal][9];
        for(int i = 0; i < nTotal; i++){
            System.arraycopy(initial[i], 0, centroids[i], 0, 2); 
        }
        for(int iter = 0; iter < 200; iter++) {
            for(double[] centroid : centroids) {
                for(int j = 2; j < 9;j++) {
                    centroid[j] = 0;
                }
            }
            // Collect points to centroid
            for(double[] point : data) {
                int foundIndex  = 0;
                double minimumDistance = 1e100;
                double dx2 = 0;
                double dy2 = 0;
                double dxy = 0;
                
                for(int i = 0; i < nTotal; i++) {
                    double dx = point[0]-centroids[i][0];
                    double dy = point[1]-centroids[i][1];
                    double tmp = dx*dy;
                    dx *= dx;
                    dy *= dy;
                    double d = dx + dy;
                    if (d < minimumDistance) {
                        foundIndex = i;
                        minimumDistance = d;
                        dx2 = dx;
                        dy2 = dy;
                        dxy = tmp;
                    }
                }
                centroids[foundIndex][2] += point[0]; 
                centroids[foundIndex][3] += point[1]; 
                centroids[foundIndex][4] += 1;
                centroids[foundIndex][5] += minimumDistance;
                centroids[foundIndex][6] += dx2;
                centroids[foundIndex][7] += dy2;
                centroids[foundIndex][8] += dxy;
            }
            // Move and evaluate stop 
            double err = 0;
            for(double[] centroid : centroids) {
                if (centroid[4] > 0) {
                    double oldx = centroid[0];
                    double oldy = centroid[1];
                    centroid[0] = centroid[2]/centroid[4];
                    centroid[1] = centroid[3]/centroid[4];
                    err += Math.abs(oldx-centroid[0]) + Math.abs(oldy-centroid[1]);
                }
            }
            
            if (err < tol) {
                break;
            }
        }
        // Clean up bad data
        ArrayList<double[]> temp = new ArrayList<>();
        
        for(double[] centroid : centroids) {
            // Remove centroids with no data
            if(centroid[4] > 0) {
                // Absorb centroids with little data
                if (centroid[4] < 4) {
                    double d_min = 1e100;
                    double[] cFound = null;
                    double dxFound = 0;
                    double dyFound = 0;
                    for(double[] c2 : centroids) {
                        if(c2 != centroid && c2[4] > 0) {
                            double dx = c2[0]-centroid[0];
                            double dy = c2[1]-centroid[1];
                            double d = dx*dx + dy*dy;
                            if (d < d_min) {
                                d_min = d;
                                cFound = c2;
                                dxFound = dx;
                                dyFound = dy;
                            }
                        }
                    }
                    if (cFound == null) {
                        System.out.println(d_min);
                    }
                    cFound[5] += d_min*centroid[4];
                    d_min = cFound[4]+centroid[4];
                    cFound[0] = (cFound[0]*cFound[4] + centroid[0]*centroid[4])/d_min;
                    cFound[1] = (cFound[1]*cFound[4] + centroid[1]*centroid[4])/d_min;
                    cFound[4] = d_min;
                    cFound[6] += dxFound*dxFound*centroid[4];
                    cFound[7] += dyFound*dyFound*centroid[4];
                    cFound[8] += dyFound*dxFound*centroid[4];
                } else {
                    temp.add(centroid);
                }
            }
            
        }
        // Add closest centroid to data without changing number
        for(double[] c1 : temp) {
            double d_min = 1e100;
            double dx2 = 0;
            double dy2 = 0;
            double dxy = 0;
            for(double[] c2 : temp) {
                if(c2 != c1) {
                    double dx = c2[0]-c1[0];
                    double dy = c2[1]-c1[1];
                    double tmp = dx*dy;
                    dx *= dx;
                    dy *= dy;
                    double d = dx + dy;
                    if (d < d_min) {
                        d_min = d;
                        dx2 = dx;
                        dy2 = dy;
                        dxy = tmp;
                    }
                }
            }
            c1[5] += d_min;
            c1[6] += dx2;
            c1[7] += dy2;
            c1[8] += dxy;
            // get Standard deviation
            for(int j = 5; j < 9; ++j) {
                c1[j] /= c1[4];
            }
        }
        // Reassign to centroids
        centroids = new double[temp.size()][9];
        nTotal = 0;
        for(double[] centroid : temp) {
            System.arraycopy(centroid, 0, centroids[nTotal++], 0, 9);
        }
        return centroids;
    }
    
    
    
    
    public static double[][] cluster(double[][] data, int nScatter) {
        double[] s = stats(data);
        
        double[][] initial = new double[nScatter+5][2];
        
        initial[0][0] = s[0];
        initial[0][1] = s[1];
        
        initial[1][0] = s[0]+s[2];
        initial[1][1] = s[1];
        initial[2][0] = s[0]-s[2];
        initial[2][1] = s[1];
        initial[3][0] = s[0];
        initial[3][1] = s[1]+s[3];
        initial[4][0] = s[0];
        initial[4][1] = s[1]-s[3];
        
        Random rand = new Random();
        for(int i = 5; i < initial.length; i++) {
            initial[i][0] = s[0]+s[2]*rand.nextGaussian();
            initial[i][1] = s[1]+s[3]*rand.nextGaussian();
        }
        
        double tol = Math.abs(s[4]-s[5])+Math.abs(s[6]-s[7]);
        tol /= 2000;
        
        return cluster(data, initial,tol);
    }
    
    private static double[] stats(double[][] data) {
        int m = data.length;
        double x_min = 1e10;
        double x_max = -1e10;
        double y_min = 1e10;
        double y_max = -1e10;
        double x_C = 0;
        double y_C = 0;
        
        for (int i = 0; i < m; i++) {
            double x = data[i][0];
            double y = data[i][1];
            x_C += x;
            y_C += y;
            if (x > x_max) {
                x_max = x;
            } 
            if (x < x_min) {
                x_min = x;
            }
            if (y > y_max) {
                y_max = y;
            } 
            if (y < y_min) {
                y_min = y;
            }
        }
        
        x_C /= m;
        y_C /= m;
        
        double std_x = 0;
        double std_y = 0;
        
        for (int i = 0; i < m; i++) {
            double dx = data[i][0]-x_C;
            double dy = data[i][1]-y_C;
            
            std_x += dx*dx;
            std_y += dy*dy;
        }
        
        std_x /= (m-1);
        std_y /= (m-1);
        
        return new double[]{x_C,y_C,Math.sqrt(std_x),Math.sqrt(std_y),x_min,x_max,y_min,y_max};
    }
   
}
