package com.meicompany.pi.realtime.clustering;

import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.realtime.generalMath.Math2;
import java.util.List;

/**
 *
 * @author mpopescu
 */
public class SphericalDistribution extends Centroid{
    private double[] directionVector1;
    private double[] directionVector2;
    private double weight;
    
    public SphericalDistribution() {
        super();
        this.location = new double[3];
        this.locationTemp = new double[3];
        this.stats = new double[4];
    }
    
    public double update(){
        this.locationTemp[0] /= n;
        this.locationTemp[1] /= n;
        this.locationTemp[2] /= n;
        double[] err = Math2.subtract(location, locationTemp);
        this.location = locationTemp;
        reset();
        return Math2.norm(err);
    }
    
    public double distance(double[] r) {
        return CoordinateFrame.earthDistanceSpherical(location, r);
    }
    
    public void addPoint(double[] r, double d, int i) {
        this.locationTemp[0] += r[0];
        this.locationTemp[1] += r[1];
        this.locationTemp[2] += r[2];
        this.stats[0] += d*d;
        this.n++;
        this.ids.add(i);
    }
    
    public double[] calcStats(List<double[]> points) {
        directionVector1 = new double[3];
        directionVector2 = new double[3];
        if (n > 1) {
            stats[0] = Math.sqrt(stats[0])/(n-1);
            for(int i = 1; i < stats.length; i++) {
                stats[i] = 0;
            }
            double[][] dr = new double[n][3];
            double[] d = new double[n];
            int i = 0;
            // Compute average direction
            // Doesnt this already implicitly compute mean distance along average direction?
            for(double[] point : points) {
                dr[i] = Math2.subtract(point, location);
                d[i] = Math2.norm(dr[i]);
                double[] n = Math2.cross(location, dr[i]);
                this.directionVector1[0] += n[0]/d[i];
                this.directionVector1[1] += n[1]/d[i];
                this.directionVector1[2] += n[2]/d[i];
                i++;
            }
            this.directionVector2 = Math2.cross(location, directionVector1); // may not be necessary if mean to be chosen as 0
            this.directionVector1 = Math2.normalize(directionVector1);
            this.directionVector2 = Math2.normalize(directionVector2);
            // Compute standard deviation along average direction
            for(int j = 0; j < n; j++) {
                double delta1 = stats[1]-Math2.dot(directionVector1, dr[j]);
                stats[1] += delta1*delta1;
                double delta2 = stats[2]-Math2.dot(directionVector2, dr[j]);
                stats[2] += delta2*delta2;
                stats[3] += delta1*delta2;
            }
        }
        return stats;
    }
    
    public double getProbability(double[] point){
        double p = Math.sqrt(stats[3]/stats[2]/stats[1]);
        double[] dr = Math2.subtract(location, point);
        double proj1 = Math2.dot(directionVector1, dr);
        double proj2 = Math2.dot(directionVector2, dr);
        double temp = Math.sqrt(stats[0]*stats[1]);
        return weight/(2*Math.PI*temp*Math.sqrt((1-p*p)))*Math.exp(proj1*proj1/stats[1]+proj2*proj2/stats[2]-2*p*proj1*proj2/temp);
    }
}
