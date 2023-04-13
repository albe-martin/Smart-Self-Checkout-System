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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.SupervisionStation;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.devices.TouchScreen;

public class LoginLogoutTest {
	SupervisionStation su;
	AttendantIOController attendantIOController;
	AttendantStationController attendantStationController;
	TouchScreen ts;
	CheckoutController checkoutController;
	public HashMap<String, String> credentials;
	private String username;
	private String password;
	
	/**
	 * Setup for testing
	 */
	@Before
	public void setup() {
		su = new SupervisionStation();
		ts = new TouchScreen();
		attendantStationController = new AttendantStationController();
		attendantIOController = new AttendantIOController(ts);
		checkoutController = new CheckoutController();
		checkoutController.registerController("AttendantIOController", attendantIOController);
		attendantIOController.setMainController(checkoutController);
		attendantIOController.setMainAttendantController(attendantStationController);
		
		
		credentials = new HashMap<String, String>();
		credentials.put("Bob", "pass123");
		credentials.put("Steve", "steve*123");
		
		
		
	}
	
	/**
	 * Tears down objects so they can be initialized again with setup
	 */
	@After
	public void teardown() {
		su = null;
		ts = null;
		attendantStationController = null;
		attendantIOController = null;
		checkoutController = null;
		credentials = null;
	}
	
	/**
	 * Tests that a user can correctly be registered into the system
	 */
	@Test
	public void testRegisterUser() {
		String username = "Maria";
		String password = "maria432";
		attendantStationController.registerUser(username, password);
		
		assertTrue(attendantStationController.getUsers().containsKey(username));
		
	}
	
	/**
	 * Tests that an error can correctly be thrown when a user already exists
	 */
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
	
	@Test
	public void testDeregisterUser() {
		String username = "Ana";
		String password = "seng123";
		credentials.put(username, password);
		attendantStationController.registerUser(username, password);
		
		attendantStationController.deregisterUser(username);
		String expectedOutput = "SUCCESS: User removed.";
		
		try {
			attendantStationController.deregisterUser(username);
			assertEquals(expectedOutput, "SUCCESS: User removed.");
		}
		catch (Exception ex) {
			fail("Exception incorrectly thrown");
		}
	}
	
	
	/**
	 * Tests that login works when person is registered
	 */
	@Test
	public void testLogin() {
		String username = "Ana";
		String password = "seng123";
		credentials.put(username, password);
		attendantStationController.registerUser(username, password);
		attendantIOController.login(username, password);
		attendantStationController.login(username, password);
		assertEquals("Ana", attendantStationController.getCurrentUser());
		
		
	}
	
	/**
	 * Tests that login will not work when using a different username
	 */
	@Test
	public void testLoginWrongPerson() {
		String username = "Ana";
		String password = "seng123";
		credentials.put(username, password);
		attendantStationController.registerUser(username, password);
		attendantIOController.login(username, password); //initializing login screen by logging in then out
		attendantIOController.logout();
		attendantStationController.login("Karen", password);

		assertEquals("", attendantStationController.getCurrentUser());
	}
	
	/**
	 * Tests that logout is properly working
	 */
	@Test
	public void testLogout() {
		String username = "Ana";
		String password = "seng123";
		
		credentials.put(username, password);
		attendantStationController.registerUser(username, password);
		attendantStationController.login(username, password);
		assertEquals("Ana", attendantStationController.getCurrentUser());
		attendantStationController.logout();
		attendantIOController.logout();
		assertEquals("", attendantStationController.getCurrentUser());
		
	}
	
	/**
	 * Tests that logout works when user is logged in
	 */
	@Test
	public void testLogoutwhenLoggedIn() {
		String username = "Ana";
		String password = "seng123";
		credentials.put(username, password);
		attendantStationController.registerUser(username, password);
		attendantIOController.login(username, password);
		
		attendantIOController.logout();
		attendantStationController.logout();
		assertFalse(attendantStationController.isLoggedIn());
	}
	

}
