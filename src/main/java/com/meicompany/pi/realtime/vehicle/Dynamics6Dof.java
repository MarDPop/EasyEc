package com.meicompany.pi.realtime.vehicle;

/**
 *
 * @author mpopescu
 */
public interface Dynamics6Dof {
    
    public double[] getAxisForces();
    
    public double[] getAxisTorques();
}
