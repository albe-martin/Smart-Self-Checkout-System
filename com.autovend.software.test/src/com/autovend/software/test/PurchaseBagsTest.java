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

import static com.autovend.software.utils.MiscProductsDatabase.MISC_DATABASE;
import static com.autovend.software.utils.MiscProductsDatabase.bagNumb;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import com.autovend.*;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.BaggingScaleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.TouchScreen;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.controllers.DeviceController;
import com.autovend.software.controllers.ReusableBagDispenserController;
import com.autovend.software.utils.MiscProductsDatabase.Bag;

public class PurchaseBagsTest {

	BarcodeScannerController scannerController;
	BarcodeScanner stubScanner;
	BaggingScaleController scaleController;
	ElectronicScale stubScale;
	Bag newBag;

	BarcodedUnit validUnit;
	CheckoutController checkoutController;
	
	BarcodedProduct reusableBag;
	
	private AttendantIOController attendantController;
	private AttendantStationController stationController;
	private CustomerIOController customerController;
	
	private ReusableBagDispenserController bagDispenserController;

	TouchScreen stubDevice;
	private ReusableBagDispenser stubBagDispenser;

	@Before
	public void setup() {
		
		//Bag Dispenser set up and fill
		ReusableBag bagArray[] = new ReusableBag[100];
		newBag = new Bag(BigDecimal.valueOf(0.5));
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(newBag.getBarcode(), newBag);
		stubBagDispenser = new ReusableBagDispenser(500);
		
		//Create controllers and devices
		
		checkoutController = new CheckoutController();

		validUnit = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 500.0);

		stubScanner = new BarcodeScanner();
		stubScale = new ElectronicScale(1000, 1);

		scannerController = new BarcodeScannerController(stubScanner);
		scannerController.setMainController(checkoutController);
		
		stationController = new AttendantStationController();
		stubDevice = new TouchScreen();

		scaleController = new BaggingScaleController(stubScale);
		scaleController.setMainController(checkoutController);
		
		attendantController = new AttendantIOController(stubDevice);
		attendantController.setMainAttendantController(stationController);
		
		
		customerController = new CustomerIOController(stubDevice);
		customerController.setMainController(checkoutController);
		customerController.registerAttendant(attendantController);
		
		bagDispenserController = new ReusableBagDispenserController(stubBagDispenser);
		bagDispenserController.setMainController(checkoutController);
		
		
		//Fill bags
		Arrays.fill(bagArray, new ReusableBag());
		
		try {
			stubBagDispenser.load(bagArray);
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//create bag in database
		
		reusableBag = (BarcodedProduct) MISC_DATABASE.get(bagNumb);
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(reusableBag.getBarcode(), reusableBag);
		
		//Force login the attendant for test
		stationController.registerUser("abc", "123");
		stationController.login("abc", "123");
	}

	/**
	 * Tears down objects so they can be initialized again with setup
	 */
	@After
	public void teardown() {

		checkoutController = null;
		stubScanner = null;
		scannerController = null;
		scaleController = null;
		stubScale = null;
		
		stationController = null;
		stubDevice = null;
		attendantController = null;
		customerController = null;
		reusableBag = null;
		
		bagDispenserController = null;
		stubBagDispenser = null;
		
	}/**
	 * METHODS TO TEST REGISTRATION FOR ADD ITEM DEVICE CONTROLLERS
	 */

	/**
	 * Testing that the checkout station is configured correctly for this test classes.
	 * It should have a scanner controller, bagging scale controller, bag Dispenser Controller, 
	 * customer controller and assigned an attendant controller.
	 */
	@Test
	public void testCorrectRegistrationControllersForAddItemTest() {
		Set<DeviceController> controllers = checkoutController.getAllDeviceControllers();
		assertTrue("BaggingScaleController should be registered.", controllers.contains(scaleController));
		assertTrue("BarcodeScannerController should be registered.", controllers.contains(scannerController));
		assertTrue("ReusableBagController should be registered.", controllers.contains(bagDispenserController));
		assertTrue("CustomerIOController should be registered.", controllers.contains(customerController));
		assertTrue("AttendantIOController should be registered.", controllers.contains(attendantController));
		assertEquals("The above 5 controllers should be registered", controllers.size(), 5);

	}
	
