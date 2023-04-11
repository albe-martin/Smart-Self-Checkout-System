package com.autovend.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.SimulationException;
import com.autovend.devices.SupervisionStation;
import com.autovend.devices.TouchScreen;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CheckoutController;

public class StartupShutdownStationTest {
	SupervisionStation su;
	CheckoutController checkoutController;
	AttendantStationController asc;
	AttendantIOController aioc;
	TouchScreen ts;
	String username;
	String password;
	public HashMap<String, String> credentials;
	
	/**
	 * Set up of objects and variables, this happens before tests
	 */
	@Before
	public void setup() {
		su = new SupervisionStation();
		ts = new TouchScreen();
		asc = new AttendantStationController();
		aioc = new AttendantIOController(ts);
		checkoutController = new CheckoutController();
		checkoutController.registerController("AttendantIOController", aioc);
		aioc.setMainController(checkoutController);
		aioc.setMainAttendantController(asc);
		
		
		credentials = new HashMap<String, String>();
		credentials.put("Bob", "pass123");
		credentials.put("Steve", "steve*123");
	}
	
	/**
	 * Tears down objects so they can be initialized again with setup
	 */
	@After
	public void teardown() {
		checkoutController = null;
		asc = null;
		aioc = null;
		
	}
	
	/**
	 * Test startup station when a user is logged in
	 */
	@Test
	public void testStartupStationLoggedIn() {
		username = "Ana";
		password = "seng123";
		credentials.put(username, password);
		asc.registerUser(username, password);
		asc.login(username, password);
		aioc.startupStation(checkoutController);
		
		assertFalse(checkoutController.isShutdown());
		
	}
	
	/**
	 * Tests startup station when a user is not logged in
	 */
	@Test
	public void testStartupStationNotLoggedIn() {
		username = "Ana";
		password = "seng123";
		credentials.put(username, password);
		asc.registerUser(username, password);
		asc.login(username, password);
		asc.logout();
		
		aioc.startupStation(checkoutController);
		assertFalse(checkoutController.isShutdown());

		
	}
	
	/**
	 * Tests shut down station
	 */
	@Test
	public void testShutdownStation() {
		username = "Ana";
		password = "seng123";
		credentials.put(username, password);
		asc.registerUser(username, password);
		asc.login(username, password);
		
		aioc.shutdownStation(checkoutController);
		
		assertTrue(checkoutController.isShutdown());
	}
	
	/**
	 * Tests shut down station when it is still in use
	 */
	@Test
	public void testShutdownStationUse() {
		username = "Ana";
		password = "seng123";
		credentials.put(username, password);
		asc.registerUser(username, password);
		asc.login(username, password);
		checkoutController.setInUse(true);
		aioc.shutdownStation(checkoutController);
		
		assertTrue(checkoutController.isInUse());
	}
	
	/**
	 * Tests force shut down station when logged in
	 */
	@Test
	public void testForceShutdownStation() {
		username = "Ana";
		password = "seng123";
		credentials.put(username, password);
		asc.registerUser(username, password);
		asc.login(username, password);
		
		aioc.forceShutDownStation(checkoutController);
		
		assertTrue(checkoutController.isShutdown());
		
	}
	
	/**
	 * Tests force shut down station when not logged in
	 */
	@Test
	public void testForceShutdownStationNotLoggedIn() {
		username = "Ana";
		password = "seng123";
		credentials.put(username, password);
		asc.registerUser(username, password);
		asc.login(username, password);
		asc.logout();
		aioc.forceShutDownStation(checkoutController);
		
		assertFalse(checkoutController.isShutdown());
	}
}
