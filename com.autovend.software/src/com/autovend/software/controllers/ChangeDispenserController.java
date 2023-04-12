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

import com.autovend.devices.AbstractDevice;
import com.autovend.devices.observers.AbstractDeviceObserver;

abstract public class ChangeDispenserController<D extends AbstractDevice<O>, O extends AbstractDeviceObserver>
		extends DeviceController<D, O> implements Comparable<ChangeDispenserController>{

	public BigDecimal denom;

	@Override
	public int compareTo(ChangeDispenserController o) {
		return (this.getDenom().compareTo(o.getDenom()));
	}

	public final String getTypeName(){
		return "ChangeDispenserController";
	}

	public ChangeDispenserController(D newDevice, BigDecimal denom) {
		super(newDevice);
		this.denom = denom;
	}


	public void setDenom(BigDecimal denom) {
		this.denom = denom;
	}

	BigDecimal getDenom() {
		return this.denom;
	}

	abstract void emitChange();
}
