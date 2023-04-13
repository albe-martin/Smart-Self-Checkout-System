/*
 * SENG 300 Project Iteration 3 - Group P3-2
 * Braedon Haensel -         UCID: 30144363
 * Umar Ahmed -             UCID: 30145076
 * Bartu Okan -             UCID: 30150180
 * Arie Goud -                 UCID: 30163410
 * Abdul Biderkab -         UCID: 30156693
 * Hamza Khan -             UCID: 30157097
 * James Hayward -             UCID: 30149513
 * Christian Salvador -     UCID: 30089672
 * Fatema Chowdhury -         UCID: 30141268
 * Sankalp Bartwal -         UCID: 30132025
 * Avani Sharma -             UCID: 30125040
 * Albe Martin -             UCID: 30161964
 * Omar Khan -                 UCID: 30143707
 * Samantha Liu -             UCID: 30123255
 * Alex Chen -                 UCID: 30140184
 * Auric Adubofour-Poku -     UCID: 30143774
 * Grant Tkachyk -             UCID: 30077137
 * Amandeep Kaur -             UCID: 30153923
 * Tashi Labowka-Poulin -     UCID: 30140749
 * Daniel Chang -             UCID: 30110252
 * Jacob Braun -             UCID: 30124507
 * Omar Ragab -             UCID: 30148549
 * Artemy Gavrilov -         UCID: 30143698
 * Colton Gowans -             UCID: 30143979
 * Hada Rahadhi Hafiyyan -     UCID: 30186484
 *
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
