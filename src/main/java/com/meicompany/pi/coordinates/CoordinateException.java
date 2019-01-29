package com.meicompany.pi.coordinates;

import com.meicompany.pi.realtime.Helper;
import static java.lang.Math.*;

/**
 *
 * @author mpopescu
 */

public class CoordinateException extends Exception{

    private static final double a5 = 42840.589930055656; //a5 = a1+a3
    public static final double EARTH_SIDEREAL = 86164.1;
    public static final double EARTH_ROT = 7.29211505392569E-5;
    private static final double a6 = 0.9933056200098622; //a6 = 1-e2
    private static final double a1 = 42697.67270715754; //a1 = a*e2
    public static final double EARTH_AVG_R = 6371000;
    public static final double EARTH_POLAR_R = 6356752.314;
    public static final double e2prime = 0.006739496819936;
    private static final double a2 = 1.8230912546075456E9; //a2 = a1*a1
    public static final double EARTH_F = 0.003352810664747;
    public static final double e2 = 0.006694380066765; //WGS-84 first eccentricity squared
    public static final double EARTH_EQUATOR_R = 6378137;
    private static final double a4 = 4.557728136518864E9; //a4 = 2.5*a2
    private static final double a3 = 142.91722289812412; //a3 = a1*e2/2
    public static final double EARTH_AVG_D = 12742000;

    public static double[][] convert2Rlonglat(double[] x, double[] y, double[] z, double[] time) {
        int n = x.length;
        double[][] out = new double[n][3];
        for (int i = 1; i < n; i++) {
            out[i] = convert2Rlonglat(x[i], y[i], z[i], time[i]);
        }
        return out;
    }

    public static double[] convert2Rlonglat(double x, double y, double z, double time) {
        double[] out = new double[3];
        out[0] = sqrt(x * x + y * y + z * z);
        out[1] = atan2(y, x) + CoordinateException.EARTH_ROT * time;
        out[2] = asin(z / out[0]);
        return out;
    }

    public static double lengthDegreeLat(double latitude) {
        return 111132.92 - 559.82 * cos(2 * latitude) + 1.175 * cos(4 * latitude) - 0.0023 * cos(6 * latitude);
    }

    public static double[] xy2ll(double[] xy) {
        double[] out = new double[2];
        out[0] = xy[1] / 6371000;
        out[1] = xy[0] / (6383485.515566318 * cos(out[0]) - 5357.155384473197 * cos(3 * out[0]) + 6.760901982543714 * cos(5 * out[0]));
        return out;
    }

    public static double flatEarthDistance(double long1, double lat1, double long2, double lat2) {
        return EARTH_AVG_D * asin(sqrt(Helper.haversin(lat2 - lat1) - cos(lat1) * cos(lat2) * Helper.haversin(long2 - long1)));
    }

    public static double flatEarthDistance(double[] xyz1, double[] xyz2) {
        return EARTH_AVG_D * asin(sqrt(1 - Helper.dot(xyz1, xyz2)) / Helper.EARTH_AVG_D * Helper.EARTH_AVG_R);
    }

    public static double[] flatEarthXY(double longitude, double latitude) {
        double[] out = new double[2];
        double R = seaLevel(latitude);
        out[1] = R * longitude;
        out[0] = R * cos(latitude) * longitude;
        return out;
    }

    public static double[] ll2xy(double[] ll) {
        double[] out = new double[2];
        out[0] = (6383485.515566318 * cos(ll[0]) - 5357.155384473197 * cos(3 * ll[0]) + 6.760901982543714 * cos(5 * ll[0])) * ll[1];
        out[1] = (6367447.280965017 * ll[0] - 16037.66164350688 * sin(2 * ll[0]) + 16.830635231967932 * sin(4 * ll[0]) - 0.021963382146682 * sin(6 * ll[0]));
        return out;
    }

