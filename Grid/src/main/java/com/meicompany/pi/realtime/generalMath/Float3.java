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
public class Float3 {
    //FASTER LIBRARY
    public float _1;
    public float _2;
    public float _3;
    
    public Float3(){}
    
    public Float3(float _1, float _2, float _3 ){
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }
    
    public Float3(Float3 a) {
        this._1 = a._1;
        this._2 = a._2;
        this._3 = a._3;
    }
    
    /**
     * Performs euclidean norm of v
     * @param v
     * @return 
     */
    public float norm() {
        return (float) Math.sqrt(mag2());
    }
    
    /**
     * Performs euclidean norm of v
     * @param v
     * @return 
     */
    public float mag2() {
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
    public void divide(float a) {
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
    public void multiply(float a) {
        _1 *= a;
        _2 *= a;
        _3 *= a;
    }
    
    /**
     * New float array of v + u
     * @param v
     * @param u
     * @return 
     */
    public void add(Float3 u) {
        _1 += u._1;
        _2 += u._2;
        _3 += u._3;
    }
    
    /**
     * New float array of v - u
     * @param v
     * @param u
     * @return 
     */
    public void subtract(Float3 u) {
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
    public float dot(Float3 u) {
        return _1*u._1+_2*u._2+_3*u._3;
    }
    
    /**
     * performs cross product
     * @param u
     * @param v
     * @return 
     */
    public Float3 cross(Float3 v) {
        Float3 out = new Float3();
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
    public float angle(Float3 v) {
        return Math2.acos(dot(v)/(float)Math.sqrt(this.dot(this)*v.dot(v)));
    }

}
