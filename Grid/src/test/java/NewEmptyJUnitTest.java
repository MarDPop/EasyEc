/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.coordinates.Earth;
import com.meicompany.pi.realtime.generalMath.Vector3;
import com.meicompany.pi.realtime.IOHelper;
import com.meicompany.pi.realtime.generalMath.Math2;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    /*
    @Test
    public void profileMe() {
        Random rand = new Random();
        double[] u = new double[]{ 1302.2, 40.0 , 64.0};
        double[] v = new double[]{ 5.2, 2.1 , -3.3};
        
        Vector3 u2 = new Vector3(1.2, 4.0 , 6.0);
        Vector3 v2 = new Vector3(5.2, 2.1 , -3.3);
        float a = 1.02f;
        float b = 0.98f;
        float c = -0.1f;
        
        int iterations = 100000;
        float ans = 0f;
        
        long start = System.nanoTime();
        for(int i = 0; i < iterations;i++) {
            ans = b;
            for(int j = 0; j < 100;j++) {
                ans *= b;
            }
        }
        long finish = System.nanoTime();

        System.out.println("Math.acos() time: "+(finish-start)/1e6+" ms ans = "+ans);
        
        
        start = System.nanoTime();
        for(int i = 0; i < iterations;i++) {
            ans = a;
            for(int j = 0; j < 100;j++) {
                ans /= a;
            }
        }
        finish = System.nanoTime();

        System.out.println("Helper.acos() time: "+(finish-start)/1e6+" ms ans = "+ans);
        
       
    }
    */
    /*
    @Test
    public void zeroTest() {
        double[] ll = CoordinateFrame.ecef2geo(new double[]{3790627,3413096,3817995});
        
        System.out.println(ll[0]*180/Math.PI + " , " +ll[1]*180/Math.PI+ " , " +ll[2]);
    }
    */
    
    
    /*
    @Test
    public void export() {
        File folder = new File("C:\\\\Users\\mdavid\\Documents\\pelican_pi_tool\\Matlab\\LandScan\\export");
        File[] listOfFiles = folder.listFiles();
        for(File f : listOfFiles) {
            try {
                IOHelper.convertLandscanFile(f.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger(NewEmptyJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
    */
    
    /*
    @Test
    public void haha2() throws IOException {
        File folder = new File("C:\\\\Users\\mdavid\\Documents\\pelican_pi_tool\\Matlab\\LandScan\\export");
        File[] listOfFiles = folder.listFiles();
        byte[] b = new byte[750000];
        long start = System.nanoTime();
        for(File f : listOfFiles) {   
            if(f.getName().contains(".bin2")) {
                int[][] i = IOHelper.loadLandScanFile2(f.getAbsolutePath(),b);
            }
        }
        long finish = System.nanoTime();
        System.out.println("Binary time: "+(finish-start)/1e6+" ms ");
        start = System.nanoTime();
        for(File f : listOfFiles) {
            if(f.getName().contains(".csv")) {
                List<double[]> h = IOHelper.csvRead(f.getAbsolutePath());
            }
        }
        finish = System.nanoTime();
        System.out.println("CSV time: "+(finish-start)/1e6+" ms ");
    }
    */
    
    /*
    @Test
    public void importCompare() throws IOException {
        File folder = new File("C:\\\\Users\\mdavid\\Documents\\pelican_pi_tool\\Matlab\\LandScan\\export");
        File[] listOfFiles = folder.listFiles();
        long start = System.nanoTime();
        for(File f : listOfFiles) {
            if(f.getName().contains(".binf")) {
                float[][] i = IOHelper.loadLandScanFileF(f.getAbsolutePath());
                System.out.println("check");
            }
        }
        long finish = System.nanoTime();
        System.out.println("binary read time: "+(finish-start)/1e6+" ms ");
    }
    */
    /*
    @Test
    public void importCompare() throws IOException {
        int iterations = 100000;
        double test = 2.3;
        double ans = 0;
        long start = System.nanoTime();
        for(int i = 0; i < iterations;i++) {
            ans = Math.sqrt(test);
        }
        long finish = System.nanoTime();

        System.out.println("time 1: "+(finish-start)/1e6+" ms ans = "+ans);
        
        
        start = System.nanoTime();
        for(int i = 0; i < iterations;i++) {
            ans = Math.pow(test,0.5);
        }
        finish = System.nanoTime();
        System.out.println("time 2: "+(finish-start)/1e6+" ms ans = "+ans);
    }

    */
    /*
    @Test
    public void importCompare() throws IOException {
        double latitude = 45*Math.PI/180;
        double a = Math2.cos((float)latitude) * Earth.EARTH_EQUATOR_R;
        double b = Math2.sin((float) latitude) * Earth.EARTH_POLAR_R;
        a *= a;
        b *= b;
        double c =  sqrt( (a*Earth.EARTH_EQUATOR_R_2 + b*Earth.EARTH_POLAR_R_2)/(a+b));
        a = Math2.cos((float)latitude) / 6378137;
        b = Math2.sin((float)latitude) / Earth.EARTH_POLAR_R;
        double d = 1 / sqrt(a * a + b * b);
        
        double h = Math2.sin((float) latitude);
        h*=h;
        h = 6378139/sqrt(1+h*0.006739501254387);
        
        System.out.println(d);
        System.out.println(c);
        System.out.println(h);
    }
*/
    /*
    @Test
    public void importCompare() throws IOException {
        int iterations = 100000;
        float test1 = 4792111f;
        float test2 = 2766726f;
        float test3 = 3173374f;
        double[] ecef = new double[]{test1,test2,test3};
        double[] ans = new double[3];
        long start = System.nanoTime();
        for(int i = 0; i < iterations;i++) {
            ans = CoordinateFrame.ecef2geo(ecef);
        }
        long finish = System.nanoTime();

        System.out.println("time 1: "+(finish-start)/1e6+" ms ans = "+ans[2]);
        
        float[] ans2 = new float[3];
        start = System.nanoTime();
        for(int i = 0; i < iterations;i++) {
            ans2 = CoordinateFrame.ecef2geo(test1,test2,test3);
        }
        finish = System.nanoTime();
        System.out.println("time 2: "+(finish-start)/1e6+" ms ans = "+ans2[2]);
    }
*/
    /*
    @Test
    public void sinCompare() throws IOException {
        for(float angle = -2*Math2.TWOPI_F; angle < 3*Math2.TWOPI_F; angle += Math2.PI_F/3) {
            System.out.println("Angle:"+angle);
            System.out.println("Math cos: "+Math.cos(angle)+" my cos: "+Math2.cos(angle));
            System.out.println("Math sin: "+Math.sin(angle)+" my sin: "+Math2.sin(angle));
        }
        
    }
    
    @Test
    public void coordCompare() throws IOException {
        for(float latitude = -Math2.PI_F; latitude < Math2.PI_F; latitude += Math2.PI_F/6) {
            for(float longitude = -Math2.PI_F; longitude < Math2.PI_F; longitude += Math2.PI_F/3) {
                System.out.println("latitude: "+latitude+ " longitude: "+longitude);
                System.out.println("ll2xySpherical: "+CoordinateFrame.ll2xySpherical(longitude, latitude)[0]+" ll2xy: "+CoordinateFrame.ll2xy(new float[]{longitude,latitude})[0]);
            }
        }
    }
*/
}
