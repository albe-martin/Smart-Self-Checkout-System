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
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.BaggingScaleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.SellableUnit;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.DisabledException;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.SimulationException;
import com.autovend.devices.TouchScreen;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.controllers.DeviceController;
import com.autovend.software.controllers.ScanningScaleController;

@SuppressWarnings("deprecation")
/**
 * Test class for the add item and add item after partial payment use case
 */
public class AddItemTest {

	private CheckoutController checkoutController;
	private BarcodeScannerController scannerController;
	private BaggingScaleController scaleController;
	private ScanningScaleController scanningScaleController;
	private AttendantIOController attendantController;
	private AttendantStationController stationController;
	private CustomerIOController customerController;
	private BarcodedProduct databaseItem1;
	private BarcodedProduct databaseItem2;
	private PLUCodedProduct pluProduct1;
	private BarcodedUnit validUnit1;
	private BarcodedUnit validUnit2;
	private BarcodeScanner barcodeScanner;

	BarcodeScanner stubScanner;
	ElectronicScale stubScale;
	ElectronicScale stubScanningScale;
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
		barcodeScanner = new BarcodeScanner();
		scannerController = new BarcodeScannerController(barcodeScanner);
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
		stubScanningScale = new ElectronicScale(1000, 1); 

		scannerController = new BarcodeScannerController(stubScanner);
		scannerController.setMainController(checkoutController);
		scaleController = new BaggingScaleController(stubScale);
		scaleController.setMainController(checkoutController);
		
		scanningScaleController = new ScanningScaleController(stubScanningScale);
		scanningScaleController.setMainController(checkoutController);
		
		attendantController = new AttendantIOController(stubDevice);
		attendantController.setMainAttendantController(stationController);
		stationController.getAttendantIOControllers();

		customerController = new CustomerIOController(stubDevice);
		customerController.setMainController(checkoutController);
		
		stubScanner.register(scannerController);
		stubScale.register(scaleController);
		
