package com.autovend.software.test;

import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

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

import com.autovend.devices.AbstractDevice;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.devices.TouchScreen;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.TouchScreenObserver;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.swing.AttendantLoginPane;
import com.autovend.software.swing.CustomerStartPane;
import com.autovend.software.swing.Language;

public class AttendantGUITest {

    private volatile int found;
	TouchScreen screen;
	AttendantLoginPaneTest attendantPane;
	
	boolean enabledEventOccurred = false;
	boolean disabledEventOccurred = false;
	
	
	/**
	 * Overrides the optionDialogPopup method of the AttendantLoginPane class
	 * to make it possible to test the language selection.
	 * @author omarkhan
	 *
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
		SupervisionStation attendantStation = new SupervisionStation();
		
		// Get and set up screen
		
		screen = attendantStation.screen;
		
		JFrame attendantScreen = screen.getFrame();
		attendantScreen.setExtendedState(0);
		attendantScreen.setSize(800, 800);
		attendantScreen.setUndecorated(false);
		attendantScreen.setResizable(false);
		
		AttendantIOController aioc = new AttendantIOController(attendantStation.screen);
		attendantPane = new AttendantLoginPaneTest(aioc);
		attendantScreen.setContentPane(attendantPane);
		
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
	 * 
	 */
	@Test
	public void clickLanguageSelect() {

		String language = attendantPane.language;
		JButton lsb = attendantPane.languageSelectButton;
		lsb.doClick();
		
		assert(language == "English");
	}
	
	/**
	 * Tests to make sure that a login with incorrect credentials is unsuccessful
	 */
	@Test
	public void TestLoginFailure() {
        JFrame frame = screen.getFrame();
        
        JButton loginButton = attendantPane.loginButton;
		JLabel usernameLabel = attendantPane.usernameLabel;
		JLabel passwordLabel = attendantPane.passwordLabel;

		JTextField usernameTF = attendantPane.usernameTextField;
		JPasswordField passwordTF = attendantPane.passwordTextField;
		
		JLabel errorLabel = attendantPane.errorLabel;
		JButton lsb = attendantPane.languageSelectButton;
		
		String usernameText = usernameLabel.getText();
		String passwordText = passwordLabel.getText();
		
		usernameTF.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e);
				
			}
			
		});
		
		usernameTF.setText("abc");
		passwordTF.setText("123");

		
		usernameTF.registerKeyboardAction(null, null, found);
		
	}
	
	/**
	 * Tests to make sure that a login with correct credentials is successful
	 */
	@Test
	public void TestLoginSuccess() {
		
	}

}
