package com.autovend.software.test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Numeral;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.TouchScreen;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.controllers.*;
import com.autovend.software.utils.MiscProductsDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddingOwnBagsTest {
    CheckoutController checkoutController;
    BaggingAreaController bagControl;
    AttendantIOController aioc;
    CustomerIOController cioc;
    AttendantStationController asc;
    BarcodedUnit ownBag = new BarcodedUnit(new Barcode(MiscProductsDatabase.bagNumb), 10.0);

    @Before
    public void setup(){
        asc = new AttendantStationController();
        aioc = new AttendantIOController(new TouchScreen());
        aioc.setMainAttendantController(asc);
        asc.registerUser("T", "U");
        aioc.login("T","U");


        checkoutController = new CheckoutController();
        bagControl = new BaggingScaleController(new ElectronicScale(1000, 1));
        cioc = new CustomerIOController(new TouchScreen());
        checkoutController.registerController(aioc.getTypeName(),aioc);
        cioc.setMainController(checkoutController);
        bagControl.setMainController(checkoutController);
        checkoutController.registerController(aioc.getTypeName(), aioc);
    }

    @After
    public void teardown() {
        asc=null;
        aioc=null;
        checkoutController=null;
        cioc=null;
    }


    @Test
    public void testAddBagsSuccessful(){
        cioc.addOwnBags();
        assertTrue(checkoutController.addingBagsLock);
        bagControl.getDevice().enable();
        ((ElectronicScale)bagControl.getDevice()).add(ownBag);
        assertTrue(checkoutController.addingBagsLock);
        cioc.notifyAttendantBagsAdded();
        aioc.approveAddedBags(checkoutController);
        assertFalse(checkoutController.addingBagsLock);
    }

    @Test
    public void testAddBagsCancel(){
        cioc.addOwnBags();
        assertTrue(checkoutController.addingBagsLock);
        cioc.cancelAddOwnBags();
        assertFalse(checkoutController.addingBagsLock);
    }
}
