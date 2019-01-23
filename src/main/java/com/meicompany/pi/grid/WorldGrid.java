/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.grid;

import com.meicompany.pi.grid.util.SparseFloat;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

/**
 * World grid represented as a sparse matrix with 
 * geographic cells that cover the world.  If no inputs are specified, the default 
 * cell size of 30 arcsec is used for the grid.  If a numeric scalar is specified 
 * as input, it is assumed to be the grid cell size (in arcsec), and a world grid 
 * structure is created with the specified cell size.  If asparse matrix is input, 
 * it is assumed to be associated with a world grid structure and is returned unaltered. 
 * If a world grid structure is the input parameter, it is also returned unaltered.
 * 
 * @author mpopescu
 */
public class WorldGrid {
    
    /**
     * side length of cells in probability grid [arcsec]
     */
    public int cellSize;
    
    /**
    * sparse matrix containing world grid (unless a world grid was provided as input, all probabilities are initially zero).  
    * <p> Each (i, j) position in the grid identifies the geodetic latitude and longitude of the bottom left corner of a geographic quadrangle on the earth - the value at that position in the grid represents the probability of impact within that quadrangle.  The first grid position, (1, 1), always represents the geographic position (-90 deg N, 0 deg E), and the last grid position, (end, end), always represents the geographic position (90 deg N - cellSizeD, -cellSizeD), where  cellSizeD is the cell size in degrees. </p>
    */
    protected SparseFloat grid;
    
    /**
     * geodetic latitude and longitude of center of distribution [deg +N, deg +E]
     */
    protected double[] center = new double[2];
    
    /**
     * whether values are extrinsic or intrinsic, affects adding (default extrinsic)
     */
    protected boolean extensive = true;
    
    public WorldGrid() {
        this.cellSize = 30;
        this.grid = new SparseFloat(21600,43200,0.0f); // 21600 = 3600/30*180
    }
    
    public WorldGrid(int cellSize) {
        setCellSize(cellSize);
        this.grid = new SparseFloat((int)Math.ceil(648000.0/cellSize),(int)Math.ceil(1296000.0/cellSize),0.0f);
    }
    
