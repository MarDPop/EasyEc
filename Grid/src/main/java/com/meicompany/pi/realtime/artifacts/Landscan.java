/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.artifacts;

import com.meicompany.pi.realtime.IOHelper;
import com.meicompany.pi.realtime.generalMath.Math2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mpopescu
 */
public class Landscan {
    
    private static final String LANDSCAN_FOLDER = "src/main/resources/Landscan/";
    
    List<int[][]> maps = new ArrayList<>();
    List<String> filesLoaded = new ArrayList<>();
    
    byte[] buffer = new byte[500000];
    
    /**
     * Use radians
     * @param latitude
     * @param longitude 
     */
    public void loadMapAtLatLong(double latitude, double longitude) {
        // In radians
        String filename = ""; 
        
        int j = 0;
        if(latitude < 0) {
            filename += "S0";
            j = (int) (-latitude / 0.087266462599716);
        } else {
            filename += "N0";
            j = (int) (latitude / 0.087266462599716) + 1;
        }
        if(j < 2) {
            filename += "0";
        }
        filename += j*5;
        if(longitude < 0) {
            filename += "W";
            j = (int) (-longitude / 0.087266462599716);
        } else {
            filename += "E";
            j = (int) (longitude / 0.087266462599716) + 1;
        }
        if(j < 20) {
            filename += "0";
        }
        if(j < 2) {
            filename += "0";
        }
        filename += j*5;
        filesLoaded.add(filename);
        
        filename += ".bin2";
        
        try {
            //double[][] map = new double[600][600];
            maps.add(IOHelper.loadLandScanFile2(LANDSCAN_FOLDER+filename, buffer));
        } catch (IOException ex) {
            Logger.getLogger(Landscan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static double getLandscanArea(double latitude) {
        // Using Great circle area
        // 1.454441043328550e-04*6371000*6371000
        return 5.903523980437128e+09*(Math.sin(latitude)-Math.sin(latitude-1.454441043328550e-04));
    }
    
    public void loadBox() {
        
    }
    
}
