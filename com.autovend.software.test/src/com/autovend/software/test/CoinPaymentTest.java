package com.autovend.software.test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.Numeral;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.ChangeDispenserController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CoinDispenserController;
import com.autovend.software.controllers.CoinPaymentController;

public class CoinPaymentTest {
	SelfCheckoutStation selfCheckoutStation;
	CheckoutController checkoutControllerStub;
	CoinPaymentController coinPaymentControllerStub;
	Currency currency;
	int[] billDenominations;
	BigDecimal[] coinDenominations;
	LinkedHashMap<Product, Number[]> order;
	
	@Before
	public void setup() {
		currency = Currency.getInstance("CAD");
		billDenominations = new int[] {5, 10, 20, 50, 100};
		coinDenominations = new BigDecimal[] {new BigDecimal(0.01), new BigDecimal(0.05), new BigDecimal(0.1), new BigDecimal(0.25), new BigDecimal(1), new BigDecimal(2)};

		selfCheckoutStation = new SelfCheckoutStation(currency, billDenominations, coinDenominations, 200, 1);

		checkoutControllerStub = new CheckoutController();
		coinPaymentControllerStub = new CoinPaymentController(selfCheckoutStation.coinValidator);
		coinPaymentControllerStub.setMainController(checkoutControllerStub);
		checkoutControllerStub.registerController("PaymentController", coinPaymentControllerStub);

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
		barcodedProduct = new BarcodedProduct(new Barcode(Numeral.one, Numeral.six), "test item 6",
				BigDecimal.valueOf(4.25), 30.00);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);
	}
	
	@After
	public void tearDown() {
		selfCheckoutStation = null;
		checkoutControllerStub = null;
		coinPaymentControllerStub = null;
	}
	
	@Test
	public void testStandardPaymentOneProductExactAmount() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.four));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(1), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.25), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.25), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.25), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.1), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.1), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.01), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.01), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.01), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.01), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(0.00).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
	}
	
	@Test
	public void testStandardPaymentTwoProductExactAmount() {
		// total cost: $60
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.two));
		BarcodedProduct product2 = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.five));
		
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);
		order.put(product2, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			for (int i = 0; i < 30; i++) {
				selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			}
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(0.00).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
	}
	
	@Test
	public void testPartialPaymentOneProduct() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.four));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(1), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(0.99).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
	}
	
	@Test
	public void testPartialPaymentThenFullPaymentOneProduct() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.six));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(2.25).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.001);
		
		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.25), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		expectedAmount = new BigDecimal(0.00).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
	}
	
	@Test
	public void testOverPayment() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.six));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(-1.75).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
	}
	
	@Test
	public void testCompletePayment() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.six));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.25), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(0.00).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
		
		checkoutControllerStub.completePayment();
	}
	
	@Test
	public void testCompletePaymentBaggingItemLock() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.six));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.25), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(0.00).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
		
		checkoutControllerStub.baggingItemLock = true;
		checkoutControllerStub.completePayment();
	}
	
	@Test
	public void testCompletePaymentSystemProtectionLock() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.six));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.25), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(0.00).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
		
		checkoutControllerStub.systemProtectionLock = true;
		checkoutControllerStub.completePayment();
	}
	
	@Test
	public void testCompletePaymentInsufficientFunds() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.six));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(0.25).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
		
		checkoutControllerStub.completePayment();
	}
	
	@Test
	public void testCompletePaymentEmptyOrder() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.six));
		order = new LinkedHashMap<>();

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(-4.0).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
		
		checkoutControllerStub.completePayment();
	}
	
	@Test
	public void testCompletePaymentOverPayment() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.six));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(-1.75).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
		
		CoinDispenser coinDispenser = new CoinDispenser(10);
		CoinDispenserController coinController = new CoinDispenserController(coinDispenser, new BigDecimal(1));
		coinController.setMainController(checkoutControllerStub);
		checkoutControllerStub.registerController("ChangeDispenserController", coinController);
		AttendantStationController attendant = new AttendantStationController();
		AttendantIOController attendantIO = new AttendantIOController(selfCheckoutStation.screen);
		attendantIO.setMainAttendantController(attendant);
		checkoutControllerStub.registerController("AttendantIOController", attendantIO);
		checkoutControllerStub.completePayment();
	}
	
	@Test
	public void testCompletePaymentRequireAdjustment() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.six));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.25), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(0).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
		
		CoinDispenser coinDispenser = new CoinDispenser(10);
		CoinDispenserController coinController = new CoinDispenserController(coinDispenser, new BigDecimal(1));
		coinController.setMainController(checkoutControllerStub);
		checkoutControllerStub.registerController("ChangeDispenserController", coinController);
		AttendantStationController attendant = new AttendantStationController();
		AttendantIOController attendantIO = new AttendantIOController(selfCheckoutStation.screen);
		attendantIO.setMainAttendantController(attendant);
		checkoutControllerStub.registerController("AttendantIOController", attendantIO);
		checkoutControllerStub.changeDenomLow(coinController, new BigDecimal(1));
		checkoutControllerStub.completePayment();
	}
	
	@Test
	public void testPaymentOneProductInvalidCoin() {
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.four));
		order = new LinkedHashMap<>();
		order.put(product, new Number[1]);

		checkoutControllerStub.setOrder(order);

		try {
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(2), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(1), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.25), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.25), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.25), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.1), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.1), currency));
			selfCheckoutStation.coinSlot.accept(new Coin(new BigDecimal(0.04), currency));
		} catch (Exception ex) {
			System.out.printf("Exception " + ex.getMessage());
		}
		double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
		double expectedAmount = new BigDecimal(0.04).doubleValue();
		assertEquals(expectedAmount, amountRemaining, 0.01);
	}
}
