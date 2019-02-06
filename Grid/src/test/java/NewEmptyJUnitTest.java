/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.coordinates.Vector3;
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
        Random rand = new Random();
        double[] u = new double[]{ 1.2, 4.0 , 6.0};
        double[] v = new double[]{ 5.2, 2.1 , -3.3};
        
        Vector3 u2 = new Vector3(1.2, 4.0 , 6.0);
        Vector3 v2 = new Vector3(5.2, 2.1 , -3.3);
        float a = 0.04f;
        float b = 0.53f;
        float c = -0.1f;
        
        int iterations = 1000000;
        double ans = 0;
        
        long start = System.nanoTime();
        for(int i = 0; i < iterations;i++) {
            double[] x = Helper.add(v, u);
        }
        long finish = System.nanoTime();

        System.out.println("Math.acos() time: "+(finish-start)/1e6+" ms ans = "+ans);
        
        start = System.nanoTime();
        for(int i = 0; i < iterations;i++) {
            u2.add(v2);
        }
        finish = System.nanoTime();

        System.out.println("Helper.acos() time: "+(finish-start)/1e6+" ms ans = "+ans);
        
       
    }
    
    @Test
    public void zeroTest() {
        double[] ll = CoordinateFrame.ecef2geo(new double[]{3790627,3413096,3817995});
        
        System.out.println(ll[0]*180/Math.PI + " , " +ll[1]*180/Math.PI+ " , " +ll[2]);
    }
}
