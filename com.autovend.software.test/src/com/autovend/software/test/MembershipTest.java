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
