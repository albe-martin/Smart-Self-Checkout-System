package com.autovend.software.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.software.controllers.CustomerIOController;

/**
 * A class for  the customer start pane.
 */
public class CustomerOperationPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private CustomerIOController cioc;
	private String language = "English";
	private String[] languages = new String[] {"English", "French"};
	public JButton logoutButton;
	private JTable orderItemsTable;
	private JLabel totalCostLabel;
	private JButton languageSelectButton;

	/**
	 * TODO: Delete for final submission.
	 * 
	 * Quick GUI launcher. Used to allow window builder to work.
	 */
	public static void main(String[] args) {
		// Add French language.
		HashMap<String, String> french = new HashMap<>();
		french.put("Username:", "Le username:");
		french.put("Password:", "Le password:");
		french.put("Log In", "Le log in");
		french.put("Change Language", "Le Change Language");
		french.put("START", "LE START");
		Language.addLanguage("French", french);
				
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
		customerScreen.setContentPane(new CustomerOperationPane(cioc));
		
		customerScreen.setVisible(true);
	}
	
	/**
	 * Basic constructor.
	 * 
	 * @param cioc
	 * 			Linked CustomerIOController.
	 */
	public CustomerOperationPane(CustomerIOController cioc) {
		super();
		this.cioc = cioc;
		initializeOperationPane();
	}
	
	/**
	 * Initialize customer start pane.
	 */
	private void initializeOperationPane() {
		// Create operation screen pane.
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setLayout(null);
		initializeHeader();
        
        // TODO: Create cart functionalities
        initializeCartItemsGrid();

		initializeTotalCostLabel();

		initializeAddItemByPLUCodeButton();

		initializeAddItemByLookupCodeButton();

		initializePayForItemsButton();

		initializeEnterMembershipNumberButton();

		initializeLanguageSelectButton();

		initializeCallAttendantButton();

		// initializeLanguageSelectButton();


		// Initialize exit button.
		// TODO: Should have a confirmation popup (see the one I made for attendant notifyshutdownstationinuse).
        initializeExitButton();

		updateTotalCost();
        
	}

	private void initializeHeader() {
		JLabel selfCheckoutStationLabel = new JLabel("Self Checkout Station");
		selfCheckoutStationLabel.setBounds(0, 11, 800, 55);
		selfCheckoutStationLabel.setFont(new Font("Tahoma", Font.BOLD, 36));
		selfCheckoutStationLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(selfCheckoutStationLabel);
	}

	private void initializeCartItemsGrid() {
		String[] columnNames = {"Item", "Price"};
		// TODO: Get actual items in cart.
		Object[][] data = {
				{"Item 1", new BigDecimal("10.00")},
				{"Item 2", new BigDecimal("20.00")},
				{"Item 3", new BigDecimal("30.00")},
		};
		DefaultTableModel items = new DefaultTableModel(data, columnNames) {
			private static final long serialVersionUID = 1L;
			
			// Prevent user editing.
			public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		orderItemsTable = new JTable(items);
		orderItemsTable.setRowHeight(25);
		orderItemsTable.setRowSelectionAllowed(false);
		orderItemsTable.setRequestFocusEnabled(false);
		orderItemsTable.setFocusable(false);
		orderItemsTable.setShowGrid(true);
		

		JScrollPane scrollPane = new JScrollPane(orderItemsTable);
		scrollPane.setBounds(2, 64, 366, 501);

		add(scrollPane);
	}

	private void initializeTotalCostLabel() {
		totalCostLabel = new JLabel("Total Cost: $0.00");
		totalCostLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		totalCostLabel.setBounds(83, 576, 188, 30);
		totalCostLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(totalCostLabel);
	}

	private void addItemToGrid(String itemName, BigDecimal itemPrice) {
		DefaultTableModel model = (DefaultTableModel) orderItemsTable.getModel();
		model.addRow(new Object[]{itemName, itemPrice});
		updateTotalCost();
	}

	private void removeItemFromGrid(int rowIndex) {
		DefaultTableModel model = (DefaultTableModel) orderItemsTable.getModel();
		if (rowIndex >= 0 && rowIndex < model.getRowCount()) {
			model.removeRow(rowIndex);
			updateTotalCost();
		}
	}

	private void updateTotalCost() {
		BigDecimal totalCost = BigDecimal.ZERO;
		DefaultTableModel model = (DefaultTableModel) orderItemsTable.getModel();
		int rowCount = model.getRowCount();

		for (int i = 0; i < rowCount; i++) {
			BigDecimal itemPrice = (BigDecimal) model.getValueAt(i, 1);
			totalCost = totalCost.add(itemPrice);
		}

		totalCostLabel.setText("Total Cost: $" + totalCost.toString());
	}

	private void initializeEnterMembershipNumberButton() {
		JButton enterMembershipNumberButton = new JButton("Enter Membership \nNumber");
		enterMembershipNumberButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		enterMembershipNumberButton.setBounds(370, 663, 188, 76);
		add(enterMembershipNumberButton);
	}

	private void initializeAddItemByPLUCodeButton() {
		JButton addItemByPluCodeButton = new JButton("Add Item by PLU Code");
		addItemByPluCodeButton.setBounds(589, 236, 173, 60);
		add(addItemByPluCodeButton);
	}

	private void initializeAddItemByLookupCodeButton() {
		JButton addItemByLookupButton = new JButton("Add Item by Lookup");
		addItemByLookupButton.setBounds(382, 236, 188, 60);
		add(addItemByLookupButton);
	}


	private void initializePayForItemsButton() {
		JButton payForItemsButton = new JButton("Pay for Items");
		payForItemsButton.setBounds(480, 363, 173, 60);
		add(payForItemsButton);
	}

	private void initializeCallAttendantButton() {
		JButton callAttendantButton = new JButton("Call For Attendant");
		callAttendantButton.setBounds(83, 671, 173, 60);
		add(callAttendantButton);
	}

	private void initializeLanguageSelectButton() {

		JButton selectLanguageButton = new JButton("Select Language");
		selectLanguageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

				// Create a label for the language selection
				JLabel label = new JLabel("Select a language:");
				label.setAlignmentX(Component.CENTER_ALIGNMENT);
				panel.add(label);

				// Create a group of radio buttons for the available languages
				ButtonGroup group = new ButtonGroup();
				for (String language : languages) {
					JRadioButton radioButton = new JRadioButton(language);
					radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
					group.add(radioButton);
					panel.add(radioButton);
				}

				// Show the language selection dialog and get the selected language
				int result = JOptionPane.showOptionDialog(null, panel, "Language Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				if (result == JOptionPane.OK_OPTION) {
					String newLanguage = null;
					// Determine selected button's text
					for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements(); ) {
						AbstractButton button = buttons.nextElement();
						if (button.isSelected()) {
							newLanguage = button.getText();
							break;
						}
					}

//					if (newLanguage != null) {
//						// Update the language variable
//						language = newLanguage;
//
//						// Update texts to new language
//						notificationsLabel.setText(Language.translate(language, "Station Notifications:"));
//						manageEnabledLabel.setText(Language.translate(language, "Manage Enabled Stations:"));
//						manageDisabledLabel.setText(Language.translate(language, "Manage Disabled Stations:"));
//						logoutButton.setText(Language.translate(language, "Log Out"));
//						languageSelectButton.setText(Language.translate(language, "Change Language"));
//						populateManagementPanes();
//					}
				}
			}
		});
		selectLanguageButton.setBounds(589, 663, 173, 76);
		add(selectLanguageButton);
	}


