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

package com.autovend.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Map;

import com.autovend.software.controllers.BaggingScaleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Numeral;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.controllers.BaggingAreaController;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;

@SuppressWarnings("rawtypes")

public class AddOwnBagsTest {
	BarcodeScannerController scannerController;
	BarcodeScanner stubScanner;

	BaggingScaleController scaleController;
	ElectronicScale stubScale;
	BarcodedProduct databaseItem;

	BarcodedUnit validUnit;
	BarcodedUnit bag;
	CheckoutController checkoutController;
	SelfCheckoutStation stubStation;

	/**
	 * Set up of objects, variables etc.. that happens before tests
	 */
	@Before
	public void setup() {
		SelfCheckoutStation stubStation = new SelfCheckoutStation(Currency.getInstance("CAD"),
				new int[] { 5, 10, 20, 50, 100 },
				new BigDecimal[] { new BigDecimal(25), new BigDecimal(100), new BigDecimal(5) }, 1000, 1);
		databaseItem = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item",
				BigDecimal.valueOf(83.29), 359.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(databaseItem.getBarcode(), databaseItem);
		validUnit = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 76.0);
		bag = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 0.75);

		stubScanner = stubStation.mainScanner;
		stubScale = stubStation.baggingArea;
		checkoutController = new CheckoutController(stubStation);

		scannerController = new BarcodeScannerController(stubScanner);
		scannerController.setMainController(checkoutController);
		scannerController.enableDevice();

		scaleController = new BaggingScaleController(stubScale);
		scaleController.setMainController(checkoutController);
		scaleController.enableDevice();

		stubScanner.register(scannerController);
		stubScale.register(scaleController);
	}

	/**
	 * Tears down objects so they can be initialized again with setup
	 */
	@After
	public void teardown() {
		stubScanner = null;
		checkoutController = null;
		scannerController = null;
		scaleController = null;
		stubScale = null;
		stubStation = null;
	}

}