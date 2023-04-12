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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.devices.TouchScreen;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.TouchScreenObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.BaggingScaleController;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.swing.AttendantLoginPane;
import com.autovend.software.swing.AttendantOperationPane;
import com.autovend.software.swing.CustomerStartPane;
import com.autovend.software.swing.Language;


public class CustomerGUITest {
	TouchScreen screen;
	CustomerIOController cioc;
	CustomerStartPaneTest customerPane;
	
	PLUCodedProduct pluCodedProduct1;
	
	boolean enabledEventOccurred = false;
	boolean disabledEventOccurred = false;
	
	/**
	 * Overrides the optionDialogPopup method of the AttendantLoginPane class
	 * to make it possible to test the language selection.
	 * @author omarkhan
	 */
	public class CustomerStartPaneTest extends CustomerStartPane {
		private static final long serialVersionUID = 1L;

		public CustomerStartPaneTest(CustomerIOController cioc) {
			super(cioc);
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
		
		SelfCheckoutStation customerStation = new SelfCheckoutStation(Currency.getInstance(Locale.CANADA), 
				new int[] {1}, new BigDecimal[] {new BigDecimal(0.25)}, 100, 1);
		
		
		//get and setup screen
		screen = customerStation.screen;
		
		JFrame customerScreen = screen.getFrame();
		customerScreen.setExtendedState(0);
		customerScreen.setSize(800, 800);
		customerScreen.setUndecorated(false);
		customerScreen.setResizable(false);
		
		cioc = new CustomerIOController(customerStation.screen);
		customerPane = new CustomerStartPaneTest(cioc);
		customerScreen.setContentPane(customerPane);
		
		CheckoutController customerStationController = new CheckoutController();
		cioc.setMainController(customerStationController);
		customerStationController.registerController("CustomerIOCOntroller", cioc);
		
		// Create demo products.
		BarcodedProduct bcproduct1 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "box of chocolates",
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
		String language = customerPane.language;
		JButton lsb = customerPane.languageSelectButton;
		lsb.doClick();
		
		assert(language == "English");
	}
}