//	public void initializeLanguageSelectButton() {
//		// Create language select button.
//		languageSelectButton = new JButton(Language.translate(language, "Change Language"));
//		languageSelectButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
//		languageSelectButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// Create a panel to hold the language select pop-up
//				JPanel panel = new JPanel();
//				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//				panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//				// Create a label for the language selection
//				JLabel label = new JLabel("Select a language:");
//				label.setAlignmentX(Component.CENTER_ALIGNMENT);
//				panel.add(label);
//
//				// Create a group of radio buttons for the available languages
//				ButtonGroup group = new ButtonGroup();
//				for (String language : languages) {
//					JRadioButton radioButton = new JRadioButton(language);
//					radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//					group.add(radioButton);
//					panel.add(radioButton);
//				}
//
//				// Show the language selection dialog and get the selected language
//				int result = JOptionPane.showOptionDialog(null, panel, "Language Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
//				if (result == JOptionPane.OK_OPTION) {
//					String newLanguage = null;
//					// Determine selected button's text
//					for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
//						AbstractButton button = buttons.nextElement();
//						if (button.isSelected()) {
//							newLanguage = button.getText();
//							break;
//						}
//					}
//					// TODO: Update relevant text fields/labels/buttons
////					if (newLanguage != null) {
////						// Update the language variable
////						language = newLanguage;
////
////						// Update texts to new language
////						notificationsLabel.setText(Language.translate(language, "Station Notifications:"));
////						manageEnabledLabel.setText(Language.translate(language, "Manage Enabled Stations:"));
////						manageDisabledLabel.setText(Language.translate(language, "Manage Disabled Stations:"));
////						logoutButton.setText(Language.translate(language, "Log Out"));
////						languageSelectButton.setText(Language.translate(language, "Change Language"));
////						populateManagementPanes();
////					}
//				}
//			}
//		});
//		languageSelectButton.setBounds(573, 661, 189, 76);
//		this.add(languageSelectButton);
//	}


	/**
	 * Initialize the exit button.
	 */
	private void initializeExitButton() {
		// Create exit button.
		logoutButton = new JButton("Exit");
        logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logout button pressed.

                // Notify controller that logout is requested.
                cioc.logoutPressed();
            }
        });
        logoutButton.setBounds(511, 503, 120, 63);
        this.add(logoutButton);
	}
}
