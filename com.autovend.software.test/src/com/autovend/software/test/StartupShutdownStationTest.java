package com.autovend.software.test;

import static org.junit.Assert.assertEquals;
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
	
	@After
	public void teardown() {
		checkoutController = null;
		asc = null;
		aioc = null;
		
	}
	
	/**
	 * Tess startup station when a user is logged in
	 * @throws SimulationException
	 * 		If there is no user logged in
	 */
	@Test (expected = SimulationException.class)
	public void testStartupStationLoggedIn() {
		username = "Ana";
		password = "seng123";
		credentials.put(username, password);
		asc.registerUser(username, password);
		asc.login(username, password);
		
		aioc.startupStation(checkoutController);
		
	}
	
	@Test (expected = SimulationException.class)
	public void testStartupStationNotLoggedIn() {
		username = "Ana";
		password = "seng123";
		credentials.put(username, password);
		asc.registerUser(username, password);
		asc.login(username, password);
		asc.logout();
		
		aioc.startupStation(checkoutController);
		
	}
	@Test (expected = SimulationException.class)
	public void testShutdownStation() {
		username = "Ana";
		password = "seng123";
		credentials.put(username, password);
		asc.registerUser(username, password);
		asc.login(username, password);
		
		aioc.shutdownStation(checkoutController);
	}
	
	@Test (expected = SimulationException.class)
	public void testShutdownStationUse() {
		username = "Ana";
		password = "seng123";
		credentials.put(username, password);
		asc.registerUser(username, password);
		asc.login(username, password);
		System.out.println(checkoutController.isInUse());
	}
	
	@Test (expected = SimulationException.class)
	public void testForceShutdownStation() {
		username = "Ana";
		password = "seng123";
		credentials.put(username, password);
		asc.registerUser(username, password);
		asc.login(username, password);
		
		aioc.forceShutDownStation(checkoutController);
		
	}
}
