package com.autovend.software.test;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.TouchScreen;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.BaggingScaleController;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;

public class AddItemPLU {
	private CheckoutController checkoutController;
	private BarcodeScannerController scannerController;
	private BaggingScaleController scaleController;
	private AttendantIOController attendantController;
	private AttendantStationController stationController;
	private CustomerIOController customerController;
	private BarcodedProduct databaseItem1;
	private BarcodedProduct databaseItem2;
	private PLUCodedProduct pluProduct1;
	private BarcodedUnit validUnit1;
	private BarcodedUnit validUnit2;

	BarcodeScanner stubScanner;
	ElectronicScale stubScale;
	TouchScreen stubDevice;

	/**
	 * Setup for testing
	 */
	@Before
	public void setup() {
		checkoutController = new CheckoutController();
		scannerController = new BarcodeScannerController(new BarcodeScanner());
		scaleController = new BaggingScaleController(new ElectronicScale(1000, 1));
		stationController = new AttendantStationController();
		stubDevice = new TouchScreen();

		// First item to be scanned
		databaseItem1 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item 1",
				BigDecimal.valueOf(83.29), 359.0);

		// Second item to be scanned
		databaseItem2 = new BarcodedProduct(new Barcode(Numeral.four, Numeral.five), "test item 2",
				BigDecimal.valueOf(42), 60.0);

		validUnit1 = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 359.0);
		validUnit2 = new BarcodedUnit(new Barcode(Numeral.four, Numeral.five), 60.0);

		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(databaseItem1.getBarcode(), databaseItem1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(databaseItem2.getBarcode(), databaseItem2);
		
		// PLU products
		pluProduct1 = new PLUCodedProduct(new PriceLookUpCode(Numeral.five, Numeral.five, Numeral.five, Numeral.five, Numeral.five), "test item 1",BigDecimal.valueOf(83.29));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluProduct1.getPLUCode(), pluProduct1);

		stubScanner = new BarcodeScanner();
		stubScale = new ElectronicScale(1000, 1);

		scannerController = new BarcodeScannerController(stubScanner);
		scannerController.setMainController(checkoutController);
		scaleController = new BaggingScaleController(stubScale);
		scaleController.setMainController(checkoutController);
		attendantController = new AttendantIOController(stubDevice);
		attendantController.setMainAttendantController(stationController);
		customerController = new CustomerIOController(stubDevice);
		customerController.setMainController(checkoutController);

		stubScanner.register(scannerController);
		stubScale.register(scaleController);

	}

	/**
	 * Tears down objects so they can be initialized again with setup
	 */
	@After
	public void teardown() {
		checkoutController = null;
		scannerController = null;
		scaleController = null;
		stubScale = null;
		stubScanner = null;

	}
	@Test
	public void testInvalidPLUCode() {
		
	}
	
}
