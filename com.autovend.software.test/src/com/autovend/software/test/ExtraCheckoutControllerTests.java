package com.autovend.software.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.DeviceController;
import com.autovend.software.controllers.ScanningScaleController;
import com.autovend.devices.ElectronicScale;

public class ExtraCheckoutControllerTests {
	@Test
	public void testGetDeviceControllersRevised() {
		CheckoutController checkout = new CheckoutController();
		assertTrue(checkout.getAllDeviceControllersRevised() instanceof HashMap<String, ArrayList<DeviceController>>);
	}
	
	@Test
	public void testDeregisterUnknownType() {
		CheckoutController checkout = new CheckoutController();
		checkout.deregisterController("foo", null);
	}
	
	@Test
	public void testDeregisterNotContained() {
		CheckoutController checkout = new CheckoutController();
		checkout.deregisterController("foo", null);
		checkout.deregisterController("ScanningScaleController", new ScanningScaleController(new ElectronicScale(5, 1)));
	}
	
	@Test
	public void testRegisterNull() {
		CheckoutController checkout = new CheckoutController();
		checkout.registerController("foo", null);
	}
}
