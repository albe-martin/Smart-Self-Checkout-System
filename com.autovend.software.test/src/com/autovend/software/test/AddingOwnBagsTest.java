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
    
    @Test
    public void testAttendantApproveWithoutAddingBagsLock() {
    	aioc.approveAddedBags(checkoutController);
        assertFalse(checkoutController.addingBagsLock);
    }
    
    @Test
    public void testCancelAddBagsWithoutBagsAdded() {
    	cioc.cancelAddOwnBags();
    	assertFalse(checkoutController.addingBagsLock);
    	assertFalse(checkoutController.baggingItemLock);
    }
    
    @Test
    public void testCancelAddBagsNoBagController() {
    	checkoutController.deregisterController("BaggingAreaController", bagControl);
    	cioc.cancelAddOwnBags();
    	assertFalse(checkoutController.addingBagsLock);
    	assertTrue(checkoutController.baggingItemLock);
    }
    
    @Test
    public void testCancelAddBagsBaggingValid() {
    	bagControl.setBaggingValid(true);
    	cioc.cancelAddOwnBags();
    	assertFalse(checkoutController.addingBagsLock);
    	assertTrue(checkoutController.baggingItemLock);
    }
}
