package com.autovend.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;

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
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.controllers.DeviceController;

public class AttendantIOTest {
	
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
		
		product1 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item 1", BigDecimal.valueOf(83.29), 359.0);
	}
	
	/**
	 * Tests if station is prevented from use by the Attendant,
	 * by trying to add an item when the station is disabled.
	 */
	@Test
	public void testPreventStationUse_TryAddItem() {
		for (DeviceController io : asc.getAttendantIOControllers()) {
			((AttendantIOController)io).disableStation(checkoutController1);
		}
		
		checkoutController1.addItem(product1);
		
		// Item should not be added, and order size should be 0
		assertEquals(0, checkoutController1.getOrder().size());
	}
	
	/**
	 * Tests if station is prevented from use by the Attendant,
	 * by trying to purchase bags when the station is disabled.
	 */
	@Test
	public void testPreventStationUse_TryPayWithCard() {
		for (DeviceController io : asc.getAttendantIOControllers()) {
			((AttendantIOController)io).disableStation(checkoutController1);
		}
		
		CardIssuer cardIssuer = new CardIssuer("Stub");
		checkoutController1.payByCard(cardIssuer, new BigDecimal(0.00));
		
		for (DeviceController controller : checkoutController1.getControllersByType("PaymentController")) {
			if (controller instanceof CardReaderController) {
				// payByCard should not go through, 
				// so that the controller would not set the bank to be the cardIssuer
				assertFalse(((CardReaderController) controller).bank == cardIssuer);
			}
		}		
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
		
		checkoutController1.addItem(product1);
		
		// Item should not be added, and order size should be 0
		assertEquals(0, checkoutController1.getOrder().size());
	}
}
