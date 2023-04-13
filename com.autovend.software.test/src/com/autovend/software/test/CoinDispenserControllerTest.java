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

import com.autovend.Barcode;
import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.Numeral;
import com.autovend.devices.BillDispenser;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.BillDispenserController;
import com.autovend.software.controllers.BillPaymentController;
import com.autovend.software.controllers.ChangeDispenserController;
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
import static org.junit.Assert.assertTrue;

public class CoinDispenserControllerTest {
    SelfCheckoutStation selfCheckoutStation;
    CheckoutController checkoutControllerStub;
    CoinDispenser coinDispenserStub;
    CoinDispenserController coinDispenserControllerStub;
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
        
        result = false;
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
    	
    	
        for(Map.Entry<BigDecimal, CoinDispenser> entry : selfCheckoutStation.coinDispensers.entrySet()) {
        	
	        CoinDispenserController coinDispenserController = new CoinDispenserController(entry.getValue(), denom);
	        coinDispenserController.emitChange();
        }
        

    	
    	
    }
    
    @Test
	public void emitChangeForCoinDispenserErrorTest() {
    	BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.three));
        order = new LinkedHashMap<>();
        order.put(product, new Number[1]);

        checkoutControllerStub.setOrder(order);
        BigDecimal denom = new BigDecimal (0.01);

    	

        
        for (int i = 0; i < 100; i++) {
	        for(Map.Entry<BigDecimal, CoinDispenser> entry : selfCheckoutStation.coinDispensers.entrySet()) {
		        CoinDispenserController coinDispenserController = new CoinDispenserController(entry.getValue(), denom);
		        coinDispenserController.setMainController(checkoutControllerStub);

		        coinDispenserController.emitChange();
	        }
	        

            selfCheckoutStation.coinTray.collectCoins();
        }
    	
        Assert.assertNotEquals(selfCheckoutStation.coinDispensers.size(), 0);
    	
    }
    
    @Test
	public void emitChangeForCoinDispenserIfStatementTest() {
    	BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.three));
        order = new LinkedHashMap<>();
        order.put(product, new Number[1]);

        checkoutControllerStub.setOrder(order);
        BigDecimal denom = new BigDecimal (0.01);
        for (int i = 0; i < 95; i++) {
	        for(Map.Entry<BigDecimal, CoinDispenser> entry : selfCheckoutStation.coinDispensers.entrySet()) {
		        CoinDispenserController coinDispenserController = new CoinDispenserController(entry.getValue(), denom);
		        coinDispenserController.setMainController(checkoutControllerStub);

		        coinDispenserController.emitChange();
	        }
	        

            selfCheckoutStation.coinTray.collectCoins();
        }
        assertEquals(selfCheckoutStation.coinDispensers.size(), 5);
    	
    }
    
    @Test
    public void testSetDenom() {
    	coinDispenserControllerStub.setDenom(BigDecimal.ONE);
    }
    
    @Test
    public void testCompareTo() {
    	coinDispenserControllerStub.compareTo(coinDispenserControllerStub);
    }
    
    private boolean eventTriggered;
	@Test
	public void constructortest() {
		Coin c=new Coin(BigDecimal.valueOf(0.25),Currency.getInstance("CAD"));
		CoinDispenser cd= new CoinDispenser(10);
		CoinDispenserController cdc=new CoinDispenserController(cd, BigDecimal.ONE);
	}
	@Test
	public void reacttocoinfull() {
		Coin c=new Coin(BigDecimal.ONE,Currency.getInstance("CAD"));
		CoinDispenser cd= new CoinDispenser(2);
		
		try {
			cd.load(c);
			cd.load(c);
		} catch (SimulationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CoinDispenserController cdc=new CoinDispenserController(cd, BigDecimal.ONE);
		cdc.reactToCoinsFullEvent(cd);
	}
	@Test
	public void reacttocoinadd() {
		Coin c=new Coin(BigDecimal.ONE,Currency.getInstance("CAD"));
		CoinDispenser cd= new CoinDispenser(2);
		
		try {
			cd.load(c);
			cd.load(c);
		} catch (SimulationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CoinDispenserController cdc=new CoinDispenserController(cd, BigDecimal.ONE);
		cdc.reactToCoinAddedEvent(cd, c);
	}
	@Test
	public void reacttocoinremove() {
		Coin c=new Coin(BigDecimal.ONE,Currency.getInstance("CAD"));
		CoinDispenser cd= new CoinDispenser(2);
		
		try {
			cd.load(c);
			cd.load(c);
		} catch (SimulationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CoinDispenserController cdc=new CoinDispenserController(cd, BigDecimal.ONE);
		cdc.reactToCoinRemovedEvent(cd, c);
	}
	@Test
	public void reacttocoinempty() {
		Coin c=new Coin(BigDecimal.ONE,Currency.getInstance("CAD"));
		CoinDispenser cd= new CoinDispenser(2);
		
		try {
			cd.load(c);
			cd.load(c);
		} catch (SimulationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CoinDispenserController cdc=new CoinDispenserController(cd, BigDecimal.ONE);
		cdc.reactToCoinsEmptyEvent(cd);
	}
	@Test
	public void reacttocoinloaded() {
		Coin c=new Coin(BigDecimal.ONE,Currency.getInstance("CAD"));
		CoinDispenser cd= new CoinDispenser(2);
		
		try {
			cd.load(c);
			cd.load(c);
		} catch (SimulationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CoinDispenserController cdc=new CoinDispenserController(cd, BigDecimal.ONE);
		cdc.reactToCoinsLoadedEvent(cd, c);	
		}
	@Test
	public void reacttocoinunloaded() {
		Coin c=new Coin(BigDecimal.ONE,Currency.getInstance("CAD"));
		CoinDispenser cd= new CoinDispenser(2);
		
		try {
			cd.load(c);
			cd.load(c);
		} catch (SimulationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CoinDispenserController cdc=new CoinDispenserController(cd, BigDecimal.ONE);
		cdc.reactToCoinsUnloadedEvent(cd, c);
		}
	@Test
	public void emittest() {
		Coin c=new Coin(BigDecimal.ONE,Currency.getInstance("CAD"));
		CoinDispenser cd= new CoinDispenser(2);
		CoinDispenserController cdc=new CoinDispenserController(cd, BigDecimal.ONE);
		cdc.setDevice(cd);
		cdc.enableDevice();
		CheckoutController che=new CheckoutController() {
			@Override
			public void changeDispenseFailed(ChangeDispenserController controller, BigDecimal denom) {
				result = true;
			}
		};
		cdc.setMainController(che);
		cdc.emitChange();
		assertTrue(result);
	}
}