		//Register the attendant to this customer checkout station
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
		scanningScaleController = null;
		attendantController = null;
		stationController = null;
		customerController = null;
		databaseItem1 = null;
		databaseItem2 = null;
		pluProduct1 = null;
		validUnit1 = null;
		validUnit2 = null;
		stubScanner = null;
		stubScale = null;
		stubScanningScale = null;
		stubDevice = null;
	}
	
	/**
	 * METHODS TO TEST REGISTRATION FOR ADD ITEM DEVICE CONTROLLERS
	 */

	/**
	 * Testing that the checkout station is configured correctly for this test classes.
	 * It should have a scanner controller, bagging scale controller, scanning scale controller, 
	 * customer controller and assigned an attendant controller.
	 */
	@Test
	public void testCorrectRegistrationControllersForAddItemTest() {
		Set<DeviceController> controllers = checkoutController.getAllDeviceControllers();
		assertTrue("BaggingScaleController should be registered.", controllers.contains(scaleController));
		assertTrue("BarcodeScannerController should be registered.", controllers.contains(scannerController));
		assertTrue("ScanningScaleController should be registered.", controllers.contains(scanningScaleController));
		assertTrue("CustomerIOController should be registered.", controllers.contains(customerController));
		assertTrue("AttendantIOController should be registered.", controllers.contains(attendantController));
		assertEquals("Only scaleController, scannerController and customerController should be registered", controllers.size(), 5);

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
		scannerController.setMainController(newMainController);

		assertNotSame("New checkout controller should be set in BarcodeScannerController field", checkoutController,
				scannerController.getMainController());
		assertTrue("BarcodeScannerController should be in the new checkout controller's item adder list",
				newMainController.getAllDeviceControllers().contains(scannerController));
		assertFalse("BarcodeScannerController should not be in the old checkout controller's item adder list",
				checkoutController.getAllDeviceControllers().contains(scannerController));
		
		scaleController.setMainController(newMainController);

		assertNotSame("New checkout controller should be set in BaggingScaleController field", checkoutController,
				scaleController.getMainController());
		assertTrue("BaggingScaleController should be in the new checkout controller's item adder list",
				newMainController.getAllDeviceControllers().contains(scaleController));
		assertFalse("BaggingScaleController should not be in the old checkout controller's item adder list",
				checkoutController.getAllDeviceControllers().contains(scaleController));
		
		scanningScaleController.setMainController(newMainController);

		assertNotSame("New checkout controller should be set in ScanningScaleController field", checkoutController,
				scanningScaleController.getMainController());
		assertTrue("ScanningScaleController should be in the new checkout controller's item adder list",
				newMainController.getAllDeviceControllers().contains(scanningScaleController));
		assertFalse("ScanningScaleController should not be in the old checkout controller's item adder list",
				checkoutController.getAllDeviceControllers().contains(scanningScaleController));
	}

	/**
	 * METHODS TO TEST ADD ITEMS BY SCANNING
	 */

	/**
	 * Tests that the barcode scanner was able to scan an item.
	 * Expected Results:
	 * 		- Item is in order, and the only one in the order.
	 * 		- Item's cost was added
	 * 		- Item's weight was added
	 */
	@Test
	public void testValidScan() {
		while (!stubScanner.scan(validUnit1)) {
		} // loop until successful scan
		
		//Check order
		LinkedHashMap<Product,Number[]> order = scannerController.getMainController().getOrder();
		assertTrue("Only one item should be in the order.", order.keySet().size() == 1);
		
		Product equivalentItem = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(validUnit1.getBarcode());
		
		assertTrue("Correct equivalent database item should be in the order", order.keySet().contains(equivalentItem));
		
		assertEquals("Scanned product's amount should be added to order", 1, order.get(equivalentItem)[0]);
		assertEquals("Scanned product's total cost should be added to order", equivalentItem.getPrice(), order.get(equivalentItem)[1]);
		
		assertEquals("Scanned product's item should be added to total cost", equivalentItem.getPrice(), checkoutController.getCost());
		
		assertEquals("Scanned product's weight should be added to expected weight of bagging area.", validUnit1.getWeight(), scaleController.getExpectedWeight(), 0.01d);
		
	}

	/**
	 * Tests that the BarcodeScannerController reacts correctly to the scan of an
	 * item not in the database
	 * Expected Results:
	 * 		- Item is not in the order.
	 * 		- Expected Weight is still 0
	 * 		- Cost is still 0
	 */
	@Test
	public void testNotFoundScan() {
		BarcodedUnit notUnit = new BarcodedUnit(new Barcode(Numeral.three, Numeral.four), 359.0);
		while (!stubScanner.scan(notUnit)) {
		} // loop until successful scan
		assertTrue("Scanned product is not in database so should not be in order list",
				scannerController.getMainController().getOrder().isEmpty());
		
		assertEquals("Scanned product's weight should not be added.", BigDecimal.ZERO, checkoutController.getCost());
		assertEquals("Scanned product's cost should not be added.", 0.0, scaleController.getExpectedWeight(), 0.01d);
	}
	


	/**
	 * Tests that the BarcodeScanner still throws a SimulationException if the barcode is null
	 * Expected Results:
	 * 		- SimulationException is thrown.
	 */
	@Test(expected = SimulationException.class)
	public void testNullScan() {
		BarcodedUnit notUnit = null;
		stubScanner.scan(notUnit);
		
		fail("SimulationException was expected to be thrown.");
	}

	/**
	 * Tests that the disableDevice method of DeviceController causes a
	 * DisabledException to be thrown when a scan is attempted.
	 * Expected Results:
	 * 		- DisabledException is thrown.
	 */
	@Test(expected = DisabledException.class)
	public void testDisabledScanController() {
		scannerController.disableDevice();
		stubScanner.scan(validUnit1);
		
		fail("DisabledException was expected to be thrown.");
	}

	/**
	 * Tests that the enableDevice method of DeviceController works correctly,
	 * allowing scans to take place again
	 * 
	 * Expected Results:
	 * 		- Item is in order, and the only one in the order.
	 * 		- Item's cost was added
	 * 		- Item's weight was added
	 */
	@Test
	public void testReenabledScanController() {
		scannerController.disableDevice();
		scannerController.enableDevice();
		while (!stubScanner.scan(validUnit1)) {
		} // loop until successful scan
		
		//Check order
		LinkedHashMap<Product,Number[]> order = scannerController.getMainController().getOrder();
		assertTrue("Only one item should be in the order.", order.keySet().size() == 1);
		
		Product equivalentItem = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(validUnit1.getBarcode());
		
		assertTrue("Correct equivalent database item should be in the order", order.keySet().contains(equivalentItem));
		
		assertEquals("Scanned product's amount should be added to order", 1, order.get(equivalentItem)[0]);
		assertEquals("Scanned product's total cost should be added to order", equivalentItem.getPrice(), order.get(equivalentItem)[1]);
		
		assertEquals("Scanned product's item should be added to total cost", equivalentItem.getPrice(), checkoutController.getCost());
		
		assertEquals("Scanned product's weight should be added to expected weight of bagging area.", validUnit1.getWeight(), scaleController.getExpectedWeight(), 0.01d);
	}

	/**
	 * Tests that the setDevice method of DeviceController correctly replaces the
	 * old BarcodeScanner with the new one
	 */
	@Test
	public void testNewScanner() {
		BarcodeScanner newScanner = new BarcodeScanner();
		scannerController.setDevice(newScanner);
		assertNotSame("New barcode scanner should be ..", stubScanner, scannerController.getDevice());
	}

