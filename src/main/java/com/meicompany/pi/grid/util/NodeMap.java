/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.grid.util;

import com.meicompany.pi.realtime.Helper;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author mpopescu
 */
public class NodeMap {
    
    final double xCenter;
    final double yCenter;
    final int m;
    final int n;
    final public NodeFlat[][] nodes;
    final double delta;
    
    private short lowestLevel;
    
    public NodeMap(double xCenter, double yCenter, int m, int n, double delta) {
        this.xCenter = xCenter;
        this.yCenter = yCenter;
        this.m = m;
        this.n = n;
        this.nodes = new NodeFlat[2*m+1][2*n+1];
        this.delta = delta;
        for(int i = 0; i < (m+1); i++){
            for(int j = 0; j < (n+1); j++) {
                nodes[m+i][n+j] = new NodeFlat(xCenter+i*delta,yCenter+j*delta,delta);
                nodes[m+i][n-j] = new NodeFlat(xCenter+i*delta,yCenter-j*delta,delta);
            }
            for(int j = 0; j < (n+1); j++) {
                nodes[m-i][n+j] = new NodeFlat(xCenter-i*delta,yCenter+j*delta,delta);
                nodes[m-i][n-j] = new NodeFlat(xCenter-i*delta,yCenter-j*delta,delta);
            }
        }
    }
    
    public NodeFlat getNodeAt(double x, double y) {
        int i = (int) ((x-xCenter)/delta)+m;
        int j = (int) ((y-yCenter)/delta)+n;
        return nodes[i][j];
    }
    
    public double getValue(double x, double y) {
        return getNodeAt(x,y).getValue(x,y);
    }
    
    public ArrayList<double[]> nodeGrid() {
        short depth = 0;
        ArrayList<double[]> points = new ArrayList<>();
        for(NodeFlat[] row : nodes) {
            dive(points,row,depth);
        }
        return points;
    }
    
    public void printCsv() {
        try (FileWriter fw = new FileWriter("World.csv"); PrintWriter out = new PrintWriter(fw)) {
            ArrayList<double[]> points = nodeGrid();
            for (double[] point : points) {
                for (double d : point) {
                    out.print(d);
                    out.print(",");
                }
                out.println();
            }
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public double smallestAngle() {
        // returns NaN if points hasn't run
        return this.delta/(Math.pow(2, lowestLevel));
    }
    
    private void dive(ArrayList<double[]> points, NodeFlat[] list, short depth) {
        depth++;
        if (depth > lowestLevel) {
            lowestLevel = depth;
        }
        for(NodeFlat child : list) {
            points.add(new double[]{child.x,child.y,child.getValue(),child.size});
            if(child.children != null) {      
                dive(points,child.children,depth);
            }
        }
    }
    
    public SparseFloat toSparse(float baseValue){
        ArrayList<double[]> points = nodeGrid();
        double a = smallestAngle();
        double a2 = a/2; //bias
        SparseFloat out = new SparseFloat(2*m+1,2*n+1,baseValue);
        points.forEach((point) -> {
            int i = (int)((point[0]+a2)/a);
            int j = (int)((point[0]+a2)/a);
            out.add(i, j, (float)point[2]);
        });
        return out;
    }
    
    public JSONObject toJson(boolean convert) {
        JSONObject jo = new JSONObject();
        Collection<JSONObject> arr = new ArrayList<>();
        ArrayList<double[]> points = nodeGrid();
        points.forEach((point) -> {
            JSONObject p = new JSONObject();
            if(convert){
                double[] c = Helper.xy2ll(point);
                p.put("latitude",c[0]);
                p.put("longitude",c[1]);
            } else {
                p.put("x",point[0]);
                p.put("y",point[1]);
            }
            p.put("value",point[2]);
            arr.add(p);
        });
        jo.put("Points", new JSONArray(arr));
        return jo;
    }
    
}
