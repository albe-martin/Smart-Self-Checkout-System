package com.autovend.software.controllers;

import com.autovend.devices.ElectronicScale;
import com.autovend.devices.observers.ElectronicScaleObserver;

/**
 * Just a boilerplate controller used to interact with the scale in the scanning area
 * used to get values for adding items by PLU codes.
 * TODO: Finish the body of this class (this is just created currently for termporary use)
 */
public class ScanningScaleController extends DeviceController<ElectronicScale, ElectronicScaleObserver> implements ElectronicScaleObserver{
    double currentWeight;
    
    public ScanningScaleController(ElectronicScale newDevice) {
        super(newDevice);
		this.currentWeight = 0;
    }

    public final String getTypeName(){
        return "ScanningScaleController";
    }
    
    
    public double getCurrentWeight() {
    	
        return this.currentWeight;
    }

    @Override
    public void reactToWeightChangedEvent(ElectronicScale scale, double weightInGrams) {
		if (scale != this.getDevice()) {return;}
		this.currentWeight = weightInGrams;
    }

    @Override
    public void reactToOverloadEvent(ElectronicScale scale) {}

    @Override
    public void reactToOutOfOverloadEvent(ElectronicScale scale) {}
}
