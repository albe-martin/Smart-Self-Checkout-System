package com.autovend.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.SupervisionStation;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.devices.TouchScreen;

public class TestAttendentIOController {
	SupervisionStation su;
	AttendantIOController attendantIOController;
	AttendantStationController attendantStationController;
	TouchScreen ts;
	CheckoutController checkoutController;
	public HashMap<String, String> credentials;
	private String username;
	private String password;
	
	@Before
	public void setup() {
		su = new SupervisionStation();
		ts = new TouchScreen();
		attendantStationController = new AttendantStationController();
		attendantIOController = new AttendantIOController(ts);
		checkoutController = new CheckoutController();
		checkoutController.registerController("AttendantIOController", attendantIOController);
		attendantIOController.setMainController(checkoutController);
		
		
		credentials = new HashMap<String, String>();
		credentials.put("Bob", "pass123");
		credentials.put("Steve", "steve*123");
		
		
		
	}
	
	@Test
	public void testRegisterUser() {
		String username = "Maria";
		String password = "maria432";
		attendantStationController.registerUser(username, password);
		
		assertTrue(attendantStationController.getUsers().containsKey(username));
		
	}
	
	@Test
	public void testRegisterUserExists() {
		String username = "Steve";
		String password = "12345";
		credentials.put(username, password);
        attendantStationController.registerUser(username, "newPassword");
        String expectedOutput = "ERROR: Username already exists.";
        
        try {
        	attendantStationController.registerUser(username, "newPassword");
        	assertEquals(expectedOutput, "ERROR: Username already exists.");
        }
        catch (Exception ex) {
        	fail("Exception incorrectly thrown");
        }
		
	}
	
	public void testderegisterUser() {
		String username = "Steve";
		String password = "steve123";
		attendantStationController.re
	}

}
