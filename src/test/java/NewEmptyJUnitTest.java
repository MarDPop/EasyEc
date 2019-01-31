/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.meicompany.pi.realtime.Helper;
import java.util.Random;
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
public class NewEmptyJUnitTest {
    
    public NewEmptyJUnitTest() {
        
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void profileMe() {
        double[] u = new double[]{ 1.2, 4.0 , 6.0};
        double[] v = new double[]{ 5.2, 2.1 , -3.3};
        float a = 0.55f;
        float b = 0.73f;
        float c = -0.98f;
        
        int iterations = 1000000;
        double ans = 0;
        
        long start = System.nanoTime();
        for(int i = 0; i < iterations;i++) {
            ans = Math.acos(c);
        }
        long finish = System.nanoTime();

        System.out.println((finish-start)/1e6+" ms run time "+ans);
        
        start = System.nanoTime();
        for(int i = 0; i < iterations;i++) {
            ans = Helper.acos(c);
        }
        finish = System.nanoTime();

        System.out.println((finish-start)/1e6+" ms run time "+ans);
        
    }
}
