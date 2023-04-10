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
import com.autovend.software.controllers.BaggingScaleController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
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
public class AddItemTest {

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

//	Testing BarcodeScannerController methods

	/**
	 * Tests that the BarcodeScannerController reacts correctly to the scan of an
	 * item in the database
	 */
	@Test
	public void testValidScan() {
		while (!stubScanner.scan(validUnit1)) {
		} // loop until successful scan
		Set<Product> orderSet = scannerController.getMainController().getOrder().keySet();
		Product[] orderArr = orderSet.toArray(new Product[orderSet.size()]);
		assertSame("Scanned product should be in the order list", orderArr[0].getPrice(), databaseItem1.getPrice());
	}

	/**
	 * Tests that the BarcodeScannerController reacts correctly to the scan of an
	 * item not in the database
	 */
	@Test
	public void testNotFoundScan() {
		BarcodedUnit notUnit = new BarcodedUnit(new Barcode(Numeral.three, Numeral.four), 359.0);
		while (!stubScanner.scan(notUnit)) {
		} // loop until successful scan
		assertTrue("Scanned product is not in database so should not be in order list",
				scannerController.getMainController().getOrder().isEmpty());
	}

//	Testing ItemAdderController methods

	/**
	 * Tests that the setMainController method of ItemAdderController correctly
	 * replaces the controller's main controller and deregisters the controller from
	 * the old CheckoutController
	 */
	@Test
	public void testNewMainController() {
		CheckoutController newMainController = new CheckoutController();
		scannerController.setMainController(newMainController);

		assertNotSame("New checkout controller should be set in BarcodeScannerController field", checkoutController,
				scannerController.getMainController());
		assertTrue("BarcodeScannerController should be in the new checkout controller's item adder list",
				newMainController.getAllDeviceControllers().contains(scannerController));
		assertTrue("BarcodeScannerController should not be in the old checkout controller's item adder list",
				checkoutController.getAllDeviceControllers().isEmpty());
	}

//	Testing DeviceController methods

	/**
	 * Tests that the disableDevice method of DeviceController causes a
	 * DisabledException to be thrown when a scan is attempted
	 */
	@Test(expected = DisabledException.class)
	public void testDisabledScanController() {
		scannerController.disableDevice();
		stubScanner.scan(validUnit1);
	}

	/**
	 * Tests that the enableDevice method of DeviceController works correctly,
	 * allowing scans to take place again
	 */
	@Test
	public void testReenabledScanController() {
		scannerController.disableDevice();
		scannerController.enableDevice();
		while (!stubScanner.scan(validUnit1)) {
		} // loop until successful scan
		Set<Product> orderSet = scannerController.getMainController().getOrder().keySet();
		Product[] orderArr = orderSet.toArray(new Product[orderSet.size()]);
		assertSame("Scanned product should be in the order list", orderArr[0].getPrice(), databaseItem1.getPrice());
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
	 * Tests that the BaggingScaleController reacts correctly to adding items to
	 * order.
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
		stubScale.add(validUnit2);
		while (!stubScanner.scan(validUnit2)) {
		}
		count = order.get(databaseItem1)[0].intValue();
		assertEquals("Since item was put on scale, it should count 2 copies of product", 1, count);
		validUnit2 = null;
	}

	@Test
	public void testDiscrepancyResolved() {
		scaleController.resetOrder();
		scaleController.attendantInput(true);
		scaleController.reactToWeightChangedEvent(stubScale, 10.0);
		assertFalse(scaleController.getMainController().baggingItemLock);
	}

	@Test
	public void testDiscrepancUnesolved() {
		scaleController.resetOrder();
		scaleController.attendantInput(false);
		scaleController.reactToWeightChangedEvent(stubScale, 10.0);
		assertTrue(scaleController.getMainController().baggingItemLock);
	}

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
	 * Tests that the setMainController method of BaggingScaleController
	 * correctly replaces the controller's main controller and deregisters the
	 * controller from the old CheckoutController
	 */
	@Test
	public void testNewMainControllerScale() {
		CheckoutController newMainController = new CheckoutController();
		scaleController.setMainController(newMainController);

		assertNotSame("New checkout controller should be set in BaggingScaleController field", checkoutController,
				scaleController.getMainController());
		assertTrue("BaggingScaleController should be in the new checkout controller's bagging controller list",
				newMainController.getAllDeviceControllers().contains(scaleController));
		assertTrue("BaggingScaleController should not be in the old checkout controller's bagging controller list",
				checkoutController.getAllDeviceControllers().isEmpty());
	}

	/**
	 * Testing that the checkout only has the scale and scanner controllers as
	 * peripherals
	 */

	@Test
	public void testCorrectRegistrationControllers() {
		Set<DeviceController> controllers = checkoutController.getAllDeviceControllers();
		assertTrue("Only controllers should be scale and scanner controller", controllers.contains(scaleController));
		assertTrue("Only controllers should be scale and scanner controller", controllers.contains(scannerController));
		assertEquals("Only controllers should be scale and scanner controller", controllers.size(), 2);

	}

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
		checkoutController.baggedItemsValid();

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
		checkoutController.baggedItemsValid();

		// First item is added
		checkoutController.addItem(databaseItem2);

		// Adds the cost of the second item to the total
		total = total.add(databaseItem2.getPrice());

		// Rounds the value to 2 decimal places
		total = total.setScale(2, BigDecimal.ROUND_HALF_UP);

		// Amount paid is updated
		checkoutController.addToAmountPaid(BigDecimal.valueOf(50));

		// Subtracts the amount paid from the total
		total = total.subtract(BigDecimal.valueOf(50));

		// Rounds the value to 2 decimal places
		total = total.setScale(2, BigDecimal.ROUND_HALF_UP);

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

		// Unblocks the station and lets a new item be scanned
		checkoutController.baggedItemsValid();

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
	@Test
	public void testInvalidPLUItemAdd() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
		customerController.addItemByPLU("0");
		
		String expected =  "Product not in database" + System.lineSeparator();		
		assertEquals(expected, outContent.toString());
		
		System.setOut(ORIGINAL_OUT);
	    System.setErr(ORIGINAL_ERR);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testNonNumericalPLUAdd() {
		customerController.addItemByPLU("A");
	}
	@Test(expected = NullPointerException.class)
	public void testNullPLUCodeADD() {
		customerController.addItemByPLU(null);
	}
	@Test
	public void testPLUAddItem() {
		int expected_order_len = 1;
		
		customerController.addItemByPLU(
				pluProduct1.getPLUCode().toString()
			);
		System.out.println("Code in string: "+ pluProduct1.getPLUCode().toString());
		assertEquals(
				pluProduct1.getPrice(), 
				checkoutController.getCost()
			);
		assertEquals(
				expected_order_len,
				checkoutController.getOrder().size()
			);
	}
	

}