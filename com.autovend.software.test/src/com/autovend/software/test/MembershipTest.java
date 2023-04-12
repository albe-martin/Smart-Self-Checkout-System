package com.autovend.software.test;

import static org.junit.Assert.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.BarcodeScanner;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.utils.CardIssuerDatabases;
import com.autovend.software.controllers.CardReaderController;
import com.autovend.devices.CardReader;
import com.autovend.devices.TouchScreen;


/*
 * Test the membership use cases work as expected 
 */
public class MembershipTest {
	private CustomerIOController customerController;
	private BarcodeScanner stubScanner;
	private CardReader stubCardReader;
	private CardReaderController cardReaderController;

	private CheckoutController checkoutController;
	private BarcodeScannerController barcodeScannerController;
	private String membershipID;
	private TouchScreen stubDevice;
	
	@Before
    public void setup(){
        stubScanner = new BarcodeScanner();
        stubCardReader = new CardReader();
    	stubDevice = new TouchScreen();
        checkoutController = new CheckoutController();
        cardReaderController = new CardReaderController(stubCardReader);
        cardReaderController.setMainController(checkoutController);
        checkoutController.registerController("ValidPaymentControllers", cardReaderController);
        
        barcodeScannerController = new BarcodeScannerController(stubScanner);
        barcodeScannerController.setMainController(checkoutController);
        checkoutController.registerController("ItemAdderController", barcodeScannerController);
    
		customerController = new CustomerIOController(stubDevice);
		customerController.setMainController(checkoutController);
		checkoutController.registerController("CustomerIOController", customerController);
    }
	
	@After
	public void tearDown() {
		stubScanner = null;
		stubCardReader = null;
		cardReaderController = null;
		checkoutController = null;
		barcodeScannerController = null;
		membershipID = null;
	}
	

	@Test
	public void TestsigningInAsMember() {
		checkoutController.signingInAsMember();
		assertNotNull(cardReaderController.state);
		assertFalse(barcodeScannerController.getScanningItems());
		
	}
	
	@Test
	public void TestvalidateMembership() {
		membershipID = "123456789";
		CardIssuerDatabases.MEMBERSHIP_DATABASE.put(membershipID, "some member");
		checkoutController.validateMembership(membershipID);
	    assertNotNull(cardReaderController.state);
		assertTrue(barcodeScannerController.getScanningItems());

	}
	
	
	@Test
	public void TestCancelSignIn() {
		checkoutController.cancelSigningInAsMember();
		assertTrue(barcodeScannerController.getScanningItems());
		assertNotNull(cardReaderController.state);
	}
	
	@Test
	public void TestInValidMembership() {
		membershipID = "999999999";
		checkoutController.signingInAsMember();
		checkoutController.validateMembership(membershipID);
		assertFalse(barcodeScannerController.getScanningItems());
		assertNotNull(cardReaderController.state);


	}

}
