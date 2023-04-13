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
import com.autovend.Numeral;
import com.autovend.devices.BillDispenser;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.BillDispenserController;
import com.autovend.software.controllers.BillPaymentController;
import com.autovend.software.controllers.ChangeDispenserController;
import com.autovend.software.controllers.CheckoutController;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class BillDispenserControllerTest {
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
                BigDecimal.valueOf(9.95), 26.75);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);

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
        
        result = false;
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
    public void partialPaymentChangeAndRemainingAmountTest() {
        // create order cart
        BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.four));
        order = new LinkedHashMap<>();
        order.put(product, new Number[1]);

        checkoutControllerStub.setOrder(order);

        try {
            selfCheckoutStation.billInput.accept(new Bill(20, Currency.getInstance("CAD")));
        } catch (Exception ex) {
            System.out.printf("Exception " + ex.getMessage());
        }

        BillDispenserController billDispenserController = new BillDispenserController(selfCheckoutStation.billDispensers.get(10), new BigDecimal(10));
        billDispenserController.emitChange();
        selfCheckoutStation.billOutput.removeDanglingBill();
        double amountRemaining = checkoutControllerStub.getRemainingAmount().doubleValue();
        double expextedAmount = new BigDecimal(-0.05).doubleValue();

        assertNotEquals(amountRemaining, expextedAmount);
    }

    @Test
    public void emptyBillDispenserTest() {
        // create order cart
        BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(new Barcode(Numeral.one, Numeral.four));
        order = new LinkedHashMap<>();
        order.put(product, new Number[1]);

        checkoutControllerStub.setOrder(order);

        BillDispenserController billDispenserController = new BillDispenserController(selfCheckoutStation.billDispensers.get(10), new BigDecimal(10));
        billDispenserController.setMainController(checkoutControllerStub);
        for (int i = 0; i < 100; i++) {
            billDispenserController.emitChange();
            selfCheckoutStation.billOutput.removeDanglingBill();
        }
        assertEquals(selfCheckoutStation.billDispensers.get(10).size(), 0);
    }

    @Test (expected = OverloadException.class)
    public void overfillBillDispenserTest() throws OverloadException {
        for (Map.Entry<Integer, BillDispenser> entry: selfCheckoutStation.billDispensers.entrySet()) {
            int value = entry.getKey();
            for (int i = 0; i < 2; i++) {
                entry.getValue().load(new Bill(value, Currency.getInstance("CAD")));
            }
        }
    }
    
    @Test
    public void testReactBillsFull() {
        BillDispenserController billDispenserController = new BillDispenserController(selfCheckoutStation.billDispensers.get(10), new BigDecimal(10));
        billDispenserController.reactToBillsFullEvent(null);
    }
    
    @Test
    public void testReactBillsLoaded() {
        BillDispenserController billDispenserController = new BillDispenserController(selfCheckoutStation.billDispensers.get(10), new BigDecimal(10));
        billDispenserController.reactToBillsLoadedEvent(null);
    }
    
    @Test
    public void testReactBillsAdded() {
        BillDispenserController billDispenserController = new BillDispenserController(selfCheckoutStation.billDispensers.get(10), new BigDecimal(10));
        billDispenserController.reactToBillAddedEvent(null, null);
    }
    
    @Test
    public void testReactBillsUnloaded() {
        BillDispenserController billDispenserController = new BillDispenserController(selfCheckoutStation.billDispensers.get(10), new BigDecimal(10));
        billDispenserController.reactToBillsUnloadedEvent(null);
    }
    
    @Test
    public void testEmitEmpty() {
        BillDispenser bd = new BillDispenser(1);
        BillDispenserController billDispenserController = new BillDispenserController(bd, BigDecimal.ONE);
        billDispenserController.setMainController(new CheckoutController() {
        	@Override
        	public void changeDispenseFailed(ChangeDispenserController controller, BigDecimal denom) {
        		result = true;
        	}
        });
        billDispenserController.emitChange();
        assertTrue(result);
    }
}
