
package com.meicompany.pi.coordinates;

import com.meicompany.pi.realtime.Helper;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

/**
 * ALL FUNCTIONS ASSUME RADIAN INPUT
 * @author mpopescu
 */
public abstract class CoordinateFrame {  
    /**
     * Epoch as defined in class, this is important for epoch transformations
     */
    protected int epoch; 
    
    /**
     * Time in epoch as seconds! Not all epochs define time in seconds 
     */
    protected double time; 
    
    /**
     * 3 Dimension array for coordinates (ie. all coordinate frames are 3d)
     */
    protected final double[] x = new double[3];
    
        /**
     * @return the epoch
     */
    public int getEpoch() {
        return epoch;
    }

    /**
     * @param epoch the epoch to set
     */
    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(double time) {
        this.time = time;
    }
    
    /**
     * Sets the coordinates
     * @param coordinates 
     */
    public void set(double[] coordinates){
        if(coordinates.length != 3) {
            throw new InstantiationError("must provide 3 dimensions");
        }
        System.arraycopy(coordinates, 0, this.x, 0, 3);
    }
    
    /** 
     * Generic spherical to cartesian
     * @param radius
     * @param polarAngle
     * @param azimuthAngle
     * @return 
     */
    public static double[] spherical2cartesian(double radius, double polarAngle, double azimuthAngle) {
        double[] out = new double[3];
        out[0] = radius*Math.cos(azimuthAngle)*Math.sin(polarAngle);
        out[1] = radius*Math.sin(azimuthAngle)*Math.sin(polarAngle);
        out[2] = radius*Math.cos(polarAngle);
        return out;
    }
    
    /** 
     * Generic 3d cartesian to spherical
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public static double[] cartesian2spherical(double x, double y, double z) {
        double[] out = new double[3];
        out[0] = Math.sqrt(x*x+y*y+z*z);
        out[1] = Math.acos(z/out[0]);
        out[2] = Math.atan2(y,x);
        return out;
    }

    /**
     * Gets the appoximate length of degree latitude from latitude (radians)
     * @param latitude
     * @return 
     */
    public static double lengthDegreeLat(double latitude) {
        return 111132.92 - 559.82 * cos(2 * latitude) + 1.175 * cos(4 * latitude) - 0.0023 * cos(6 * latitude);
    }

    /**
     * Converts the xy coordinates to latitude and longitude
     * @param xy
     * @return 
     */
    public static double[] xy2llFast(double[] xy) {
        double[] out = new double[2];
        out[1] = xy[1] / 6371000;
        out[0] = xy[0] / (6383485.515566318 * cos(out[1]) - 5357.155384473197 * cos(3 * out[1]) + 6.760901982543714 * cos(5 * out[1]));
        return out;
    }
    
    /**
     * Converts the xy coordinates to latitude and longitude
     * @param xy
     * @return 
     */
    public static double[] xy2ll(double[] xy) {
        double[] out = new double[2];
        double lat = xy[1] / 6371000;
        for(int i = 0; i < 10; i++) {
            double oldLat = lat;
            lat = (xy[1] + 16037.66164350688 * sin(2 * oldLat) - 16.830635231967932 * sin(4 * oldLat) + 0.021963382146682 * sin(6 * oldLat))/6367447.280965017 ; 
            if (Math.abs((oldLat-lat)/lat)< 1e-6){
                break;
            }
        }
        out[1] = lat;
        out[0] = xy[0] / (6383485.515566318 * cos(lat) - 5357.155384473197 * cos(3 * lat) + 6.760901982543714 * cos(5 * lat)); //longitude
        return out;
    }
    
    /**
     * Gets distance between two latitude and longitude coordinates (at sea level assuming spherical)
     * @param long1
     * @param lat1
     * @param long2
     * @param lat2
     * @return 
     */
    public static double earthDistanceSpherical(double long1, double lat1, double long2, double lat2) {
        return Earth.EARTH_AVG_D * asin(sqrt(Helper.haversin(lat2 - lat1) - cos(lat1) * cos(lat2) * Helper.haversin(long2 - long1)));
    }
    
