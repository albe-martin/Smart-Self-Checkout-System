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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.BaggingAreaController;
import com.autovend.software.controllers.BaggingScaleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.SellableUnit;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.DisabledException;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.TouchScreen;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.controllers.DeviceController;

@SuppressWarnings("deprecation")
/**
 * Test class for the add item and add item after partial payment use case
 */
public class RemoveItemTest {

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
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream ORIGINAL_OUT = System.out;
	private final PrintStream ORIGINAL_ERR = System.err;

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
		stubScale = new ElectronicScale(100000, 1);

		scannerController = new BarcodeScannerController(stubScanner);
		scannerController.setMainController(checkoutController);
		scaleController = new BaggingScaleController(stubScale);
		scaleController.setMainController(checkoutController);
		attendantController = new AttendantIOController(stubDevice);
		attendantController.setMainAttendantController(stationController);
		customerController = new CustomerIOController(stubDevice);
		customerController.setMainController(checkoutController);
		
		customerController.registerAttendant(attendantController);
		

		stationController.registerUser("TestUser", "TestPass");
		stationController.login("TestUser", "TestPass");
		

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

	/**
	 * Tests removeItemFromOrder by adding two items and removing one of them
	 */
	@Test
	public void testRemoveOneItem() {

		// Adds item
		checkoutController.addItem(databaseItem1);

		// Adds the cost of the first item to the total
		BigDecimal total = databaseItem1.getPrice();

		// Checks that the item was added and the order was updated to 1
		assertEquals(1, checkoutController.getOrder().size());

		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());

		// Simulates an item being placed
		stubScale.add(validUnit1);

		// Adds a second item
		checkoutController.addItem(databaseItem2);

		// Adds the cost of the second item to the total
		total = total.add(databaseItem2.getPrice());

		// Rounds the value to 2 decimal places
		total = total.setScale(2, BigDecimal.ROUND_HALF_UP);

		// Checks that the item was added and the order was updated to 2
		assertEquals(2, checkoutController.getOrder().size());

		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());

		//remove item
		checkoutController.removeItemFromOrder(databaseItem1,BigDecimal.valueOf(1));

		//subtract cost of item from total
		total = total.subtract(databaseItem1.getPrice());

		//rounds the value to 2 decimal places
		total = total.setScale(2, BigDecimal.ROUND_HALF_UP);

		// Checks that the item was removed and the order was updated to 1
		assertEquals(1, checkoutController.getOrder().size());

		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());
		
	}

	/**
	 * Tests removeItemFromOrder by adding two items of one type and one item of another type
	 * It then tries to remove 3 items of the first type. What should happen is only 2 are removed and the total cost should not be negative
	 */
	@Test
	public void testRemoveItemMoreThanTotal() {

		// Adds item - 2 of type databaseItem1
		checkoutController.addItem(databaseItem1,BigDecimal.valueOf(2));
		// Adds the cost of the first item to the total
		BigDecimal total = databaseItem1.getPrice();
		total = total.add(databaseItem1.getPrice());
		// Checks that the item was added and the order was updated to 1
		assertEquals(1, checkoutController.getOrder().size());
		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());
		
		SellableUnit twoValidUnit1s = new BarcodedUnit(databaseItem1.getBarcode(), databaseItem1.getExpectedWeight() * 2);
		
		// Simulates two items being placed
		stubScale.add(twoValidUnit1s);

		// Adds a second item
		checkoutController.addItem(databaseItem2);
		// Adds the cost of the second item to the total
		total = total.add(databaseItem2.getPrice());
		// Rounds the value to 2 decimal places
		total = total.setScale(2, RoundingMode.HALF_UP);
		// Checks that the item was added and the order was updated to 2
		assertEquals(2, checkoutController.getOrder().size());
		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());
		// Unblocks the station and lets a new item be scanned
		checkoutController.baggedItemsValid();

		//remove item
		checkoutController.removeItemFromOrder(databaseItem1,BigDecimal.valueOf(3));
		//subtract cost of 2 items from total even though we tried to remove three because there are only 2
		total = total.subtract(databaseItem1.getPrice());
		total = total.subtract(databaseItem1.getPrice());
		//rounds the value to 2 decimal places
		total = total.setScale(3, BigDecimal.ROUND_HALF_UP);
		// Checks that the item was removed and the order was updated to 1
		assertEquals(1, checkoutController.getOrder().size());
		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());
		
	}
	
	/**
	 * Tests removeItemFromOrder by adding four items of one type and one item of another type
	 * It then tries to remove 2 items of the first type.
	 */
	@Test
	public void testRemoveItemLessThanTotal() {

		// Adds item - 2 of type databaseItem1
		checkoutController.addItem(databaseItem1,BigDecimal.valueOf(4));
		// Adds the cost of the first item to the total
		BigDecimal total = databaseItem1.getPrice().multiply(BigDecimal.valueOf(4));
		// Checks that the item was added and the order was updated to 1
		assertEquals(1, checkoutController.getOrder().size());
		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());

		SellableUnit fourValidUnit1s = new BarcodedUnit(databaseItem1.getBarcode(), databaseItem1.getExpectedWeight() * 4);
		
		// Simulates 4 items being placed
		stubScale.add(fourValidUnit1s);

		// Adds a second item
		checkoutController.addItem(databaseItem2);
		// Adds the cost of the second item to the total
		total = total.add(databaseItem2.getPrice());
		// Rounds the value to 2 decimal places
		total = total.setScale(2, RoundingMode.HALF_UP);
		
		// Checks that the item was added and the order was updated to 2
		assertEquals(2, checkoutController.getOrder().size());
		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());
		// Unblocks the station and lets a new item be scanned
		checkoutController.baggedItemsValid();

		//remove item
		checkoutController.removeItemFromOrder(databaseItem1,BigDecimal.valueOf(2));
		//subtract cost of 2 items from total even though we tried to remove three because there are only 2
		total = total.subtract(databaseItem1.getPrice());
		total = total.subtract(databaseItem1.getPrice());
		//rounds the value to 2 decimal places
		total = total.setScale(2, BigDecimal.ROUND_HALF_UP);
		// Checks that there are still 2 types of items (databaseItem1 and databaseItem2)
		assertEquals(2, checkoutController.getOrder().size());
		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());
		
	}

	/**
	 * Tests removeItemFromOrder by adding two items of one type
	 * Then tries to remove one item of another type
	 */
	@Test
	public void testRemoveNotInOrder() {

		// Adds item - 2 of type databaseItem1
		checkoutController.addItem(databaseItem1,BigDecimal.valueOf(2));
		// Adds the cost of the first item to the total
		BigDecimal total = databaseItem1.getPrice();
		total = total.add(databaseItem1.getPrice());
		// Checks that the item was added and the order was updated to 1
		assertEquals(1, checkoutController.getOrder().size());
		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());
		// Unblocks the station and lets a new item be scanned
		checkoutController.baggedItemsValid();
		// Checks that the item was added and the order was updated to 2
		assertEquals(1, checkoutController.getOrder().size());
		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());

		//remove item
		checkoutController.removeItemFromOrder(databaseItem2,BigDecimal.valueOf(1));
		//subtract 0 from our testing total since databaseItem2 actually isnt in the cart
		total = total.subtract(BigDecimal.valueOf(0));
		//rounds the value to 2 decimal places
		total = total.setScale(2, BigDecimal.ROUND_HALF_UP);
		// Checks that still only 1 item in cart
		assertEquals(1, checkoutController.getOrder().size());
		// Checks that the total cost stays the same
		assertEquals(total, checkoutController.getCost());
		
	}


}
