/*
SENG 300 Project Iteration 2
Group 7
Niran Malla 30086877
Saksham Puri 30140617
Fatema Chowdhury 30141268
Janet Tesgazeab 30141335
Fabiha Fairuzz Subha 30148674
Ryan Janiszewski 30148838
Umesh Oad 30152293
Manvi Juneja 30153525
Daniel Boettcher 30153811
Zainab Bari 30154224
Arie Goud 30163410
Amasil Rahim Zihad 30164830
*/

package com.autovend.software.controllers;

import com.autovend.devices.ElectronicScale;
import com.autovend.devices.observers.ElectronicScaleObserver;
import com.autovend.products.Product;

public class BaggingScaleController extends BaggingAreaController<ElectronicScale, ElectronicScaleObserver>
		implements ElectronicScaleObserver {
	private double currentWeight;
	private double expectedWeight;

	public BaggingScaleController(ElectronicScale newDevice) {
		super(newDevice);
	}

	/**
	 * Method used to update the expected weight for validation of orders.
	 * 
	 * @param nextProduct
	 * @param weightInGrams
	 */
	@Override
	void updateExpectedBaggingArea(Product nextProduct, double weightInGrams, boolean isAdding) {
		if (isAdding) {
			this.expectedWeight += weightInGrams;
		} else {
			this.expectedWeight -= weightInGrams;
			if (this.expectedWeight!=this.currentWeight){
				this.getMainController().baggedItemsInvalid(true);
			}
		}
		this.setBaggingValid(false);
		// TODO: Figure out how changes smaller than sensitivity would be handled
		// TODO: Also figure out how items which would cause the scale to be overloaded
		// should be handled.
	}

	@Override
	public void resetOrder() {
		this.setBaggingValid(true);
		this.currentWeight = 0;
		this.expectedWeight = 0;
	}
	@Override
	public void reactToWeightChangedEvent(ElectronicScale scale, double weightInGrams) {
		if (scale != this.getDevice()) {return;}
		this.currentWeight = weightInGrams;
		if (this.currentWeight == this.expectedWeight) {
			this.setBaggingValid(true);
			this.getMainController().baggedItemsValid();

		}
		else {
			System.out.println("inval");
			this.getMainController().baggedItemsInvalid(false);
			this.setBaggingValid(false);
		}
		System.out.println(currentWeight);
		System.out.println(expectedWeight);
	}
	@Override
	public void reactToOverloadEvent(ElectronicScale scale) {
		if (scale != this.getDevice()) {return;}
		this.getMainController().baggingAreaError();
	}
	@Override
	public void reactToOutOfOverloadEvent(ElectronicScale scale) {
		if (scale != this.getDevice()) {return;}
		this.getMainController().baggingAreaErrorEnded();
	}
	public double getCurrentWeight() {
		return currentWeight;
	}
	public double getExpectedWeight() {
		return this.expectedWeight;
	}
	public void setExpectedWeight(double newWeight) {
		this.expectedWeight = newWeight;
	}
}
