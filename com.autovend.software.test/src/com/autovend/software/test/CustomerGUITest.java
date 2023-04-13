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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

 import com.autovend.PriceLookUpCodedUnit;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.devices.AbstractDevice;
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
 import com.autovend.software.controllers.CheckoutController;
 import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.controllers.DeviceController;
import com.autovend.software.controllers.ReceiptPrinterController;
import com.autovend.software.controllers.ReusableBagDispenserController;
import com.autovend.software.swing.AttendantEventSimulator;
import com.autovend.software.swing.AttendantLoginPane;
import com.autovend.software.swing.CustomerEventSimulator;
import com.autovend.software.swing.CustomerOperationPane;
import com.autovend.software.swing.CustomerStartPane;
 import com.autovend.software.swing.Language;
import com.autovend.software.utils.MiscProductsDatabase.Bag;
import com.autovend.software.utils.CardIssuerDatabases;


 @SuppressWarnings("serial")
public class CustomerGUITest {
 	private TouchScreen screen;
 	private boolean enabledEventOccurred = false;
 	private boolean disabledEventOccurred = false;

 	private CustomerStartPaneTest customerPane;
 	private JFrame customerScreen;
	private PLUCodedProduct pluCodedProduct1;
	private BarcodedProduct bcproduct1;
	
	CustomerIOController cioc;
	SupervisionStation attendantStation = new SupervisionStation();
	AttendantStationController asc = new AttendantStationController(attendantStation);
	AttendantIOController aioc = (AttendantIOController) asc.getAttendantIOControllers().iterator().next();

	ArrayList<CustomerIOController> ciocs = new ArrayList<>();
	
	
	private boolean invalidPLUDetected = false;
	private boolean PLUNotFound = false;
	private boolean negativeBagNumber = false;
	private boolean invalidBagNumber = false;
	private boolean weightDiscrepancy = false;
	
	
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
 		
 		@Override
 		public int showPopup(JPanel panel, String header) {
 			if (header == "Language Selection") {
 				for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
	                AbstractButton button = buttons.nextElement();
	                if (button.getText() == "English") {
	                    button.setSelected(true);
	                    break;
	                }
	            }
 			}
// 			else if (header == "Add Item by PLU Code") {
// 				pluCodeTextField.setText("1234");
// 				PLUenterButton.doClick();
// 			}
 			return 0;
 		}
 		
 		@Override
 		public int optionDialog(JPanel panel, String header) {
 			return 0;
 		}
 		@Override
 		public void showErrorMessage(String message) {
 			if (message.equals("PLU codes are only 4 or 5 numbers long! Please enter a valid PLU code.")) {
 				invalidPLUDetected = true;
 			}
 			else if (message.equals("That item was not found. Please enter a valid PLU code.")) {
 				PLUNotFound = true;
 			}
 			else if (message.equals("Invalid quantity. Please enter a non-negative integer.")) {
 				negativeBagNumber = true;
 			}
 			else if (message.equals("Invalid input. Please enter a non-negative integer.")) {
 				invalidBagNumber = true;
 			}
 		}
 		
 		@Override
 		public void showMessageDialog(JScrollPane scrollPane, String header) {
 			productList.setSelectedIndex(0);
 		}
 		
 		@Override
 		public void addOwnBagsOptionPane(JPanel panel) {
 			finishedAddOwnBagsButton.doClick();
 		}
 		
 		@Override
 		public void BaggingWeightProblemDialog(JPanel panel, String header) {
 			weightDiscrepancy = true;
 		}
 		
