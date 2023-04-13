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