    public WorldGrid(String path, int cellSize) throws FileNotFoundException, IOException {
        setCellSize(cellSize);
        this.grid = new SparseFloat((int)Math.ceil(648000.0/cellSize),(int)Math.ceil(1296000.0/cellSize),0.0f);
        BufferedReader br = null;
        String line = "";
        String delimiter = ",";
        try {
            br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] cell = line.split(delimiter);
                grid.set(Integer.parseInt(cell[0]),Integer.parseInt(cell[1]),Float.parseFloat(cell[2]));
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    
    /**
     * 
     * @param cellSize 
     */
    private void setCellSize(int cellSize) {
        if(cellSize < 30) {
            if (30 % cellSize == 0 && cellSize >= 1) {
                this.cellSize = cellSize;
            } else {
                throw new java.lang.InstantiationError("Cell Size is not appropriate");
            }
        } else {
            if(cellSize % 30 == 0) {
                if(cellSize < 324001) {
                    this.cellSize = cellSize;
                } else {
                    throw new java.lang.InstantiationError("Cell Size cannot be larger than 90 deg");
                }
            } else {
                throw new java.lang.InstantiationError("Cell Size is not appropriate");
            }
        }
    }
    
    public WorldGrid(WorldGrid wg) {
        this.cellSize = wg.cellSize;
        this.grid = wg.grid.copy();
        this.center = wg.getCenter();
    }
    
    /**
     * Setter for center
     * @param center 
     */
    public void setCenter(double[] center) {
        this.center = center;
    }
    
    /**
     * getter for center
     * @return 
     */
    public double[] getCenter() {
        return this.center;
    }
    
    /**
     * Getter for grid
     * @return 
     */
    public SparseFloat getGrid() {
        return this.grid;
    }
    
    /**
     * Gets the longitude and latitude of the bottom left corner of the world grid.
     * 
     * @return 
     */
    public double[][] getWorldGridEntryCellCoordinates() {
        Integer[] rows = grid.getEntryRows();
        Integer[] cols = grid.getEntryColumns();
        double[][] out = new double[grid.getEntrySize()][2];
        double cellSpacing = cellSize/3600;
        for(int i = 0; i < grid.getEntrySize(); i++) {
            out[i][0] = rows[i]*cellSpacing;
            out[i][1] = cols[i]*cellSpacing;
        }
        return out;
    }
    
    /**
     * Gets the longitude and latitude of the bottom left corner of each cell in the world grid.
     * 
     * @return 
     */
    public double[][] getWorldGridCellCoordinates() {
        double[][] out = new double[2][];
        int rows = grid.getRows();
        int cols = grid.getColumns();
        out[0] = new double[rows];
        out[1] = new double[cols];
        double cellSpacing = cellSize/3600;
        for(int i = 0; i < rows; i++) {
            out[0][i] = -180 + i*cellSpacing;
        }
        for(int j = 0; j < cols; j++) {
            out[1][j] = -90 + j*cellSpacing;
        }
        return out;
    }
    
    /**
     * Self explanatory
     * 
     * @param lat
     * @param lon 
     * @param v 
     */
    public void setValueAtLatitudeAndLongitude(double lat, double lon, float v){
        int[] idx = findWorldGridCell(lat, lon);
        grid.set(idx[0], idx[1], v);
    }
    
    /**
     * 
     * @param lat
     * @param lon
     */
    public void getValueAtLatitudeAndLongitude(double lat, double lon){
        int[] idx = findWorldGridCell(lat, lon);
        grid.get(idx[0], idx[1]);
    }
    
    /**
     * This function looks up the input latitude and longitude and returns the indices into the world grid.
     * 
     * @param lat
     * @param lon
     * @return 
     */
    public int[] findWorldGridCell(double lat, double lon) {
        int i = floorWithCheck((lat+90.0)*3600.0/cellSize);
        int j = floorWithCheck((lon+180.0)*3600.0/cellSize);
        return new int[] {i,j};
    }
    
    /**
     * 
     * @param d
     * @return 
     */
    private int floorWithCheck(double d) {
        int round = (int)(d + 0.5);
        if(round - d < 1e-10) {
            return round;
        } else {
            return (int) d;
        }
    }
    
    /**
     * 
     * @param i
     * @param j
     * @return 
     */
    public double[] latitudeAndLongitudeAtCoordinates(int i, int j) {
        return new double[]{-90.0+i*cellSize/3600.0, -180.0+j*cellSize/3600.0};
    }
    
    /**
     * Adds world grid to this one
     * @param wg
     */
    public void addWorldGrid(WorldGrid wg) {
        SparseFloat g = wg.getGrid();
        if(this.cellSize == wg.cellSize) {  
            for(int i = 0; i < g.getEntrySize() ;i++)  {
                float v = g.getValueAtIndex(i);
                int[] idx = g.coordinatesAtIndex(i);
                this.grid.set(idx[0], idx[1], v);
            }
        } else {
            // Could be one method, but must test to make sure mod operator works well 
            if(wg.cellSize < this.cellSize && this.cellSize % wg.cellSize == 0) {
                for(int i = 0; i < g.getEntrySize() ;i++)  {
                    float v = g.getValueAtIndex(i);
                    int[] idx = g.coordinatesAtIndex(i);
                    idx = findWorldGridCell(idx[0]*wg.cellSize, idx[1]*wg.cellSize);
                    this.grid.set(idx[0], idx[1], v);
                }
            }
        }
    }
    
    /**
     * 
     * @param newCellSize 
     */
    public void rescaleWorldGrid(int newCellSize) {
        if(newCellSize == 0) {
            return;
        }
        int gcCellSize = WorldGridHelper.gcd(this.cellSize, newCellSize);
        if (newCellSize > this.cellSize){
            //scaling up
            if (gcCellSize < this.cellSize) {
                refineWorldGrid(this.cellSize/gcCellSize );
                coursenWorldGrid(newCellSize/gcCellSize );
            } else {
                coursenWorldGrid(newCellSize/this.cellSize );
            }
        } else { 
            //scaling down
            if (gcCellSize < newCellSize) {
                refineWorldGrid(this.cellSize/gcCellSize );
                coursenWorldGrid(newCellSize/gcCellSize );
            } else { 
                refineWorldGrid(this.cellSize/newCellSize );
            }
        }
    }
    
    /**
     * 
     * @param scale 
     */
    public void coursenWorldGrid(int scale) {
        this.cellSize *= scale;
        SparseFloat tempGrid = new SparseFloat(this.grid.getRows()/scale,this.grid.getColumns()/scale,this.grid.getDefault());
        for(int i = 0; i < grid.getEntrySize(); i++) {
            int[] idx = grid.coordinatesAtIndex(i);
            tempGrid.add(idx[0]/scale, idx[1]/scale, grid.getValueAtIndex(i)); 
        }
        if(!extensive) {
            scale *= scale;
            for(int i = 0; i < tempGrid.getEntrySize(); i++) {
                tempGrid.setValueAtIndex(i,tempGrid.getValueAtIndex(i)/scale);
            }
        }
        this.grid = tempGrid;
    }
    
    /**
     * 
     * @param scale 
     */
    public void refineWorldGrid(int scale) {
        this.cellSize /= scale;
        SparseFloat tempGrid = new SparseFloat(this.grid.getRows()*scale,this.grid.getColumns()*scale,this.grid.getDefault());
        for(int i = 0; i < grid.getEntrySize(); i++) {
            int[] idx = grid.coordinatesAtIndex(i);
            for(int j = 0; j < scale; j++) {
                for(int k = 0; k < scale; k++) {
                    tempGrid.add(idx[0]*scale+j, idx[1]*scale+k, grid.getValueAtIndex(i)); 
                }
            }
        }
        if(extensive) {
            scale *= scale;
            for(int i = 0; i < tempGrid.getEntrySize(); i++) {
                tempGrid.setValueAtIndex(i,tempGrid.getValueAtIndex(i)/scale);
            }
        }
        this.grid = tempGrid;
    }
    
    /**
     * 
     */
    public void clearGrid() {
        this.grid.clear();
    }

    /* Static Methods */
    
    /**
     * 
     * @param wg1
     * @param wg2
     * @return 
     */
    public static WorldGrid add(WorldGrid wg1, WorldGrid wg2) {
        WorldGrid out = new WorldGrid(wg1);
        out.addWorldGrid(wg2);
        return out;
    }
    
    /**
     * 
     * @param wgArray
     * @param options
     * @return 
     */
    public static WorldGrid maxWorldGrid(WorldGrid[] wgArray, Options options) {
        // Returns for empty and single array
        if (wgArray.length == 0) {
            return null;
        }
        if (wgArray.length == 1) {
            return new WorldGrid(wgArray[0]);
        }
        // Options
        int cs = (int) wgArray[0].cellSize;
        if(options.list.containsKey("gridCellSize")) {
            cs = (int)options.list.get("gridCellSize");
        } 
        WorldGrid out = new WorldGrid(cs);     
        boolean regionalMax = true;
        if(options.list.containsKey("regionalMax")) {
            regionalMax = (boolean)options.list.get("regionalMax");
        }
        // Go through WorldGrids
        WorldGrid wgTemp = new WorldGrid(cs);
        SparseFloat gTemp = wgTemp.getGrid();
        for(WorldGrid wg : wgArray){
            gTemp.clear();
            SparseFloat g = wg.getGrid();
            for(int i = 0; i < g.getEntrySize(); i++) {
                // value
                float val = g.getValueAtIndex(i);
                // get coordinates of latitude and longitude (same if cellSize is same)
                int[] idx = g.coordinatesAtIndex(i);
                double[] ll = wg.latitudeAndLongitudeAtCoordinates(idx[0],idx[1]);
                int[] coordinates = wgTemp.findWorldGridCell(ll[0], ll[1]);
                if(wg.cellSize < cs){
                    // multiple cells within cell
                    // use regional max if option
                    if(regionalMax) {
                        if (gTemp.get(coordinates[0], coordinates[1]) < val) {
                            gTemp.set(coordinates[0], coordinates[1], val);
                        }
                    } else {
                        // check if extensive
                        if(!wg.extensive) {
                            val /= (wg.cellSize/cs)*(wg.cellSize/cs);
                        } 
                        gTemp.add(coordinates[0], coordinates[1], val);
                    }
                } else {
                    gTemp.set(coordinates[0], coordinates[1], val);
                }
            }
            for(int i = 0; i < gTemp.getEntrySize(); i++) {
                int[] coordinates = gTemp.coordinatesAtIndex(i);
                float val = gTemp.getValueAtIndex(i);
                if (val < out.getGrid().get(coordinates[0],coordinates[1])) {
                    out.getGrid().set(coordinates[0], coordinates[1], val);
                }
            }
        }
        return out;
    }
}
