/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.coordinates;

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
public class CoordinateFrameTest {
    
    public CoordinateFrameTest() {
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
     * Test of spherical2cartesian method, of class CoordinateFrame.
     */
    @Test
    public void testSpherical2cartesian() {
        System.out.println("spherical2cartesian");
        double radius = 0.0;
        double polarAngle = 0.0;
        double azimuthAngle = 0.0;
        double[] expResult = null;
        double[] result = CoordinateFrame.spherical2cartesian(radius, polarAngle, azimuthAngle);
        assertArrayEquals(expResult, result,0.001);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cartesian2spherical method, of class CoordinateFrame.
     */
    @Test
    public void testCartesian2spherical() {
        System.out.println("cartesian2spherical");
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        double[] expResult = null;
        double[] result = CoordinateFrame.cartesian2spherical(x, y, z);
        assertArrayEquals(expResult, result, 0.001);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of lengthDegreeLat method, of class CoordinateFrame.
     */
    @Test
    public void testLengthDegreeLat() {
        System.out.println("lengthDegreeLat");
        float latitude = 0.0F;
        double expResult = 0.0;
        double result = CoordinateFrame.lengthDegreeLat(latitude);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of xy2llFast method, of class CoordinateFrame.
     */
    @Test
    public void testXy2llFast() {
        System.out.println("xy2llFast");
        double[] xy = null;
        double[] expResult = null;
        double[] result = CoordinateFrame.xy2llFast(xy);
        assertArrayEquals(expResult, result,0.001);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of xy2ll method, of class CoordinateFrame.
     */
    @Test
    public void testXy2ll() {
        System.out.println("xy2ll");
        double[] xy = null;
        double[] expResult = null;
        double[] result = CoordinateFrame.xy2ll(xy);
        assertArrayEquals(expResult, result,0.001);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of xy2llBeta method, of class CoordinateFrame.
     */
    @Test
    public void testXy2llBeta() {
        System.out.println("xy2llBeta");
        double[] xy = null;
        double[] expResult = null;
        double[] result = CoordinateFrame.xy2llBeta(xy);
        assertArrayEquals(expResult, result,0.001);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of earthDistanceSpherical method, of class CoordinateFrame.
     */
    @Test
    public void testEarthDistanceSpherical_4args() {
        System.out.println("earthDistanceSpherical");
        double long1 = 0.0;
        double lat1 = 0.0;
        double long2 = 0.0;
        double lat2 = 0.0;
        double expResult = 0.0;
        double result = CoordinateFrame.earthDistanceSpherical(long1, lat1, long2, lat2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of earthDistanceSpherical method, of class CoordinateFrame.
     */
    @Test
    public void testEarthDistanceSpherical_doubleArr_doubleArr() {
        System.out.println("earthDistanceSpherical");
        double[] ecef1 = null;
        double[] ecef2 = null;
        double expResult = 0.0;
        double result = CoordinateFrame.earthDistanceSpherical(ecef1, ecef2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ll2xySpherical method, of class CoordinateFrame.
     */
    @Test
    public void testLl2xySpherical() {
        System.out.println("ll2xySpherical");
        double longitude = 0.0;
        double latitude = 0.0;
        double[] expResult = null;
        double[] result = CoordinateFrame.ll2xySpherical(longitude, latitude);
        assertArrayEquals(expResult, result,0.001);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ll2xy method, of class CoordinateFrame.
     */
    @Test
    public void testLl2xy() {
        System.out.println("ll2xy");
        double[] ll = null;
        double[] expResult = null;
        double[] result = CoordinateFrame.ll2xy(ll);
        assertArrayEquals(expResult, result,0.001);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of seaLevel method, of class CoordinateFrame.
     */
    @Test
    public void testSeaLevel() {
        System.out.println("seaLevel");
        double latitude = 50*Math.PI/180;
        double expResult = 6365632;
        double result = Earth.seaLevel(latitude);
        assertEquals(expResult, result, 0.01);
    }

    /**
     * Test of geodetic2ecef method, of class CoordinateFrame.
     */
    @Test
    public void testGeodetic2ecef() {
        System.out.println("geodetic2ecef");
        double longitude = 0.174532925199433;
        double latitude = 0.418879020478639;
        double h = 0.0;
        double[] expResult = new double[]{5741377, 1012360, 2578283};
        double[] result = CoordinateFrame.geodetic2ecef(longitude, latitude, h);
        testDoubleArray(expResult, result);
    }

    /**
     * Test of vincentyFormulae method, of class CoordinateFrame.
     */
    @Test
    public void testVincentyFormulae() {
        System.out.println("vincentyFormulae");
        double long1 = 0.0;
        double lat1 = 0.0;
        double long2 = 0.0;
        double lat2 = 0.0;
        double expResult = 0.0;
        double result = CoordinateFrame.vincentyFormulae(long1, lat1, long2, lat2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ecef2geo method, of class CoordinateFrame.
     */
    @Test
    public void testEcef2geo() {
        System.out.println("ecef2geo");
        double[] ecef = new double[]{5741377, 1012360, 2578283};
        double[] expResult = new double[]{0.174532925199433, 0.418879020478639, 0};
        double[] result = CoordinateFrame.ecef2geo(ecef);
        testDoubleArray(expResult,result, new double[]{0.01,0.01,3});
        
    }
    
    private void testDoubleArray(double[] expResult, double[] result) {
        double sum = 0;
        if (result.length != expResult.length) {
            fail("Array not correct length");
        }
        for(int i = 0; i < expResult.length; i++) {
            double e = Math.abs((result[i]-expResult[i])/expResult[i]);
            if (e > 0.03) {
                System.out.println("Expected: " + expResult[i] + " Actual: " + result[i]);
                fail("Maximum error exceeded at index " + i);
            }
            sum += e;
        }
        if (sum > 0.01) {
            fail("Norm of relative error exceeded. Check rounding errors");
        }
    }
    
    /**
     * Use for expResults of zero
     * @param expResult
     * @param result
     * @param tol 
     */
    private void testDoubleArray(double[] expResult, double[] result, double[] tol) {
        double sum = 0;
        if (result.length != expResult.length) {
            fail("Array not correct length");
        }
        for(int i = 0; i < expResult.length; i++) {
            double e = Math.abs(result[i]-expResult[i])/tol[i];
            if (e > 1) {
                System.out.println("Expected: " + expResult[i] + " Actual: " + result[i]);
                fail("Maximum error exceeded at index " + i);
            }
            sum += e*e;
        }
        
        if (Math.sqrt(sum) > 2e-1) {
            for(int i = 0; i < expResult.length; i++) {
                System.out.println("[Index "+i+"] Expected: " + expResult[i] + " Actual: " + result[i]);
            }
            fail("Norm of relative error exceeded: "+Math.sqrt(sum)+". Check rounding errors");
        }
    }

    /**
     * Test of lengthDegreeLong method, of class CoordinateFrame.
     */
    @Test
    public void testLengthDegreeLong() {
        System.out.println("lengthDegreeLong");
        float latitude = 0.0F;
        double expResult = 0.0;
        double result = CoordinateFrame.lengthDegreeLong(latitude);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of primeVerticalRadiusCurvature method, of class CoordinateFrame.
     */
    @Test
    public void testPrimeVerticalRadiusCurvature() {
        System.out.println("primeVerticalRadiusCurvature");
        double phi = 0.0;
        double expResult = 0.0;
        double result = CoordinateFrame.primeVerticalRadiusCurvature(phi);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of rotateZ method, of class CoordinateFrame.
     */
    @Test
    public void testRotateZ() {
        System.out.println("rotateZ");
        double[] r = null;
        float era = 1.0f;
        double[] expResult = null;
        double[] result = CoordinateFrame.rotateZ(r, era);
        assertArrayEquals(expResult, result, 0.001);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class CoordinateFrameImpl extends CoordinateFrame {
    }
    
}
