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

import java.awt.Component;
import java.awt.Container;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.autovend.devices.*;
import com.autovend.software.controllers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.TouchScreenObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.swing.AttendantEventSimulator;
import com.autovend.software.swing.AttendantLoginPane;
import com.autovend.software.swing.AttendantOperationPane;
import com.autovend.software.swing.CustomerStartPane;
import com.autovend.software.swing.Language;

public class AttendantGUITest {

	TouchScreen screen;

	AttendantStationController asc;
	SupervisionStation attendantStation;
	AttendantIOController aioc;
	CustomerIOController cioc;
	AttendantLoginPaneTest attendantPane;
	PLUCodedProduct pluCodedProduct1;
	BarcodedProduct bcproduct1;

	SelfCheckoutStation scs = new SelfCheckoutStation(Currency.getInstance(Locale.CANADA), 
			new int[] {1}, new BigDecimal[] {new BigDecimal(0.25)}, 100, 1);
	
	boolean enabledEventOccurred = false;
	boolean disabledEventOccurred = false;
	
	ArrayList<CustomerIOController> ciocs;
	
	/**
	 * Overrides the optionDialogPopup method of the AttendantLoginPane class
	 * to make it possible to test the language selection.
	 * @author omarkhan
	 */
	public class AttendantLoginPaneTest extends AttendantLoginPane {
		private static final long serialVersionUID = 1L;

		public AttendantLoginPaneTest(AttendantIOController aioc) {
			super(aioc);
		}
		
		@Override
		public int optionDialogPopup(JPanel panel) {
            for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.getText() == "English") {
                    button.setSelected(true);
                    break;
                }
            }
            
