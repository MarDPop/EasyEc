/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime;

import com.meicompany.pi.realtime.generalMath.Math2;
import com.meicompany.pi.coordinates.CoordinateFrame;
import com.meicompany.pi.realtime.artifacts.Landscan;
import com.meicompany.pi.realtime.clustering.CentroidPi;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mpopescu
 */
public class IOHelper {

    // Integer to bytes
    public static byte[] toByta(int[] data) {
        byte[] byts = new byte[data.length * 4];
        for (int i = 0; i < data.length; i++) {
            System.arraycopy(toByta(data[i]), 0, byts, i * 4, 4);
        }
        return byts;
    }

    public static byte[] toByta(int data) {
        return new byte[]{(byte) ((data >> 24) & 255), (byte) ((data >> 16) & 255), (byte) ((data >> 8) & 255), (byte) ((data >> 0) & 255)};
    }

    public static void csvIntToBinary(String filename) throws IOException {
        String line;
        String basename;
        if(!filename.contains(".csv")) {
            throw new IOException("must have .csv extension");
        } else {
            basename = filename.substring(0,filename.indexOf(".csv"));
        }
        ArrayList<int[]> out = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((line = br.readLine()) != null) {
                String[] numbers = line.split(",");
                int[] lineIn = new int[numbers.length];
                for (int i = 0; i < numbers.length; i++) {
                    lineIn[i] = (int)Float.parseFloat(numbers[i]);
                }
                out.add(lineIn);
            }
        }
        int[][] f = new int[out.size()][];
        for (int i = 0; i < out.size(); i++) {
            f[i] = out.get(i);
        }
        try (final FileOutputStream fout = new FileOutputStream(basename + ".bin")) {
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            for (int[] row : f) {
                // TO DO: ADD repeated numbers compression
                ArrayList<Byte> l = new ArrayList<>();
                for (int entry : row) {
                    if( entry > 127) {
                        if(entry > 16383) {
                            l.add((byte)-2);
                            l.add((byte) (entry >> 16));
                            l.add((byte) (entry >> 8));
                            l.add((byte) entry);
                        } else {
                            l.add((byte)-1);
                            l.add((byte) (entry >> 8));
                            l.add((byte) entry);
                        }
                    } else {
                        l.add((byte)entry);
                    }
                }
                l.add((byte)-127);
                byte[] nl = new byte[l.size()];
                for (int i = 0; i < nl.length; i++) {
                    nl[i] = l.get(i);
                }
                bout.write(nl,0,nl.length);
            }
            bout.write(-127);
            bout.flush();
            bout.close();
            fout.close();
        }
    }
    
    public static void convertLandscanFile(String filename) throws IOException {
        String line;
        String pathname;
        String basename;
        if(!filename.contains(".csv")) {
            throw new IOException("must have .csv extension");
        } else {
            pathname = filename.substring(0,filename.indexOf(".csv"));
            basename = pathname.substring(pathname.lastIndexOf("\\")+1);
        }
        
        char s = basename.charAt(0);
        float latTop = Float.parseFloat(basename.substring(1,4))*(float)Math2.DEG2RAD;
        if(s == 'S') {
            latTop = -latTop;
        } 
               
        float[][] f = new float[600][600];
        try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int lineNo = 0;
            while ((line = br.readLine()) != null) {
                String[] numbers = line.split(",");
                double lat = latTop - lineNo*1.454441043328550e-04;
                float area = (float)Landscan.getLandscanArea(lat);
                for (int i = 0; i < numbers.length; i++) {
                    f[lineNo][i] = Float.parseFloat(numbers[i])/area;
                }
                lineNo++;
            }
        }
        
        try (final FileOutputStream fout = new FileOutputStream(pathname + ".binf")) {
            try (BufferedOutputStream bout = new BufferedOutputStream(fout)) {
                for (float[] row : f) {
                    int i = 0;
                    while(i < row.length) {
                        float entry = row[i];
                        float repeat = -1;
                        if( i < (row.length-1)) {
                            int j = 1;
                            while(entry == row[i+j++]){repeat--; if(i+j >= row.length){break;}} // performs increment too
                            if(repeat < -2) {
                                byte[] byteF = float2ByteArray(repeat);
                                bout.write(byteF,0,4);
                                i += j;
                            } else {
                                i++;
                            }
                        } else {
                            i++;
                        }
                        byte[] byteF = float2ByteArray(entry);
                        bout.write(byteF,0,4);
                    }
                    byte[] byteF = float2ByteArray(-1000);
                    bout.write(byteF,0,4);
                }
                byte[] byteF = float2ByteArray(-1000);
                bout.write(byteF,0,4);
                bout.flush();
            }
            fout.close();
        }
    }
    
    public static void csvIntToBinary2(String filename) throws IOException {
        String line;
        String basename;
        if(!filename.contains(".csv")) {
            throw new IOException("must have .csv extension");
        } else {
            basename = filename.substring(0,filename.indexOf(".csv"));
        }
        ArrayList<int[]> out = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((line = br.readLine()) != null) {
                String[] numbers = line.split(",");
                int[] lineIn = new int[numbers.length];
                for (int i = 0; i < numbers.length; i++) {
                    lineIn[i] = (int)Float.parseFloat(numbers[i]);
                }
                out.add(lineIn);
            }
        }
        int[][] f = new int[out.size()][];
        for (int i = 0; i < out.size(); i++) {
            f[i] = out.get(i);
        }
        try (final FileOutputStream fout = new FileOutputStream(basename + ".bin2")) {
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            for (int[] row : f) {
                // TO DO: ADD repeated numbers compression
                ArrayList<Byte> l = new ArrayList<>();
                int i = 0;
                while(i < row.length) {
                    int entry = row[i];
                    byte repeat = -8;
                    if( i < (row.length-1)) {
                        while(entry == row[++i]){if(repeat-- < -90){i++;break;}; if(i == (row.length-1)){break;}} // performs increment too
                        if(repeat < -8) {
                            l.add(repeat);
                        }
                    } else {
                        i++;
                    }
                    if( entry > 127) {
                        if(entry > 16383) {
                            l.add((byte)-2);
                            l.add((byte) (entry >> 16));
                            l.add((byte) (entry >> 8));
                            l.add((byte) entry);
                        } else {
                            l.add((byte)-1);
                            l.add((byte) (entry >> 8));
                            l.add((byte) entry);
                        }
                    } else {
                        l.add((byte)entry);
                    }
                }
                l.add((byte)-127);
                byte[] nl = new byte[l.size()];
                for (int j = 0; j < nl.length; j++) {
                    nl[j] = l.get(j);
                }
                bout.write(nl,0,nl.length);
            }
            bout.write(-127);
            bout.flush();
            bout.close();
            fout.close();
        }
    }

    /**
     * prints array to csv
     * @param data
     * @param file
     */
    public static void printCsv(double[][] data, String file) {
        // ',' divides the word into columns
        try (final FileWriter fw = new FileWriter(file);final PrintWriter out = new PrintWriter(fw)) {
            // ',' divides the word into columns
            for (double[] data1 : data) {
                for (double data2 : data1) {
                    out.print(data2);
                    out.print(",");
                }
                out.println();
            }
            //Flush the output to the file
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Math2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * prints array to csv
     * @param data
     * @param file
     */
    public static void printCsv(float[][] data, String file) {
        // ',' divides the word into columns
        try (final FileWriter fw = new FileWriter(file);final PrintWriter out = new PrintWriter(fw)) {
            // ',' divides the word into columns
            for (float[] data1 : data) {
                for (float data2 : data1) {
                    out.print(data2);
                    out.print(",");
                }
                out.println();
            }
            //Flush the output to the file
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Math2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * prints array to csv
     * @param data
     * @param file
     */
    public static void printCsv(Collection<CentroidPi[]> data, String file) {
        // ',' divides the word into columns
        try (final FileWriter fw = new FileWriter(file);final PrintWriter out = new PrintWriter(fw)) {
            // ',' divides the word into columns
            for (CentroidPi[] data2 : data) {
                for (CentroidPi data1 : data2) {
                    double[] ll = CoordinateFrame.xy2ll(new double[]{data1.x_Center, data1.y_Center});
                    out.print(ll[0]);
                    out.print(",");
                    out.print(ll[1]);
                    out.print(",");
                    out.print(data1.number);
                    out.print(",");
                    out.print(data1.sigma_x);
                    out.print(",");
                    out.print(data1.sigma_y);
                    out.print(",");
                    out.print(data1.time);
                    out.println();
                }
            }
            //Flush the output to the file
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Math2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

    /**
     * prints array to csv
     * @param runs
     * @param file
     */
    public static void printCsv(List<double[][]> runs, String file) {
        try (final FileWriter fw = new FileWriter(file);final PrintWriter out = new PrintWriter(fw)) {
            for (double[][] data : runs) {
                for (double[] data1 : data) {
                    for (double data2 : data1) {
                        out.print(data2);
                        out.print(",");
                    }
                    out.println();
                }
            }
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Math2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * prints array to csv
     * @param runs
     * @param file
     */
    public static void printCsv3(List<float[][]> runs, String file) {
        try (final FileWriter fw = new FileWriter(file);final PrintWriter out = new PrintWriter(fw)) {
            for (float[][] data : runs) {
                for (float[] data1 : data) {
                    for (float data2 : data1) {
                        out.print(data2);
                        out.print(",");
                    }
                    out.println();
                }
            }
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Math2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * prints array to csv
     * @param frags
     */
    public static void printFrags(ArrayList<ArrayList<double[]>> frags) {
        int count = 1;
        for (ArrayList<double[]> frag : frags) {
            printCsv2(frag, "fragments/frag" + (count++) + ".csv");
        }
    }

    /**
     * prints array to csv
     * @param runs
     * @param file
     */
    public static void printCsv2(List<double[]> runs, String file) {
        try (final FileWriter fw = new FileWriter(file);final PrintWriter out = new PrintWriter(fw)) {
            for (int i = 0; i < runs.size(); i += 10) {
                double[] data1 = runs.get(i);
                for (double data2 : data1) {
                    out.print(data2);
                    out.print(",");
                }
                out.println();
            }
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Math2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Read file
     * @param filename
     * @return
     * @throws java.io.IOException
     */
    public static List<double[]> csvRead(String filename) throws IOException {
        String line;
        ArrayList<double[]> out = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((line = br.readLine()) != null) {
                String[] numbers = line.split(",");
                double[] lineIn = new double[numbers.length];
                for (int i = 0; i < numbers.length; i++) {
                    lineIn[i] = Double.parseDouble(numbers[i]);
                }
                out.add(lineIn);
            }
        }
        return out;
    }
    
    /**
     * loads
     * @param filename
     * @param buffer
     * @return 
     */
    public static int[][] loadLandScanFile2(String filename, byte[] buffer) throws IOException  {
        int[][] out = new int[601][601];
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
            in.read(buffer);
            short line = 0;
            short col = 0;

            int i = 0;
            while(i < buffer.length) {
                int bt = buffer[i++];
                if(bt == -127) {
                    line++;
                    if(line > 600) {
                        return out;
                    }
                    col = 0;
                } else {
                    if(bt < -8 && bt > -99) {
                        byte repeatMax = (byte)bt;
                        bt = buffer[i++];
                        if(bt < 0) {
                            if(bt == -2) {
                                byte b1 = buffer[i++];
                                byte b2 = buffer[i++];
                                byte b3 = buffer[i++];
                                bt = ( 0x88 | ((b1 & 0xFF) << 16) | ((b2 & 0xFF) << 8) | ((b3 & 0xFF)));
                            } else if(bt == -1)  {
                                byte b1 = buffer[i++];
                                byte b2 = buffer[i++];
                                bt = (short)((b1 << 8) | (b2 & 0xFF));
                            } 
                        }
                        for(byte repeat = -8; repeatMax < repeat ; repeat--){
                            out[line][col++] = bt;
                        }
                    }
                    if(bt < 0) {
                        if(bt == -2) {
                            byte b1 = buffer[i++];
                            byte b2 = buffer[i++];
                            byte b3 = buffer[i++];
                            bt = ( 0x88 | ((b1 & 0xFF) << 16) | ((b2 & 0xFF) << 8) | ((b3 & 0xFF)));
                        } else if(bt == -1)  {
                            byte b1 = buffer[i++];
                            byte b2 = buffer[i++];
                            bt = (short)((b1 << 8) | (b2 & 0xFF));
                        } 
                    }
                    out[line][col++] = bt;
                }
                
            } 
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Math2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out;
    }
    
    /**
     * loads
     * @param filename
     * @return 
     * @throws java.io.IOException 
     */
    public static float[][] loadLandScanFileF(String filename) throws IOException  {
        float[][] out = new float[600][601];
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
            short line = 0;
            short col = 0;
            float f = 0;
            while(in.available() > 0) {
                f = in.readFloat();
                if (f < 0) {
                    if(f > -999) {
                        short repeat = (short)(-f+0.49);
                        f = in.readFloat();
                        for(short i = 0; i < repeat; i++){
                            out[line][col++] = f;
                        }
                    } else {
                        line++;
                        col = 0;
                    }
                } else {
                    out[line][col++] = f;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Math2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out;
    }
    
    public static int[][] loadLandScanFile(String filename, byte[] b) throws IOException  {
        int[][] out = new int[601][601];
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
            in.read(b);
            short line = 0;
            short col = 0;
            int input = 0;
            int i = 0;
            while(i < b.length) {
                byte bt = b[i++];
                if(bt == -127) {
                    line++;
                    if(line > 600) {
                        return out;
                    }
                    col = 0;
                } else {
                    input = bt;
                    if(bt < 0) {
                        if(bt == -2) {
                            byte b1 = b[i++];
                            byte b2 = b[i++];
                            byte b3 = b[i++];
                            input = ( 0x88 | ((b1 & 0xFF) << 16) | ((b2 & 0xFF) << 8) | ((b3 & 0xFF)));
                        } else if(input == -1)  {
                            byte b1 = b[i++];
                            byte b2 = b[i++];
                            input = (short)((b1 << 8) | (b2 & 0xFF));
                        } 
                    }
                    out[line][col++] = input;
                }
                
            } 
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Math2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out;
    }
    
    
    public static byte [] long2ByteArray (long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }

    public static byte [] float2ByteArray (float value) {  
         return ByteBuffer.allocate(4).putFloat(value).array();
    }
}
