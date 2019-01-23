/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.grid.util;

/**
 *
 * @author mpopescu
 */
public class NodeFlat {
    //of center
    public final double x;
    public final double y;
    public final double size;
    
    private double value;
    
    public NodeFlat parent = null;
    public NodeFlat[] children = null;
    
    public static final double UPPER_LEFT = 1;
    public static final double UPPER_RIGHT = 2;
    public static final double LOWER_LEFT = -1;
    public static final double LOWER_RIGHT = -2;
    
    public NodeFlat(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }
    
    public NodeFlat(NodeFlat parent, int corner) {
        // 2 = upper right, 1 = upper left, -1 = lower left, -2 = lower right
        // 0 = upper right, 1 = upper left, 2 = lower left, 3 = lower right
        this.parent = parent;
        this.size = parent.size/2;
        this.value = parent.getValue();
        if (corner % 2 == 0) {
            this.x = parent.x + this.size;
        } else {
            this.x = parent.x  - this.size;
        }
        if (corner > 0) {
            this.y = parent.y + this.size;
        } else {
            this.y = parent.y - this.size;
        }
    }
    
    public void divide() {
        this.children = new NodeFlat[4];
        // 0 = upper right, 1 = upper left, 2 = lower left, 3 = lower right
        // children are in classical quadrant definition
        this.children[0] = new NodeFlat(this,2);
        this.children[1] = new NodeFlat(this,1);
        this.children[2] = new NodeFlat(this,-1);
        this.children[3] = new NodeFlat(this,-2);
    }
    
    public double distance(double x, double y) {
        double dx = x-this.x;
        double dy = y-this.y;
        return dx*dx+dy*dy;
    }
    
    public void setValue(double value) {
        this.value = value;
        if (children != null) {
            for(NodeFlat child : children) {
                child.setValue(value);
            }
        }
        // might add a statement if parent to recalc average value ?
    }
    
    public double getValue() {
        return value;
    }
    
    public double getValue(double x, double y){
        if(children == null){
            if(parent == null) {
                return value;
            } else {
                double sumDen = parent.distance(x,y);
                if (sumDen < 1e-20) {
                    return parent.getValue();
                }
                sumDen = 1/sumDen;
                double sumNum = sumDen*parent.getValue();
                for(int i = 0; i < 4; i++) {
                    double d = parent.children[i].distance(x,y);
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
            if(x < this.x) {
                if(y < this.y) {
                    return children[2].getValue(x, y);
                } else {
                    return children[1].getValue(x, y);
                }
            } else {
                if(y < this.y) {
                    return children[3].getValue(x, y);
                } else {
                    return children[0].getValue(x, y);
                }
            }
        }
    }
    
}
