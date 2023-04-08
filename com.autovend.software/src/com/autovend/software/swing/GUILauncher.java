package com.autovend.software.swing;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JFrame;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CustomerIOController;

public class GUILauncher {
	public static void main(String[] args) {
		// Add French language.
		HashMap<String, String> french = new HashMap<>();
		french.put("Username:", "Le username:");
		french.put("Password:", "Le password:");
		french.put("Log In", "Le log in");
		french.put("Change Language", "Le Change Language");
		french.put("START", "LE START");
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
		
		// Create checkout station.
		SelfCheckoutStation customerStation = new SelfCheckoutStation(Currency.getInstance(Locale.CANADA), 
				new int[] {1}, new BigDecimal[] {new BigDecimal(0.25)}, 100, 1);
		
		// Get and set up screen
		JFrame customerScreen = customerStation.screen.getFrame();
		customerScreen.setExtendedState(0);
		customerScreen.setSize(800, 800);
		customerScreen.setUndecorated(false);
		customerScreen.setResizable(false);
		
		CustomerIOController cioc = new CustomerIOController(customerStation.screen);
		customerScreen.setContentPane(new CustomerStartPane(cioc));
		
		customerScreen.setVisible(true);
	}
}
