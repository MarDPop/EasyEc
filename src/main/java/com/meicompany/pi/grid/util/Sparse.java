/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.grid.util;

import java.util.ArrayList;

/**
 *
 * @author mpopescu
 */
public class Sparse {
    
    /**
     *  row indexes
     */
    ArrayList<Integer> i = new ArrayList<>();
    
    /**
     *  column indexes
     */
    ArrayList<Integer> j = new ArrayList<>();
    
    /**
     * values
     */
    ArrayList<Object> v = new ArrayList<>();
    
    /**
    * standard value
    */
    Object a = null;
    
    /**
     * number of rows
     */
    int m;
    
    /**
     * number of columns 
     */
    int n;
    
    /**
     * Constructor for Sparse
     * 
     * @param i
     * @param j
     * @param v
     * @throws MatrixError 
     */
    
    public Sparse(int[] i, int[] j, Object[] v) {
        if (i.length == j.length) {
            if(v.length == i.length) {
                for(int idx = 0; idx < i.length;idx++) {
                    this.i.add(i[idx]);
                    this.j.add(j[idx]);
                    this.v.add(v[idx]);
                }
            } else {
                throw new MatrixError("values must be equal length");
            }
        } else {
            throw new MatrixError("indexes must be equal length");
        } 
    }
    
    public Sparse(ArrayList<Integer> i, ArrayList<Integer> j, ArrayList<Object> v) {
        this.i = i;
        this.j = j;
        this.v = v;
    }
    
    public Sparse(int m, int n, Object a, ArrayList<Integer> i, ArrayList<Integer> j, ArrayList<Object> v)  {
        this(i,j,v);
        this.m = m;
        this.n = n;
        this.a = a;
    }
    
    public Sparse(int[] i, int[] j, Object[] v, Object a) {
        this(i,j,v); 
        this.a = a;
    }
    
    public Sparse(int m , int n, Object a) {
        this.m = m;
        this.n = n;
        this.a = a;
    }
    
    public Sparse(int m , int n, int[] i, int[] j, Object[] v) {
        this(i,j,v); 
        if(m < i.length || n < j.length) {
            throw new MatrixError("size must be larger than index length");
        } else {
            this.m = m;
            this.n = n;
        }
    }
    
    /**
     * Sets the default value of the matrix at undefined indexes
     * 
     * @param a 
     */
    public void setDefault(Object a) {
        this.a = a;
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
    public Object get(int itest, int jtest) {
        if(i.contains(itest) && j.contains(jtest)) {
            return v.get(i.indexOf(itest));
        }
        return a;
    }
    
    /**
     * Sets value at specified index
     * 
     * @param itest
     * @param jtest
     * @param vin
     * @throws MatrixError 
     */
    public void set(int itest, int jtest, Object vin) {
        if(i.contains(itest) && j.contains(jtest)) {
            v.set(i.indexOf(itest),vin);
        } else {
            i.add(itest);
            j.add(jtest);
            v.add(vin);
        }
    }
    
    /**
     * Copy of this matrix
     * @return 
     * @throws com.meicompany.pi.grid.util.MatrixError 
     */
    public Sparse copy() {
        ArrayList<Integer> i_temp = new ArrayList<>(i);
        ArrayList<Integer> j_temp = new ArrayList<>(j);
        ArrayList<Object> v_temp = new ArrayList<>(v);
        return new Sparse(this.m, this.n, this.a, i_temp, j_temp, v_temp);
    }
}
