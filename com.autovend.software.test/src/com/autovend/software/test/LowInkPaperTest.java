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
import java.util.Currency;
import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReceiptPrinter;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.ReceiptPrinterController;

public class LowInkPaperTest {

	SelfCheckoutStation station;
	ReceiptPrinterController receiptPrinterController;
	CheckoutController checkoutController;
	ReceiptPrinter receiptPrinter;

	Currency currency;
	int[] billDenominations;
	BigDecimal[] coinDenominations;

	BarcodedProduct firstTestItem;
	BarcodedProduct secondTestItem;
	BarcodedProduct thirdTestItem;

	LinkedHashMap<Product, Number[]> order;
	BigDecimal Product;
	BigDecimal totalCost;

	/*
	 * Set up for the tests (before)
	 */
	@Before
	public void setup() {
		currency = Currency.getInstance("CAD");
		billDenominations = new int[] { 5, 10, 20, 50, 100 };
		coinDenominations = new BigDecimal[] { new BigDecimal(25), new BigDecimal(100), new BigDecimal(5) };
		station = new SelfCheckoutStation(currency, billDenominations, coinDenominations, 1000, 1);

		checkoutController = new CheckoutController(station);

		receiptPrinter = station.printer;
		receiptPrinterController = new ReceiptPrinterController(receiptPrinter);

		checkoutController.registerReceiptPrinter(receiptPrinterController);
		receiptPrinterController.setMainController(checkoutController);

		firstTestItem = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "first item",
				BigDecimal.valueOf(23.23), 45.54);
		secondTestItem = new BarcodedProduct(new Barcode(Numeral.six, Numeral.nine), "second item",
				BigDecimal.valueOf(75.43), 115.67);
		thirdTestItem = new BarcodedProduct(new Barcode(Numeral.four, Numeral.two), "third item",
				BigDecimal.valueOf(14.86), 539.28);

		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(firstTestItem.getBarcode(), firstTestItem);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(secondTestItem.getBarcode(), secondTestItem);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(thirdTestItem.getBarcode(), thirdTestItem);

