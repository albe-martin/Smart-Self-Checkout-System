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

import com.autovend.devices.CoinValidator;
import com.autovend.devices.observers.CoinValidatorObserver;

public class CoinPaymentController extends PaymentController<CoinValidator, CoinValidatorObserver>
		implements CoinValidatorObserver {
	public CoinPaymentController(CoinValidator device) {
		super(device);
	}

	@Override
	public void reactToValidCoinDetectedEvent(CoinValidator validator, BigDecimal value) {
		if (validator != this.getDevice()) {
			return;
		}
		this.getMainController().addToAmountPaid(value);
	}

	@Override
	public void reactToInvalidCoinDetectedEvent(CoinValidator validator) {}
}