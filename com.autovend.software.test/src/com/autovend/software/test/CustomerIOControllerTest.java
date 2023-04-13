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

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

public class CustomerIOControllerTest {
    SelfCheckoutStation testCheck;
    SupervisionStation testAttend;
    AttendantStationController acontrol;
    CheckoutController mcontrol;
    CustomerIOController cioc;
    AttendantIOController aioc;
    @Before
    public void setup() {
        int[] i1 = new int[1];
        i1[0] = 5;
        BigDecimal[] i2 = new BigDecimal[1];
        i2[0] = BigDecimal.ONE;
        testCheck = new SelfCheckoutStation(
                Currency.getInstance("USD"),
                i1, i2, 10000, 1);
        testAttend = new SupervisionStation();
        acontrol = new AttendantStationController(testAttend);

        mcontrol = new CheckoutController(testCheck);
        cioc = (CustomerIOController) mcontrol.getControllersByType("CustomerIOController").get(0);
        acontrol.addStation(testCheck, cioc);
        aioc = (AttendantIOController) acontrol.getAttendantIOControllers().iterator().next();
        mcontrol.registerController("AttendantIOController", aioc);
        acontrol.registerUser("T", "P");
        aioc.login("T", "P");
    }
    @After
    public void teardown(){
        testCheck=null;
        testAttend=null;
        acontrol=null;
        mcontrol=null;
        cioc=null;
        aioc=null;
    }


    @Test
    public void test(){

    }

}
