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
 import javax.swing.JOptionPane;
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
import com.autovend.software.swing.CustomerOperationPane;
import com.autovend.software.swing.CustomerStartPane;
 import com.autovend.software.swing.Language;


 public class CustomerGUITest {
 	TouchScreen screen;
 	boolean enabledEventOccurred = false;
 	boolean disabledEventOccurred = false;
 	CustomerIOController cioc;
 	CustomerStartPaneTest customerPane;
 	JFrame customerScreen;
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
 	
 	public class CustomerOperationPaneTest extends CustomerOperationPane {
 		public CustomerOperationPaneTest(CustomerIOController cioc) {
 			super(cioc);
 		}
 	}
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

 		// Get and set up screen

 		screen = customerStation.screen;

 		customerScreen = customerStation.screen.getFrame();
 		customerScreen.setExtendedState(0);
 		customerScreen.setSize(800, 800);
 		customerScreen.setUndecorated(false);
 		customerScreen.setResizable(false);

 		cioc = new CustomerIOController(customerStation.screen);

 		CheckoutController controller = new CheckoutController();
 		cioc.setMainController(controller);
 		customerPane = new CustomerStartPaneTest(cioc);
 		customerScreen.setContentPane(customerPane);

 		//customerScreen.setVisible(true);
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

 	@Test
 	public void clickLanguageSelect() {
 		String language = customerPane.language;
 		JButton lsb = customerPane.languageSelectButton;
 		lsb.doClick();
 		assert(language == "English");
 	}

 	@Test
 	public void pressStartButtonTest() {
 		JButton startButton = customerPane.startButton;
 		startButton.doClick();
 	}
 	
 	@Test
 	public void operationLanguageSelectTest() {
 		JButton startButton = customerPane.startButton;
 		startButton.doClick();
 		
 		JFrame frame = screen.getFrame();

 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		String language = cop.language;
 		JButton lsb = cop.languageSelectButton;
 		lsb.doClick();
 		
 		assert(language == "English");
 		
 	}

 	@After
 	public void tearDown() {
 		screen.disable();
 	}
 }