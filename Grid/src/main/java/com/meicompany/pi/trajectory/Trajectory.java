
package com.meicompany.pi.trajectory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author mpopescu
 */
public class Trajectory {
    
    public final String name;    
    
    private final ArrayList<Double[]> position = new ArrayList<>();
    
    private final ArrayList<Double[]> velocity = new ArrayList<>(); 
        
    private final ArrayList<Double> times = new ArrayList<>();
    
    protected int coordinateFrame = 1; // Default ECEF
    
    private int index;
    
    
    public Trajectory() {
        name = "";
    }
    
    public void load(String filename) throws IOException {
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            double[] numbers = new double[7];
            while ((line = br.readLine()) != null) {
                String[] state = line.split(cvsSplitBy);
                for(int i = 0; i < 7; i++){
                    numbers[i] = Double.parseDouble(state[i]);
                }
                position.add(new Double[]{numbers[1],numbers[2],numbers[3]});
                velocity.add(new Double[]{numbers[4],numbers[5],numbers[6]});
                times.add(numbers[0]);
            }
        }
        index = 1;
    }
    
    public double[][] getStateCountup(double time) {
        double[][] out = new double[2][3];

        while(times.get(index) < time) {
            index++;
        }
        int i1 = index-1;
        double dt = (time-times.get(i1))/(times.get(index)-times.get(i1));
        Double[] pos2 = position.get(index);
        Double[] pos1 = position.get(i1);
        Double[] vel2 = velocity.get(index);
        Double[] vel1 = velocity.get(i1);
        for(int j = 0; j < 3; j++) {
            out[0][j] = pos1[j] + dt*(pos2[j]-pos1[j]); 
            out[1][j] = vel1[j] + dt*(vel2[j]-vel1[j]); 
        }
        return out;
    }

    /**
     * @return the coordinateFrame
     */
    public int getCoordinateFrame() {
        return coordinateFrame;
    }

    /**
     * @param coordinateFrame the coordinateFrame to set
     */
    public void setCoordinateFrame(int coordinateFrame) {
        this.coordinateFrame = coordinateFrame;
    }
    
    
}
