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

import com.autovend.devices.BillSlot;
import com.autovend.devices.observers.BillSlotObserver;

public class BillChangeSlotController extends ChangeSlotController<BillSlot, BillSlotObserver>
		implements BillSlotObserver {
	public BillChangeSlotController(BillSlot newDevice) {
		super(newDevice);
	}

	@Override
	public void reactToBillInsertedEvent(BillSlot slot) {
	}

	@Override
	public void reactToBillEjectedEvent(BillSlot slot) {
	}

	@Override
	public void reactToBillRemovedEvent(BillSlot slot) {
		this.getMainController().dispenseChange();
	}
}
