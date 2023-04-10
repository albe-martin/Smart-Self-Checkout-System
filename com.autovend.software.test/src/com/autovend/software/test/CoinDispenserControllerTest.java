package com.autovend.software.test;

import com.autovend.Barcode;
import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.Numeral;
import com.autovend.devices.BillDispenser;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.BillDispenserController;
import com.autovend.software.controllers.BillPaymentController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CoinDispenserController;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CoinDispenserControllerTest {
	    SelfCheckoutStation selfCheckoutStation;
	    CheckoutController checkoutControllerStub;
	    CoinDispenser coinDispenserStub;
	    CoinDispenserController coinDispenserControllerStub;
	    BillPaymentController billPaymentControllerStub;
	    int[] billDenominations;
	    BigDecimal[] coinDenominations;
	    LinkedHashMap<Product, Number[]> order;

	    @Before
	    public void setup() {
	        // Init denominations
	        billDenominations = new int[] {5, 10, 20, 50, 100};
	        coinDenominations = new BigDecimal[] {new BigDecimal(0.05), new BigDecimal(0.1), new BigDecimal(0.25), new BigDecimal(100), new BigDecimal(200)};
	        BigDecimal denom =  new BigDecimal (0.01);
	        selfCheckoutStation = new SelfCheckoutStation(Currency.getInstance("CAD"), billDenominations, coinDenominations,200, 1);

	        checkoutControllerStub = new CheckoutController();
	        coinDispenserStub = new CoinDispenser(100);
	        coinDispenserControllerStub = new CoinDispenserController(coinDispenserStub, denom);
	        coinDispenserControllerStub.setMainController(checkoutControllerStub);
	        checkoutControllerStub.registerController("PaymentController", coinDispenserControllerStub);

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
	                BigDecimal.valueOf(9.95), 26.75);
	        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);
	        // make the smae for loop but for coin
	        int billCountToAdd = 100;
	        for (Map.Entry<Integer, BillDispenser> entry: selfCheckoutStation.billDispensers.entrySet()) {
	            int value = entry.getKey();
	            try {
	                for (int i = 0; i < billCountToAdd; i++) {
	                    entry.getValue().load(new Bill(value, Currency.getInstance("CAD")));
	                }
	            } catch (OverloadException e) {
	                throw new RuntimeException(e);
	            }
	        }
	        int coinToAdd = 100;
	        for (Map.Entry<BigDecimal, CoinDispenser> entry: selfCheckoutStation.coinDispensers.entrySet()) {
	            BigDecimal value = entry.getKey();
	            try {
	                for (int i = 0; i < coinToAdd; i++) {
	                    entry.getValue().load(new Coin(value, Currency.getInstance("CAD")));
	                }
	            } catch (OverloadException e) {
	                throw new RuntimeException(e);
	            }
	        }
	    }

	    @Test
		public void emitChangeForCoinDispenserTest() {
	    	BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.three));
	        order = new LinkedHashMap<>();
	        order.put(product, new Number[1]);

	        checkoutControllerStub.setOrder(order);
            BigDecimal denom = new BigDecimal (0.01);
	        try {

				selfCheckoutStation.coinSlot.accept(new Coin(denom, Currency.getInstance("CAD")));
	        } catch (Exception ex) {
	            System.out.printf("Exception " + ex.getMessage());
	        }
	    	
	    	
	        CoinDispenserController coinDispenserController = new CoinDispenserController(coinDispenserStub, denom);
	        coinDispenserController.emitChange();
	    	
	    	
	    }
	    
	    @Test
		public void emitChangeForCoinDispenserErrorTest() {
	    	BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.three));
	        order = new LinkedHashMap<>();
	        order.put(product, new Number[1]);

	        checkoutControllerStub.setOrder(order);
	        BigDecimal denom = new BigDecimal (0.01);

	    	
	        CoinDispenserController coinDispenserController = new CoinDispenserController(selfCheckoutStation.coinDispensers.get(1), denom);
	        coinDispenserController.setMainController(checkoutControllerStub);
	        for (int i = 0; i < 100; i++) {
	        	coinDispenserController.emitChange();
	            selfCheckoutStation.coinTray.collectCoins();
	        }
	    	
	        assertEquals(selfCheckoutStation.coinDispensers.size(), 0);
	    	
	    }
	    
	    @Test
		public void emitChangeForCoinDispenserIfStatementTest() {
	    	BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.three));
	        order = new LinkedHashMap<>();
	        order.put(product, new Number[1]);

	        checkoutControllerStub.setOrder(order);
	        BigDecimal denom = new BigDecimal (0.01);
	        try {
	            
				selfCheckoutStation.coinSlot.accept(new Coin(denom, Currency.getInstance("CAD")));
	        } catch (Exception ex) {
	            System.out.printf("Exception " + ex.getMessage());
	        }
	    	
	    	
	        for (int i = 0; i < 95; i++) {
		    	coinDispenserControllerStub.emitChange();
	            selfCheckoutStation.coinTray.collectCoins();
	        }
	    	
	        assertEquals(selfCheckoutStation.coinDispensers.get(10).size(), 5);
	    	
	    }
	    
	}