// Testing BaggingScaleController

	/**
	 * Tests that an item is not added again before the previous scanned item has not been added to the bagging area.
	 * Expected Results:
	 * 		- 1 copy of an item is added from an initial scan.
	 * 		- Only 1 copy of an item still remains in the order after scanning item twice, but before putting item in bagging area.
	 * 		- 2 copies of an item is added from scan then on.
	 */
	@Test
	public void testScaleScanLock() {
		while (!stubScanner.scan(validUnit1)) {
		} // loop until successful scan
		HashMap<Product, Number[]> order = scannerController.getMainController().getOrder();
		// getting amount of first item in order
		int count = order.get(databaseItem1)[0].intValue();
		assertEquals("Only 1 copy of the item should be added to the order", 1, count);

		// scan an item again and verify that the order wasn't updated since it hasn't
		// been added to
		// the bagging area.
		while (!stubScanner.scan(validUnit1)) {
		}
		count = order.get(databaseItem1)[0].intValue();
		assertEquals("Item wasn't added to scale yet so should be still 1", 1, count);
		stubScale.add(validUnit1);
		while (!stubScanner.scan(validUnit1)) {
		}
		count = order.get(databaseItem1)[0].intValue();
		assertEquals("Since item was put on scale, it should count 2 copies of product", 2, count);
	}

	/**
	 * Tests that an item is not added again before the previous scanned item has not been added to the bagging area.
	 * Expected Results:
	 * 		- 1 copy of an item is added from an initial scan.
	 * 		- 1 copy still remains from a second scan after putting item with wrong expected weight in bagging area.
	 */
	@Test
	public void testScaleIncorrectWeightScanLock() {
		BarcodedUnit validUnit2 = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 500.0);

		while (!stubScanner.scan(validUnit2)) {
		} // loop until successful scan
		HashMap<Product, Number[]> order = scannerController.getMainController().getOrder();
		// getting amount of first item in order
		int count = order.get(databaseItem1)[0].intValue();
		assertEquals("Only 1 copy of the item should be added to the order", 1, count);
		// add item to bagging area then verify that since the weight is off by so much,
		// it shouldn't add another
		// to the count.
		// Scale is expecting 359 but weight of 500 was added instead.
		stubScale.add(validUnit2);
		while (!stubScanner.scan(validUnit2)) {
		}
		count = order.get(databaseItem1)[0].intValue();
		assertEquals("Expected and current weight should be very different, so another validUnit2 should not be added", 1, count);
		validUnit2 = null;
	}

	/**
	 * Tests to see if attendant can approve a weight discrepancy.
	 * Expected Results:
	 * 		- Discrepancy approve, baggingItemLock should be lifted.
	 */
	@Test
	public void testDiscrepancyResolved() {
		scaleController.resetOrder();
		scaleController.reactToWeightChangedEvent(stubScale, 10.0);
		
		attendantController.approveWeightDiscrepancy(checkoutController);
		
		assertFalse(scaleController.getMainController().baggingItemLock);
	}


	/**
	 * Tests to see if station's bagging item lock is flipped on when a discrepancy occurs.
	 * Expected Results:
	 * 		- baggingItemLock should be true
	 */
	@Test
	public void testDiscrepancUnresolved() {
		scaleController.resetOrder();
		scaleController.reactToWeightChangedEvent(stubScale, 10.0);
		
		assertTrue(scaleController.getMainController().baggingItemLock);
	}

	/**
	 * Very heavy object outside the normal operation limit for the scale is added.
	 * Expected Results:
	 * 		System protection lock is turned on when added to bagging area.
	 * 		System protection lock is turned off when added to bagging area.
	 */
	@Test
	public void testScaleErrorLock() {
		BarcodedUnit validUnit2 = new BarcodedUnit(new Barcode(Numeral.three, Numeral.three), 100000.0);

		while (!stubScanner.scan(validUnit2)) {
		} // loop until successful scan
			// add item to bagging area then verify that the error lock to avoid damage to
			// the scale
			// is true, and that taking it off would end that
		stubScale.add(validUnit2);
		assertTrue(checkoutController.systemProtectionLock);
		stubScale.remove(validUnit2);
		assertFalse(checkoutController.systemProtectionLock);
		validUnit2 = null;
	}

