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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedHashMap;

import com.autovend.Bill;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.BillValidator;
import com.autovend.devices.observers.BillSlotObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.BillPaymentController;
import com.autovend.software.controllers.CheckoutController;

public class BillPaymentControllerTest {
	SelfCheckoutStation selfCheckoutStation;
	CheckoutController checkoutControllerStub;
	BillPaymentController billPaymentControllerStub;
	int[] billDenominations;
	BigDecimal[] coinDenominations;
	LinkedHashMap<Product, Number[]> order;
	private boolean result;
	

	@Before
	public void setup() {
		// Init denominations
		billDenominations = new int[] {5, 10, 20, 50, 100};
		coinDenominations = new BigDecimal[] {new BigDecimal(0.05), new BigDecimal(0.1), new BigDecimal(0.25), new BigDecimal(100), new BigDecimal(200)};

		selfCheckoutStation = new SelfCheckoutStation(Currency.getInstance("CAD"), billDenominations, coinDenominations,200, 1);

		checkoutControllerStub = new CheckoutController();
		billPaymentControllerStub = new BillPaymentController(selfCheckoutStation.billValidator);
		billPaymentControllerStub.setMainController(checkoutControllerStub);
		checkoutControllerStub.registerController("PaymentController", billPaymentControllerStub);

		BarcodedProduct barcodedProduct;
		barcodedProduct = new BarcodedProduct(new Barcode(Numeral.one, Numeral.one), "test item 1",
				BigDecimal.valueOf(83.29), 400.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);
		barcodedProduct = new BarcodedProduct(new Barcode(Numeral.one, Numeral.two), "test item 2",
				BigDecimal.valueOf(50.00), 359.00);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);
		barcodedProduct = new BarcodedProduct(new Barcode(Numeral.one, Numeral.three), "test item 3",
				BigDecimal.valueOf(29.99), 125.25);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);
		barcodedProduct = new BarcodedProduct(new Barcode(Numeral.one, Numeral.four), "test item 4",
				BigDecimal.valueOf(9.99), 26.75);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);
		barcodedProduct = new BarcodedProduct(new Barcode(Numeral.one, Numeral.five), "test item 5",
				BigDecimal.valueOf(10.00), 30.00);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);
		
		result = false;
	}
	
	@Test
	public void standardPaymentTest() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.five));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.billInput.accept(new Bill(10, Currency.getInstance("CAD")));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(0).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0);
	}
	

	@Test
	public void partialPaymentAndRemainingAmountTest() {
		// create order cart
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.three));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.billInput.accept(new Bill(10, Currency.getInstance("CAD")));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expextedAmount = new BigDecimal(19.99).doubleValue();
		assertEquals(amountRemaining, expextedAmount, 0);
	}

	@Test
	public void overpaymentTest() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.one));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.billInput.accept(new Bill(10, Currency.getInstance("CAD")));
			selfCheckoutStation.billInput.accept(new Bill(20, Currency.getInstance("CAD")));
			selfCheckoutStation.billInput.accept(new Bill(20, Currency.getInstance("CAD")));
			selfCheckoutStation.billInput.accept(new Bill(50, Currency.getInstance("CAD")));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expextedAmount = new BigDecimal(-16.71).doubleValue();
		assertEquals(amountRemaining, expextedAmount, 0);
	}

	@Test (expected = OverloadException.class)
	public void invalidBillDanglingTest() throws OverloadException {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.one));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		selfCheckoutStation.billInput.accept(new Bill(15, Currency.getInstance("CAD")));
		selfCheckoutStation.billInput.accept(new Bill(10, Currency.getInstance("CAD")));
	}

	@Test
	public void InvalidDanglingBillRemovedTest() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.one));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);



		try {
			selfCheckoutStation.billInput.accept(new Bill(15, Currency.getInstance("CAD")));
			selfCheckoutStation.billInput.removeDanglingBill();
			selfCheckoutStation.billInput.accept(new Bill(10, Currency.getInstance("CAD")));
		} catch (OverloadException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
    public void reactToInvalidBill() {
        BillValidator bb = new BillValidator(Currency.getInstance("CAD"),billDenominations);
        bb.disable();
        BillPaymentController b = new BillPaymentController(bb);    
    }
	
	@Test
	public void reactToValidBill() {
        BillValidator bb = new BillValidator(Currency.getInstance("CAD"),billDenominations);
        bb.disable();
        BillPaymentController b = new BillPaymentController(bb); 
        b.setMainController(new CheckoutController() {
        	@Override
        	public void addToAmountPaid(BigDecimal value) {
        		result = true;
        	}
        });
        b.reactToValidBillDetectedEvent(bb, null, 0);
        assertTrue(result);
	}
}	