		order = new LinkedHashMap<Product, Number[]>();
	}

	/*
	 * Testing whether the checkout controller returns the new check out controller
	 */
	@Test
	public void testSetMainController() {
		CheckoutController mainController = new CheckoutController();
		receiptPrinterController.setMainController(mainController);
		assertEquals(mainController, receiptPrinterController.getMainController());
	}

	/*
	 * Testing whether the old checkout controller is any different from the new
	 * check out controller
	 */
	@Test
	public void testSetMainControllerDiff() {
		CheckoutController mainController = new CheckoutController();
		receiptPrinterController.setMainController(mainController);
		assertNotSame(checkoutController, receiptPrinterController.getMainController());
	}

	// Testing if ink is being added and if it stacks (in software side of things
	// only)
	@Test
	public void testAddedInkSoftware() {
		receiptPrinterController.addedInk(100);
		assertEquals(100, receiptPrinterController.estimatedInk);

		receiptPrinterController.addedInk(50);
		assertEquals(150, receiptPrinterController.estimatedInk);

		receiptPrinterController.addedInk(0);
		assertEquals(150, receiptPrinterController.estimatedInk);
	}

	// Testing if addedInk is 0 after adding 0
	@Test
	public void testAddedZeroInkSoftware() {
		receiptPrinterController.addedInk(0);
		assertEquals(0, receiptPrinterController.estimatedInk);
	}

	// Testing if addedInk is a negative value subtracts from positive ink value
	// (negative values should be ignored and
	// cause error)
	@Test
	public void testAddedNegInkSoftware() {
		receiptPrinterController.addedInk(100);
		receiptPrinterController.addedInk(-15);
		assertEquals(100, receiptPrinterController.estimatedInk);
	}

	// Testing if addedInk adds negative value (negative values should be ignored
	// and
	// cause error)
	@Test
	public void testAddedNegInkOnlySoftware(){
		receiptPrinterController.addedInk(-15);
		assertEquals(0, receiptPrinterController.estimatedInk);
		System.out.println("Out of Ink");
	}

	// Testing if addedPaper correctly works
	@Test
	public void testAddedPaperSoftware() {
		receiptPrinterController.addedPaper(100);
		assertEquals(100, receiptPrinterController.estimatedPaper);
	}

	// Testing if addedPaper is 0
	@Test
	public void testAddedZeroPaperSoftware() {
		receiptPrinterController.addedPaper(0);
		assertEquals(0, receiptPrinterController.estimatedPaper);
	}

	// Testing if addedPaper subtracts negative value from the positive value
	// (negative values should be ignored
	// and cause error)
	@Test
	public void testAddedNegPaperSoftware() {
		receiptPrinterController.addedPaper(100);
		receiptPrinterController.addedInk(-15);
		assertEquals(100, receiptPrinterController.estimatedPaper);
	}

	// Testing if addedPaper adds negative value (negative values should be ignored
	// and cause error)
	@Test
	public void testAddedNegPaperOnlySoftware() throws EmptyException {
		receiptPrinterController.addedPaper(-15);
		assertEquals(0, receiptPrinterController.estimatedPaper);
		System.out.println("Out of Paper");
	}

	// Testing if printReceipt works correctly
	@Test
	public void testPrintReceiptNormal() throws OverloadException {
		receiptPrinter.addInk(1000);
		receiptPrinter.addPaper(1000);
		receiptPrinterController.addedInk(1000);
		receiptPrinterController.addedPaper(1000);
		Number[] qItem1 = { 5, (5 * 23.23) };
		Number[] qItem2 = { 2, (2 * 75.43) };
		Number[] qItem3 = { 1, (1 * 14.86) };
		order.put(firstTestItem, qItem1);
		order.put(secondTestItem, qItem2);
		order.put(thirdTestItem, qItem3);

		double costOfqItem1 = 5 * 23.23;
		double costOfqItem2 = 2 * 75.43;
		double costOfqItem3 = 1 * 14.86;
		totalCost = BigDecimal.valueOf(costOfqItem1 + costOfqItem2 + costOfqItem3);

		receiptPrinterController.printReceipt(order, totalCost);
		receiptPrinter.cutPaper();
		String receipt = receiptPrinter.removeReceipt();
		System.out.println(receipt);

		// testing if the software keeps track of the paper and ink used
		assertEquals(897, receiptPrinterController.estimatedInk);
		assertEquals(995, receiptPrinterController.estimatedPaper);
	}

	// Testing if printReceipt works correctly with exactly enough ink
	@Test
	public void testPrintReceiptExactInk() throws OverloadException {
		receiptPrinter.addInk(103);
		receiptPrinter.addPaper(30);
		receiptPrinterController.addedInk(103);
		receiptPrinterController.addedPaper(30);
		Number[] qItem1 = { 4, (4 * 23.23) };
		Number[] qItem2 = { 5, (5 * 75.63) };
		Number[] qItem3 = { 1, (1 * 178.86) };
		order.put(firstTestItem, qItem1);
		order.put(secondTestItem, qItem2);
		order.put(thirdTestItem, qItem3);

		double costOfqItem1 = 4 * 23.23;
		double costOfqItem2 = 5 * 75.63;
		double costOfqItem3 = 1 * 178.86;
		totalCost = BigDecimal.valueOf(costOfqItem1 + costOfqItem2 + costOfqItem3);

		receiptPrinterController.printReceipt(order, totalCost);
		receiptPrinter.cutPaper();
		String receipt = receiptPrinter.removeReceipt();
		System.out.println(receipt);

		// testing if the software keeps track of the paper and ink used
		assertEquals(0, receiptPrinterController.estimatedInk);
		assertEquals(25, receiptPrinterController.estimatedPaper);
	}

	// Testing if printReceipt works correctly with exactly enough paper
	@Test
	public void testPrintReceiptExactPaper() throws OverloadException, SimulationException {
		receiptPrinter.addInk(115);
		receiptPrinter.addPaper(5);
		receiptPrinterController.addedInk(115);
		receiptPrinterController.addedPaper(5);
		Number[] qItem1 = { 70, (70 * 23.23) };
		Number[] qItem2 = { 55, (55 * 725.63) };
		Number[] qItem3 = { 1, (1 * 1686.86) };
		order.put(firstTestItem, qItem1);
		order.put(secondTestItem, qItem2);
		order.put(thirdTestItem, qItem3);

		double costOfqItem1 = 6 * 23.23;
		double costOfqItem2 = 5 * 725.63;
		double costOfqItem3 = 1 * 1686.86;
		totalCost = BigDecimal.valueOf(costOfqItem1 + costOfqItem2 + costOfqItem3);

		receiptPrinterController.printReceipt(order, totalCost);
		receiptPrinter.cutPaper();
		String receipt = receiptPrinter.removeReceipt();
		System.out.println(receipt);
		assertEquals(4, receiptPrinterController.estimatedInk);
		assertEquals(0, receiptPrinterController.estimatedPaper);
	}

	// Testing if printReceipt low ink flag/indicator works correctly
	@Test
	public void testPrintReceiptLowInkFlag() throws OverloadException, EmptyException {
		receiptPrinter.addInk(505);
		receiptPrinter.addPaper(505);
		receiptPrinterController.addedInk(505);
		receiptPrinterController.addedPaper(505);
		Number[] qItem1 = { 35, (35 * 24.23) };
		Number[] qItem2 = { 21, (21 * 75.43) };
		Number[] qItem3 = { 13, (13 * 189.86) };
		order.put(firstTestItem, qItem1);
		order.put(secondTestItem, qItem2);
		order.put(thirdTestItem, qItem3);

		double costOfqItem1 = 35 * 24.23;
		double costOfqItem2 = 21 * 75.43;
		double costOfqItem3 = 13 * 189.86;
		totalCost = BigDecimal.valueOf(costOfqItem1 + costOfqItem2 + costOfqItem3);

		receiptPrinterController.printReceipt(order, totalCost);
		receiptPrinter.cutPaper();
		String receipt = receiptPrinter.removeReceipt();
		System.out.println(receipt);

		// testing if the software keeps track of the paper and ink used
		assertTrue(receiptPrinterController.inkLow);
	}

	// Testing if printReceipt low paper flag/indicator works correctly
	@Test
	public void testPrintReceiptLowPaperFlag() throws OverloadException {
		receiptPrinter.addInk(765);
		receiptPrinter.addPaper(205);
		receiptPrinterController.addedInk(765);
		receiptPrinterController.addedPaper(205);
		Number[] qItem1 = { 4, (4 * 23.23) };
		Number[] qItem2 = { 26, (26 * 75.43) };
		Number[] qItem3 = { 11, (11 * 14.86) };
		order.put(firstTestItem, qItem1);
		order.put(secondTestItem, qItem2);
		order.put(thirdTestItem, qItem3);

		double costOfqItem1 = 5 * 23.23;
		double costOfqItem2 = 2 * 75.43;
		double costOfqItem3 = 1 * 14.86;
		totalCost = BigDecimal.valueOf(costOfqItem1 + costOfqItem2 + costOfqItem3);

		receiptPrinterController.printReceipt(order, totalCost);
		receiptPrinter.cutPaper();
		String receipt = receiptPrinter.removeReceipt();
		System.out.println(receipt);

		// testing if the software keeps track of the paper and ink used
		assertTrue(receiptPrinterController.paperLow);
	}

	// Testing if printReceipt low paper and ink flag/indicator works correctly
	@Test
	public void testPrintReceiptLowPaperInkFlag() throws OverloadException {
		receiptPrinter.addInk(504);
		receiptPrinter.addPaper(204);
		receiptPrinterController.addedInk(504);
		receiptPrinterController.addedPaper(204);
		Number[] qItem1 = { 4, (4 * 23.23) };
		Number[] qItem2 = { 26, (26 * 75.43) };
		Number[] qItem3 = { 11, (11 * 14.86) };
		order.put(firstTestItem, qItem1);
		order.put(secondTestItem, qItem2);
		order.put(thirdTestItem, qItem3);

		double costOfqItem1 = 5 * 23.23;
		double costOfqItem2 = 2 * 75.43;
		double costOfqItem3 = 1 * 14.86;
		totalCost = BigDecimal.valueOf(costOfqItem1 + costOfqItem2 + costOfqItem3);

		receiptPrinterController.printReceipt(order, totalCost);
		receiptPrinter.cutPaper();
		String receipt = receiptPrinter.removeReceipt();
		System.out.println(receipt);

		// testing if the software keeps track of the paper and ink used
		assertTrue(receiptPrinterController.paperLow);
		assertTrue(receiptPrinterController.inkLow);

	}

	// Testing if printReceipt when receipt is too long
	@Test
	public void testPrintReceiptTooLong() throws OverloadException {
		receiptPrinter.addInk(1000);
		receiptPrinter.addPaper(1000);
		receiptPrinterController.addedInk(1000);
		receiptPrinterController.addedPaper(1000);
		Number[] qItem1 = { Integer.MAX_VALUE, (Double.MAX_VALUE) };
		order.put(firstTestItem, qItem1);
		totalCost = BigDecimal.valueOf(Double.MAX_VALUE);
		receiptPrinterController.printReceipt(order, totalCost);
		receiptPrinter.cutPaper();
		String receipt = receiptPrinter.removeReceipt();
		System.out.println(receipt);
		System.out.println("Receipt too long");
	}
	
	// Testing how printReceipt would work if there isn't enough ink and paper
	@Test
	public void testPrintReceiptNotEnoughInkPaper() throws OverloadException, SimulationException {
		receiptPrinter.addInk(101);
		receiptPrinter.addPaper(4);
		receiptPrinterController.addedInk(101);
		receiptPrinterController.addedPaper(4);
		Number[] qItem1 = { 4, (4 * 23.23) };
		Number[] qItem2 = { 5, (5 * 75.63) };
		Number[] qItem3 = { 1, (1 * 178.86) };
		order.put(firstTestItem, qItem1);
		order.put(secondTestItem, qItem2);
		order.put(thirdTestItem, qItem3);

		double costOfqItem1 = 4 * 23.23;
		double costOfqItem2 = 5 * 75.63;
		double costOfqItem3 = 1 * 178.86;
		totalCost = BigDecimal.valueOf(costOfqItem1 + costOfqItem2 + costOfqItem3);

		receiptPrinterController.printReceipt(order, totalCost);
		receiptPrinter.cutPaper();
		String receipt = receiptPrinter.removeReceipt();
		System.out.println(receipt);
	}
}