    /**
     * Gets distance between two ecef coordinates (at sea level assuming spherical)
     * @param ecef1
     * @param ecef2
     * @return 
     */
    public static double earthDistanceSpherical(double[] ecef1, double[] ecef2) {
        return Earth.EARTH_AVG_D * asin(sqrt(1 - Helper.dot(ecef1, ecef2)) / Earth.EARTH_AVG_D * Earth.EARTH_AVG_R);
    }
    
    /**
     * Converts latitude and longitude to xy coordinates assuming spherical earth
     * @param longitude
     * @param latitude
     * @return 
     */
    public static double[] ll2xySpherical(double longitude, double latitude) {
        double[] out = new double[2];
        out[0] = Earth.EARTH_AVG_R * cos(latitude) * longitude;
        out[1] = Earth.EARTH_AVG_R * latitude;
        return out;
    }
    
    /**
     * Converts latitude and longitude to x and y coordinates 
     * @param ll
     * @return 
     */
    public static double[] ll2xy(double[] ll) {
        double[] out = new double[2];
        out[0] = (6383485.515566318 * cos(ll[1]) - 5357.155384473197 * cos(3 * ll[1]) + 6.760901982543714 * cos(5 * ll[1])) * ll[0];
        out[1] = (6367447.280965017 * ll[1] - 16037.66164350688 * sin(2 * ll[1]) + 16.830635231967932 * sin(4 * ll[1]) - 0.021963382146682 * sin(6 * ll[1]));
        return out;
    }

    /**
     * Gets sea level at latitude assuming ellipsoid earth
     * @param latitude
     * @return 
     */
    public static double seaLevel(double latitude) {
        double a = cos(latitude) / 6378137;
        double b = sin(latitude) / 6356752.3;
        return 1 / sqrt(a * a + b * b);
    }

    /**
     * Gets ecef coordinate from geodetic assuming ellipsoid earth
     * @param longitude
     * @param latitude
     * @param h
     * @return 
     */
    public static double[] geodetic2ecef(double longitude, double latitude, double h) {
        double s = sin(latitude);
        double n = Earth.EARTH_EQUATOR_R / sqrt(1 - Earth.e2 * s * s);
        double[] ecef = new double[3];
        ecef[0] = (n + h) * cos(latitude) * cos(longitude);
        ecef[1] = ecef[0] * tan(longitude);
        ecef[2] = s * (n * (1 - Earth.e2) + h);
        return ecef;
    }

    /**
     * Accurate distance between two latitude and longitudes on earth
     * @param long1
     * @param lat1
     * @param long2
     * @param lat2
     * @return 
     */
    public static double vincentyFormulae(double long1, double lat1, double long2, double lat2) {
        double U1 = atan((1 - Earth.EARTH_FLATTENING) * tan(lat1));
        double U2 = atan((1 - Earth.EARTH_FLATTENING) * tan(lat2));
        double L = long2 - long1;
        double l = L;
        double calpha;
        double st;
        double ct;
        double d;
        double a;
        double b;
        calpha = st = ct = d = a = b = 0;
        for (int i = 0; i < 20; i++) {
            double su2 = sin(U2);
            double su1 = sin(U1);
            double cu2 = cos(U2);
            double cu1 = cos(U1);
            a = cu2 * sin(l);
            b = cu1 * su2 - su1 * cu2 * cos(l);
            st = sqrt(a * a + b * b);
            ct = su2 * su1 + cu1 * cu2 * cos(l);
            a = atan2(st, ct);
            b = cu2 * cu1 * sin(l) / st;
            calpha = 1 - b * b;
            d = ct - 2 * su1 * su2 / calpha;
            double C = Earth.EARTH_FLATTENING / 16 * calpha * (4 + Earth.EARTH_FLATTENING * (4 - 3 * calpha));
            l = L + (1 - C) * Earth.EARTH_FLATTENING * b * (a + C * st * (d + C * ct * (2 * d - 1)));
        }
        double u2 = calpha * (Earth.EARTH_EQUATOR_R * Earth.EARTH_EQUATOR_R / (Earth.EARTH_POLAR_R * Earth.EARTH_POLAR_R) - 1);
        double A = 1 - u2 / 16384 * (4096 + u2 * (-768 + u2 * (320 - 175 * u2)));
        double B = u2 / 1024 * (256 + u2 * (-128 + u2 * (74 - 47 * u2)));
        L = B * st * (d + 0.25 * B * (ct * (2 * d - 1)) - 0.1666 * B * d * (-3 + 4 * b * b) * (-3 + 4 * d));
        return Earth.EARTH_POLAR_R * A * (a - L);
    }

