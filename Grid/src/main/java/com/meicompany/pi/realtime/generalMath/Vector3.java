/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.generalMath;

/**
 *
 * @author mpopescu
 */
public class Vector3 {
    //FASTER LIBRARY
    public double _1;
    public double _2;
    public double _3;
    
    public Vector3(){}
    
    public Vector3(double _1, double _2, double _3 ){
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }
    
    public Vector3(Vector3 a) {
        this._1 = a._1;
        this._2 = a._2;
        this._3 = a._3;
    }
    
    /**
     * Performs euclidean norm of v
     * @param v
     * @return 
     */
    public double norm() {
        return Math.sqrt(mag2());
    }
    
    /**
     * Performs euclidean norm of v
     * @param v
     * @return 
     */
    public double mag2() {
        return _1*_1+_2*_2+_3*_3;
    }
    
    /**
     * Unit vector of v
     * @param v
     * @return 
     */
    public void normalize() {
        divide(norm());
    }
    
    /**
     * Divide v by scalar a
     * @param v
     * @param a
     * @return 
     */
    public void divide(double a) {
        _1 /= a;
        _2 /= a;
        _3 /= a;
    }
    
    /**
     * Multiply v by scalar a
     * @param v
     * @param a
     * @return 
     */
    public void multiply(double a) {
        _1 *= a;
        _2 *= a;
        _3 *= a;
    }
    
    /**
     * New double array of v + u
     * @param v
     * @param u
     * @return 
     */
    public void add(Vector3 u) {
        _1 += u._1;
        _2 += u._2;
        _3 += u._3;
    }
    
    /**
     * New double array of v - u
     * @param v
     * @param u
     * @return 
     */
    public void subtract(Vector3 u) {
        _1 -= u._1;
        _2 -= u._2;
        _3 -= u._3;
    }
    
    /** 
     * performs dot product
     * @param u
     * @param v
     * @return 
     */
    public double dot(Vector3 u) {
        return _1*u._1+_2*u._2+_3*u._3;
    }
    
    /**
     * performs cross product
     * @param u
     * @param v
     * @return 
     */
    public Vector3 cross(Vector3 v) {
        Vector3 out = new Vector3();
        out._1 = _2*v._3 - _3*v._2;
        out._2 = _3*v._1 - _1*v._3;
        out._3 = _1*v._2 - _2*v._1;
        return out;
    }
    
    /**
     * Gets the angle between u and v, with faster implementation accuracy is about 8e-5
     * @param u
     * @param v
     * @return 
     */
    public float angle(Vector3 v) {
        return Math2.acos((float)(dot(v)/Math.sqrt(this.dot(this)*v.dot(v))));
    }

}
