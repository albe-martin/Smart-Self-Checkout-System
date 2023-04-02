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

import com.autovend.Bill;
import com.autovend.devices.BillDispenser;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.observers.BillDispenserObserver;

import java.math.BigDecimal;

public class BillDispenserController extends ChangeDispenserController<BillDispenser, BillDispenserObserver>
		implements BillDispenserObserver {
	public BillDispenserController(BillDispenser newDevice, BigDecimal denom) {
		super(newDevice, denom);
	}

	@Override
	public void emitChange() {
		try {
			this.getDevice().emit();
		} catch (EmptyException ex) {
			this.getMainController().changeDispenseFailed(this, this.getDenom());
		} catch (OverloadException ex) {
			System.out.println("This can't physically happen, something went wrong.");
		}
	}

	@Override
	public void reactToBillsFullEvent(BillDispenser dispenser) {
	}

	@Override
	public void reactToBillsEmptyEvent(BillDispenser dispenser) {

	}

	@Override
	public void reactToBillAddedEvent(BillDispenser dispenser, Bill bill) {
	}

	@Override
	public void reactToBillRemovedEvent(BillDispenser dispenser, Bill bill) {
	}

	@Override
	public void reactToBillsLoadedEvent(BillDispenser dispenser, Bill... bills) {
	}

	@Override
	public void reactToBillsUnloadedEvent(BillDispenser dispenser, Bill... bills) {
	}
}