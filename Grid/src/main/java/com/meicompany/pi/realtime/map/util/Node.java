/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.map.util;

/**
 *
 * @author mpopescu
 */
public class Node {
    //of center
    public final double x;
    public final double y;
    public final double size;
    
    protected double value;
    
    protected Node parent = null;
    protected Node[] children = null;
    
    public static final double UPPER_LEFT = 1;
    public static final double UPPER_RIGHT = 2;
    public static final double LOWER_LEFT = -1;
    public static final double LOWER_RIGHT = -2;
    
    public Node(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }
    
    public Node(Node parent, int corner) {
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
    /**
     * creates children Nodes
     */
    public void divide() {
        this.setChildren(new Node[4]);
        // 0 = upper right, 1 = upper left, 2 = lower left, 3 = lower right
        // children are in classical quadrant definition
        this.getChildren()[0] = new Node(this,2);
        this.getChildren()[1] = new Node(this,1);
        this.getChildren()[2] = new Node(this,-1);
        this.getChildren()[3] = new Node(this,-2);
    }
    
    public double distance(double x, double y) {
        double dx = x-this.x;
        double dy = y-this.y;
        return dx*dx+dy*dy;
    }
    
    public void setValue(double value) {
        this.value = value;
        if (getChildren() != null) {
            for(Node child : getChildren()) {
                child.setValue(value);
            }
        }
        // might add a statement if parent to recalc average value ?
    }
    
    public double getValue() {
        return value;
    }
    
    public double getValue(double x, double y){
        if(getChildren() == null){
            if(getParent() == null) {
                return value;
            } else {
                double sumDen = getParent().distance(x,y);
                if (sumDen < 1e-20) {
                    return getParent().getValue();
                }
                sumDen = 1/sumDen;
                double sumNum = sumDen*getParent().getValue();
                for(int i = 0; i < 4; i++) {
                    double d = getParent().getChildren()[i].distance(x,y);
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
            if(x < this.x) {
                if(y < this.y) {
                    return getChildren()[2].getValue(x, y);
                } else {
                    return getChildren()[1].getValue(x, y);
                }
            } else {
                if(y < this.y) {
                    return getChildren()[3].getValue(x, y);
                } else {
                    return getChildren()[0].getValue(x, y);
                }
            }
        }
    }

    /**
     * @return the parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    public Node[] getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(Node[] children) {
        this.children = children;
    }
    
}
