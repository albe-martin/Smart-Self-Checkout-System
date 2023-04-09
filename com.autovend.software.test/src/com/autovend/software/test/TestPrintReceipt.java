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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReceiptPrinter;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.ReceiptPrinterController;

public class TestPrintReceipt {
	ReceiptPrinter testPrinter;
	ReceiptPrinterController testReceiptPrinterController;
	CheckoutController checkoutController;

	BarcodedProduct testItem1;
	BarcodedProduct testItem2;
	BarcodedProduct testItem3;

	LinkedHashMap<Product, Number[]> order;
	BigDecimal totalCost;
	private StubBarcodedProduct testItem4;
	private StubBarcodedProduct testItem5;
	private StubBarcodedProduct testItem6;

	/**
	 * Set up of objects, variables etc.. that happens before tests
	 */
	@Before
	public void setup() {
		// Create a test receipt printer and its controllers
		testPrinter = new ReceiptPrinter();
		testReceiptPrinterController = new ReceiptPrinterController(testPrinter);
		checkoutController = new CheckoutController();
		checkoutController.registerController("ReceiptPrinterController", testReceiptPrinterController);
		testReceiptPrinterController.setMainController(checkoutController);

		// Create 3 test items
		testItem1 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item 1",
				BigDecimal.valueOf(83.29), 359.0);
		testItem2 = new BarcodedProduct(new Barcode(Numeral.seven, Numeral.one), "test item 2",
				BigDecimal.valueOf(9.29), 169.0);
		testItem3 = new BarcodedProduct(new Barcode(Numeral.nine, Numeral.two), "test item 3",
				BigDecimal.valueOf(32.79), 245.0);

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

			// Call printReceipt()
			testReceiptPrinterController.printReceipt(order, totalCost);

			// Cut the paper to finalize the output string
			testPrinter.cutPaper();
			String result = testPrinter.removeReceipt();
			assertEquals(expectedOutput, result);
		} catch (Exception ex) {
			fail("Exception incorrectly thrown");
		}
	}
	
	/**
	 * Testing to see if unit per kg works
	 */
	@Test
	public void testPrintPerKgItems() {
		// Creating first parameter HashMap<Product, Number[]> in printReceipt()
		Number[] quantityItem1 = { 1, (83.29) };
		Number[] quantityItem2 = { 1, (9.29) };
		Number[] quantityItem3 = { 1, (32.79) };
		
		// Creating 3 test items
		testItem4 = new StubBarcodedProduct(new Barcode(Numeral.three, Numeral.three), "test item 4",
				BigDecimal.valueOf(83.29), 359.0, false);
		testItem5 = new StubBarcodedProduct(new Barcode(Numeral.seven, Numeral.one), "test item 5",
				BigDecimal.valueOf(9.29), 169.0, false);
		testItem6 = new StubBarcodedProduct(new Barcode(Numeral.nine, Numeral.two), "test item 6",
				BigDecimal.valueOf(32.79), 245.0, false);
		
		order.put(testItem4, quantityItem1);
		order.put(testItem5, quantityItem2);
		order.put(testItem6, quantityItem3);
		
		totalCost = BigDecimal.valueOf(83.29 + 9.29 + 32.79);

		String expectedOutput = "Purchase Details:\n" + "1 $83.29 1kg StubBarcodedProduct\n"
				+ "2 $9.29 1kg StubBarcodedProduct\n" + "3 $32.79 1kg StubBarcodedProduct\n" + "Total: $125.37\n";
		try {
			// Add ink and paper into printer
			testPrinter.addInk(1000);
			testPrinter.addPaper(1000);

			// Call printReceipt()
			testReceiptPrinterController.printReceipt(order, totalCost);

			// Cut the paper to finalize the output string
			testPrinter.cutPaper();
			String result = testPrinter.removeReceipt();
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
			testReceiptPrinterController.printReceipt(order, totalCost);
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
			testReceiptPrinterController.printReceipt(order, totalCost);
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

			// Call printReceipt()
			testReceiptPrinterController.printReceipt(order, totalCost);
			testPrinter.cutPaper();
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

			// Call printReceipt()

			checkoutController.setOrder(order);
			checkoutController.cost = totalCost;
			checkoutController.printReceipt();

			// Cut the paper to finalize the output string
			testPrinter.cutPaper();
			String result = testPrinter.removeReceipt();
			assertEquals(expectedOutput, result);
		} catch (Exception ex) {
			fail("Exception incorrectly thrown");
		}
	}
}
