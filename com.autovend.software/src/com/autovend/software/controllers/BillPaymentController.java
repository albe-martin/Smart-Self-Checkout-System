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

import java.math.BigDecimal;
import java.util.Currency;

import com.autovend.devices.BillValidator;
import com.autovend.devices.observers.BillValidatorObserver;

/*
 * A class for objects that controls payment made with cash bills
 */
public class BillPaymentController extends PaymentController<BillValidator, BillValidatorObserver>
		implements BillValidatorObserver {

	public BillPaymentController(BillValidator device) {
		super(device);
	}

	/**
	 * The following class checks we have the same device and then
	 */
	@Override
	public void reactToValidBillDetectedEvent(BillValidator validator, Currency currency, int value) {
		this.getMainController().addToAmountPaid(new BigDecimal(value));
	}

	@Override
	public void reactToInvalidBillDetectedEvent(BillValidator validator) {
	}

}
