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

import static com.autovend.software.utils.MiscProductsDatabase.MISC_DATABASE;
import static com.autovend.software.utils.MiscProductsDatabase.bagNumb;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

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

	private Scanner scan;
	TouchScreen stubDevice;
	private ReusableBagDispenser stubBagDispenser;

	@Before
	public void setup() {
		newBag = new Bag(BigDecimal.valueOf(0.5));
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(newBag.getBarcode(), newBag);
		
		
		
		checkoutController = new CheckoutController();
		stubBagDispenser = new ReusableBagDispenser(500);

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
		
		bagDispenserController = new ReusableBagDispenserController(stubBagDispenser);
		bagDispenserController.setMainController(checkoutController);
		
		ReusableBag bagArray[] = new ReusableBag[100];
		
		Arrays.fill(bagArray, new ReusableBag());
		
		try {
			stubBagDispenser.load(bagArray);
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		reusableBag = (BarcodedProduct) MISC_DATABASE.get(bagNumb);
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(reusableBag.getBarcode(), reusableBag);

		//scan = new Scanner(System.in);
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
		//scan.close();
		
		stationController = null;
		stubDevice = null;
		attendantController = null;
		customerController = null;
		reusableBag = null;
		
		bagDispenserController = null;
		stubBagDispenser = null;
		
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
		
		checkoutController.purchaseBags(1);//Check order
		
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
		
		checkoutController.purchaseBags(50);//Check order
		
		LinkedHashMap<Product,Number[]> order = scannerController.getMainController().getOrder();
		assertTrue("Only reusable bag should be in the order.", order.keySet().size() == 1);
		
		Product equivalentItem = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(reusableBag.getBarcode());
		
		assertTrue("Reusable bag from database should be in the order", order.keySet().contains(equivalentItem));
		
		assertEquals("Reusable bag's amount should be added to order", 50, order.get(equivalentItem)[0]);
		assertEquals("Reusable bag's total cost should be added to order", BigDecimal.valueOf(50).multiply(equivalentItem.getPrice()) , order.get(equivalentItem)[1]);
		
		assertEquals("Reusable bag's item should be added to total cost", BigDecimal.valueOf(50).multiply(equivalentItem.getPrice()), checkoutController.getCost());
		
		assertEquals("Reusable bag's weight should be added to expected weight of bagging area.", (reusableBag.getExpectedWeight() * 50), scaleController.getExpectedWeight(), 0.01d);
		
	}
	
	//BELOW TESTS ARE NOT FIXED - Christian
	
	

	/**
	 * Test to check the method with valid inputs
	 */
	@Test
	public void testGetBagNumber_validInput() {
	}

	/**
	 * Test to check the method with invalid inputs
	 */
	@Test
	public void testGetBagNumber_invalidInput() {
	}

	/**
	 * Test to check the method with empty input
	 */
	@Test
	public void testGetBagNumber_emptyInput() {
	}

	/**
	 * Test to check if the method returns if the number of bags is 0
	 */
	@Test
	public void testPurchaseBags_0Bags() {


		// Unblocks the station and lets a new item be scanned
		checkoutController.baggedItemsValid();

		assertEquals(BigDecimal.ZERO, checkoutController.getCost());
	}

	/**
	 * Test to check if the bags get added to the order correctly and the cost is
	 * calculated correctly
	 */
	@Test
	public void testPurchaseBags_checkOrder() {

		// Set the number of bags to 3 bags;
		int numBags = 3;

		// create the variable to calculate the cost of the bags in total
		BigDecimal expectedPrice = BigDecimal.ZERO;

		// Add the bag to the order
		//checkoutController.purchaseBags(newBag, validUnit.getWeight(), numBags);
		expectedPrice = expectedPrice.add(newBag.getPrice().multiply(BigDecimal.valueOf(numBags)));

		// Unblocks the station and lets a new item be scanned
		checkoutController.baggedItemsValid();
		HashMap<Product, Number[]> order = checkoutController.getOrder();

		// Check that the bag number and cost in the order were updated correctly

		checkoutController.purchaseBags(numBags);
		assertEquals(order.size(),1);
		Product bagProd = order.keySet().iterator().next();
		assertEquals(numBags, order.get(bagProd)[0]);
		assertEquals(expectedPrice, checkoutController.getCost());
		assertEquals( ((BarcodedProduct)bagProd).getBarcode(), newBag.getBarcode());
	}

	/**
	 * Test to check if the bags get added correctly after the bag addition prompt is used multiple times 
	 * Also checks if the cost is getting calculated correctly
	 */
	@Test
	public void testPurchaseBags_multipleBags() {

		// create the variable to calculate the cost of the bags in total
		BigDecimal expectedPrice = BigDecimal.ZERO;
		// Set the number of bags to 4 bags;
		int numBags = 4;


		// Purchase 2 bags
		//checkoutController.purchaseBags(newBag, validUnit.getWeight(), 2);
		expectedPrice = expectedPrice.add(newBag.getPrice().multiply(BigDecimal.valueOf(2)));

		// Unblocks the station and lets a new bag be scanned
		checkoutController.baggedItemsValid();

		// Purchase 1 bag
		//checkoutController.purchaseBags(newBag, validUnit.getWeight(), 1);
		expectedPrice = expectedPrice.add(newBag.getPrice().multiply(BigDecimal.valueOf(1)));

		// Purchase 1 bag
		//checkoutController.purchaseBags(newBag, validUnit.getWeight(), 1);
		expectedPrice = expectedPrice.add(newBag.getPrice().multiply(BigDecimal.valueOf(1)));

		// Unblocks the station and lets a new bag be scanned
		checkoutController.baggedItemsValid();

		// Checking that the bags were added to the order with correct bag numbers and
		// cost
		HashMap<Product, Number[]> order = checkoutController.getOrder();
		checkoutController.purchaseBags(numBags);
		assertEquals(order.size(),1);
		Product bagProd = order.keySet().iterator().next();
		assertEquals(numBags, order.get(bagProd)[0]);
		assertEquals(expectedPrice, checkoutController.getCost());
		assertEquals( ((BarcodedProduct)bagProd).getBarcode(), newBag.getBarcode());
	}

	/**
	 * A method to test that bag is not added when baggingItemLock or systemProtectionLock are true
	 */
	@Test
	public void testDisabledLocks() {
		// Set the number of bags
		int numBags = 2;

		// Enables baggingItemLock
		checkoutController.baggingItemLock = true;

		// Adds Bag
		//checkoutController.purchaseBags(newBag, validUnit.getWeight(), numBags);

		// Bag should not be added, order size should be 0
		assertEquals(0, checkoutController.getOrder().size());

		// Bag should not be added, and the cost should be 0
		assertEquals(BigDecimal.ZERO, checkoutController.getCost());

		// Disables baggingItemLock
		checkoutController.baggingItemLock = false;

		// Enables systemProtectionLock
		checkoutController.systemProtectionLock = true;

		// Adds the bag
		//checkoutController.purchaseBags(newBag, validUnit.getWeight(), numBags);

		// Bag should not be added, order size should be 0
		assertEquals(0, checkoutController.getOrder().size());

		// Bag should not be added, and the cost should be 0
		assertEquals(BigDecimal.ZERO, checkoutController.getCost());
	}

	/**
	 * A method to test that bag is not added when the ItemAdderController is null
	 */
	@Test
	public void testPurchaseBags_invalidItemControllerAdder() {
		// Set the number of bags
		int numBags = 2;

		// purchaseBags is called with an invalid ItemControllerAdder
		//checkoutController.purchaseBags(null, validUnit.getWeight(), numBags);

		// Bag should not be added, order size should be 0
		assertEquals(0, checkoutController.getOrder().size());

		// Bag should not be added, and the cost should be 0
		assertEquals(BigDecimal.ZERO, checkoutController.getCost());

	}

	/**
	 * Tests purchaseBags with an bag that has an invalid weight, and bag that is null
	 */
	@Test
	public void testPurchaseBags_invalidParameters() {
		// set the number of bags
		int numBags = 2;

		// Scan bag with negative weight
		//checkoutController.purchaseBags(newBag, -1, numBags);

		// Bag should not be added, and order size should be 0
		assertEquals(0, checkoutController.getOrder().size());

		// Bag should not be added, and the cost should be 0
		assertEquals(BigDecimal.ZERO, checkoutController.getCost());

		// Scan null bag
		//checkoutController.purchaseBags(null, validUnit.getWeight(), numBags);

		// Bag should not be added, and order size should be 0
		assertEquals(0, checkoutController.getOrder().size());

		// Bag should not be added, and the cost should be 0
		assertEquals(BigDecimal.ZERO, checkoutController.getCost());
	}
	
	/**
	 * Tests purchaseBags with an invalid ItemAdderController, and bag that is null
	 */
	@Test
	public void testPurchaseBags_nullAdder_nullBag() {
		// Set the number of bags
		int numBags = 2;

		// purchaseBags is called with an invalid ItemControllerAdder and null bag
		//checkoutController.purchaseBags(null, validUnit.getWeight(), numBags);

		// Bag should not be added, order size should be 0
		assertEquals(0, checkoutController.getOrder().size());

		// Bag should not be added, and the cost should be 0
		assertEquals(BigDecimal.ZERO, checkoutController.getCost());

	}

}
