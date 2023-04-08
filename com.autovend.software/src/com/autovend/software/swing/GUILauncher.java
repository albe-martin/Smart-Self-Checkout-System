package com.autovend.software.swing;

import java.awt.Container;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JFrame;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;

public class GUILauncher {
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
		}
		
		// Shut down a station
		ciocs.get(1).getMainController().shutDown();
		
	}
}
