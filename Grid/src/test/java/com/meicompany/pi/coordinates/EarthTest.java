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
public class EarthTest {
    
    public EarthTest() {
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
     * Test of seaLevel method, of class Earth.
     */
    @Test
    public void testSeaLevel() {
        System.out.println("seaLevel");
        double latitude = 50.0*Math.PI/180;
        double expResult = 6365632;
        double result = Earth.seaLevel(latitude);
        assertEquals(expResult, result, 0.01);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWGS84Gravity method, of class Earth.
     */
    @Test
    public void testGetWGS84Gravity() {
        System.out.println("getWGS84Gravity");
        double latitude = 0.0;
        double expResult = 0.0;
        double result = Earth.getWGS84Gravity(latitude);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
