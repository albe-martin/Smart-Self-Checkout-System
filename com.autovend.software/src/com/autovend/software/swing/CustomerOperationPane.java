package com.autovend.software.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.CardReaderControllerState;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.utils.MiscProductsDatabase;

/**
 * A class for  the customer start pane.
 */
public class CustomerOperationPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private CustomerIOController cioc;
	private final ArrayList<String> languages = Language.languages;
	public String language = Language.defaultLanguage;

	public JButton logoutButton;
	private JTable orderItemsTable;
	private JLabel totalCostLabel;
	private JButton languageSelectButton;
	private JPanel glassPane;

	/**
	 * TODO: Delete for final submission.
	 * <p>
	 * Quick GUI launcher. Used to allow window builder to work.
	 */
	public static void main(String[] args) {
		// Create checkout station.
		SelfCheckoutStation customerStation = new SelfCheckoutStation(Currency.getInstance(Locale.CANADA),
				new int[]{1}, new BigDecimal[]{new BigDecimal(0.25)}, 100, 1);

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
	 * @param cioc Linked CustomerIOController.
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

		initializeTransparentPane();

		this.add(glassPane);

		initializeHeader();

		// TODO: Create cart functionalities
		initializeCartItemsGrid();

		initializeTotalCostLabel();

		initializeAddItemByPLUCodeButton();

		initializeAddItemByLookupCodeButton();

		initializePurchaseBagsButton();

		initializeAddOwnBagsButton();

//		initializePayForItemsButton();
        
        initializeCashButton();
        initializeCreditButton();
        initializeDebitButton();
        initializeGiftCardButton();

		initializeEnterMembershipNumberButton();

		initializeLanguageSelectButton();

		// initializeCallAttendantButton();

		// initializeLanguageSelectButton();


		// TODO: Should have a confirmation popup (see the one I made for attendant notifyshutdownstationinuse).

		// initializeExitButton();

		refreshOrderGrid();

	}

	private void initializeHeader() {
		JLabel selfCheckoutStationLabel = new JLabel(Language.translate(language, "Self Checkout Station #") + cioc.getMainController().getID());
		selfCheckoutStationLabel.setBounds(0, 11, 800, 55);
		selfCheckoutStationLabel.setFont(new Font("Tahoma", Font.BOLD, 36));
		selfCheckoutStationLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(selfCheckoutStationLabel);
	}

	private void initializeCartItemsGrid() {
		String[] columnNames = {Language.translate(language, "Item"), Language.translate(language, "Price"), Language.translate(language, "Qty")};
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

		int tableWidth = orderItemsTable.getPreferredSize().width;
		TableColumnModel columnModel = orderItemsTable.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(tableWidth / 2);
		columnModel.getColumn(1).setPreferredWidth(tableWidth / 4);
		columnModel.getColumn(2).setPreferredWidth(tableWidth / 4);

		JScrollPane scrollPane = new JScrollPane(orderItemsTable);
		scrollPane.setBounds(2, 64, 366, 501);

		add(scrollPane);
	}

	public void refreshOrderGrid() {
		DefaultTableModel model = (DefaultTableModel) orderItemsTable.getModel();
		model.setRowCount(0);

		HashMap<Product, Number[]> orderItems = cioc.getCart();
//		System.out.println("\n\n" + orderItems.entrySet());
		System.out.println(cioc.getMainController().getOrder());
		for (Map.Entry<Product, Number[]> entry : orderItems.entrySet()) {
			Product product = entry.getKey();
			System.out.println("refresh loop product: " + product);
			if (product instanceof PLUCodedProduct pluProduct) {
				updateGrid(model, entry, pluProduct.getDescription(), pluProduct.getPrice());
			} else if (product instanceof BarcodedProduct barcodeProduct) {
				updateGrid(model, entry, barcodeProduct.getDescription(), barcodeProduct.getPrice());
			} else if (product instanceof MiscProductsDatabase.Bag bagProduct){
				updateGrid(model, entry, "bag(s)", bagProduct.getPrice());
			}

		}

		// todo: actually get the right bag number and not reading the console??? (???) ((???))
		// Add purchased bags to the order grid
		// int bagQuantity = cioc.getMainController().getBagNumber();


		// todo: Not sure where to get the bag price from
		BigDecimal bagPrice = new BigDecimal("0.10");

//		if (bagQuantity > 0) {
//			model.addRow(new Object[]{"Bags", bagPrice.multiply(BigDecimal.valueOf(bagQuantity))});
//		}

		updateTotalCost();
	}

	private void updateGrid(DefaultTableModel model, Map.Entry<Product, Number[]> entry, String description, BigDecimal price) {
		Number[] quantities = entry.getValue();
		Number quantity = quantities[0];
//		System.out.println(description);
//		System.out.println(Arrays.toString(quantities));

		model.addRow(new Object[]{description, price, quantity});

//		for (int i = 0; i < quantities.length; i++) {
//			int quantity = quantities[i].intValue();
//			if (quantity > 0) {
//				model.addRow(new Object[]{description, price});
//			}
//		}
	}

	private void initializeTotalCostLabel() {
		totalCostLabel = new JLabel("Total Cost: $0.00");
		totalCostLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		totalCostLabel.setBounds(83, 576, 188, 30);
		totalCostLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(totalCostLabel);
	}
	private void updateTotalCost() {
//		DefaultTableModel model = (DefaultTableModel) orderItemsTable.getModel();
//		int rowCount = model.getRowCount();
//
//		for (int i = 0; i < rowCount; i++) {
//			BigDecimal itemPrice = (BigDecimal) model.getValueAt(i, 1);
//			totalCost = totalCost.add(itemPrice);
//		}

		//System.out.println(cioc.getCart());
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

	private void initializePurchaseBagsButton() {
		JButton purchaseBagsButton = new JButton("Purchase Bags");
		purchaseBagsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPurchaseBagsPane();
			}
		});
		purchaseBagsButton.setBounds(589, 230, 173, 60);
		add(purchaseBagsButton);
	}

	private void initializeAddOwnBagsButton() {
		JButton purchaseBagsButton = new JButton("Add Own Bags");
		purchaseBagsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cioc.addOwnBags();
				showAddOwnBagsPane();
			}
		});
		purchaseBagsButton.setBounds(388, 230, 173, 60);
		add(purchaseBagsButton);
	}

	private void initializeCashButton() {
		// Create pay with cash button.
		JButton cashButton = new JButton("Pay with Cash");
		cashButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: pay with cash
			}
		});
		cashButton.setBounds(490, 351, 173, 60);
		add(cashButton);
	}

	private void initializeCreditButton() {
		// Create pay with credit button.
		JButton cashButton = new JButton("Pay with Credit");
		cashButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cioc.choosePayByBankCard(CardReaderControllerState.PAYINGBYCREDIT, null, cioc.getMainController().getRemainingAmount());
			}
		});
		cashButton.setBounds(490, 411, 173, 60);
		add(cashButton);
	}

	private void initializeDebitButton() {
		// Create pay with debit button.
		JButton cashButton = new JButton("Pay with Debit");
		cashButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cioc.choosePayByBankCard(CardReaderControllerState.PAYINGBYDEBIT, null, cioc.getMainController().getRemainingAmount());
			}
		});
		cashButton.setBounds(490, 471, 173, 60);
		add(cashButton);
	}

	private void initializeGiftCardButton() {
		// Create pay with gift card button.
		JButton cashButton = new JButton("Pay with Gift Card");
		cashButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cioc.choosePayByGiftCard();
			}
		});
		cashButton.setBounds(490, 531, 173, 60);
		add(cashButton);
	}