	/**
	 * Testing appropriate behaviour of assigning a new main controller to the hardware device controllers used by addItem such as:
	 * 	BaggingScaleController, ScanningScaleController and BarcodeScannerController, 
	 *  Expected Results:
	 *  	- All three controllers should not still be in the old checkout controller's list of device controllers.
	 *  	- All three controllers should point to the new checkoutController as their main controller.
	 *  	- All three controllers should be in the new checkout controller's list of devices
	 */
	@Test
	public void testNewMainControllers() {
		CheckoutController newMainController = new CheckoutController();
		
		scaleController.setMainController(newMainController);

		assertNotSame("New checkout controller should be set in BaggingScaleController field", checkoutController,
				scaleController.getMainController());
		assertTrue("BaggingScaleController should be in the new checkout controller's item adder list",
				newMainController.getAllDeviceControllers().contains(scaleController));
		assertFalse("BaggingScaleController should not be in the old checkout controller's item adder list",
				checkoutController.getAllDeviceControllers().contains(scaleController));
		
		bagDispenserController.setMainController(newMainController);

		assertNotSame("New checkout controller should be set in ScanningScaleController field", checkoutController,
				bagDispenserController.getMainController());
		assertTrue("ScanningScaleController should be in the new checkout controller's item adder list",
				newMainController.getAllDeviceControllers().contains(bagDispenserController));
		assertFalse("ScanningScaleController should not be in the old checkout controller's item adder list",
				checkoutController.getAllDeviceControllers().contains(bagDispenserController));
	}

	/**
	 * Simple test to check if purchasing one Reusable Bag adds it to the order
	 * Expected Result:
	 * 		- Reusable Bag is in order, and the only one in the order.
	 * 		- Reusable Bag's cost was added
	 * 		- Reusable Bag's weight was added
	 */
	@Test
	public void testPurchaseBags_oneBag_inOrder() {
		
		customerController.purchaseBags(1);//Check order
		
		LinkedHashMap<Product,Number[]> order = scannerController.getMainController().getOrder();
		assertTrue("Only one reusable bag should be in the order.", order.keySet().size() == 1);
		
		Product equivalentItem = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(reusableBag.getBarcode());
		
		assertTrue("Reusable bag from database should be in the order", order.keySet().contains(equivalentItem));
		
		assertEquals("Reusable bag's amount should be added to order", 1, order.get(equivalentItem)[0]);
		assertEquals("Reusable bag's total cost should be added to order", equivalentItem.getPrice(), order.get(equivalentItem)[1]);
		
		assertEquals("Reusable bag's item should be added to total cost", equivalentItem.getPrice(), checkoutController.getCost());
		
		assertEquals("Reusable bag's weight should be added to expected weight of bagging area.", reusableBag.getExpectedWeight(), scaleController.getExpectedWeight(), 0.01d);
		
	}
	
	/**
	 * Simple test to check if purchasing multiple Reusable Bag adds them to the order without locking and waiting for them one by one
	 * Expected Result:
	 * 		- Reusable Bags are in order, and the only one in the order.
	 * 		- Reusable Bags total cost was added
	 * 		- Reusable Bags total weight was added
	 */
	@Test
	public void testPurchaseBags_multipleBags_inOrder() {
		
		customerController.purchaseBags(50);//Check order
		
		LinkedHashMap<Product,Number[]> order = scannerController.getMainController().getOrder();
		assertTrue("Only reusable bag should be in the order.", order.keySet().size() == 1);
		
		Product equivalentItem = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(reusableBag.getBarcode());
		
		assertTrue("Reusable bag from database should be in the order", order.keySet().contains(equivalentItem));
		
		assertEquals("Reusable bag's amount should be added to order", 50, order.get(equivalentItem)[0]);
		assertEquals("Reusable bag's total cost should be added to order", BigDecimal.valueOf(50).multiply(equivalentItem.getPrice()) , order.get(equivalentItem)[1]);
		
		assertEquals("Reusable bag's item should be added to total cost", BigDecimal.valueOf(50).multiply(equivalentItem.getPrice()), checkoutController.getCost());
		
		assertEquals("Reusable bag's weight should be added to expected weight of bagging area.", (reusableBag.getExpectedWeight() * 50), scaleController.getExpectedWeight(), 0.01d);
		
	}

	/**
	 * Simple test to see that purchasing 0 bags will not add anything to the order.
	 * Expected Results:
	 * 		- Nothing is in order
	 * 		- Cost is 0
	 * 		- Expected Weight is 0
	 */
	@Test
	public void testPurchaseBags_0Bags() {
		
		customerController.purchaseBags(0);
		
		LinkedHashMap<Product,Number[]> order = scannerController.getMainController().getOrder();
		
		assertTrue("Order should be empty", order.keySet().isEmpty());
		
		assertEquals("Total cost should be 0", BigDecimal.ZERO, checkoutController.getCost());
		
		assertEquals("Expected Weight should be 0", 0, scaleController.getExpectedWeight(), 0.01d);
	}