    public static double seaLevel(double latitude) {
        double a = cos(latitude) / 6378137;
        double b = sin(latitude) / 6356752.3;
        return 1 / sqrt(a * a + b * b);
    }

    public static double[] geodetic2ecef(double longitude, double latitude, double h) {
        double s = sin(latitude);
        double n = Helper.EARTH_EQUATOR_R / sqrt(1 - Helper.e2 * s * s);
        double[] ecef = new double[3];
        ecef[0] = (n + h) * cos(latitude) * cos(longitude);
        ecef[1] = ecef[0] * tan(longitude);
        ecef[2] = s * (n * (1 - Helper.e2) + h);
        return ecef;
    }

    public static double vincentyFormulae(double long1, double lat1, double long2, double lat2) {
        double U1 = atan((1 - Helper.EARTH_F) * tan(lat1));
        double U2 = atan((1 - Helper.EARTH_F) * tan(lat2));
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
            double C = Helper.EARTH_F / 16 * calpha * (4 + Helper.EARTH_F * (4 - 3 * calpha));
            l = L + (1 - C) * Helper.EARTH_F * b * (a + C * st * (d + C * ct * (2 * d - 1)));
        }
        double u2 = calpha * (Helper.EARTH_EQUATOR_R * Helper.EARTH_EQUATOR_R / (Helper.EARTH_POLAR_R * Helper.EARTH_POLAR_R) - 1);
        double A = 1 - u2 / 16384 * (4096 + u2 * (-768 + u2 * (320 - 175 * u2)));
        double B = u2 / 1024 * (256 + u2 * (-128 + u2 * (74 - 47 * u2)));
        L = B * st * (d + 0.25 * B * (ct * (2 * d - 1)) - 0.1666 * B * d * (-3 + 4 * b * b) * (-3 + 4 * d));
        return Helper.EARTH_POLAR_R * A * (a - L);
    }

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
        double ss;
        double[] geo = new double[3]; //Results go here (Lat, Lon, Altitude)
        x = ecef[0];
        y = ecef[1];
        z = ecef[2];
        zp = Math.abs(z);
        w2 = x * x + y * y;
        w = Math.sqrt(w2);
        r2 = w2 + z * z;
        r = Math.sqrt(r2);
        geo[1] = Math.atan2(y, x); //Lon (final)
        s2 = z * z / r2;
        c2 = w2 / r2;
        u = Helper.a2 / r;
        v = Helper.a3 - Helper.a4 / r;
        if (c2 > 0.3) {
            s = (zp / r) * (1.0 + c2 * (Helper.a1 + u + s2 * v) / r);
            geo[0] = Math.asin(s); //Lat
            ss = s * s;
            c = Math.sqrt(1.0 - ss);
        } else {
            c = (w / r) * (1.0 - s2 * (Helper.a5 - u - c2 * v) / r);
            geo[0] = Math.acos(c); //Lat
            ss = 1.0 - c * c;
            s = Math.sqrt(ss);
        }
        g = 1.0 - Helper.e2 * ss;
        rg = Helper.EARTH_EQUATOR_R / Math.sqrt(g);
        rf = Helper.a6 * rg;
        u = w - rg * c;
        v = zp - rf * s;
        f = c * u + s * v;
        m = c * v - s * u;
        p = m / (rf / g + f);
        geo[0] = geo[0] + p; //Lat
        geo[2] = f + m * p / 2.0; //Altitude
        if (z < 0.0) {
            geo[0] *= -1.0; //Lat
        }
        return geo; //Return Lat, Lon, Altitude in that order
    }

    public static double era(double UT1) {
        return Helper.TWOPI * (0.779057273264 + 1.0027378119113546 * (UT1 - 2451545));
    }

    public static double lengthDegreeLong(double latitude) {
        return 111412.84 * cos(latitude) - 93.5 * cos(3 * latitude) + 0.118 * cos(5 * latitude);
    }

    
}
