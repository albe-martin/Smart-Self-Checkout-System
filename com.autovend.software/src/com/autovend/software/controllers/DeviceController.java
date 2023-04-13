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

	public abstract String getTypeName();
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
