/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.grid.util;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author mpopescu
 */
public class SparseFloat {
    
    /**
     *  row indexes
     */
    private ArrayList<Integer> i = new ArrayList<>();
    
    /**
     *  column indexes
     */
    private ArrayList<Integer> j = new ArrayList<>();
    
    /**
     * values
     */
    private ArrayList<Float> v = new ArrayList<>();
    
    /**
    * standard value
    */
    private float a = 0.0f;
    
    /**
     * number of rows
     */
    private int m;
    
    /**
     * number of columns 
     */
    private int n;
    
    
    public SparseFloat(int m , int n) {
        this.m = m;
        this.n = n;
    }
    
    public SparseFloat(int m , int n, float a) {
        this.m = m;
        this.n = n;
        this.a = a;
    }
    
    public SparseFloat(ArrayList<Integer> i, ArrayList<Integer> j, ArrayList<Float> v) {
        this.i = i;
        this.j = j;
        this.v = v;
        this.m = Collections.max(i);
        this.n = Collections.max(j);
    }
    
    public SparseFloat(int[] i, int[] j, float[] v) {
        if (i.length == j.length) {
            if(v.length == i.length) {
                int max_rows = 0;
                int max_cols = 0;
                for(int idx = 0; idx < i.length;idx++) {
                    this.i.add(i[idx]);
                    if(i[idx] > max_rows) {
                        max_rows = i[idx];
                    }
                    this.j.add(j[idx]);
                    if(j[idx] > max_cols) {
                        max_cols = j[idx];
                    }
                    this.v.add(v[idx]);
                }
                this.m = max_rows;
                this.n = max_cols;
            } else {
                throw new MatrixError("values must be equal length");
            }
        } else {
            throw new MatrixError("indexes must be equal length");
        } 
    }
    
    public SparseFloat(int[] i, int[] j, float[] v, float a) {
        this(i,j,v); 
        this.a = a;
    }
    
    public SparseFloat(int m , int n, int[] i, int[] j, float[] v) {
        this(i,j,v); 
        if(m < i.length || n < j.length) {
            throw new MatrixError("size must be larger than index length");
        } else {
            this.m = m;
            this.n = n;
        }
    }
    
    public SparseFloat(int m, int n, float a, ArrayList<Integer> i, ArrayList<Integer> j, ArrayList<Float> v)  {
        this(i,j,v);
        this.m = m;
        this.n = n;
        this.a = a;
    }
    
    /**
     * Sets the default value of the matrix at undefined indexes
     * 
     * @param a 
     */
    public void setDefault(float a) {
        this.a = a;
    }
    
    /**
     * 
     * @return 
     */
    public float getDefault() {
        return a;
    }
    
    /**
     * Getter for rows
     * 
     * @return rows
     */
    public int getRows() {
        return m;
    }
    
    /**
     * Getter for columns
     * 
     * @return columns
     */
    public int getColumns() {
        return n;
    }
    
    /**
     * gets number of entries that are not default
     * 
     * @return number of entries
     */
    public int getEntrySize() {
        return v.size();
    }
    
    /**
     * getter for rows
     * 
     * @return row list
     */
    public Integer[] getEntryRows() {
        return i.toArray(new Integer[0]);
    }
    
    /**
     * getter for columns
     * 
     * @return column list
     */
    public Integer[] getEntryColumns() {
        return j.toArray(new Integer[0]);
    }
    
    /**
     * getter for Values
     * 
     * @return value list
     */
    public Float[] getEntries() {
        return v.toArray(new Float[0]);
    }
    
    /**
     * gets value at specified index 
     * 
     * @param itest
     * @param jtest
     * @return 
     */
    public float get(int itest, int jtest) {
        if(i.contains(itest) && j.contains(jtest)) {
            return v.get(i.indexOf(itest));
        }
        return a;
    }
    
    /**
     * returns row and column of item index
     * 
     * @param test
     * @return 
     */
    public int[] coordinatesAtIndex(int test) {
        int[] out = new int[2];
        out[0] = i.get(test);
        out[1] = j.get(test);
        return out;
    }
    
    /**
     * Returns index of value if found else -1
     * @param test
     * @return 
     */
    public int indexOf(float test) {
        if(v.contains(test)) {
            return v.indexOf(test);
        } else {
            return -1;
        }
    } 
    
    /**
     * Don't use for index not set
     * @param idx
     * @param vin 
     */
    public void setValueAtIndex(int idx, float vin) {
        v.set(idx, vin);
    }
    
    /**
     * Returns value at item index
     * @param test
     * @return 
     */
    public float getValueAtIndex(int test) {
        return v.get(test);
    }
    
    /**
     * Sets value at specified index
     * 
     * @param itest
     * @param jtest
     * @param vin
     * @throws MatrixError 
     */
    public void set(int itest, int jtest, float vin) {
        if(i.contains(itest) && j.contains(jtest)) {
            v.set(i.indexOf(itest),vin);
        } else {
            i.add(itest);
            j.add(jtest);
            v.add(vin);
        }
    }
    
    /**
     * 
     * @param itest
     * @param jtest
     * @param vin 
     */
    public void add(int itest, int jtest, float vin) {
        if(i.contains(itest) && j.contains(jtest)) {
            int idx = i.indexOf(itest);
            v.set(idx,vin+v.get(idx));
        } else {
            i.add(itest);
            j.add(jtest);
            v.add(vin+a);
        }
    }
    
    /**
     * 
     * @param itest
     * @param jtest
     * @param vin 
     */
    public void multiply(int itest, int jtest, float vin) {
        if(i.contains(itest) && j.contains(jtest)) {
            int idx = i.indexOf(itest);
            v.set(idx,vin*v.get(idx));
        } else {
            if(a < 1e-100) {
                i.add(itest);
                j.add(jtest);
                v.add(vin*a);
            }
        }
    }
    
    /**
     * Copy of this matrix
     * @return 
     * @throws com.meicompany.pi.grid.util.MatrixError 
     */
    public SparseFloat copy() {
        ArrayList<Integer> i_temp = new ArrayList<>(i);
        ArrayList<Integer> j_temp = new ArrayList<>(j);
        ArrayList<Float> v_temp = new ArrayList<>(v);
        return new SparseFloat(this.m, this.n, this.a, i_temp, j_temp, v_temp);
    }
    
    /**
     * 
     */
    public void clear() {
        i.clear();
        j.clear();
        v.clear();
    }
}
