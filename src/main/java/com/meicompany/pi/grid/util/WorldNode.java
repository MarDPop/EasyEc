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
public class WorldNode {
    
    final public NodeFlat[][] nodes;
    final double angle;
    final int equator;
    
    private short lowestLevel;
    private final double PI2 = Math.PI/2;
    
    public WorldNode(int m) {
        this.nodes = new NodeFlat[2*m][m];
        this.angle = Math.PI/m;
        this.equator = (int)(m/2.0 + 0.51);
        for(int i = 0; i < nodes.length; i++){
            for(int j = 0; j < nodes[0].length; j++) {
                nodes[i][j] = new NodeFlat(angle/2+i*angle-Math.PI,angle/2+j*angle-PI2,angle);
            }
        }
    }
    
    public NodeFlat getNodeAt(double longitude, double latitude) {
        int i = (int) ((longitude+Math.PI)/angle);
        int j = (int) ((latitude+PI2)/angle);
        return nodes[i][j];
    }
    
    public double getValue(double longitude, double latitude) {
        return getNodeAt(longitude,latitude).getValue(longitude,latitude);
    }
    
    public ArrayList<double[]> nodeGrid() {
        this.lowestLevel = 0;
        ArrayList<double[]> points = new ArrayList<>();
        for(NodeFlat[] row : nodes) {
            dive(points,row);
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
        return this.angle/(Math.pow(2, lowestLevel));
    }
    
    private void dive(ArrayList<double[]> points, NodeFlat[] list) {
        this.lowestLevel++;
        for(NodeFlat child : list) {
            if(child.getChildren() == null) {
                points.add(new double[]{child.x,child.y,child.getValue(),child.size});
            } else {
                dive(points, child.getChildren());
            }
        }
    }
    
    public SparseFloat toSparse(float baseValue){
        ArrayList<double[]> points = nodeGrid();
        double a = smallestAngle();
        double a2 = a/2; //bias
        int m = (int)(180/a);
        SparseFloat out = new SparseFloat(2*m,m,baseValue);
        points.forEach((point) -> {
            int i = (int)((point[0]+a2)/a);
            int j = (int)((point[0]+a2)/a);
            out.add(i, j, (float)point[2]);
        });
        return out;
    }
    
    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        Collection<JSONObject> arr = new ArrayList<>();
        ArrayList<double[]> points = nodeGrid();
        points.forEach((point) -> {
            JSONObject p = new JSONObject();
            p.put("latitude",point[0]);
            p.put("longitude",point[1]);
            p.put("value",point[2]);
            arr.add(p);
        });
        jo.put("Points", new JSONArray(arr));
        return jo;
    }
}
