/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.fragment;

import com.meicompany.pi.realtime.ode.util.Material;
import java.util.Random;

/**
 *
 * @author mpopescu
 */
public class FragmentOptions {
    private Random rand = new Random();
    
    private double dragCoefficient;
    private double liftCoefficient;
    private double lift2DragRatio;
    private double ballisticCoefficient;
    private double nominalMass;
    private double areaCrossSection;   
    
    private double explosionSpeed;
    private double explosionDirectionFactor;
    
    private double characteristicLength;
    
    private Material material;
    private double hollownessFactor;
    
    private final double[] sigma_l2d = new double[] {0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.8};

    
    /**
     * Generates this as a pseudo fragment, ie not set from a list. 
     */
    public void generatePseudo() {
        this.material = new Material();
        this.ballisticCoefficient = Math.pow(10,rand.nextFloat()*3)+2;
        this.dragCoefficient = rand.nextFloat()*0.2+0.6;
        this.characteristicLength = 1;
        this.lift2DragRatio = sigma_l2d[rand.nextInt(6)];
        this.explosionSpeed = Math.pow(15,rand.nextFloat()*2);
        this.hollownessFactor = 0.25;
    }

    /**
     * @return the dragCoefficient
     */
    public double getDragCoefficient() {
        return dragCoefficient;
    }

    /**
     * @param dragCoefficient the dragCoefficient to set
     */
    public void setDragCoefficient(double dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    /**
     * @return the liftCoefficient
     */
    public double getLiftCoefficient() {
        return liftCoefficient;
    }

    /**
     * @param liftCoefficient the liftCoefficient to set
     */
    public void setLiftCoefficient(double liftCoefficient) {
        this.liftCoefficient = liftCoefficient;
    }

    /**
     * @return the lift2DragRatio
     */
    public double getLift2DragRatio() {
        return lift2DragRatio;
    }

    /**
     * @param lift2DragRatio the lift2DragRatio to set
     */
    public void setLift2DragRatio(double lift2DragRatio) {
        this.lift2DragRatio = lift2DragRatio;
    }

    /**
     * @return the ballisticCoefficient
     */
    public double getBallisticCoefficient() {
        return ballisticCoefficient;
    }

    /**
     * @param ballisticCoefficient the ballisticCoefficient to set
     */
    public void setBallisticCoefficient(double ballisticCoefficient) {
        this.ballisticCoefficient = ballisticCoefficient;
    }

    /**
     * @return the nominalMass
     */
    public double getNominalMass() {
        return nominalMass;
    }

    /**
     * @param nominalMass the nominalMass to set
     */
    public void setNominalMass(double nominalMass) {
        this.nominalMass = nominalMass;
    }

    /**
     * @return the areaCrossSection
     */
    public double getAreaCrossSection() {
        return areaCrossSection;
    }

    /**
     * @param areaCrossSection the areaCrossSection to set
     */
    public void setAreaCrossSection(double areaCrossSection) {
        this.areaCrossSection = areaCrossSection;
    }

    /**
     * @return the explosionSpeed
     */
    public double getExplosionSpeed() {
        return explosionSpeed;
    }

    /**
     * @param explosionSpeed the explosionSpeed to set
     */
    public void setExplosionSpeed(double explosionSpeed) {
        this.explosionSpeed = explosionSpeed;
    }

    /**
     * @return the explosionDirectionFactor
     */
    public double getExplosionDirectionFactor() {
        return explosionDirectionFactor;
    }

    /**
     * @param explosionDirectionFactor the explosionDirectionFactor to set
     */
    public void setExplosionDirectionFactor(double explosionDirectionFactor) {
        this.explosionDirectionFactor = explosionDirectionFactor;
    }

    /**
     * @return the characteristicLength
     */
    public double getCharacteristicLength() {
        return characteristicLength;
    }

    /**
     * @param characteristicLength the characteristicLength to set
     */
    public void setCharacteristicLength(double characteristicLength) {
        this.characteristicLength = characteristicLength;
    }

    /**
     * @return the material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * @param material the material to set
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * @return the hollownessFactor
     */
    public double getHollownessFactor() {
        return hollownessFactor;
    }

    /**
     * @param hollownessFactor the hollownessFactor to set
     */
    public void setHollownessFactor(double hollownessFactor) {
        this.hollownessFactor = hollownessFactor;
    }
    
    
    
    
}