    /**
     * Gets geodetic coordinates from ecef
     * @param ecef
     * @return 
     */
    public static double[] ecef2geo(double[] ecef) {
        double g;
        double rg;
        double rf;
        double u;
        double v;
        double m;
        double f;
        double p;
        double x;
        double y;
        double z;
        double zp;
        double w2;
        double w;
        double r2;
        double r;
        double s2;
        double c2;
        double s;
        double c;
        double[] geo = new double[3]; //Results go here (Lat, Lon, Altitude)
        x = ecef[0];
        y = ecef[1];
        z = ecef[2];
        w2 = x*x + y*y;
        w = Math.sqrt(w2);
        zp = z*z;
        r2 = w2 + zp;
        r = Math.sqrt(r2);
        geo[0] = Math.atan2(y, x); //Lon (final)
        s2 = zp/r2;
        c2 = w2/r2;
        u = Earth.a2/r;
        v = Earth.a3 - Earth.a4/r;
        zp = Math.abs(z);
        if (c2 > 0.3) {
            s = (zp/r)*(1.0 + c2*(Earth.a1 + u + s2*v)/r);
            geo[1] = Math.asin(s); //Lat
            c2 = s*s;
            c = Math.sqrt(1.0 - c2);
        } else {
            c = (w/r) * (1.0 - s2*(Earth.a5 - u - c2*v)/r);
            geo[1] = Math.acos(c); //Lat
            c2 = 1.0 - c*c;
            s = Math.sqrt(c2);
        }
        g = 1.0 - Earth.e2*c2;
        rg = Earth.EARTH_EQUATOR_R/Math.sqrt(g);
        rf = Earth.a6 * rg;
        u = w - rg*c;
        v = zp - rf*s;
        f = c*u + s*v;
        m = c*v - s*u;
        p = m / (rf / g + f);
        geo[1] += p; //Lat
        geo[2] = f + m * p / 2.0; //Altitude
        if (z < 0.0) {
            geo[1] *= -1.0; //Lat
        }
        return geo; //Return Lon, Lat, Altitude in that order
    }

    /**
     * Earth sidereal rotation angle from UT1 (julian days) time
     * @param UT1
     * @return 
     */
    public static double era(double UT1) {
        return Helper.TWOPI * (0.779057273264 + 1.0027378119113546 * (UT1 - 2451545));
    }

    /**
     * Length of degree longitude from latitude (radians)
     * @param latitude
     * @return 
     */
    public static double lengthDegreeLong(double latitude) {
        return 111412.84 * cos(latitude) - 93.5 * cos(3 * latitude) + 0.118 * cos(5 * latitude);
    }
    
    /**
     * 
     * @param phi
     * @return 
     */
    public static double primeVerticalRadiusCurvature(double phi){
        return Earth.EARTH_EQUATOR_R/Math.sqrt(1-Earth.e2*Math.sin(phi)*Math.sin(phi));
    }


    
    
}
