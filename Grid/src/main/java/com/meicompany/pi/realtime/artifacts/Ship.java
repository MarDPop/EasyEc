package com.meicompany.pi.realtime.artifacts;

import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.coordinates.Earth;

/**
 *
 * @author mpopescu
 */
public class Ship {
    private double longitude;
    private double latitude;
    private double heading;
    private double[] vector;
    private double speed;
    final double length;
    final double width;
    final double size;
    
    public Ship(double length, double width) {
        this.length = length;
        this.width = width;
        this.size = width*length;
    }
    
    public Ship(double length, double width, double longitude, double latitude, double heading, double speed) {
        this(length,width);
        this.longitude = longitude;
        this.latitude = latitude;
        this.heading = heading;
        this.speed = speed;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the heading
     */
    public double getHeading() {
        return heading;
    }

    /**
     * @param heading the heading to set
     * @param speed
     */
    public void setVector(double heading, double speed) {
        this.heading = heading;
        this.speed = speed;
        this.vector = new double[] {speed*Math.sin(heading),speed*Math.cos(heading)};
    }
    
    /**
     * @return the vector
     */
    public double[] getVector() {
        return vector;
    }

    /**
     * @return the speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @param dt the change in time
     */
    public void move(double dt) {
        double distance = speed*dt;
        double r = Earth.seaLevel(latitude);
        double maxDistance = 20000;
        int multiple = 1;
        while(distance > maxDistance) {
            distance /= 2;
            multiple *=2;
        }
        for(int i = 0; i < multiple; i++){
            double dlat = distance*Math.sin(latitude)*CoordinateFrame.lengthDegreeLat((float)latitude);
            double dlong = distance*Math.cos(latitude)*CoordinateFrame.lengthDegreeLong((float)latitude);
            this.latitude += dlat/r;
            this.longitude += dlong/r;
        }
        
    }
    
    
}