			return 0;
		}
	}
	

	/**
	 * Overrides the optionDialogPopup method and the yesNoPopup method of the 
	 * AttendantOperationPane class to make it possible to test the language selection
	 * and shutdown station use.
	 * @author omarkhan
	 */
	public class AttendantOperationPaneTest extends AttendantOperationPane {
		private static final long serialVersionUID = 1L;
		boolean searchAlreadyAttempted = false; // This is to test whether it attempts another search if no product is selected
		
		public AttendantOperationPaneTest(AttendantIOController aioc) {
			super(aioc);
		}
		
		@Override
		public int optionDialogPopup(JPanel panel, String header) {
			
			if (header == "Language Selection") {
	            for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
	                AbstractButton button = buttons.nextElement();
	                if (button.getText() == "English") {
	                    button.setSelected(true);
	                    break;
	                }
	            }
			} else if (header == "Action Selection") {
	            for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
	                AbstractButton button = buttons.nextElement();
	                if (button.getText() == "Startup Station" || button.getText() == "Disable Station" || button.getText() == "Enable Station") {
	                    button.setSelected(true);
	                    break;
	                }
	            }
			} else if (header == Language.translate(language, "No Products Found")) {
				return -1;
			} else if (header == Language.translate(language, "Add Item By Text Search")) {
				setItemSearch();
			} else if (header == "Choose found product") {

	            for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
	                AbstractButton button = buttons.nextElement();
					if (searchAlreadyAttempted) {
		                button.setSelected(true);
					} else {
						searchAlreadyAttempted = true;
					}
	                break;
	            }
	            
			} else if (header == Language.translate(language, "Remove Item")) {
	            for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
	                AbstractButton button = buttons.nextElement();
                    button.setSelected(true);
                    break;
	            }
			}
			
			return 0;
		}
		
		@Override
		public int yesNoPopup(JPanel panel) {
				return 0; // Simulates click on "yes"
		}
		
		public void setItemSearch() {
			searchField.setText("");
		}
	}
	
	/**
	 * Overrides the setItem method AttendantOperationPane class to make it possible to 
	 * test noFoundProductsPopup
	 * @author omarkhan
	 */
	public class AttendantOperationPaneTest2 extends AttendantOperationPaneTest {
		private static final long serialVersionUID = 1L;

		public AttendantOperationPaneTest2(AttendantIOController aioc) {
			super(aioc);
		}
		
		@Override
		public void setItemSearch() {
			searchField.setText("A thousand and fourteen rocks"); // Random text that doesn't show results in the search
		}
		
	}
	
	
	// Stub for TouchScreenObserver
	TouchScreenObserver tso = new TouchScreenObserver() {
		@Override
		public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
			enabledEventOccurred = true;
		}

		@Override
		public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
			disabledEventOccurred = true;
		}
    };
	
    @Before
    public void setup() {
		// Add French language.
		// TODO: This is just a demo. The submission will only be in english.
		// TODO: However, testing will need to create and use a french demo (just for a couple labels to ensure it works).
		HashMap<String, String> french = new HashMap<>();
		french.put("Username:", "Le username:");
		french.put("Password:", "Le password:");
		french.put("Log In", "Le log in");
		french.put("Change Language", "Le Change Language");
		french.put("START", "LE START");
		french.put("Station Notifications:", "Le Station Notifications:");
		french.put("Manage Enabled Stations:", "Le Manage Enabled Stations");
		french.put("Manage Disabled Stations:", "Le Manage Disabled Stations:");
		french.put("Log Out", "Le Log Out");
		french.put("Station", "Le Station");
		french.put("Change Language", "Le Change Language");
		Language.addLanguage("French", french);
		
		// Create attendant station.
		attendantStation = new SupervisionStation();
		
		// Get and set up screen
		
		screen = attendantStation.screen;
		
		JFrame attendantScreen = screen.getFrame();
		attendantScreen.setExtendedState(0);
		attendantScreen.setSize(800, 800);
		attendantScreen.setUndecorated(false);
		attendantScreen.setResizable(false);
		asc = new AttendantStationController(attendantStation);

		aioc = (AttendantIOController) asc.getAttendantIOControllers().iterator().next();
		attendantPane = new AttendantLoginPaneTest(aioc);
		attendantScreen.setContentPane(attendantPane);

		// Add valid username and password.
		asc.registerUser("abc", "123");
		
//		attendantScreen.setVisible(true);
		
		// Create list of checkout stations
		int num_stations = 2;
		ciocs = new ArrayList<>();
		for (int i = 0; i < num_stations; i++) {
			SelfCheckoutStation customerStation = new SelfCheckoutStation(Currency.getInstance(Locale.CANADA), 
					new int[] {1}, new BigDecimal[] {new BigDecimal(0.25)}, 100, 1);
			
			// Get and set up screen
			JFrame customerScreen = customerStation.screen.getFrame();
			customerScreen.setExtendedState(0);
			customerScreen.setSize(800, 800);
			customerScreen.setUndecorated(false);
			customerScreen.setResizable(false);
			
			// Create controller
			cioc = new CustomerIOController(customerStation.screen);
			cioc.setMainController(new CheckoutController());
			
			// Add to array
			ciocs.add(cioc);
			
			customerScreen.setContentPane(new CustomerStartPane(cioc));
//			customerScreen.setVisible(true);
			
			// Register customer to attendant
			asc.registerController(cioc);
		}
		
		// Shut down a station
		ciocs.get(1).getMainController().shutDown();
		
		// Create demo products.
		bcproduct1 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "box of chocolates",
				BigDecimal.valueOf(83.29), 359.0);
		BarcodedProduct bcproduct2 = new BarcodedProduct(new Barcode(Numeral.four, Numeral.five), "screwdriver",
				BigDecimal.valueOf(42), 60.0);

		// Add demo products to database.
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bcproduct1.getBarcode(), bcproduct1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bcproduct2.getBarcode(), bcproduct2);

		pluCodedProduct1 = new PLUCodedProduct(new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.four), "apple" , BigDecimal.valueOf(0.89));
		PLUCodedProduct pluCodedProduct2 = new PLUCodedProduct(new PriceLookUpCode(Numeral.four, Numeral.three, Numeral.two, Numeral.one), "banana" , BigDecimal.valueOf(0.82));
		PLUCodedProduct pluCodedProduct3 = new PLUCodedProduct(new PriceLookUpCode(Numeral.one, Numeral.one, Numeral.one, Numeral.one), "bunch of jabuticaba" , BigDecimal.valueOf(17.38));
		
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCodedProduct1.getPLUCode(), pluCodedProduct1);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCodedProduct2.getPLUCode(), pluCodedProduct2);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCodedProduct3.getPLUCode(), pluCodedProduct3);
    }
    
    @After
    public void tearDown() {
		screen.disable();
		screen=null;
		asc=null;
		attendantStation=null;
		aioc=null;
		cioc=null;
		attendantPane=null;
    }
    

	/**
	 * Tests the observer stub to make sure that the screen gets enabled and disabled.
	 */
	@Test
	public void enableDisableScreenTest() {
        screen.register(tso);
        
		screen.disable();
		screen.enable();
		
		assert(enabledEventOccurred && disabledEventOccurred);
	}
	

	/**
	 * Tests the functionality of the change language use case in the login screen
	 */
	@Test
	public void loginLanguageSelectTest() {
		String language = attendantPane.language;
		JButton lsb = attendantPane.languageSelectButton;
		lsb.doClick();
		
		assert(language == "English");
	}
	
	/**
	 * Tests to make sure that a login with incorrect credentials is unsuccessful
	 */
	@Test
	public void loginFailureTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("wrong"); // Wrong login credentials
		passwordTF.setText("wrong");	
		loginButton.doClick();
		
		int numberOfComponents = screen.getFrame().getContentPane().getComponentCount();
		// If the number of components is not greater than 7, that is evidence that the pane is still the login screen
		assert(numberOfComponents <= 7);
	}
	
	/**
	 * Tests to make sure that a login with correct credentials is successful
	 */
	@Test
	public void loginSuccessTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
		
		int numberOfComponents = screen.getFrame().getContentPane().getComponentCount();
		
		// If the number of components is greater than 7, that is evidence that the pane is no longer the login screen
		assert(numberOfComponents > 7);
	}
	
	/**
	 * Tests the functionality of log out button
	 */
	@Test
	public void logoutTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
		
		AttendantOperationPane aop = (AttendantOperationPane) screen.getFrame().getContentPane();
		JButton logout = aop.logoutButton;
		logout.doClick();
		
		int numberOfComponents = screen.getFrame().getContentPane().getComponentCount();

		// If the number of components is not greater than 7, that is evidence that the pane is still the login screen
		assert(numberOfComponents <= 7);
	}
	
	/**
	 * Tests the functionality of the change language use case in the operation screen
	 */
	@Test
	public void operationLanguageSelectTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
		
		JFrame frame = screen.getFrame();
		
		AttendantOperationPaneTest aop = new AttendantOperationPaneTest(aioc);
		frame.setContentPane(aop);
		
		String language = aop.language;
		JButton lsb = aop.languageSelectButton;
		lsb.doClick();
		
		assert(language == "English");
	}
	
	/**
	 * Tests creation of a bag request event in the GUI
	 */
	@Test
	public void bagRequestTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
				
		AttendantOperationPane aop = (AttendantOperationPane) screen.getFrame().getContentPane();
		aop.notifyConfirmAddedBags(cioc.getMainController());
		aop.button.doClick();		
	}
	
	/**
	 * Tests creation of a low bills event in the GUI
	 */
	@Test
	public void lowCoinsTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
				
		AttendantOperationPane aop = (AttendantOperationPane) screen.getFrame().getContentPane();
		aop.notifyLowCoinDenomination(cioc.getMainController(), new BigDecimal("0.25"));
		aop.button.doClick();	
	}
	
	/**
	 * Tests creation of a low bills event in the GUI
	 */
	@Test
	public void lowBillTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
				
		AttendantOperationPane aop = (AttendantOperationPane) screen.getFrame().getContentPane();
		aop.notifyLowBillDenomination(cioc.getMainController(), new BigDecimal("5"));
		aop.button.doClick();		
	}
	
	/**
	 * Tests creation of a low ink event in the GUI
	 */
	@Test
	public void lowInkTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
				
		AttendantOperationPane aop = (AttendantOperationPane) screen.getFrame().getContentPane();
		aop.notifyLowInk(cioc.getMainController(), null);
		aop.button.doClick();	
	}
	
	/**
	 * Tests the resolution of a low ink event in the GUI
	 */
	@Test
	public void resolveLowInkTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
				
		((AttendantOperationPane) screen.getFrame().getContentPane()).notifyLowInk(cioc.getMainController(), null);
		((AttendantOperationPane) screen.getFrame().getContentPane()).notifyLowInkResolved(cioc.getMainController());
	}

	/**
	 * Tests creation on a low paper event in the GUI
	 */
	@Test
	public void lowPaperTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
				
		AttendantOperationPane aop = (AttendantOperationPane) screen.getFrame().getContentPane();
		aop.notifyLowPaper(cioc.getMainController(), null);
		aop.button.doClick();	}
	
	/**
	 * Tests the resolution of a no bag event in the GUI
	 */
	@Test
	public void noBagTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
				
		AttendantOperationPane aop = (AttendantOperationPane) screen.getFrame().getContentPane();
		aop.notifyNoBag(cioc.getMainController());
		aop.button.doClick();
	}
	
	/**
	 * Tests the resolution of a weight discrepancy event in the GUI
	 */
	@Test
	public void weightDiscrepancyTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
				
		AttendantOperationPane aop = (AttendantOperationPane) screen.getFrame().getContentPane();
		aop.notifyWeightDiscrepancy(cioc.getMainController());
		aop.button.doClick();	}
	
	/**
	 * Tests the resolution of a receipt reprint event in the GUI
	 */
	@Test
	public void receiptReprintTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();

		ReceiptPrinter testPrinter = new ReceiptPrinter();
		try {
			testPrinter.addInk(1000);
			testPrinter.addPaper(1000);
		} catch (OverloadException e) {
			throw new RuntimeException(e);
		}
		ReceiptPrinterController testPrinterController = new ReceiptPrinterController(testPrinter);
		asc.registerController(testPrinterController);
		testPrinterController.addedPaper(1000);
		testPrinterController.addedInk(1000);


		StringBuilder receipt = new StringBuilder();
		AttendantOperationPane aop = (AttendantOperationPane) screen.getFrame().getContentPane();
		aop.notifyReceiptRePrint(cioc.getMainController(), receipt);
		aop.button.doClick();	
	}
	
	/**
	 * Tests the resolution of a low paper event in the GUI
	 */
	@Test
	public void resolveLowPaperTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
				
		((AttendantOperationPane) screen.getFrame().getContentPane()).notifyLowPaperResolved(cioc.getMainController());

	}
	
	/**
	 * Tests the startup event in the GUI
	 */
	@Test
	public void startupTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
				
		((AttendantOperationPane) screen.getFrame().getContentPane()).notifyStartup(cioc.getMainController());
		

	}
	
	/**
	 * Tests the shutdown event in the GUI, then simulates "yes" click on popup
	 */
	@Test
	public void shutdownTestYes() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
						
		JFrame frame = screen.getFrame();
		AttendantOperationPaneTest aop = new AttendantOperationPaneTest(aioc);
		frame.setContentPane(aop);
		
		aop.notifyShutdownStationInUse(cioc.getMainController());
	}
	

	
	/**
	 * Tests the functionality of addIssue method
	 */
	@Test
	public void addIssues() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
		
		AttendantOperationPane operationPane = (AttendantOperationPane) screen.getFrame().getContentPane();
		int visibleIssues = operationPane.activeIssuesPane.getComponentCount();
				
		String issue = "issue";
		operationPane.activeIssues.add(issue);
		operationPane.populateActiveIssuesPane();
		int newVisibleIssues = operationPane.activeIssuesPane.getComponentCount();
		
		assert(newVisibleIssues == visibleIssues + 1); // assert that the new issue is showing up in the GUI
	}
	
	/**
	 * Tests the functionality of recieveMessage method
	 */
	@Test
	public void recieveMessageTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
		
		AttendantOperationPane operationPane = (AttendantOperationPane) screen.getFrame().getContentPane();
		int oldCount = operationPane.notificationsData.size();
		
		operationPane.receiveMessage("message");
		int newCount = operationPane.notificationsData.size();
		
		assert(newCount == oldCount + 1); // assert that the new message is showing up in the GUI
		
		operationPane.button.doClick();
	}
	
	/**
	 * Tests the functionality of the addShutdownActionPopop case in the operation screen
	 */
	@Test
	public void shutdownActionPopupTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
		
		JFrame frame = screen.getFrame();
		AttendantOperationPaneTest aop = new AttendantOperationPaneTest(aioc);

		frame.setContentPane(aop);
		
		JButton btn = new JButton();
		aop.add(btn);
		aop.addShutdownActionPopup(btn, cioc.getMainController());
		btn.doClick();
	}
	
	/**
	 * Tests the functionality of the addDisabledActionPopup case in the operation screen
	 */
	@Test
	public void disabledActionPopupTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
		
		JFrame frame = screen.getFrame();
		AttendantOperationPaneTest aop = new AttendantOperationPaneTest(aioc);

		frame.setContentPane(aop);
		
		JButton btn = new JButton();
		aop.add(btn);
		aop.addEnabledActionPopup(btn, cioc.getMainController()); // disabling the station to so I can test enabling it
		btn.doClick();
		
		aop.addDisabledActionPopup(btn, cioc.getMainController());
		btn.doClick();
	}
	
	/**
	 * Tests the functionality of the addensabledActionPopup case in the operation screen
	 */
	@Test
	public void enabledActionPopupTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
		
		JFrame frame = screen.getFrame();
		AttendantOperationPaneTest aop = new AttendantOperationPaneTest(aioc);

		frame.setContentPane(aop);
		
		JButton btn = new JButton();
		aop.add(btn);
		aop.addEnabledActionPopup(btn, cioc.getMainController());
		btn.doClick();
	}
	
	/**
	 * Tests the functionality of the createTextSearchPopup case in the operation screen,
	 * with a blank search input
	 */
	@Test
	public void createBlankTextSearchPopupTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
		
		JFrame frame = screen.getFrame();
		AttendantOperationPaneTest aop = new AttendantOperationPaneTest(aioc);

		frame.setContentPane(aop);
		
		aop.createTextSearchPopup(cioc.getMainController());
	}
	
	/**
	 * Tests the functionality of the createTextSearchPopup case in the operation screen,
	 * with a non-blank search input
	 */
	@Test
	public void createNonBlankTextSearchPopupTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
		
		JFrame frame = screen.getFrame();
		AttendantOperationPaneTest aop = new AttendantOperationPaneTest2(aioc);

		frame.setContentPane(aop);
		
		aop.createTextSearchPopup(cioc.getMainController());
	}
	
	/**
	 * Tests the functionality of the createRemoveItemPopup case in the operation screen
	 */
	@Test
	public void createRemoveItemPopupTest() {
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();
		
		JFrame frame = screen.getFrame();
		AttendantOperationPaneTest aop = new AttendantOperationPaneTest(aioc);

		frame.setContentPane(aop);
		
//		aop.createTextSearchPopup(cioc.getMainController()); 
		
		aioc.getCart(cioc.getMainController()).put(bcproduct1, new Number[] {10, 10}); // Add barcoded item and PLU item
		aioc.getCart(cioc.getMainController()).put(pluCodedProduct1, new Number[] {10, 10});
		aop.createRemoveItemPopup(cioc.getMainController()); // Remove the items
		aop.createRemoveItemPopup(cioc.getMainController());

	}
	

	/**
	 * Tests the functionality of each button in AttendantEventSimulator
	 */
	@Test
	public void eventSimulatorTest() {
		
        JButton loginButton = attendantPane.loginButton;
		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		usernameTF.setText("abc"); // Correct login credentials
		passwordTF.setText("123");	
		loginButton.doClick();

		ReceiptPrinterController rpc = new ReceiptPrinterController(scs.printer);
		CheckoutController cc = new CheckoutController();
		rpc.setMainController(cc);
		ciocs.get(0).getMainController().checkoutStation = scs;
		
		// Run attendant event simulator.
		AttendantEventSimulator aesframe = new AttendantEventSimulator(aioc.getDevice().getFrame(), ciocs.get(0).getMainController(), ciocs.get(0).getMainController());
		aesframe.setVisible(true);
		aesframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		try {
			aesframe.bagRequest1.doClick();
			aesframe.bagRequest2.doClick();
			aesframe.lowChange1.doClick();
			aesframe.lowChange2.doClick();
			aesframe.lowBill1.doClick();
			aesframe.lowBill2.doClick();
			aesframe.lowink1.doClick();
			aesframe.lowink2.doClick();
			aesframe.lowinkResolve1.doClick();
			aesframe.lowinkResolve2.doClick();
			aesframe.lowPaper1.doClick();
			aesframe.lowPaper2.doClick();
			aesframe.lowPaperResolve1.doClick();
			aesframe.lowPaperResolve2.doClick();
			aesframe.noBag1.doClick();
			aesframe.noBag2.doClick();
			aesframe.weight1.doClick();
			aesframe.weight2.doClick();
			aesframe.reprint1.doClick();
			aesframe.reprint2.doClick();
			aesframe.fillBagsResolve1.doClick();
			aesframe.fillBagsResolve2.doClick();
		} catch (Exception e) {
			return;
		}
		
		fail("No exception expected");

	}
}
