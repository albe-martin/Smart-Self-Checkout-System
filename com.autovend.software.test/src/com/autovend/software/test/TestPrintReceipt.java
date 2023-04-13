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
import java.util.LinkedHashMap;

import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.controllers.ReceiptPrinterController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReceiptPrinter;
import com.autovend.devices.TouchScreen;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;

import static org.junit.Assert.*;

public class TestPrintReceipt {
	ReceiptPrinter testPrinter;
	ReceiptPrinterController testReceiptPrinterController;
	CheckoutController checkoutController;

	private AttendantIOController attendantController;
	private AttendantStationController stationController;
	private CustomerIOController customerController;

	BarcodedProduct testItem1;
	BarcodedProduct testItem2;
	BarcodedProduct testItem3;
	BarcodedProduct testItem4;
	StubBarcodedProduct testItem5;
	StubBarcodedProduct testItem6;
	StubBarcodedProduct testItem7;

	StringBuilder receipt;
	TouchScreen stubDevice;

	LinkedHashMap<Product, Number[]> order;
	BigDecimal totalCost;

	/**
	 * Set up of objects, variables etc.. that happens before tests
	 */
	@Before
	public void setup() {
		// Create a test receipt printer and its controllers
		testPrinter = new ReceiptPrinter();
		checkoutController = new CheckoutController();
		testReceiptPrinterController = new ReceiptPrinterController(testPrinter);
		stationController = new AttendantStationController();
		stubDevice = new TouchScreen();
		receipt = new StringBuilder();
		
			
		checkoutController.registerController("ReceiptPrinterController", testReceiptPrinterController);
		testReceiptPrinterController.setMainController(checkoutController);
		attendantController = new AttendantIOController(stubDevice);
		attendantController.setMainAttendantController(stationController);
		checkoutController.registerController("AttendantIOController", attendantController);
		customerController = new CustomerIOController(stubDevice);
		customerController.setMainController(checkoutController);



		stationController.registerUser("TestUser", "TestPass");
		stationController.login("TestUser", "TestPass");


		// Create 3 test items
		testItem1 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item 1",
				BigDecimal.valueOf(83.29), 359.0);
		testItem2 = new BarcodedProduct(new Barcode(Numeral.seven, Numeral.one), "test item 2",
				BigDecimal.valueOf(9.29), 169.0);
		testItem3 = new BarcodedProduct(new Barcode(Numeral.nine, Numeral.two), "test item 3",
				BigDecimal.valueOf(32.79), 245.0);
		testItem4 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.seven), "test item 4",
				BigDecimal.valueOf(21), 10.0);

		// Enters the 3 test items int othe Product Database
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(testItem1.getBarcode(), testItem1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(testItem2.getBarcode(), testItem2);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(testItem3.getBarcode(), testItem3);

		// Instantiate HashMap of Products to Number[] called order
		order = new LinkedHashMap<Product, Number[]>();
	}

	/**
	 * Tears down objects so they can be initialized again with setup
	 */
	@After
	public void teardown() {
		// Readability
		System.out.println();
		receipt = null;
	}

	/**
	 * Creates an order using the test items and checks to see if printReceipt()
	 * properly prints out the order.
	 *
	 * @throws OverloadException
	 */
	@Test
	public void testPrintItems() {
		// Creating first parameter HashMap<Product, Number[]> in printReceipt()
		Number[] quantityItem1 = { 2, (2 * 83.29) };
		Number[] quantityItem2 = { 3, (3 * 9.29) };
		Number[] quantityItem3 = { 1, (32.79) };
		order.put(testItem1, quantityItem1);
		order.put(testItem2, quantityItem2);
		order.put(testItem3, quantityItem3);

		// Computing total cost
		totalCost = BigDecimal.valueOf(2 * 83.29 + 3 * 9.29 + 32.79);

		String expectedOutput = "Purchase Details:\n" + "1 $166.58 2x BarcodedProduct\n"
				+ "2 $27.87 3x BarcodedProduct\n" + "3 $32.79 1x BarcodedProduct\n" + "Total: $227.24\n";
		try {
			// Add ink and paper into printer
			testPrinter.addInk(1000);
			testPrinter.addPaper(1000);
			testReceiptPrinterController.addedInk(1000);
			testReceiptPrinterController.addedPaper(1000);

			// Call printReceipt()
			receipt = testReceiptPrinterController.createReceipt(order, totalCost);
			//print the receipt
			testReceiptPrinterController.printReceipt(receipt);
			// Cut the paper to finalize the output string
			String result = testPrinter.removeReceipt();
			assertEquals(expectedOutput, result);
		} catch (Exception ex) {
			fail("Exception incorrectly thrown");
		}
	}
	
	@Test
	public void testPrintKgItems() {
		// Creating first parameter HashMap<Product, Number[]> in printReceipt()
		Number[] quantityItem5 = { 2, (2 * 83.29) };
		Number[] quantityItem6 = { 3, (3 * 9.29) };
		Number[] quantityItem7 = { 1, (32.79) };
		
		testItem5 = new StubBarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item 5",
				BigDecimal.valueOf(83.29), 359.0, false);
		testItem6 = new StubBarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item 6",
				BigDecimal.valueOf(83.29), 359.0, false);
		testItem6 = new StubBarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item 7",
				BigDecimal.valueOf(83.29), 359.0, false);
		
		order.put(testItem5, quantityItem5);
		order.put(testItem6, quantityItem6);
		order.put(testItem7, quantityItem7);
		
		totalCost = BigDecimal.valueOf((2 * 83.29 + 3 * 9.29 + 32.79)/359.0);
		
		String expectedOutput = "Purchase Details:\n" + "1 $166.58 2kg StubBarcodedProduct\n"
				+ "2 $27.87 3kg StubBarcodedProduct\n" + "3 $32.79 1kg StubBarcodedProduct\n" + "Total: $227.24\n";
		
		try {
			// Add ink and paper into printer
			testPrinter.addInk(1000);
			testPrinter.addPaper(1000);
			testReceiptPrinterController.addedInk(1000);
			testReceiptPrinterController.addedPaper(1000);

			// Call printReceipt()
			receipt = testReceiptPrinterController.createReceipt(order, totalCost);
			//print the receipt
			testReceiptPrinterController.printReceipt(receipt);
			// Cut the paper to finalize the output string
			String result = testPrinter.removeReceipt();
			System.out.println(result);
			assertEquals(expectedOutput, result);
		} catch (Exception ex) {
			fail("Exception incorrectly thrown");
		}
	}
		

	/**
	 * Ensures a no paper/ink message is received when the printer has ink but no
	 * paper
	 *
	 * @throws OverloadException
	 */
	@Test
	public void testNoPaper() {
		// Creating first parameter HashMap<Product, Number[]> in printReceipt()
		Number[] quantityItem1 = { 1, (83.29) };
		order.put(testItem1, quantityItem1);

		// Computing total cost
		totalCost = BigDecimal.valueOf(83.29);
		try {
			// Add ink into printer
			testPrinter.addInk(5);
			// Call printReceipt()
			receipt = testReceiptPrinterController.createReceipt(order, totalCost);
			//print the receipt
			testReceiptPrinterController.printReceipt(receipt);
		} catch (OverloadException ex) {
			fail("Overload Exception Unexpectedly Thrown");
		}
		assertTrue(checkoutController.needPrinterRefill);
	}

	/**
	 * Ensures a no paper/ink message is received when the printer has paper but no
	 * ink
	 *
	 * @throws OverloadException
	 */
	@Test
	public void testNoInk() {
		// Creating first parameter HashMap<Product, Number[]> in printReceipt()
		Number[] quantityItem1 = { 1, (83.29) };
		order.put(testItem1, quantityItem1);

		// Computing total cost
		totalCost = BigDecimal.valueOf(83.29);
		try {
			// Add ink into printer
			testPrinter.addPaper(5);
			// Call printReceipt()
			receipt = testReceiptPrinterController.createReceipt(order, totalCost);
			//print the receipt
			testReceiptPrinterController.printReceipt(receipt);
		} catch (OverloadException ex) {
			fail("Overload Exception Unexpectedly Thrown");
		}
		assertTrue(checkoutController.needPrinterRefill);
	}

	/**
	 * Checks that if a receipt is too long message is received when there is an
	 * overload of characters/the receipt is too long. ***I BELIEVE THIS IS AN ERROR
	 * IN THE HARDWARE CODE***
	 *
	 * @throws OverloadException
	 */
	@Test
	public void testTooLong() {
		// Creating first parameter HashMap<Product, Number[]> in printReceipt(). Trying
		// to make string as large as possible given our code
		Number[] quantityItem1 = { 1,
				100000000000000000000000000000000000000000000000000000000000000000000000000000000000000.0 };
		order.put(testItem1, quantityItem1);
		String expectedOutput = "Purchase Details:\n" + "1 $10000000000000000000000000000000000000000000000000000000-\n"
				+ "    -0000000000000000000000000000000.00 1x BarcodedProduct\n" + "Total: $100.00\n";
		// Computing total cost
		totalCost = BigDecimal.valueOf(100.0);
		try {
			// Add paper and ink into printer
			testPrinter.addInk(1000);
			testPrinter.addPaper(1000);
			testReceiptPrinterController.addedInk(1000);
			testReceiptPrinterController.addedPaper(1000);

			// Call printReceipt()
			receipt = testReceiptPrinterController.createReceipt(order, totalCost);
			//print the receipt
			testReceiptPrinterController.printReceipt(receipt);
			String result = testPrinter.removeReceipt();
			assertEquals(expectedOutput, result);
		} catch (Exception ex) {
			fail("Exception unexpectedly thrown");
		}
	}

	/**
	 * Checks that the old checkout controller is different from the new check out
	 * controller that has just been set.
	 */
	@Test
	public void testNewMainControllerDifferent() {
		// Create new main controller
		CheckoutController newMainController = new CheckoutController();

		// Setting new main controller to testReceiptPrinterController
		testReceiptPrinterController.setMainController(newMainController);

		// They should not be the same.
		assertNotSame(checkoutController, testReceiptPrinterController.getMainController());
	}

	/**
	 * Checks that the getter for the checkout controller does in fact return the
	 * new check out controller.
	 */
	@Test
	public void testNewMainControllerSame() {
		// Create new main controller
		CheckoutController newMainController = new CheckoutController();

		// Setting new main controller to testReceiptPrinterController
		testReceiptPrinterController.setMainController(newMainController);

		// New controller and getter controller should be the same
		assertEquals(newMainController, testReceiptPrinterController.getMainController());
	}

	/**
	 * If you try to print while the printer is disabled, expect a DisabledException
	 *
	 * @throws OverloadException This is a bug in the hardware code.
	 */
	/*
	 * @Test(expected = DisabledException.class) public void testDisabledPrinter()
	 * throws OverloadException { // Disabling ReceiptPrinterController
	 * testReceiptPrinterController.disableDevice();
	 *
	 * // Creating first parameter HashMap<Product, Number[]> in printReceipt()
	 * Number[] quantityItem1 = {2, (2*83.29)}; order.put(testItem1, quantityItem1);
	 *
	 * // Computing total cost totalCost = BigDecimal.valueOf(2*83.29);
	 *
	 * // Adding ink and paper into machine testPrinter.addInk(100);
	 * testPrinter.addPaper(100);
	 *
	 * // Call printReceipt() testReceiptPrinterController.printReceipt(order,
	 * totalCost); }
	 *
	 */

	@Test
	public void testCheckoutControllerPrintReceipt() {
		// Creating first parameter HashMap<Product, Number[]> in printReceipt()
		Number[] quantityItem1 = { 2, (2 * 83.29) };
		Number[] quantityItem2 = { 3, (3 * 9.29) };
		Number[] quantityItem3 = { 1, (32.79) };
		order.put(testItem1, quantityItem1);
		order.put(testItem2, quantityItem2);
		order.put(testItem3, quantityItem3);

		// Computing total cost
		totalCost = BigDecimal.valueOf(2 * 83.29 + 3 * 9.29 + 32.79);

		String expectedOutput = "Purchase Details:\n" + "1 $166.58 2x BarcodedProduct\n"
				+ "2 $27.87 3x BarcodedProduct\n" + "3 $32.79 1x BarcodedProduct\n" + "Total: $227.24\n";
		try {
			// Add ink and paper into printer
			testPrinter.addInk(1000);
			testPrinter.addPaper(1000);
			testReceiptPrinterController.addedInk(1000);
			testReceiptPrinterController.addedPaper(1000);

			// Call printReceipt()

			checkoutController.setOrder(order);
			checkoutController.cost = totalCost;
			checkoutController.printReceipt();

			// Cut the paper to finalize the output string

			String result = testPrinter.removeReceipt();
			assertEquals(expectedOutput, result);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception incorrectly thrown");
		}
	}

	@Test
	public void testAddedInk_negative() {
		int initial = testReceiptPrinterController.estimatedInk;
		testReceiptPrinterController.addedInk(-50);
		assertEquals(initial, testReceiptPrinterController.estimatedInk);
	}
	@Test
	public void testAddedInk() {
		int initial = testReceiptPrinterController.estimatedInk;
		testReceiptPrinterController.addedInk(100);
		assertEquals(initial+100, testReceiptPrinterController.estimatedInk);
	}

	@Test
	public void testAddedInk_highAmount() {
		int initialInk = testReceiptPrinterController.estimatedInk;
		testReceiptPrinterController.addedInk(1000);
		assertEquals(initialInk + 1000, testReceiptPrinterController.estimatedInk);
		assertEquals(false, testReceiptPrinterController.inkLow);
	}

	@Test
	public void testAddedPaper_negative() {
		int initial = testReceiptPrinterController.estimatedPaper;
		testReceiptPrinterController.addedPaper(-50);
		assertEquals(initial, testReceiptPrinterController.estimatedPaper);
	}
	@Test
	public void testAddedPaper() {
		int initial = testReceiptPrinterController.estimatedPaper;
		testReceiptPrinterController.addedPaper(100);
		assertEquals(initial+100, testReceiptPrinterController.estimatedPaper);
	}

	@Test
	public void testAddedPaper_highAmount() {
		int initial = testReceiptPrinterController.estimatedPaper;
		testReceiptPrinterController.addedPaper(1000);
		assertEquals(initial + 1000, testReceiptPrinterController.estimatedPaper);
		assertFalse(testReceiptPrinterController.paperLow);
	}
}
