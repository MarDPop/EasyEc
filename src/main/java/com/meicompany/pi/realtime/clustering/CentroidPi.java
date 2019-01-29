/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.clustering;

import com.meicompany.pi.coordinates.CoordinateException;
import static com.meicompany.pi.realtime.Helper.TWOPI;
import static com.meicompany.pi.realtime.Helper.flatEarthXY;

/**
 *
 * @author mpopescu
 */
public class CentroidPi {
    public final double x_Center;
    public final double y_Center;
    public final double sigma_x;
    public final double sigma_y;
    public final double sigma_xy;
    public final double number;
    
    public final double p;
    public final double ss;
    public final double alpha;
    
    public CentroidPi(double[] basic, double[] xtra) {
        this.x_Center = basic[0];
        this.y_Center = basic[1];
        this.number = basic[4];
        this.sigma_x = basic[6];
        this.sigma_y = basic[7];
        this.sigma_xy = basic[8];
        this.ss = xtra[0];
        this.p = xtra[1];
        this.alpha = xtra[2];
    }
    
    public double calcAt(double x, double y) {
        double dx = x-x_Center;
        double dy = y-y_Center;
        return number*Math.exp(-(dx*dx/sigma_x+dy*(dy/sigma_y-2*p*dx/ss))/(2*alpha*alpha))/(TWOPI*alpha*ss);
    }
    
    public double calcAtLatLong(double latitude, double longitude) {
        double[] xy = CoordinateException.flatEarthXY(latitude,longitude);
        return calcAt(xy[0],xy[1]);
    }
}
