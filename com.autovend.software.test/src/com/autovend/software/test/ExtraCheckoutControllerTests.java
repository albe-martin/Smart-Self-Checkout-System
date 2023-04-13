package com.autovend.software.test;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.ChangeDispenserController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CoinDispenserController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.controllers.DeviceController;
import com.autovend.software.controllers.ScanningScaleController;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.TouchScreen;

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
	
	@Test
	public void testCompletePaymentEmptyOrder() {
		CheckoutController checkout = new CheckoutController();
		checkout.completePayment();
	}
	
	@Test
	public void testCompletePaymentBaggingLock() {
		CheckoutController checkout = new CheckoutController();
		checkout.baggingItemLock = true;
		checkout.completePayment();
	}
	
	@Test
	public void testCompletePaymentProtectionLock() {
		CheckoutController checkout = new CheckoutController();
		checkout.systemProtectionLock = true;
		checkout.completePayment();
	}
	
	@Test
	public void testCompletePaymentDisabled() {
		CheckoutController checkout = new CheckoutController();
		checkout.disableStation();
		checkout.completePayment();
	}
	
	@Test
	public void testCompleteShortFunds() {
		CheckoutController checkout = new CheckoutController();
		checkout.cost = BigDecimal.ONE;
		checkout.completePayment();
	}
	
	@Test
	public void testDispenseChangeNoExtra() {
		CheckoutController checkout = new CheckoutController();
		checkout.payingChangeLock = true;
		CustomerIOController cioc = new CustomerIOController(new TouchScreen());
		checkout.registerController("CustomerIOController", cioc);
		cioc.setMainController(checkout);
		cioc.startPressed();
		checkout.dispenseChange();
	}
	
	@Test
	public void testDispenseChangeExtra() {
		CheckoutController checkout = new CheckoutController();
		checkout.payingChangeLock = true;
		CustomerIOController cioc = new CustomerIOController(new TouchScreen());
		checkout.registerController("CustomerIOController", cioc);
		cioc.setMainController(checkout);
		cioc.startPressed();
		checkout.addToAmountPaid(BigDecimal.ONE);
		CoinDispenserController cc = new CoinDispenserController(new CoinDispenser(5), BigDecimal.ONE);
		cc.setMainController(checkout);
		AttendantIOController aioc = new AttendantIOController(new TouchScreen());
		aioc.setMainController(checkout);
		AttendantStationController ac = new AttendantStationController();
		aioc.setMainAttendantController(ac);
		ac.registerController(cioc);
		ac.registerController(aioc);
		ac.registerUser("a", "b");
		ac.login("a","b");
		aioc.loginValidity(true, "bob");
		checkout.registerController("AttendantIOController", aioc);
		checkout.registerController("ChangeDispenserController", cc);
		checkout.dispenseChange();
	}
}
