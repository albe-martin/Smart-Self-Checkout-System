package com.autovend.software.swing;

import java.awt.EventQueue;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JFrame;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;

/**
 * Launches the customer and attendant GUIs.
 */
public class GUILauncher {
	
	/**
	 * Main runner.
	 */
	public static void main(String[] args) {
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
		SupervisionStation attendantStation = new SupervisionStation();
		
		// Get and set up screen
		JFrame attendantScreen = attendantStation.screen.getFrame();
		attendantScreen.setExtendedState(0);
		attendantScreen.setSize(800, 800);
		attendantScreen.setUndecorated(false);
		attendantScreen.setResizable(false);
		AttendantIOController aioc = new AttendantIOController(attendantStation.screen);
		attendantScreen.setContentPane(new AttendantLoginPane(aioc));
		
		AttendantStationController asc = new AttendantStationController();
		aioc.setMainAttendantController(asc);
		asc.registerController(aioc);
		
		// Add valid username and password.
		asc.registerUser("abc", "123");
		
		attendantScreen.setVisible(true);
		
		// Create list of checkout stations
		int num_stations = 2;
		ArrayList<CustomerIOController> ciocs = new ArrayList<>();
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
			CustomerIOController cioc = new CustomerIOController(customerStation.screen);
			cioc.setMainController(new CheckoutController());
			
			// Add to array
			ciocs.add(cioc);
			
			customerScreen.setContentPane(new CustomerStartPane(cioc));
			customerScreen.setVisible(true);
			
			// Register customer to attendant
			asc.registerController(cioc);
			cioc.getMainController().registerController("AttendantIOController", aioc);
		}
		
		// Shut down one station.
		ciocs.get(1).getMainController().shutDown();
		
		// Create demo products.
		BarcodedProduct bcproduct1 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "box of chocolates",
				BigDecimal.valueOf(83.29), 359.0);
		BarcodedProduct bcproduct2 = new BarcodedProduct(new Barcode(Numeral.four, Numeral.five), "screwdriver",
				BigDecimal.valueOf(42), 60.0);

		// Add demo products to database.
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bcproduct1.getBarcode(), bcproduct1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bcproduct2.getBarcode(), bcproduct2);

		PLUCodedProduct pluCodedProduct1 = new PLUCodedProduct(new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.four), "apple" , BigDecimal.valueOf(0.89));
		PLUCodedProduct pluCodedProduct2 = new PLUCodedProduct(new PriceLookUpCode(Numeral.four, Numeral.three, Numeral.two, Numeral.one), "banana" , BigDecimal.valueOf(0.82));
		PLUCodedProduct pluCodedProduct3 = new PLUCodedProduct(new PriceLookUpCode(Numeral.one, Numeral.one, Numeral.one, Numeral.one), "bunch of jabuticaba" , BigDecimal.valueOf(17.38));

		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCodedProduct1.getPLUCode(), pluCodedProduct1);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCodedProduct2.getPLUCode(), pluCodedProduct2);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCodedProduct3.getPLUCode(), pluCodedProduct3);
		
		// TODO: Can be removed if it conflicts with the customer testing. Just used for testing attendantIO.
		ciocs.get(0).addProduct(bcproduct1);
		
		
		
		
		// Run attendant event simulator.
		AttendantEventSimulator attendantEventSimulatorFrame = new AttendantEventSimulator(aioc.getDevice().getFrame(), ciocs.get(0), ciocs.get(1));
		attendantEventSimulatorFrame.setVisible(true);
		attendantEventSimulatorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