//	Testing BaggingScaleController methods

	/**
	 * Tests addItem by adding two items
	 */
	@Test
	public void testAddItem() {

		// Adds item
		checkoutController.addItem(databaseItem1);

		// Adds the cost of the first item to the total
		BigDecimal total = databaseItem1.getPrice();

		// Checks that the item was added and the order was updated to 1
		assertEquals(1, checkoutController.getOrder().size());

		// Checks that the total cost was updated
		assertEquals(total, checkoutController.getCost());

		
		// Unblocks the station and lets a new item be scanned
		stubScale.add(validUnit1);

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
	}

	/**
	 * Tests addItem with an item that has an invalid weight, and an item that is
	 * null
	 */
	@Test
	public void testAddItemWithInvalidParameters() {
		BarcodedProduct databaseItem3 = new BarcodedProduct(new Barcode(Numeral.four, Numeral.five, Numeral.six), "test item 2",
				BigDecimal.valueOf(42), -1.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(databaseItem3.getBarcode(), databaseItem3);



		// Scan item with negative weight
		checkoutController.addItem(databaseItem3);

		// Item should not be added, and order size should be 0
		assertEquals(0, checkoutController.getOrder().size());

		// Item should not be added, and the cost should be 0
		assertEquals(BigDecimal.ZERO, checkoutController.getCost());

		// Scan null item
		checkoutController.addItem(null);

		// Item should not be added, and order size should be 0
		assertEquals(0, checkoutController.getOrder().size());

		// Item should not be added, and the cost should be 0
		assertEquals(BigDecimal.ZERO, checkoutController.getCost());

		ProductDatabases.BARCODED_PRODUCT_DATABASE.remove(databaseItem3.getBarcode());
		databaseItem3 = null;
	}

	/**
	 * Test the remaining amount after two partial payments
	 */
	@Test
	public void testGetRemainingAmount() {

		// First Item is scanned
		checkoutController.addItem(databaseItem1);

		// Adds the cost of the first item to the total
		BigDecimal total = databaseItem1.getPrice();

		// Simulates the item being put on the bagging area and lets us scan another
		// item.
		stubScale.add(validUnit1);

		// First item is added
		checkoutController.addItem(databaseItem2);

		// Adds the cost of the second item to the total
		total = total.add(databaseItem2.getPrice());

		// Rounds the value to 2 decimal places
		total = total.setScale(2, RoundingMode.HALF_UP);
		
		customerController.startPressed();
		// Amount paid is updated
		checkoutController.addToAmountPaid(BigDecimal.valueOf(50));

		// Subtracts the amount paid from the total
		total = total.subtract(BigDecimal.valueOf(50));

		// Rounds the value to 2 decimal places
		total = total.setScale(2, RoundingMode.HALF_UP);

		// Checks that amount to be paid is the total unpaid amount
		assertEquals(total, checkoutController.getRemainingAmount());

		// Amount paid is updated
		checkoutController.addToAmountPaid(BigDecimal.valueOf(75.29));

		// Subtracts the amount paid from the total
		total = total.subtract(BigDecimal.valueOf(75.29));

		// Rounds the value to 2 decimal places
		total = total.setScale(2, BigDecimal.ROUND_HALF_UP);

		// Checks that amount to be paid is the total unpaid amount
		assertEquals(total, checkoutController.getRemainingAmount());
	}

	/**
	 * A method to test if getRemaining amount is zero without any items
	 */
	@Test
	public void testGetRemainingAmountWithNoItems() {
		assertEquals(BigDecimal.ZERO, checkoutController.getRemainingAmount());
	}

	/**
	 * A method to test that item is not added when baggingItemLock or
	 * systemProtectionLock are true
	 */
	@Test
	public void testDisabledLocks() {

		// Enables baggingItemLock
		checkoutController.baggingItemLock = true;

		// Adds item
		checkoutController.addItem(databaseItem1);

		// Item should not be added, order size should be 0
		assertEquals(0, checkoutController.getOrder().size());

		// Item should not be added, and the cost should be 0
		assertEquals(BigDecimal.ZERO, checkoutController.getCost());

		// Disables baggingItemLock
		checkoutController.baggingItemLock = false;

		// Enables systemProtectionLock
		checkoutController.systemProtectionLock = true;

		// Adds item
		checkoutController.addItem(databaseItem1);

		// Item should not be added, order size should be 0
		assertEquals(0, checkoutController.getOrder().size());

		// Item should not be added, and the cost should be 0
		assertEquals(BigDecimal.ZERO, checkoutController.getCost());
	}

	/**
	 * A method to test that item is not added when the ItemAdderController is not
	 * valid
	 */

	@Test
	public void testInvalidItemControllerAdder() {

		// addItem is called with an invalid ItemControllerAdder
		checkoutController.addItem(null);

		// Item should not be added, order size should be 0
		assertEquals(0, checkoutController.getOrder().size());

		// Item should not be added, and the cost should be 0
		assertEquals(BigDecimal.ZERO, checkoutController.getCost());

	}

	/**
	 * A method to test that more than one of the same item is added correctly
	 */
	@Test
	public void testAddingDuplicateItems() {

		// Stores the item information
		HashMap<Product, Number[]> order = checkoutController.getOrder();

		// Add the same bag to the order
		checkoutController.addItem(databaseItem1);

		// Adds the cost of the first item to the total
		BigDecimal total = databaseItem1.getPrice();

		// Check that the item number and cost in the order were updated correctly
		assertEquals(1, order.get(databaseItem1)[0]);
		assertEquals(total, checkoutController.getCost());

		// Unblocks the station and lets a new item be scanned by simulating putting an item on scale
		stubScale.add(validUnit1);

		// Add another of the same item to the order
		checkoutController.addItem(databaseItem1);

		// Adds the cost of the second item to the total
		total = total.add(databaseItem1.getPrice());

		// Rounds the value to 2 decimal places
		total = total.setScale(2, RoundingMode.HALF_UP);

		// Check that the item number and cost in the order were updated correctly
		assertEquals(2, order.get(databaseItem1)[0]);
		assertEquals(total, checkoutController.getCost());

	}
	
	
	/*
	 * A method to test if addingItemByTextSearch works properly and returns the proper HashSet
	 */
	@Test
	public void testAddingItemByTextSearch() {
		Set<Product> expected = new HashSet<Product>();
		PLUCodedProduct pluProd = pluProduct1; 
		BarcodedProduct exproduct = databaseItem1;
		BarcodedProduct exproduct2 = databaseItem2;
		expected.add((Product) pluProd);
		expected.add((Product) exproduct);
		expected.add((Product) exproduct2);
		
		
		
		Set<Product> actual = new HashSet<Product>();
		String inputString = "test item 1";
		actual = attendantController.searchProductsByText(inputString);
		assertEquals(expected,actual);
		
	}
	
	/*
	 * A method to test if addItemByBrowsing successfully adds an item to the order
	 */
	@Test
	public void testAddItemByBrowsing() {	
		BigDecimal expectedTotal = databaseItem1.getPrice();
		int expectedCount = 1;
		
		// adding the item
		customerController.addProduct(databaseItem1);
		
		// checking if the cost is correctly updated when an item is added by browsing
		assertEquals(expectedTotal, checkoutController.getCost());
		
		// checking if the size of the order is correctly updated when an item is added by browsing
		assertEquals(expectedCount, checkoutController.getOrder().size());
		
	}
	
	/*
	 * A method to test if addItemByBrowsing successfully recognizes a null product and does not add it to the order
	 */
	@Test
	public void testAddNullItemByBrowsing() {
		BigDecimal expectedTotal = BigDecimal.ZERO;
		int expectedCount = 0;
		
		// adding null item
		customerController.addProduct(null);
		
		assertEquals(expectedTotal, checkoutController.getCost());
		assertEquals(expectedCount, checkoutController.getOrder().size());
	}
	
	/**
	 * Expected for hardware sim to catch this. 
	 */
	@Test(expected = SimulationException.class)
	public void testInvalidPLUItemAdd() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
		customerController.addItemByPLU("0");
		fail("Expected hardware simulation to throw a SimulationException for an invalid PLU code.");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testNonNumericalPLUAdd() {
		customerController.addItemByPLU("A");
		fail("Expected NumberFormatException for this invalid PLU code.");
	}
	@Test(expected = NullPointerException.class)
	public void testNullPLUCodeADD() {
		customerController.addItemByPLU(null);
		fail("Expected NullPointerException for this invalid PLU code.");
	}
	
	/**
	 * Test that adding a valid item by PLU behaves correctly.
	 * 		- Item is in order, and the only one in the order with correct amount by weight.
	 * 		- Item's cost was added
	 * 		- Item's weight was added
	 */
	@Test
	public void testPLUAddItem() {
		
		System.out.println("Code in string: "+ pluProduct1.getPLUCode().toString());
		
		PriceLookUpCodedUnit placedPLUItem = new PriceLookUpCodedUnit(pluProduct1.getPLUCode(), 10);
		
		//Simulates placing the item on the 
		scanningScaleController.getDevice().add(placedPLUItem);
		
		customerController.addItemByPLU(pluProduct1.getPLUCode().toString());
		
		//Check order
		LinkedHashMap<Product,Number[]> order = scannerController.getMainController().getOrder();
		assertTrue("Only one item should be in the order.", order.keySet().size() == 1);
		
		Product equivalentItem = ProductDatabases.PLU_PRODUCT_DATABASE.get(pluProduct1.getPLUCode());
		
		BigDecimal expectedAmount = pluProduct1.getPrice().multiply(BigDecimal.valueOf(placedPLUItem.getWeight()));
		double expectedWeight = placedPLUItem.getWeight();
		
		assertTrue("Correct equivalent database item should be in the order", order.keySet().contains(equivalentItem));
		
		assertEquals("PLU product's amount equal to weight should be added to order", expectedWeight, order.get(equivalentItem)[0].doubleValue(), 0.01d);
		assertEquals("PLU product's total cost should be added to order", expectedAmount, order.get(equivalentItem)[1]);
		
		assertEquals("PLU product's item should be added to total cost", expectedAmount, checkoutController.getCost());
		
		assertEquals("PLU product's weight should be added to expected weight of bagging area.", expectedWeight, scaleController.getExpectedWeight(), 0.01d);
		
		
	}
	
	@Test
	public void testReactOverload() {
		scanningScaleController.reactToOverloadEvent(null);
	}
	
	@Test
	public void reactToOutOfOverloadEvent() {
		scanningScaleController.reactToOutOfOverloadEvent(null);
	}
	
	@Test
	public void reactWeightChangeWrongScale() {
		scanningScaleController.reactToWeightChangedEvent(new ElectronicScale(5, 1), 0);
	}
	
	@Test
	public void testReactWrongDevice() {
		scannerController.reactToBarcodeScannedEvent(new BarcodeScanner(), null);
	}
	
	@Test
	public void testSetGetScanning() {
		scannerController.setScanningItems(false);
		assertFalse(scannerController.getScanningItems());
	}
	
	@Test
	public void testReactNotScanning() {
		scannerController.setScanningItems(false);
		scannerController.reactToBarcodeScannedEvent(scannerController.getDevice(), new Barcode(Numeral.one));
	}
	
	/**
	 * A method to test that more than one of the same item is added correctly
	 */
	@Test
	public void testDoNotBagLatest() {

		// Stores the item information
		HashMap<Product, Number[]> order = checkoutController.getOrder();

		// Add the same bag to the order
		checkoutController.addItem(databaseItem1);

		// Adds the cost of the first item to the total
		BigDecimal total = databaseItem1.getPrice();

		// Check that the item number and cost in the order were updated correctly
		assertEquals(1, order.get(databaseItem1)[0]);
		assertEquals(total, checkoutController.getCost());

		checkoutController.doNotBagLatest();
	}
	
	/**
	 * A method to test that more than one of the same item is added correctly
	 */
	@Test
	public void testDoNotBagLatestNotBlocked() {

		// Stores the item information
		HashMap<Product, Number[]> order = checkoutController.getOrder();

		// Add the same bag to the order
		checkoutController.addItem(databaseItem1);

		// Adds the cost of the first item to the total
		BigDecimal total = databaseItem1.getPrice();

		// Check that the item number and cost in the order were updated correctly
		assertEquals(1, order.get(databaseItem1)[0]);
		assertEquals(total, checkoutController.getCost());

		checkoutController.baggingItemLock = false;
		checkoutController.doNotBagLatest();
	}
}
