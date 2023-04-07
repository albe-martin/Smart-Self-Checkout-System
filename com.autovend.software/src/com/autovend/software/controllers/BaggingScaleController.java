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

import java.util.ArrayList;

public class BaggingScaleController extends BaggingAreaController<ElectronicScale, ElectronicScaleObserver>
		implements ElectronicScaleObserver {
	private double currentWeight;
	private double expectedWeight;
	private double savedWeight;
	private boolean addingBags;

	private boolean AttendantApproval;

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
	
	public void attendantInput(boolean approval) {
		AttendantApproval = approval;
		return;
	}

	@Override
	public void reactToWeightChangedEvent(ElectronicScale scale, double weightInGrams) {
		if (scale != this.getDevice()) {
			return;
		}
		;
		this.currentWeight = weightInGrams;
		// if the customer is adding their own bags, no need to check the expected
		// weight as there is not one yet
		if (addingBags) {
			return;
		}
		if (this.currentWeight == this.expectedWeight) {
			this.getMainController().baggedItemsValid();
		}
		// case of weight discrepancy
		else {
			

			// boolean value resolveDisrepancy:
			// true if discrepancy is resolved by:
			// -a do not bag request from customer IO
			// -attendant approval
			boolean resolveDiscrepancy = false;

			// system blocks checkout from further interaction
			this.getMainController().baggingItemLock = true;

			// discrepancy resolved if customer signals a dnb request or attendant approves
			if (AttendantApproval)
				resolveDiscrepancy = true;

			// validates bagging if the discrepancy was resolved
			if (resolveDiscrepancy) {
				this.getMainController().baggedItemsValid();
				this.getMainController().baggingItemLock = false;
			}
			else {
			this.getMainController().baggedItemsInvalid("The items in the bagging area don't have the correct weight.");
			}

		}
	}

	@Override
	public void reactToOverloadEvent(ElectronicScale scale) {
		if (scale != this.getDevice()) {
			return;
		}
		;
		this.getMainController().baggingAreaError("The scale is currently overloaded, please take items off it to avoid damaging the system.");
	}

	@Override
	public void reactToOutOfOverloadEvent(ElectronicScale scale) {
		if (scale != this.getDevice()) {
			return;
		}
		;
		this.getMainController().baggingAreaErrorEnded("The scale is no longer overloaded.");
	}

	public double getCurrentWeight() {
		return currentWeight;
	}

	public void updateWithBagWeight(double weight) {
		this.expectedWeight += weight;
	}

	/**
	 * Saves the current weight on the scale in the savedWeights ArrayList.
	 */
	public void saveCurrentWeight(){
		savedWeight = this.currentWeight;
	}

	public void setAddingBags(boolean value) {
		this.addingBags = value;
	}

	public double getExpectedWeight() {
		return this.expectedWeight;
	}

	public void setExpectedWeight (double weight){
		this.expectedWeight = weight;
	}

	public boolean getAddingBags() {
		return this.addingBags;
	}

	public double getSavedWeight(){
		return this.savedWeight;
	}

}
