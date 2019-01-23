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
public class SparseStore {
    
    /**
     *  row indexes
     */
    short[] i;
    
    /**
     *  column indexes
     */
    short[] j;
    
    /**
     * values
     */
    float[] v;
    
    /**
    * standard value
    */
    float a = 0;
    
    public SparseStore(short[] i, short[] j, float[] v) {
        if (i.length == j.length) {
            if(v.length == i.length) {
                this.i = i;
                this.j = j;
                this.v = v;
            } else {
                throw new MatrixError("values must be equal length");
            }
        } else {
            throw new MatrixError("indexes must be equal length");
        } 
    }
    
    public SparseStore(short[] i, short[] j, float[] v,float a) {
        this(i,j,v); 
        this.a = a;
    }
    
    public double get(short itest, short jtest) {
        for(int idx = 0; idx < i.length; idx++){
            if(i[idx] == itest && j[idx] == jtest) {
                return v[idx];
            }
        }
        return a;
    }
    
    public void set(short itest, short jtest, float vin) {
        int idx = 0;
        while(idx < i.length){
            if(i[idx] == itest && j[idx] == jtest) {
                break;
            }
            idx++;
        }
        if(idx < i.length){
            v[idx] = vin;
        } else {
            throw new MatrixError("cannot be inserted at index");
        }
    }
    
}
