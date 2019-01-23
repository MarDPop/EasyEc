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
public class BoundingBox {
    public final double[] longitudes = new double[4];
    public final double[] latitudes = new double[4];
    public final double[][] pointsXY = new double[4][2];
    public final double[][] pointsLL = new double[4][2];
    
    public BoundingBox(double[] stats){
        double p = stats[4]/Math.sqrt(stats[2]*stats[3]);
        double slope = Math.sqrt(stats[3]/stats[2]);
        double sigma = 3*Math.sqrt(stats[3]+stats[2]);
        double x_C = stats[0];
        double y_C = stats[1];
        double invSlope = -1/slope;
        double d1 = p*sigma;
        double d2 = Math.sqrt(1-p*p)*sigma;
        double dx = d1/Math.sqrt(1+slope*slope);
        double dy = d1/Math.sqrt(1+1/slope/slope);
        double p1x = x_C + dx;
        double p1y = y_C + dy;
        double p2x = x_C - dx;
        double p2y = y_C - dy;
        dx = d2/Math.sqrt(1+invSlope*invSlope);
        dy = d2/Math.sqrt(1+1/invSlope/invSlope);
        pointsXY[0][0] = p1x-dx;
        pointsXY[0][1] = p1y+dy;
        pointsXY[1][0] = p1x+dx;
        pointsXY[1][1] = p1y-dy;
        pointsXY[2][0] = p2x-dx;
        pointsXY[2][1] = p2y+dy;
        pointsXY[3][0] = p2x+dx;
        pointsXY[3][1] = p2y-dy;
        for(int i = 0; i < 4; i++){
            double[] ll = Helper.xy2ll(pointsXY[i]);
            latitudes[i] = ll[0];
            longitudes[i] = ll[1];
            pointsLL[i][0] = ll[0];
            pointsLL[i][1] = ll[1];
        }
    }
}
