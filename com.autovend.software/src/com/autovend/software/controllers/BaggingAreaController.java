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

import com.autovend.devices.AbstractDevice;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.products.Product;

/**
 * An abstract class for objects which monitor and control the bagging area to
 * determine whether the customers order is valid or not, whether it be
 * validating the net weight is as expected, or through visual analysis of the
 * bagging area.
 */
public abstract class BaggingAreaController<D extends AbstractDevice<O>, O extends AbstractDeviceObserver>
		extends DeviceController<D, O> {

	private boolean orderValidated;

	public String getTypeName(){
		return "BaggingAreaController";
	}

	public BaggingAreaController(D newDevice) {
		super(newDevice);
	}


	/**
	 * A method used to inform the bagging area controller to update the expected
	 * items in the area how this is done will vary by the method used for
	 * validation.
	 *
	 */
	// Note: this method is not very generalized, I want to generalize this code so
	// that it works with
	// more than just weight based bagging area devices (so it can implement more
	// types of validation)
	abstract void updateExpectedBaggingArea(Product nextProduct, double weightInGrams, boolean isAdding);

	abstract public void resetOrder();

	boolean getBaggingValid() {
		return orderValidated;
	}

	void setBaggingValid(boolean validation) {
		this.orderValidated = validation;
	}

}