// 		@Override
// 		public int membershipDialog() {
// 			
// 			
// 		}
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
 		CardIssuerDatabases.MEMBERSHIP_DATABASE.put("12345", "Bob");
 		
 		
 		AttendantIOController aioc = new AttendantIOController(screen);
 		cioc = new CustomerIOController(customerStation.screen);
 		CheckoutController checkoutController = new CheckoutController(customerStation);
 		cioc.setMainController(checkoutController);
 		SupervisionStation supStation = new SupervisionStation();
 		AttendantStationController attendantController = new AttendantStationController(supStation);
 		attendantController.addStation(customerStation, cioc);
 		aioc.setMainAttendantController(attendantController);
 		checkoutController.setSupervisor(attendantController.getID());

 		customerPane = new CustomerStartPaneTest(cioc);
 		customerScreen.setContentPane(customerPane);


		 attendantController.registerUser("Test", "Test");
		 attendantController.login("Test","Test");
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

     
     public JButton getButton(String text, CustomerOperationPaneTest cop) {
    	 JButton button = null; 		
  		for (int i = 0; i<=cop.getComponentCount(); i++) {
  			Object comp = cop.getComponent(i);
  			if (comp.getClass() == JButton.class) {
  				button = (JButton) comp;
  				if (button.getText().equals(text)) {
  					return button;
  				}
  			}
  		}
		return null;
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

 	@Test
 	public void clickLanguageSelect() {
 		String language = customerPane.language;
 		JButton lsb = customerPane.languageSelectButton;
 		lsb.doClick();
 		assert(language == "English");
 	}

 	@Test
 	public void pressStartButton() {
 		JButton startButton = customerPane.startButton;
 		startButton.doClick();
 	}
 	
 	@Test
 	public void operationLanguageSelect() {
// 		JButton startButton = customerPane.startButton;
// 		startButton.doClick();
 		
 		JFrame frame = screen.getFrame();

 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		String language = cop.language;
 		JButton lsb = getButton("Select Language", cop);
 		lsb.doClick();
 		
 		assert(language == "English");
 		
 	}
 	
 	@Test
 	public void transparentPane() {
 		customerPane.initializeTransparentPane();
 		JLabel message = customerPane.disabledMessage;
 		String messageText = message.getText();
 		
 		assertEquals("Station disabled: waiting for attendant to enable", messageText);
 	}
 	
 	@Test
 	public void operationTransparentPane() {
// 		JButton startButton = customerPane.startButton;
// 		startButton.doClick();
 		
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		cop.initializeTransparentPane();
 		JLabel message = cop.disabledMessage;
 		String messageText = message.getText();
 		
 		assertEquals("Station disabled: waiting for attendant to enable", messageText);
 	}
 	
 	@Test
 	public void RefreshOrderGrid_AddByPLU() {
// 		JButton startButton = customerPane.startButton;
// 		startButton.doClick();
 		
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		cop.refreshOrderGrid();
		cioc.getMainController().checkoutStation.scale.add(new PriceLookUpCodedUnit(new PriceLookUpCode(Numeral.one,Numeral.two,Numeral.three,Numeral.four), 10.0));
		JButton addItemByPLUCodeButton = getButton("Add Item by PLU Code", cop);
		addItemByPLUCodeButton.doClick();

		DefaultTableModel model = cop.model;
 		String actualDescription = (String) model.getValueAt(0, 0);
 		BigDecimal actualPrice = (BigDecimal) model.getValueAt(0, 1);
 		Number actualQuantity = (Number) model.getValueAt(0, 2);

 		String expDescription = "apple";
		BigDecimal expPrice = BigDecimal.valueOf(8.90);
		BigDecimal expQuantity = BigDecimal.valueOf(10.0);

		//assertEquals(expDescription, actualDescription);
		assertEquals(expPrice, actualPrice.stripTrailingZeros());
		assertEquals(expQuantity, actualQuantity);

 		assertEquals(expDescription, actualDescription);
 	}
 	
 	@Test
 	public void RefreshOrderGrid_AddByBarcode() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		cioc.addProduct(bcproduct1);
 		cop.refreshOrderGrid();
 		
 		DefaultTableModel model = cop.model;
 		String actualDescription = (String) model.getValueAt(0, 0);
 		BigDecimal actualPrice = (BigDecimal) model.getValueAt(0, 1);
 		Number actualQuantity = (Number) model.getValueAt(0, 2);
 		
 		String expDescription = bcproduct1.getDescription();
 		BigDecimal expPrice = bcproduct1.getPrice();
 		Number expQuantity = (Number)1;
 		
 		assertEquals(expDescription, actualDescription);
 		assertEquals(expPrice, actualPrice);
 		assertEquals(expQuantity, actualQuantity);
 	}
 	
 	@Test
 	public void RefreshOrderGrid_AddBags() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		
 		
 		cioc.purchaseBags(1);
 		cop.refreshOrderGrid();
 		
 		DefaultTableModel model = cop.model;
 		String actualDescription = (String) model.getValueAt(0, 0);
 		BigDecimal actualPrice = (BigDecimal) model.getValueAt(0, 1);
 		Number actualQuantity = (Number) model.getValueAt(0, 2);
 		
 		String expDescription = "A reusable bag";
 		BigDecimal expPrice = BigDecimal.valueOf(0.5);
 		Number expQuantity = 1;
 		
 		assertEquals(expDescription, actualDescription);
 		assertEquals(expPrice, actualPrice);
 		assertEquals(expQuantity, actualQuantity);
 	}
 	
 	
 	@Test
 	public void UpdateTotalCost() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		cioc.addProduct(bcproduct1);
 		cioc.addItemByPLU("1234");
 		
 		cop.refreshOrderGrid();
 		JLabel totalCostLabel = cop.totalCostLabel;
 		String actualCost = totalCostLabel.getText();
 		String expCost = "Total Cost: $" + bcproduct1.getPrice();
 		
 		assertEquals(expCost, actualCost);
 	}
 	
 	@Test
 	public void AddItemByPLUCodeButton_ValidPLU() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);

		 cioc.getMainController().checkoutStation.scale.add(new PriceLookUpCodedUnit(new PriceLookUpCode(Numeral.one,Numeral.two,Numeral.three,Numeral.four), 10.0));
 		
 		JButton addItemByPLUCodeButton = getButton("Add Item by PLU Code", cop);
 		addItemByPLUCodeButton.doClick();
 		JPanel PluCodePanel = cop.PluCodePanel;
 		JTextField pluCodeTextField = cop.pluCodeTextField;
 		JButton PLUenterButton = cop.PLUenterButton;
 		pluCodeTextField.setText("1234");
 		PLUenterButton.doClick();
 		
 		cop.refreshOrderGrid();
 		DefaultTableModel model = cop.model;
 		String actualDescription = (String) model.getValueAt(0, 0);
 		BigDecimal actualPrice = (BigDecimal) model.getValueAt(0, 1);
 		Number actualQuantity = (Number) model.getValueAt(0, 2);
 		
 		String expDescription = "apple";
 		BigDecimal expPrice = BigDecimal.valueOf(8.90);
 		BigDecimal expQuantity = BigDecimal.valueOf(10.0);
 		
 		//assertEquals(expDescription, actualDescription);
 		assertEquals(expPrice, actualPrice.stripTrailingZeros());
 		assertEquals(expQuantity, actualQuantity);
 	}
 	
 	@Test
 	public void AddItemByPLUCodeButton_InvalidPLU() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		
 		JButton addItemByPLUCodeButton = getButton("Add Item by PLU Code", cop);
 		addItemByPLUCodeButton.doClick();
 		
 		JPanel PluCodePanel = cop.PluCodePanel;
 		JTextField pluCodeTextField = cop.pluCodeTextField;
 		JButton PLUenterButton = cop.PLUenterButton;
 		
 		pluCodeTextField.setText("1");
 		PLUenterButton.doClick();
 		
 		assertTrue(invalidPLUDetected);
 	}
 	
 	@Test
 	public void AddItemByPLUCodeButton_PLUNotFound() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		JButton addItemByPLUCodeButton = getButton("Add Item by PLU Code", cop);
 		addItemByPLUCodeButton.doClick();
 		
 		JPanel PluCodePanel = cop.PluCodePanel;
 		JTextField pluCodeTextField = cop.pluCodeTextField;
 		JButton PLUenterButton = cop.PLUenterButton;
 		
 		pluCodeTextField.setText("5555");
 		PLUenterButton.doClick();
 		
 		assertTrue(PLUNotFound);
 	}
 	
 	@Test
 	public void PurchaseBags() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		JButton purchaseBagsButton = getButton("Purchase Bags", cop);
 		purchaseBagsButton.doClick();
 		
 		JPanel purchaseBagsPanel = cop.purchaseBagsPanel;
 		JTextField bagQuantityTextField = cop.bagQuantityTextField;
 		JButton purchaseBagsEnterButton = cop.purchaseBagsEnterButton;
 		 		
 		bagQuantityTextField.setText("1");
 		purchaseBagsEnterButton.doClick();
 		
 		cop.refreshOrderGrid();
 		
 		DefaultTableModel model = cop.model;
 		String actualDescription = (String) model.getValueAt(0, 0);
 		BigDecimal actualPrice = (BigDecimal) model.getValueAt(0, 1);
 		Number actualQuantity = (Number) model.getValueAt(0, 2);
 		
 		String expDescription = "A reusable bag";
 		BigDecimal expPrice = BigDecimal.valueOf(0.5);
 		Number expQuantity = 1;
 		
 		assertEquals(expDescription, actualDescription);
 		assertEquals(expPrice, actualPrice);
 		assertEquals(expQuantity, actualQuantity);
 	}
 	
 	@Test
 	public void PurchaseBags_numberLessThanZero() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		JButton purchaseBagsButton = getButton("Purchase Bags", cop);
 		purchaseBagsButton.doClick();
 		
 		JPanel purchaseBagsPanel = cop.purchaseBagsPanel;
 		JTextField bagQuantityTextField = cop.bagQuantityTextField;
 		JButton purchaseBagsEnterButton = cop.purchaseBagsEnterButton;
 		
 		bagQuantityTextField.setText("-1");
 		purchaseBagsEnterButton.doClick();
 		
 		assertTrue(negativeBagNumber);
 	}
 	
 	@Test
 	public void PurchaseBags_invalidInput() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		JButton purchaseBagsButton = getButton("Purchase Bags", cop);
 		purchaseBagsButton.doClick();
 		
 		JPanel purchaseBagsPanel = cop.purchaseBagsPanel;
 		JTextField bagQuantityTextField = cop.bagQuantityTextField;
 		JButton purchaseBagsEnterButton = cop.purchaseBagsEnterButton;
 		
 		bagQuantityTextField.setText("a");
 		purchaseBagsEnterButton.doClick();
 		
 		assertTrue(invalidBagNumber);
 	}
 	
 	@Test
 	public void MembershipButton() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		JButton enterMembershipNumberButton = getButton("Enter Membership", cop);
 		enterMembershipNumberButton.doClick();
 		
 		cop.pluCodeTextField.setText("12345");;
 		cop.PLUenterButton.doClick();
 		
 	}
 	
 	@Test
 	public void AddItemByLookup() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		JButton addItemByLookupButton = getButton("Add Item by Lookup", cop);
 		addItemByLookupButton.doClick();
 		cop.refreshOrderGrid();
 		
 		DefaultTableModel model = cop.model;
 		String actualDescription = (String) model.getValueAt(0, 0);
 		BigDecimal actualPrice = (BigDecimal) model.getValueAt(0, 1);
 		Number actualQuantity = (Number) model.getValueAt(0, 2);
 		
 		Product selectedProduct = cop.selectedProduct;
 		BigDecimal expPrice = selectedProduct.getPrice();
 		
 		assertEquals(expPrice, actualPrice);
 	}
 	
 	@Test
 	public void addOwnBags() {
		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		JButton addOwnBagsButton = getButton("Add Own Bags", cop);
 		addOwnBagsButton.doClick();
 	}
 	
 	@Test
 	public void baggingPrompt() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		
 	}
 	
 	@Test
 	public void pay() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		JButton cashButton = getButton("Complete Payment", cop);
 		cashButton.doClick();
 	}
 	
 	@Test
 	public void BaggingWeightProblem() {
 		JFrame frame = screen.getFrame();
 		CustomerOperationPaneTest cop = new CustomerOperationPaneTest(cioc);
 		frame.setContentPane(cop);
 		
 		cop.createBaggingWeightProblemPopup();
 		assertTrue(weightDiscrepancy);
 	}
 	
	/**
	 * Tests the functionality of each button in CustomerEventSimulator
	 */
	@Test
	public void eventSimulatorTest() {
		
		ciocs.add(cioc);
		CustomerEventSimulator cesframe = new CustomerEventSimulator(aioc.getDevice().getFrame(),ciocs.get(0).getMainController().checkoutStation);
		cesframe.setVisible(true);
		cesframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		try {
			
			cesframe.scanItem.doClick();
			cesframe.scanItem2.doClick();
			cesframe.addBagging.doClick();
			cesframe.addScale.doClick();
			cesframe.addItem1Direct.doClick();
			cesframe.addItem2Direct.doClick();
			cesframe.scanMembership.doClick();
			cesframe.addPurchasedBags.doClick();
			cesframe.input5Bill.doClick();
			cesframe.inputCoin.doClick();
			cesframe.tapCard.doClick();
			cesframe.swipeCard.doClick();
			cesframe.rightPinCardInsert.doClick();
			cesframe.wrongPinCardInsert.doClick();
			cesframe.removeItems.doClick();
			cesframe.removeLatestFromBaggingArea.doClick();
			cesframe.giftCardPay.doClick();
			cesframe.removeChange.doClick();
			cesframe.removeReceipt.doClick();
			
		} catch (Exception e) {
			return;
		}
		
		fail("No exception expected");

	}

 	@After
 	public void tearDown() {
 		screen.disable();
 		screen = null;
 		customerPane = null;
 		cioc = null;
 	}
 }
