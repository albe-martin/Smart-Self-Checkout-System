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
