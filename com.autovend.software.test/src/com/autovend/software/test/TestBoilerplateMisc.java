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

import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.devices.*;
import com.autovend.software.controllers.*;
import com.autovend.software.controllers.CustomerIOController;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * This class is just used to clear through a bunch of methods
 * which are required for interface specs which we don't care about or use.

 */
public class TestBoilerplateMisc {
    @Test
    public void TestMisc(){
        int[] i1 = new int[1];
        i1[0]=5;
        BigDecimal[] i2 = new BigDecimal[1];
        i2[0]=BigDecimal.ONE;
        SelfCheckoutStation testCheck = new SelfCheckoutStation(
                Currency.getInstance("USD"),
                i1, i2,10000, 1);
        SupervisionStation testAttend = new SupervisionStation();
        AttendantStationController acontrol= new AttendantStationController(testAttend);

        CheckoutController mcontrol = new CheckoutController(testCheck);
        CustomerIOController cioc = (CustomerIOController) mcontrol.getControllersByType("CustomerIOController").get(0);
        acontrol.addStation(testCheck, cioc);
        AttendantIOController aioc =(AttendantIOController) acontrol.getAttendantIOControllers().iterator().next();
        mcontrol.registerController("AttendantIOController", aioc);

        acontrol.registerUser("T", "P");
        aioc.login("T","P");

        cioc.startPressed();

        Bill bill1 = new Bill(5, Currency.getInstance("USD"));
        Coin coin1 = new Coin(BigDecimal.ONE, Currency.getInstance("USD"));
        try {
            testCheck.billInput.accept(bill1);
            testCheck.billOutput.accept(bill1);
            testCheck.billOutput.removeDanglingBill();
            testCheck.billOutput.emit(bill1);
            testCheck.billOutput.removeDanglingBill();

            testCheck.coinTray.accept(coin1);
            testCheck.billDispensers.get(5).load(bill1);
            testCheck.billDispensers.get(5).unload();
            CoinDispenserController coincont;
            ChangeDispenserController dispCont = (ChangeDispenserController)mcontrol.getAllDeviceControllersRevised().get("ChangeDispenserController").get(0);
            if (((ChangeDispenserController<?, ?>) dispCont).denom.equals(BigDecimal.ONE)) {
                coincont = (CoinDispenserController) dispCont;
                dispCont = (ChangeDispenserController)mcontrol.getAllDeviceControllersRevised().get("ChangeDispenserController").get(1);

            } else {
                coincont = (CoinDispenserController) mcontrol.getAllDeviceControllersRevised().get("ChangeDispenserController").get(1);;
            }

            BillDispenserController bdspCont = (BillDispenserController) dispCont;
            bdspCont.reactToBillAddedEvent(null, bill1);
            bdspCont.reactToBillsFullEvent(null);
            try {
                (bdspCont).emitChange();
            } catch (Exception ex) {
            }
            mcontrol.enableStation();
            try {
                for (int ii = 0; ii < 10000000; ii++) {
                    testCheck.billDispensers.get(5).load(bill1);
                }
            } catch (Exception ex) {
            }
            try {
                for (int ii = 0; ii < 1000; ii++) {
                    bdspCont.emitChange();
                }
            } catch (Exception ex) {

            }
            mcontrol.enableStation();
            bdspCont.setDenom(BigDecimal.valueOf(5));

            coincont.reactToCoinAddedEvent(null, coin1);
            coincont.reactToCoinRemovedEvent(null, coin1);
            coincont.reactToCoinsEmptyEvent(null);
            coincont.reactToCoinsUnloadedEvent(null, coin1);
            coincont.reactToCoinsLoadedEvent(null, coin1);
            coincont.reactToCoinsFullEvent(null);


        } catch (OverloadException e) {
            throw new RuntimeException(e);
        }

    }

}
