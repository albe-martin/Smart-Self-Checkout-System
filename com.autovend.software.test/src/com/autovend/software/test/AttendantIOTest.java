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

import java.math.BigDecimal;
import java.util.Currency;

import com.autovend.BarcodedUnit;
import com.autovend.IBarcoded;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.external.CardIssuer;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CardReaderController;
import com.autovend.software.controllers.CardReaderControllerState;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.controllers.DeviceController;

import static org.junit.Assert.*;

public class AttendantIOTest {
	CheckoutController checkoutController;
	
	SupervisionStation attendantStation;
	AttendantStationController asc;
	
	SelfCheckoutStation station1;
	CheckoutController checkoutController1;
	
	SelfCheckoutStation station2;
	CheckoutController checkoutController2;
	
	SelfCheckoutStation station3;
	CheckoutController checkoutController3;
	
	Currency currency;
	int[] billDenominations;
	BigDecimal[] coinDenominations;
	
	BarcodedProduct product1;
	BarcodedUnit item1;
	
	@Before
	public void setup() {
		currency = Currency.getInstance("CAD");
		billDenominations = new int[] {5, 10, 20, 50, 100};
		coinDenominations = new BigDecimal[] {new BigDecimal(0.05), new BigDecimal(0.10), new BigDecimal(0.25), new BigDecimal(1), new BigDecimal(2)};
		station1 = new SelfCheckoutStation(currency, billDenominations, coinDenominations, 1000, 1);
		station2 = new SelfCheckoutStation(currency, billDenominations, coinDenominations, 1000, 1);
		station3 = new SelfCheckoutStation(currency, billDenominations, coinDenominations, 1000, 1);
		
		checkoutController1 = new CheckoutController(station1);
		checkoutController2 = new CheckoutController(station2);
		checkoutController3 = new CheckoutController(station3);
		
		attendantStation = new SupervisionStation();
		asc = new AttendantStationController(attendantStation);
		
		for (DeviceController io : checkoutController1.getControllersByType("CustomerIOController")) {
			asc.addStation(station1, (CustomerIOController)io);
		}
		
		for (DeviceController io : checkoutController2.getControllersByType("CustomerIOController")) {
			asc.addStation(station2, (CustomerIOController)io);
		}
		
		for (DeviceController io : checkoutController3.getControllersByType("CustomerIOController")) {
			asc.addStation(station3, (CustomerIOController)io);
		}
		
		asc.registerUser("test", "test");
		asc.login("test", "test");

		product1 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item 1", BigDecimal.valueOf(83.29), 359.0);
		item1 = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three),359.0);

	}
	
	/**
	 * Tests if station is prevented from use by the Attendant,
	 * by trying to add an item when the station is disabled.
	 */
	@Test
	public void testPreventStationUse_TryAddItem() {
		for (DeviceController io : asc.getAttendantIOControllers()) {
			((AttendantIOController) io).disableStation(checkoutController1);
		}
		checkoutController1.addItem(product1);

		// Item should not be added, and order size should be 0
		assertEquals(0, checkoutController1.getOrder().size());
	}
	
	/**
	 * Tests if station is prevented from use by the Attendant,
	 * by trying to pay with credit card when the station is disabled.
	 */
	@Test
	public void testPreventStationUse_TryPayWithCreditCard() {
		for (DeviceController io : asc.getAttendantIOControllers()) {
			((AttendantIOController)io).disableStation(checkoutController1);
		}
		
		CardIssuer cardIssuer = new CardIssuer("Stub");
		checkoutController1.payByBankCard(CardReaderControllerState.PAYINGBYCREDIT, cardIssuer, new BigDecimal(0.00));
		
		for (DeviceController controller : checkoutController1.getControllersByType("PaymentController")) {
			if (controller instanceof CardReaderController) {
				// payByCard should not go through, 
				// so that the controller would not set the bank to be the cardIssuer
				assertNotSame(((CardReaderController) controller).bank, cardIssuer);
			}
		}		
	}
	
	/**
	 * Tests if station is prevented from use by the Attendant,
	 * by trying to pay with credit card when the station is disabled.
	 */
	@Test
	public void testPreventStationUse_TryPurchaseBags() {
		for (DeviceController io : asc.getAttendantIOControllers()) {
			((AttendantIOController)io).disableStation(checkoutController1);
		}
		
		CardIssuer cardIssuer = new CardIssuer("Stub");
		checkoutController1.purchaseBags(5);
		
		// Item should not be added, and order size should be 0
		assertEquals(0, checkoutController1.getOrder().size());
	}
	
	/**
	 * Tests if station is prevented from use by the Attendant,
	 * by trying to add an item when the station is disabled.
	 */
	@Test
	public void testPreventStationUseAndUnlock_TryAddItem() {
		for (DeviceController io : asc.getAttendantIOControllers()) {
			((AttendantIOController)io).disableStation(checkoutController1);
			((AttendantIOController)io).enableStation(checkoutController1);
		}
		
		checkoutController1.addItem(product1);
		
		// Item should be added, so order size should be 1
		assertEquals(1, checkoutController1.getOrder().size());
	}
	
	/**
	 * Tests if station is prevented from use by the Attendant,
	 * when the station is not registered to the Attendant.
	 */
	@Test
	public void testPreventStationUse_NotRegistered_TryAddItem() {
		for (DeviceController io : checkoutController1.getControllersByType("CustomerIOController")) {
			asc.removeStation(station1, (CustomerIOController)io);
		}
		
		for (DeviceController io : asc.getAttendantIOControllers()) {
			((AttendantIOController)io).disableStation(checkoutController1);
		}
		station1.mainScanner.enable();
		station1.mainScanner.scan(item1);
		
		// Item should not be added, and order size should be 0
		assertEquals(0, checkoutController1.getOrder().size());
	}
}