//	private void initializeCallAttendantButton() {
//		JButton callAttendantButton = new JButton("Call For Attendant");
//		callAttendantButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//
//			}
//		});
//		callAttendantButton.setBounds(83, 671, 173, 60);
//		add(callAttendantButton);
//	}

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

	private void initializeTransparentPane() {
		glassPane = new JPanel(new GridBagLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(new Color(128, 128, 128, 128)); // Semi-transparent gray
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		glassPane.setOpaque(false);
		glassPane.setBounds(0, 0, 800, 800); // Set the bounds to match the size of the CustomerStartPane
		glassPane.setVisible(false);

		JLabel disabledMessage = new JLabel("Station disabled: waiting for attendant to enable");
		disabledMessage.setFont(new Font("Tahoma", Font.BOLD, 20));
		glassPane.add(disabledMessage);

		// Make the glass pane "absorb" the mouse events, so that nothing behind it (the buttons) can be clicked while it is displayed
		glassPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				e.consume();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				e.consume();
			}
		});

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

				if (pluCode.length() < 4 || pluCode.length() > 5) {
					JOptionPane.showMessageDialog(null, "PLU codes are only 4 or 5 numbers long! Please enter a valid PLU code.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				//System.out.println("1" + cioc.getCart());
				boolean itemAddedSuccessfully = cioc.addItemByPLU(pluCode);
				//System.out.println("2" + cioc.getCart());

				if (itemAddedSuccessfully) {
					//System.out.println("ehre");
					refreshOrderGrid();
					//System.out.println("aawdawd");

					Window window = SwingUtilities.getWindowAncestor(enterButton);
					if (window != null) {
						window.dispose();
					}

					// cioc.promptAddItemToBaggingArea();
					showPlaceInBaggingAreaPane();
				} else {
					JOptionPane.showMessageDialog(null, "That item was not found. Please enter a valid PLU code.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		panel.add(enterButton, gbc);

		JOptionPane.showOptionDialog(cioc.getDevice().getFrame(), panel, "Add Item by PLU Code", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
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

						//System.out.println("here");

						Window window = SwingUtilities.getWindowAncestor(enterButton);
						if (window != null) {
							window.dispose();
						}

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

	private void showAddOwnBagsPane() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		panel.add(new JLabel("Please add your own bags to the bagging area, and press \"Finished\" when you are done."), gbc);

		JButton finishedButton = new JButton("Finished");
		finishedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cioc.notifyAttendantBagsAdded();

				Window window1 = SwingUtilities.getWindowAncestor(finishedButton);
				if (window1 != null) {
					window1.dispose();
				}
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		panel.add(finishedButton, gbc);

		JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		JDialog dialog = optionPane.createDialog(cioc.getDevice().getFrame(), "Add Own Bags");

		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Code to run when the JOptionPane is closed
				System.out.println("JOptionPane closed");
				cioc.cancelAddOwnBags();
			}
		});

		dialog.setVisible(true);
	}

	public void showPlaceInBaggingAreaPane() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		panel.add(new JLabel("Please add that item to the bagging area."), gbc);

		JButton finishedButton = new JButton("Finished");
		finishedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cioc.itemWasAddedToTheBaggingArea();

				Window window1 = SwingUtilities.getWindowAncestor(finishedButton);
				if (window1 != null) {
					window1.dispose();
				}
			}
		});

		JButton doNotBagButton = new JButton("Do not bag this item");
		doNotBagButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.out.println("Do not bag this item pressed");
				cioc.selectDoNotBag();

				Window window1 = SwingUtilities.getWindowAncestor(doNotBagButton);
				if (window1 != null) {
					window1.dispose();
				}
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		panel.add(finishedButton, gbc);

		gbc.gridx = 1;
		panel.add(doNotBagButton, gbc);

		JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		JDialog dialog = optionPane.createDialog(cioc.getDevice().getFrame(), "Place Item In Bagging Area");

		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Code to run when the JOptionPane is closed
				System.out.println("JOptionPane closed");
				cioc.cancelAddOwnBags();
			}
		});

		dialog.setVisible(true);
	}


	public void enableStation() {
		glassPane.setVisible(false);
	}

	public void disableStation() {
		glassPane.setVisible(true);
	}
}