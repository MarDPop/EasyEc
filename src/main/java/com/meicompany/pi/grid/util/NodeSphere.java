/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.grid.util;

import com.meicompany.pi.realtime.Helper;

/**
 *
 * @author mpopescu
 */
public class NodeSphere {
    //of center
    public final double longitude;
    public final double latitude;
    public final double size;
    
    private double value;
    
    public NodeSphere parent = null;
    public NodeSphere[] children = null;
    
    public static final double UPPER_LEFT = 1;
    public static final double UPPER_RIGHT = 2;
    public static final double LOWER_LEFT = -1;
    public static final double LOWER_RIGHT = -2;
    
    public NodeSphere(double longitude, double latitude, double size) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.size = size;
    }
    
    public NodeSphere(NodeSphere parent, int corner) {
        // 2 = upper right, 1 = upper left, -1 = lower left, -2 = lower right
        // 0 = upper right, 1 = upper left, 2 = lower left, 3 = lower right
        this.parent = parent;
        this.size = parent.size/2;
        this.value = parent.getValue();
        if (corner % 2 == 0) {
            this.longitude = parent.longitude + this.size;
        } else {
            this.longitude = parent.longitude  - this.size;
        }
        if (corner > 0) {
            this.latitude = parent.latitude + this.size;
        } else {
            this.latitude = parent.latitude - this.size;
        }
    }
    
    public void divide() {
        this.children = new NodeSphere[4];
        // 0 = upper right, 1 = upper left, 2 = lower left, 3 = lower right
        // children are in classical quadrant definition
        this.children[0] = new NodeSphere(this,2);
        this.children[1] = new NodeSphere(this,1);
        this.children[2] = new NodeSphere(this,-1);
        this.children[3] = new NodeSphere(this,-2);
    }
    
    public double distance(double longitude, double latitude) {
        return Helper.flatEarthDistance(longitude, latitude, this.longitude, this.latitude);
    }
    
    public void setValue(double value) {
        this.value = value;
        if (children != null) {
            for(NodeSphere child : children) {
                child.setValue(value);
            }
        }
        // might add a statement if parent to recalc average value ?
    }
    
    public double getValue() {
        return value;
    }
    
    public double getValue(double longitude, double latitude){
        if(children == null){
            if(parent == null) {
                return value;
            } else {
                double sumDen = parent.distance(longitude,latitude);
                if (sumDen < 1e-20) {
                    return parent.getValue();
                }
                sumDen = 1/sumDen;
                double sumNum = sumDen*parent.getValue();
                for(int i = 0; i < 4; i++) {
                    double d = parent.children[i].distance(longitude,latitude);
                    if(d < 1) {
                        return parent.children[i].getValue();
                    } else {
                        d = 1/d;
                        sumDen += d;
                        sumNum += parent.children[i].getValue()*d;
                    }
                }
                return sumNum/sumDen;
            }
        } else {
            // 0 = upper right, 1 = upper left, 2 = lower left, 3 = lower right
            if(longitude < this.longitude) {
                if(latitude < this.latitude) {
                    return children[2].getValue(longitude, latitude);
                } else {
                    return children[1].getValue(longitude, latitude);
                }
            } else {
                if(latitude < this.latitude) {
                    return children[3].getValue(longitude, latitude);
                } else {
                    return children[0].getValue(longitude, latitude);
                }
            }
        }
    }
    
}
