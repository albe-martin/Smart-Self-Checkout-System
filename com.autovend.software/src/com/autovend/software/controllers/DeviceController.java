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

@SuppressWarnings("unchecked")

public abstract class DeviceController<D extends AbstractDevice<O>, O extends AbstractDeviceObserver> {
	private D device;
	private CheckoutController mainController;
	public D getDevice() {
		return this.device;
	}

	public DeviceController(D newDevice) {
		this.device = newDevice;
		this.device.register((O) this);
		mainController=null;
	}

	public void setDevice(D newDevice) {
		if (device != null) {
			this.device.deregister((O) this);
		}
		this.device = newDevice;
		if (device != null) {
			this.device.register((O) this);
		}
	}

	abstract String getTypeName();
	//This is used as an identifier for the type of controller, for a single unified controller
	//hashset in the checkout controller, which cuts down its length but
	//around half.

	public final CheckoutController getMainController() {
		return this.mainController;
	};
	public final void setMainController(CheckoutController newMainController) {
		if (this.mainController != null) {
			this.mainController.deregisterController(getTypeName(),this);
		}
		this.mainController = newMainController;
		if (this.mainController != null) {
			this.mainController.registerController(this.getTypeName(),this);
		}
	}

	public void enableDevice() {
		this.device.enable();
	}

	public void disableDevice() {
		this.device.disable();
	}

	boolean isDeviceDisabled() {
		return this.device.isDisabled();
	}

	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}
}
