/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import java.util.ArrayList;
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
public class HelperTest {
    
    public HelperTest() {
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
     * Test of norm method, of class Helper.
     */
    @Test
    public void testNorm() {
        System.out.println("norm");
        double[] v = null;
        double expResult = 0.0;
        double result = Helper.norm(v);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of normalize method, of class Helper.
     */
    @Test
    public void testNormalize() {
        System.out.println("normalize");
        double[] v = null;
        double[] expResult = new double[]{};
        double[] result = Helper.normalize(v);
        assertArrayEquals(expResult, result, 0.001);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of divide method, of class Helper.
     */
    @Test
    public void testDivide() {
        System.out.println("divide");
        double[] v = null;
        double a = 0.0;
        double[] expResult = null;
        double[] result = Helper.divide(v, a);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of multiply method, of class Helper.
     */
    @Test
    public void testMultiply() {
        System.out.println("multiply");
        double[] v = null;
        double a = 0.0;
        double[] expResult = null;
        double[] result = Helper.multiply(v, a);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class Helper.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        double[] v = null;
        double[] u = null;
        double[] expResult = null;
        double[] result = Helper.add(v, u);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of subtract method, of class Helper.
     */
    @Test
    public void testSubtract() {
        System.out.println("subtract");
        double[] v = null;
        double[] u = null;
        double[] expResult = null;
        double[] result = Helper.subtract(v, u);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dot method, of class Helper.
     */
    @Test
    public void testDot() {
        System.out.println("dot");
        double[] u = null;
        double[] v = null;
        double expResult = 0.0;
        double result = Helper.dot(u, v);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cross method, of class Helper.
     */
    @Test
    public void testCross() {
        System.out.println("cross");
        double[] u = null;
        double[] v = null;
        double[] expResult = null;
        double[] result = Helper.cross(u, v);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of impactLatLong method, of class Helper.
     */
    @Test
    public void testImpactLatLong() {
        System.out.println("impactLatLong");
        double[] impact = null;
        double[] expResult = null;
        double[] result = Helper.impactLatLong(impact);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of impact2xy method, of class Helper.
     */
    @Test
    public void testImpact2xy() {
        System.out.println("impact2xy");
        double[] impact = null;
        double[] expResult = null;
        double[] result = Helper.impact2xy(impact);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of impactECEF2XY method, of class Helper.
     */
    @Test
    public void testImpactECEF2XY() {
        System.out.println("impactECEF2XY");
        double[] ecef = null;
        double[] expResult = null;
        double[] result = Helper.impactECEF2XY(ecef);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of haversin method, of class Helper.
     */
    @Test
    public void testHaversin() {
        System.out.println("haversin");
        double A = 0.0;
        double expResult = 0.0;
        double result = Helper.haversin(A);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copy method, of class Helper.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        double[][] arr = null;
        double[][] expResult = null;
        double[][] result = Helper.copy(arr);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of gcd method, of class Helper.
     */
    @Test
    public void testGcd() {
        System.out.println("gcd");
        int a = 0;
        int b = 0;
        int expResult = 0;
        int result = Helper.gcd(a, b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of primeFactors method, of class Helper.
     */
    @Test
    public void testPrimeFactors() {
        System.out.println("primeFactors");
        int numbers = 0;
        ArrayList<Integer> expResult = null;
        ArrayList<Integer> result = Helper.primeFactors(numbers);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    
}
