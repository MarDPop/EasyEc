/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.clustering;

import com.meicompany.pi.coordinates.CoordinateFrame;
import static com.meicompany.pi.realtime.generalMath.Math2.TWOPI_F;

/**
 *
 * @author mpopescu
 */
public class CentroidPi {
    
    public final float x_Center;
    public final float y_Center;
    public final float sigma_x;
    public final float sigma_y;
    public final float sigma_xy;
    public final float number;
    public final float time;
    
    public final float p;
    public final float ss;
    public final float alpha;
    
    public CentroidPi(float[] stats, double time) {
        this.x_Center = stats[0];
        this.y_Center = stats[1];
        this.number = stats[4];
        this.sigma_x = stats[6];
        this.sigma_y = stats[7];
        this.sigma_xy = stats[8];
        this.ss = (float)Math.sqrt(stats[6]*stats[7]);
        this.p = stats[8]/ss;
        this.alpha = (float)Math.sqrt(1-p*p);
        this.time = (float)time;
    }
    
    public float calcAt(float x, float y) {
        float dx = x-x_Center;
        float dy = y-y_Center;
        if(number > 3) {
            return number*(float)Math.exp(-(dx*dx/sigma_x+dy*(dy/sigma_y-2*p*dx/ss))/(2*alpha*alpha))/(TWOPI_F*alpha*ss);
        } else {
            double tmp = 2*(sigma_y+sigma_x);
            return (float)(number/Math.sqrt(Math.PI*tmp)*Math.exp(-(dx*dx+dy*dy)/tmp));
        }
    }
    
    public double calcAtLongLat(float longitude, float latitude) {
        float[] xy = CoordinateFrame.ll2xy(new float[]{longitude,latitude});
        return calcAt(xy[0],xy[1]);
    }
}
