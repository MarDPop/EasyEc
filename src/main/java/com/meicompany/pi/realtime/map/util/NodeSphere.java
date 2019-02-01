/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.map.util;

import com.meicompany.pi.coordinates.Coordinates;

/**
 *
 * @author mpopescu
 */
public class NodeSphere extends Node{
    
    public NodeSphere(double longitude, double latitude, double size) {
        super(longitude,latitude,size);
    }
    
    public NodeSphere(NodeSphere parent, int corner) {
        super(parent,corner);
    }
    
    @Override
    public double distance(double longitude, double latitude) {
        return Coordinates.earthDistanceSpherical(longitude, latitude, this.x, this.y);
    }
    
    @Override
    public double getValue(double longitude, double latitude){
        if(getChildren() == null){
            if(getParent() == null) {
                return value;
            } else {
                double sumDen = getParent().distance(longitude,latitude);
                if (sumDen < 1e-20) {
                    return getParent().getValue();
                }
                sumDen = 1/sumDen;
                double sumNum = sumDen*getParent().getValue();
                for(int i = 0; i < 4; i++) {
                    double d = getParent().getChildren()[i].distance(longitude,latitude);
                    if(d < 1) {
                        return getParent().getChildren()[i].getValue();
                    } else {
                        d = 1/d;
                        sumDen += d;
                        sumNum += getParent().getChildren()[i].getValue()*d;
                    }
                }
                return sumNum/sumDen;
            }
        } else {
            // 0 = upper right, 1 = upper left, 2 = lower left, 3 = lower right
            if(longitude < this.x) {
                if(latitude < this.y) {
                    return getChildren()[2].getValue(longitude, latitude);
                } else {
                    return getChildren()[1].getValue(longitude, latitude);
                }
            } else {
                if(latitude < this.x) {
                    return getChildren()[3].getValue(longitude, latitude);
                } else {
                    return getChildren()[0].getValue(longitude, latitude);
                }
            }
        }
    }
    
}