	/**
	 * Simple test to see that purchasing 2 bags will not add anything to the order when the station is disabled.
	 * Expected Results:
	 * 		- Nothing is in order
	 * 		- Cost is 0
	 * 		- Expected Weight is 0
	 */
	@Test
	public void testPurchaseBags_disabled() {
		
		attendantController.disableStation(checkoutController);
		
		customerController.purchaseBags(2);
		
		LinkedHashMap<Product,Number[]> order = scannerController.getMainController().getOrder();
		
		assertTrue("Order should be empty", order.keySet().isEmpty());
		
		assertEquals("Total cost should be 0", BigDecimal.ZERO, checkoutController.getCost());
		
		assertEquals("Expected Weight should be 0", 0, scaleController.getExpectedWeight(), 0.01d);
	}

	/**
	 * Simple test to see that purchasing 2 bags will not add anything to the order when the station is under a protection lock.
	 * Expected Results:
	 * 		- Nothing is in order
	 * 		- Cost is 0
	 * 		- Expected Weight is 0
	 */
	@Test
	public void testPurchaseBags_systemProtectionLocked() {
		
		checkoutController.systemProtectionLock = true;
		
		customerController.purchaseBags(2);
		
		LinkedHashMap<Product,Number[]> order = scannerController.getMainController().getOrder();
		
		assertTrue("Order should be empty", order.keySet().isEmpty());
		
		assertEquals("Total cost should be 0", BigDecimal.ZERO, checkoutController.getCost());
		
		assertEquals("Expected Weight should be 0", 0, scaleController.getExpectedWeight(), 0.01d);
	}
	
	/**
	 * Simple test to see that purchasing 1 bag, then purchasing another bag despite the hardware not simulating
	 * the first bag going onto the bagging area, a second purchased bag will not be added.
	 * Expected Results:
	 * 		- One bag is in order
	 * 		- Cost is of a single bag
	 * 		- Expected Weight is is of a single bag
	 */
	@Test
	public void testPurchaseBags_baggingAreaLocked() {
		
		//Purchasing 1 bag
		customerController.purchaseBags(1);
		
		//Purchasing a bag without resolving discrepancy.
		customerController.purchaseBags(1);
		
		LinkedHashMap<Product,Number[]> order = scannerController.getMainController().getOrder();
		
		Product equivalentItem = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(reusableBag.getBarcode());
		
		assertTrue("Order should have one item and it should be a bag", order.keySet().size() == 1 && order.containsKey(equivalentItem));
		
		assertEquals("Total cost should be a single bag", reusableBag.getPrice(), checkoutController.getCost());
		
		assertEquals("Expected Weight should be a single bag", reusableBag.getExpectedWeight(), scaleController.getExpectedWeight(), 0.01d);
	}
	
	/**
	 * Simple test to see that purchasing 1 bag, simulating placing a bag onto the bagging area after,
	 * then purchasing another bag, that second bag is added to the order
	 * Expected Results:
	 * 		- Bag is the only type in order
	 * 		- Cost is of two bags
	 * 		- Expected Weight is of two bags
	 */
	@Test
	public void testPurchaseBags_purchaseBagsAfterValidPurchasBag() {
		
		Product equivalentItem = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(reusableBag.getBarcode());
		
		//Purchasing 1 bag
		customerController.purchaseBags(10);
		
		//Simulate placing that product in the 
		stubScale.add(new BarcodedUnit(reusableBag.getBarcode(), reusableBag.getExpectedWeight() * 10));
		
		//Purchasing a bag without resolving discrepancy.
		customerController.purchaseBags(10);
		
		LinkedHashMap<Product,Number[]> order = scannerController.getMainController().getOrder();
		
		
		assertTrue("Order should have one type of item and it should be a bag", order.keySet().size() == 1 && order.containsKey(equivalentItem));
		
		assertEquals("Total cost should be of two bags", reusableBag.getPrice().multiply(BigDecimal.valueOf(20)), checkoutController.getCost());
		
		assertEquals("Expected Weight should be of two bags", reusableBag.getExpectedWeight() * 20, scaleController.getExpectedWeight(), 0.01d);
	}
	
	@Test
	public void testOutOfBags() {
		bagDispenserController.outOfBags(stubBagDispenser);
	}
	
	@Test
	public void testDispenseEmpty() {
		bagDispenserController = new ReusableBagDispenserController(new ReusableBagDispenser(1));
		bagDispenserController.setMainController(checkoutController);
		bagDispenserController.dispenseBags(10);
	}
}
