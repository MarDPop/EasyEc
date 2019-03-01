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
            if(centroid[4] > 3) {
                 temp.add(centroid);
            }
        }
        
        // Reassign to centroids
        centroids = new double[temp.size()][9]; // STICK CENTROIDPI HERE
        nTotal = 0;
        for(double[] centroid : temp) {
            System.arraycopy(centroid, 0, centroids[nTotal++], 0, 2);
        }
        
        double[] cFound = null;
        double d_min;
        // Reassign Points
        for(double[] point : data) {
            d_min = 1e100;
            for(double[] centroid : centroids) {
                double dx = point[0]-centroid[0];
                double dy = point[1]-centroid[1];
                double d = dx*dx + dy*dy;
                if (d < d_min) {
                    cFound = centroid;
                    d_min = d;
                }
            }
            cFound[2] += point[0]; 
            cFound[3] += point[1]; 
            cFound[4] += 1;
        }
        
        // shift
        for(double[] c1 : centroids) {
            c1[0] = c1[2]/c1[4]; 
            c1[1] = c1[3]/c1[4]; 
        }
        
        // Recalc
        double dx2 = 0;
        double dy2 = 0;
        double dxy = 0;
        for(double[] point : data) {
            d_min = 1e100;
            for(double[] centroid : centroids) {
                double dx = point[0]-centroid[0];
                double dy = point[1]-centroid[1];
                double tmp = dx*dy;
                dx*=dx;
                dy*=dy;
                double d = dx + dy;
                if (d < d_min) {
                    cFound = centroid;
                    d_min = d;
                    dx2 = dx;
                    dy2 = dy;
                    dxy = tmp;
                }
            }
            cFound[4] += 1;
            cFound[5] += d_min;
            cFound[6] += dx2;
            cFound[7] += dy2;
            cFound[8] += dxy;
        }
        
        // Add closest centroid to data without changing number (Provides Smoothing)
        for(double[] c1 : centroids) {            
            d_min = 1e100;
            dx2 = 0;
            dy2 = 0;
            dxy = 0;
            for(double[] c2 : centroids) {
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
        
        return centroids;
    }
    
    public static float[][] cluster(float[][] data, float[][] initial, float tol) {
        int nTotal = initial.length;
        
        float dx;
        float dy;
        float dx2;
        float dy2;
        float dxy;
        float d;
        float tmp;
        
        float[][] centroids = new float[nTotal][9];
        for(int i = 0; i < nTotal; i++){
            System.arraycopy(initial[i], 0, centroids[i], 0, 2); 
        }
        for(int iter = 0; iter < 200; iter++) {
            for(float[] centroid : centroids) {
                for(int j = 2; j < 9;j++) {
                    centroid[j] = 0;
                }
            }
            // Collect points to centroid
            for(float[] point : data) {
                int foundIndex  = 0;
                float minimumDistance = 1e30f;
                dx2 = 0;
                dy2 = 0;
                dxy = 0;
                
                for(int i = 0; i < nTotal; i++) {
                    dx = point[0]-centroids[i][0];
                    dy = point[1]-centroids[i][1];
                    tmp = dx*dy;
                    dx *= dx;
                    dy *= dy;
                    d = dx + dy;
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
            float err = 0;
            for(float[] centroid : centroids) {
                if (centroid[4] > 0) {
                    float oldx = centroid[0];
                    float oldy = centroid[1];
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
        ArrayList<float[]> temp = new ArrayList<>();
        for(float[] centroid : centroids) {
            // Remove centroids with no data
            if(centroid[4] > 3) {
                 temp.add(centroid);
            }
        }
        
        // Reassign to centroids
        centroids = new float[temp.size()][9]; // STICK CENTROIDPI HERE
        nTotal = 0;
        for(float[] centroid : temp) {
            System.arraycopy(centroid, 0, centroids[nTotal++], 0, 2);
        }
        
        float[] cFound = null;
        float d_min;
        // Reassign Points
        for(float[] point : data) {
            d_min = 1e30f;
            for(float[] centroid : centroids) {
                dx = point[0]-centroid[0];
                dy = point[1]-centroid[1];
                d = dx*dx + dy*dy;
                if (d < d_min) {
                    cFound = centroid;
                    d_min = d;
                }
            }
            cFound[2] += point[0]; 
            cFound[3] += point[1]; 
            cFound[4] += 1;
        }
        
        // shift
        for(float[] c1 : centroids) {
            c1[0] = c1[2]/c1[4]; 
            c1[1] = c1[3]/c1[4]; 
        }
        
        // Recalc
        dx2 = 0;
        dy2 = 0;
        dxy = 0;
        for(float[] point : data) {
            d_min = 1e30f;
            for(float[] centroid : centroids) {
                dx = point[0]-centroid[0];
                dy = point[1]-centroid[1];
                tmp = dx*dy;
                dx*=dx;
                dy*=dy;
                d = dx + dy;
                if (d < d_min) {
                    cFound = centroid;
                    d_min = d;
                    dx2 = dx;
                    dy2 = dy;
                    dxy = tmp;
                }
            }
            cFound[4] += 1;
            cFound[5] += d_min;
            cFound[6] += dx2;
            cFound[7] += dy2;
            cFound[8] += dxy;
        }
        
        // Add closest centroid to data without changing number (Provides Smoothing)
        for(float[] c1 : centroids) {            
            d_min = 1e30f;
            dx2 = 0;
            dy2 = 0;
            dxy = 0;
            for(float[] c2 : centroids) {
                if(c2 != c1) {
                    dx = c2[0]-c1[0];
                    dy = c2[1]-c1[1];
                    tmp = dx*dy;
                    dx *= dx;
                    dy *= dy;
                    d = dx + dy;
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
        
        return centroids;
    }
    
    public static float[][] cluster(float[][] data, int nScatter) {
        float[] s = stats(data);
        
        float[][] initial = new float[nScatter+5][2];
        
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
            initial[i][0] = s[0]+s[2]*(float)rand.nextGaussian();
            initial[i][1] = s[1]+s[3]*(float)rand.nextGaussian();
        }
        
        float tol = Math.abs(s[4]-s[5])+Math.abs(s[6]-s[7]);
        tol /= 2000;
        
        return cluster(data, initial,tol);
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
    
    private static float[] stats(float[][] data) {
        int m = data.length;
        float x_min = 1e10f;
        float x_max = -1e10f;
        float y_min = 1e10f;
        float y_max = -1e10f;
        float x_C = 0;
        float y_C = 0;
        
        for (int i = 0; i < m; i++) {
            float x = data[i][0];
            float y = data[i][1];
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
        
        float std_x = 0;
        float std_y = 0;
        
        for (int i = 0; i < m; i++) {
            float dx = data[i][0]-x_C;
            float dy = data[i][1]-y_C;
            
            std_x += dx*dx;
            std_y += dy*dy;
        }
        
        std_x /= (m-1);
        std_y /= (m-1);
        
        return new float[]{x_C,y_C,(float)Math.sqrt(std_x),(float)Math.sqrt(std_y),x_min,x_max,y_min,y_max};
    }
   
}
