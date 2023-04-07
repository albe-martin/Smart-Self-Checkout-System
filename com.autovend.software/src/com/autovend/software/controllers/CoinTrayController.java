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

import com.autovend.devices.CoinTray;
import com.autovend.devices.observers.CoinTrayObserver;

public class CoinTrayController extends ChangeSlotController<CoinTray, CoinTrayObserver> implements CoinTrayObserver {
	public CoinTrayController(CoinTray coinTray) {
		super(coinTray);
	}

	@Override
	public void reactToCoinAddedEvent(CoinTray tray) {
		// if stuff in coin tray, then check if the tray is full and if not then tell
		// the checkout controller
		// to give more change
		if (tray != this.getDevice()) {
			return;
		}
		;
		if (tray.hasSpace()) {
			this.getMainController().dispenseChange();
		}
	}
}
