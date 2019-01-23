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
    
    private NodeSphere parent = null;
    private NodeSphere[] children = null;
    
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
        this.setChildren(new NodeSphere[4]);
        // 0 = upper right, 1 = upper left, 2 = lower left, 3 = lower right
        // children are in classical quadrant definition
        this.getChildren()[0] = new NodeSphere(this,2);
        this.getChildren()[1] = new NodeSphere(this,1);
        this.getChildren()[2] = new NodeSphere(this,-1);
        this.getChildren()[3] = new NodeSphere(this,-2);
    }
    
    public double distance(double longitude, double latitude) {
        return Helper.flatEarthDistance(longitude, latitude, this.longitude, this.latitude);
    }
    
    public void setValue(double value) {
        this.value = value;
        if (getChildren() != null) {
            for(NodeSphere child : getChildren()) {
                child.setValue(value);
            }
        }
        // might add a statement if parent to recalc average value ?
    }
    
    public double getValue() {
        return value;
    }
    
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
            if(longitude < this.longitude) {
                if(latitude < this.latitude) {
                    return getChildren()[2].getValue(longitude, latitude);
                } else {
                    return getChildren()[1].getValue(longitude, latitude);
                }
            } else {
                if(latitude < this.latitude) {
                    return getChildren()[3].getValue(longitude, latitude);
                } else {
                    return getChildren()[0].getValue(longitude, latitude);
                }
            }
        }
    }

    /**
     * @return the parent
     */
    public NodeSphere getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(NodeSphere parent) {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    public NodeSphere[] getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(NodeSphere[] children) {
        this.children = children;
    }
    
}
