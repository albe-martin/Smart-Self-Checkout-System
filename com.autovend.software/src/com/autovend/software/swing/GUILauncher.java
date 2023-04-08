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
		
		// Set up attendant screen
		SupervisionStation attendantStation = new SupervisionStation();
		JFrame attendantScreen = attendantStation.screen.getFrame();
		AttendantIOController aioc = new AttendantIOController(attendantStation.screen);
		attendantScreen.setContentPane(new AttendantLoginPane(aioc));
		
		AttendantStationController asc = new AttendantStationController();
		aioc.setMainAttendantController(asc);
		asc.registerController(aioc);
		
		// Add valid username and password.
		asc.registerUser("abc", "123");
		
		attendantScreen.setVisible(true);
		
		// Add French language.
		HashMap<String, String> french = new HashMap<>();
		french.put("Username:", "Le username:");
		french.put("Password:", "Le password:");
		french.put("Log In", "Le log in");
		french.put("Change Language", "Le Change Language");
		french.put("START", "LE START");
		Language.addLanguage("French", french);
	
		
		// Set up customer screen
		SelfCheckoutStation customerStation = new SelfCheckoutStation(Currency.getInstance(Locale.CANADA), 
				new int[] {1}, new BigDecimal[] {new BigDecimal(0.25)}, 100, 1);
		JFrame customerScreen = customerStation.screen.getFrame();
		CustomerIOController cioc = new CustomerIOController(customerStation.screen);
		customerScreen.setContentPane(new CustomerStartPane(cioc));
		
		customerScreen.setVisible(true);
	}
}
