package com.autovend.software.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
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
		
		initializePurchaseBagsButton();

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
		JLabel selfCheckoutStationLabel = new JLabel("Self Checkout Station #" + cioc.getMainController().getID());
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
		DefaultTableModel items = new DefaultTableModel(null, columnNames) {
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

	public void refreshOrderGrid() {

		DefaultTableModel model = (DefaultTableModel) orderItemsTable.getModel();
		model.setRowCount(0);

		HashMap<Product, Number[]> orderItems = cioc.getMainController().getOrder();
		for (Map.Entry<Product, Number[]> entry : orderItems.entrySet()) {
			Product product = entry.getKey();
			if (product instanceof PLUCodedProduct pluProduct) {
				updateGrid(model, entry, pluProduct.getDescription(), pluProduct.getPrice());
			} else if (product instanceof BarcodedProduct barcodeProduct) {
				updateGrid(model, entry, barcodeProduct.getDescription(), barcodeProduct.getPrice());
			}
		}
		updateTotalCost();
	}

	private void updateGrid(DefaultTableModel model, Map.Entry<Product, Number[]> entry, String description, BigDecimal price) {
		Number[] quantities = entry.getValue();
		for (int i = 0; i < quantities.length; i++) {
			int quantity = quantities[i].intValue();
			if (quantity > 0) {
				model.addRow(new Object[]{description, price});
			}
		}
	}

	private void initializeTotalCostLabel() {
		totalCostLabel = new JLabel("Total Cost: $0.00");
		totalCostLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		totalCostLabel.setBounds(83, 576, 188, 30);
		totalCostLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(totalCostLabel);
	}

//	private void addItemToGrid(String itemName, BigDecimal itemPrice) {
//		DefaultTableModel model = (DefaultTableModel) orderItemsTable.getModel();
//		model.addRow(new Object[]{itemName, itemPrice});
//		updateTotalCost();
//	}
//
//	private void removeItemFromGrid(int rowIndex) {
//		DefaultTableModel model = (DefaultTableModel) orderItemsTable.getModel();
//		if (rowIndex >= 0 && rowIndex < model.getRowCount()) {
//			model.removeRow(rowIndex);
//			updateTotalCost();
//		}
//	}

	private void updateTotalCost() {
//		DefaultTableModel model = (DefaultTableModel) orderItemsTable.getModel();
//		int rowCount = model.getRowCount();
//
//		for (int i = 0; i < rowCount; i++) {
//			BigDecimal itemPrice = (BigDecimal) model.getValueAt(i, 1);
//			totalCost = totalCost.add(itemPrice);
//		}

		totalCostLabel.setText("Total Cost: $" + cioc.getMainController().getCost().toString());
	}

	private void initializeEnterMembershipNumberButton() {
		JButton enterMembershipNumberButton = new JButton("Enter Membership \nNumber");
		enterMembershipNumberButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cioc.beginSignInAsMember();
			}
		});
		enterMembershipNumberButton.setBounds(370, 663, 188, 76);
		add(enterMembershipNumberButton);
	}
	private void initializeAddItemByPLUCodeButton() {
		JButton addItemByPluCodeButton = new JButton("Add Item by PLU Code");
		addItemByPluCodeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAddItemByPLUCodePane();
			}
		});
		addItemByPluCodeButton.setBounds(589, 112, 173, 60);
		add(addItemByPluCodeButton);
	}
	private void initializeAddItemByLookupCodeButton() {
		JButton addItemByLookupButton = new JButton("Add Item by Lookup");
		addItemByLookupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//cioc.addProduct();
			}
		});
		addItemByLookupButton.setBounds(388, 112, 173, 60);
		add(addItemByLookupButton);
	}
	private void initializePayForItemsButton() {
		JButton payForItemsButton = new JButton("Pay for Items");
		payForItemsButton.setBounds(490, 351, 173, 60);
		add(payForItemsButton);
	}
	private void initializePurchaseBagsButton() {
		JButton purchaseBagsButton = new JButton("Purchase Bags");
		purchaseBagsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPurchaseBagsPane();
			}
		});
		purchaseBagsButton.setBounds(490, 251, 173, 60);
		add(purchaseBagsButton);
	}
	private void initializeCallAttendantButton() {
		JButton callAttendantButton = new JButton("Call For Attendant");
		callAttendantButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
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
				int result = JOptionPane.showOptionDialog(cioc.getDevice().getFrame(), panel, "Language Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
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

	private void showPurchaseBagsPane() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		panel.add(new JLabel("Please enter the number of bags you wish to purchase:"), gbc);

		JTextField bagQuantityTextField = new JTextField(10);
		gbc.gridx = 1;
		panel.add(bagQuantityTextField, gbc);

		JButton enterButton = new JButton("Enter");
		enterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int bagQuantity = Integer.parseInt(bagQuantityTextField.getText());

					if (bagQuantity < 0) {
						JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a non-negative integer.", "Error", JOptionPane.ERROR_MESSAGE);
					} else {
						// Add the purchased bags to the order.
						cioc.purchaseBags(bagQuantity);

						// Update the order grid to display the bags.
						refreshOrderGrid();

						JOptionPane.getRootFrame().dispose();
						System.out.println("Bags purchased: " + bagQuantity);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Invalid input. Please enter a non-negative integer.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		panel.add(enterButton, gbc);

		JOptionPane.showOptionDialog(cioc.getDevice().getFrame(), panel, "Purchase Bags", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
	}

	private void showAddItemByPLUCodePane() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		panel.add(new JLabel("Please enter the PLU code:"), gbc);

		JTextField pluCodeTextField = new JTextField(10);
		gbc.gridx = 1;
		panel.add(pluCodeTextField, gbc);

		JButton enterButton = new JButton("Enter");
		enterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String pluCode = pluCodeTextField.getText();
				Numeral[] numerals = new Numeral[pluCode.length()];

				for (int i = 0; i < pluCode.length(); i++) {
					byte b = Byte.parseByte(pluCode.substring(i, i + 1));
					numerals[i] = Numeral.valueOf(b);
				}

				PriceLookUpCode plu = new PriceLookUpCode(numerals);
				PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(plu);

				if (product == null) {
					JOptionPane.showMessageDialog(null, "Item not found. Please enter a valid PLU code.", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					cioc.addProduct(product);

					// Add the item to the grid.
					// addItemToGrid(product.getDescription(), product.getPrice());
					refreshOrderGrid();
					JOptionPane.getRootFrame().dispose();

					System.out.println("PLU coded product added");
				}
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		panel.add(enterButton, gbc);

		JOptionPane.showOptionDialog(cioc.getDevice().getFrame(), panel, "Add Item by PLU Code", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
	}


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
