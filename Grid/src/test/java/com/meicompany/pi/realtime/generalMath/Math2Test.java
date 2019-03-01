/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.generalMath;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mpopescu
 */
public class Math2Test {
    
    public Math2Test() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }


    /**
     * Test of angle method, of class Math2.
     */
    @Test
    public void testAngle() {
        System.out.println("angle");
        double[] u = null;
        double[] v = null;
        double expResult = 0.0;
        double result = Math2.angle(u, v);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of acos method, of class Math2.
     */
    @Test
    public void testAcos() {
        System.out.println("acos");
        float x = 0.0F;
        float expResult = 0.0F;
        float result = Math2.acos(x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sin method, of class Math2.
     */
    @Test
    public void testSin() {
        System.out.println("sin");
        float x = 0.0F;
        float expResult = 0.0F;
        float result = Math2.sin(x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cos method, of class Math2.
     */
    @Test
    public void testCos() {
        System.out.println("cos");
        float x = 0.0F;
        float expResult = 0.0F;
        float result = Math2.cos(x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tanFast method, of class Math2.
     */
    @Test
    public void testTanFast() {
        System.out.println("tanFast");
        float x = 0.0F;
        float expResult = 0.0F;
        float result = Math2.tanFast(x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tan method, of class Math2.
     */
    @Test
    public void testTan() {
        System.out.println("tan");
        float x = 0.0F;
        float expResult = 0.0F;
        float result = Math2.tan(x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of asin method, of class Math2.
     */
    @Test
    public void testAsin() {
        System.out.println("asin");
        float x = 0.0F;
        float expResult = 0.0F;
        float result = Math2.asin(x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of atan method, of class Math2.
     */
    @Test
    public void testAtan() {
        System.out.println("atan");
        float x = 0.0F;
        float expResult = 0.0F;
        float result = Math2.atan(x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of atan2 method, of class Math2.
     */
    @Test
    public void testAtan2() {
        System.out.println("atan2");
        float y = 0.0F;
        float x = 0.0F;
        float expResult = 0.0F;
        float result = Math2.atan2(y, x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copy method, of class Math2.
     */
    @Test
    public void testCopy_doubleArrArr() {
        System.out.println("copy");
        double[][] arr = null;
        double[][] expResult = null;
        double[][] result = Math2.copy(arr);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copy method, of class Math2.
     */
    @Test
    public void testCopy_floatArrArr() {
        System.out.println("copy");
        float[][] arr = null;
        float[][] expResult = null;
        float[][] result = Math2.copy(arr);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of gcd method, of class Math2.
     */
    @Test
    public void testGcd() {
        System.out.println("gcd");
        int a = 0;
        int b = 0;
        int expResult = 0;
        int result = Math2.gcd(a, b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of primeFactors method, of class Math2.
     */
    @Test
    public void testPrimeFactors() {
        System.out.println("primeFactors");
        int number = 0;
        List<Integer> expResult = null;
        List<Integer> result = Math2.primeFactors(number);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of halleySqrt method, of class Math2.
     */
    @Test
    public void testHalleySqrt() {
        System.out.println("halleySqrt");
        double S = 0.0;
        double guess = 0.0;
        double expResult = 0.0;
        double result = Math2.halleySqrt(S, guess);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of rsqrt method, of class Math2.
     */
    @Test
    public void testRsqrt() {
        System.out.println("rsqrt");
        float x = 0.0F;
        float expResult = 0.0F;
        float result = Math2.rsqrt(x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
