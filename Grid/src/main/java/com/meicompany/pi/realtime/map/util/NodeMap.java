/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.map.util;

import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.realtime.generalMath.Math2;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author mpopescu
 */
public class NodeMap {
    
    final double longitudeCenter;
    final double latitudeCenter;
    final int m;
    final int n;
    public final Node[][] nodes;
    final double delta;
    
    private short lowestLevel;
    
    public NodeMap(double longitudeCenter, double latitudeCenter, int m, int n, double delta) {
        this.longitudeCenter = longitudeCenter;
        this.latitudeCenter = latitudeCenter;
        this.m = m;
        this.n = n;
        this.nodes = new Node[2*m+1][2*n+1];
        this.delta = delta;
        for(int i = 0; i < (m+1); i++){
            for(int j = 0; j < (n+1); j++) {
                nodes[m+i][n+j] = new Node(longitudeCenter+i*delta,latitudeCenter+j*delta,delta);
                nodes[m+i][n-j] = new Node(longitudeCenter+i*delta,latitudeCenter-j*delta,delta);
            }
            for(int j = 0; j < (n+1); j++) {
                nodes[m-i][n+j] = new Node(longitudeCenter-i*delta,latitudeCenter+j*delta,delta);
                nodes[m-i][n-j] = new Node(longitudeCenter-i*delta,latitudeCenter-j*delta,delta);
            }
        }
    }
    
    /**
     * 
     * @param longitude
     * @param latitude
     * @return 
     */
    public Node getNodeAt(double longitude, double latitude) {
        int i = (int) ((longitude-longitudeCenter)/delta)+m;
        int j = (int) ((latitude-latitudeCenter)/delta)+n;
        return nodes[i][j];
    }
    
    public double getValue(double longitude, double latitude) {
        return getNodeAt(longitude,latitude).getValue(longitude,latitude);
    }
    
    public List<double[]> nodeGrid() {
        short depth = 0;
        ArrayList<double[]> points = new ArrayList<>();
        for(Node[] row : nodes) {
            dive(points,row,depth);
        }
        return points;
    }
    
    public void printCsv() {
        try (FileWriter fw = new FileWriter("World.csv"); PrintWriter out = new PrintWriter(fw)) {
            List<double[]> points = nodeGrid();
            for (double[] point : points) {
                out.print(point[0]);
                out.print(",");
                out.print(point[1]);
                out.print(",");
                out.print(point[2]);
                out.println();
            }
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Math2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public double smallestAngle() {
        // returns NaN if points hasn't run
        return this.delta/(Math.pow(2, lowestLevel));
    }
    
    private void dive(ArrayList<double[]> points, Node[] list, short depth) {
        depth++;
        if (depth > lowestLevel) {
            lowestLevel = depth;
        }
        for(Node child : list) {
            points.add(new double[]{child.longitude,child.latitude,child.getValue(),child.size});
            if(child.getChildren() != null) {      
                dive(points, child.getChildren(),depth);
            }
        }
    }
    
    public SparseFloat toSparse(float baseValue){
        List<double[]> points = nodeGrid();
        double a = smallestAngle();
        double a2 = a/2; //bias
        SparseFloat out = new SparseFloat(2*m+1,2*n+1,baseValue);
        points.forEach(point -> {
            int i = (int)((point[0]+a2)/a);
            int j = (int)((point[0]+a2)/a);
            out.add(i, j, (float)point[2]);
        });
        return out;
    }
    
    public JSONObject toJson(boolean convert) {
        JSONObject jo = new JSONObject();
        Collection<JSONObject> arr = new ArrayList<>();
        List<double[]> points = nodeGrid();
        points.forEach(point -> {
            JSONObject p = new JSONObject();
            if(convert){
                double[] c = CoordinateFrame.xy2ll(point);
                p.put("latitude",c[1]);
                p.put("longitude",c[0]);
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
