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
package com.autovend.software.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.autovend.external.CardIssuer;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.CardReaderControllerState;

import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CheckoutController.completePaymentErrorEnum;

import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.utils.CardIssuerDatabases;
import com.autovend.software.utils.MiscProductsDatabase;

import static com.autovend.external.ProductDatabases.BARCODED_PRODUCT_DATABASE;
import static com.autovend.external.ProductDatabases.PLU_PRODUCT_DATABASE;

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

	public JLabel totalCostLabel, amountPaidLabel;


	public JButton languageSelectButton;
	private JPanel glassPane;
	private JPanel cashGlassPane;

	private JPanel baggingGlassPane;
	public ButtonGroup group;
	public JLabel disabledMessage;
	
	public JButton addItemByPluCodeButton;
	public JPanel PluCodePanel;
	public JTextField pluCodeTextField;
	public JButton PLUenterButton;


	public JTextField bankTextField;
	public JTextField amountTextField;
	public JButton debitEnterButton;
	
	public JButton purchaseBagsButton;
	public JPanel purchaseBagsPanel;
	public JTextField bagQuantityTextField;
	public JButton purchaseBagsEnterButton;

	public JButton addItemByLookupButton;
	public DefaultListModel<String> listModel;
	public JList<String> productList;
	public JScrollPane productScrollPane;
	
	public Product selectedProduct;
	
	public JButton addOwnBagsButton;
	public JPanel addOwnBagsPanel;
	public JButton finishedAddOwnBagsButton;
	
	public JButton cashButton;
	
	
	public DefaultTableModel model;


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
		
		// Create pane for bagging prompt
		initializeBaggingPromptGlassPane();

		initializeHeader();

		// TODO: Create cart functionalities

		initializeCartItemsGrid();

		initializeTotalCostLabel();
		initializeAmountPaidLabel();

		// initializeCallAttendantButton();


		String[] buttonText = {
				"Enter Membership",
				"Add Item by PLU Code",
				"Add Item by Lookup",
				"Purchase Bags",
				"Add Own Bags",
				"Complete Payment",
				"Select Language",
				"Pay By Debit",
				"Pay By Credit",
				"Pay By Gift-Card",
		};
		Runnable[] initButtonFuncs = {
				()->{showMembershipPane();cioc.beginSignInAsMember();},
				()->{showAddItemByPLUCodePane();},
				()->{showAddItemByLookup();},
				()->{showPurchaseBagsPane();},
				()->{cioc.addOwnBags();showAddOwnBagsPane();},
				()->{cioc.finalizeOrder();},
				()->{createLangSelectPane();},
				()->{payByDebitPane();},
				()->{payByCreditPane();},
				()->cioc.choosePayByGiftCard(),
		};
		int[][] buttonDims = {
				{388, 240, 190, 60},
				{589, 100, 190, 60},
				{388, 100, 190, 60},
				{589, 170, 190, 60},
				{388, 170, 190, 60},
				{589, 240, 190, 60},
				{589, 350, 190, 60},
				{589, 430, 190, 60},
				{388, 430, 190, 60},
				{388, 350, 190, 60},
		};
		for (int ii=0;ii<buttonText.length;ii++){
			JButton newButton = new JButton(buttonText[ii]);
			int finalIi = ii;
			newButton.addActionListener(e -> initButtonFuncs[finalIi].run());
			newButton.setBounds(buttonDims[ii][0], buttonDims[ii][1], buttonDims[ii][2], buttonDims[ii][3]);
			add(newButton);
		}

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
		model = (DefaultTableModel) orderItemsTable.getModel();
		model.setRowCount(0);

		HashMap<Product, Number[]> orderItems = cioc.getCart();

//		System.out.println("\n\n" + orderItems.entrySet());
		for (Map.Entry<Product, Number[]> entry : orderItems.entrySet()) {
			Product product = entry.getKey();
			if (product instanceof PLUCodedProduct pluProduct) {
				updateGrid(model, entry, pluProduct.getDescription());
			} else if (product instanceof BarcodedProduct barcodeProduct) {
				updateGrid(model, entry, barcodeProduct.getDescription());
			} else if (product instanceof MiscProductsDatabase.Bag bagProduct){
				updateGrid(model, entry, "bag(s)");
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
		updateAmountPaid();
	}

	private void updateGrid(DefaultTableModel model, Map.Entry<Product, Number[]> entry, String description) {
		Number[] quantities = entry.getValue();
		Number quantity = quantities[0];

		model.addRow(new Object[]{description, quantities[1], quantity});

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

		totalCostLabel.setText("Total Cost: $" + cioc.getMainController().getCost().toString());
	}

	private void initializeAmountPaidLabel() {
		amountPaidLabel = new JLabel("Remaining Amount: $0.00");
		amountPaidLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		amountPaidLabel.setBounds(300, 576, 400, 30);
		amountPaidLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(amountPaidLabel);
	}

	public void updateAmountPaid() {
		amountPaidLabel.setText("Remaining Amount: $" + (cioc.getMainController().getRemainingAmount()));
		cioc.cancelPayment();
	}

	private void createLangSelectPane() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


		// Create a label for the language selection
		JLabel label = new JLabel("Select a language:");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(label);

		// Create a group of radio buttons for the available languages
		group = new ButtonGroup();
		for (String language : languages) {
			JRadioButton radioButton = new JRadioButton(language);
			radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			group.add(radioButton);
			panel.add(radioButton);
		}

		// Show the language selection dialog and get the selected language
		if (showPopup(panel, "Language Selection") == JOptionPane.OK_OPTION) {
			String newLanguage = null;
			// Determine selected button's text
			for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements(); ) {
				AbstractButton button = buttons.nextElement();
				if (button.isSelected()) {
					newLanguage = button.getText();
					break;
				}
			}
		}
	}

	public void initializeTransparentPane() {
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

		disabledMessage = new JLabel("Station disabled: waiting for attendant to enable");
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
	private void payByCreditPane() {
		JPanel debitPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 10, 5);
		debitPanel.add(new JLabel("Credit Source Name:"), gbc);
		bankTextField=new JTextField(12);
		amountTextField = new JTextField(12);
		gbc.gridx = 1;
		debitPanel.add(bankTextField, gbc);
		gbc.gridy=1;
		gbc.gridx=0;
		debitPanel.add(new JLabel("Amount: "), gbc);
		gbc.gridx = 1;
		debitPanel.add(amountTextField, gbc);


		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		int opt = optionDialogPopup(debitPanel, "Payment");
		if (opt==JOptionPane.OK_OPTION){
			String bankName = bankTextField.getText();
			String amount = amountTextField.getText();
			if (CardIssuerDatabases.ISSUER_DATABASE.get(bankName) == null) {
				showErrorMessage("Bank Not Recognized");
				return;
			}
			double amntDbl = 0;
			try {
				amntDbl = Double.parseDouble(amount);
			} catch (Exception ex) {
				showErrorMessage("Amount Not Recognized as a number.");
				return;
			}

			if (cioc.getMainController().getRemainingAmount().compareTo(BigDecimal.valueOf(amntDbl)) < 0) {
				showErrorMessage("You cannot pay more than the remaining amount for the order!");
				return;
			}
			cioc.choosePayByBankCard(
					CardReaderControllerState.PAYINGBYDEBIT,
					CardIssuerDatabases.ISSUER_DATABASE.get(bankName),
					BigDecimal.valueOf(amntDbl)
			);
		}
	}
	
	public void showMessageDialog(JScrollPane scrollPane, String header) {
		JOptionPane.showMessageDialog(cioc.getDevice().getFrame(), scrollPane, header, JOptionPane.PLAIN_MESSAGE);
	}

	private void payByDebitPane() {
		JPanel debitPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		debitPanel.add(new JLabel("Bank Name:"), gbc);
		bankTextField=new JTextField(12);
		amountTextField = new JTextField(12);
		gbc.gridx = 1;
		debitPanel.add(bankTextField, gbc);
		gbc.gridy=1;
		gbc.gridx=0;
		debitPanel.add(new JLabel("Amount: "), gbc);
		gbc.gridx = 1;
		debitPanel.add(amountTextField, gbc);


		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		int opt = optionDialogPopup(debitPanel, "Payment");

		if (opt==JOptionPane.OK_OPTION){
			System.out.println("Paid");
			String bankName = bankTextField.getText();
			String amount = amountTextField.getText();
			if (CardIssuerDatabases.ISSUER_DATABASE.get(bankName) == null) {
				showErrorMessage("Bank Not Recognized");
				return;
			}
			double amntDbl = 0;
			try {
				amntDbl = Double.parseDouble(amount);
			} catch (Exception ex) {
				showErrorMessage("Amount Not Recognized as a number.");
				return;
			}

			if (cioc.getMainController().getRemainingAmount().compareTo(BigDecimal.valueOf(amntDbl)) < 0) {
				showErrorMessage("You cannot pay more than the remaining amount for the order!");
				return;
			}
			cioc.choosePayByBankCard(
					CardReaderControllerState.PAYINGBYDEBIT,
					CardIssuerDatabases.ISSUER_DATABASE.get(bankName),
					BigDecimal.valueOf(amntDbl)
			);
		}
	}

	private void showAddItemByPLUCodePane() {
		PluCodePanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		PluCodePanel.add(new JLabel("Please enter the PLU code:"), gbc);

		pluCodeTextField = new JTextField(10);
		gbc.gridx = 1;
		PluCodePanel.add(pluCodeTextField, gbc);

		PLUenterButton = new JButton("Enter");
		PLUenterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String pluCode = pluCodeTextField.getText();

				if (pluCode.length() < 4 || pluCode.length() > 5) {
					showErrorMessage("PLU codes are only 4 or 5 numbers long! Please enter a valid PLU code.");
					//JOptionPane.showMessageDialog(null, "PLU codes are only 4 or 5 numbers long! Please enter a valid PLU code.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				//System.out.println("1" + cioc.getCart());
				boolean itemAddedSuccessfully = cioc.addItemByPLU(pluCode);
				//System.out.println("2" + cioc.getCart());

				if (itemAddedSuccessfully) {
					refreshOrderGrid();

					Window window = SwingUtilities.getWindowAncestor(PLUenterButton);
					if (window != null) {
						window.dispose();
					}

					// cioc.promptAddItemToBaggingArea();
					baggingGlassPane.setVisible(true);
				} else {
					showErrorMessage("That item was not found. Please enter a valid PLU code.");
					//JOptionPane.showMessageDialog(null, "That item was not found. Please enter a valid PLU code.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		PluCodePanel.add(PLUenterButton, gbc);
		
		
		showPopup(PluCodePanel, "Add Item by PLU Code");
		//JOptionPane.showOptionDialog(cioc.getDevice().getFrame(), PluCodePanel, "Add Item by PLU Code", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
	}


	private void showMembershipPane() {

		PluCodePanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		PluCodePanel.add(new JLabel("Please enter membership number:"), gbc);

		pluCodeTextField = new JTextField(10);
		gbc.gridx = 1;
		PluCodePanel.add(pluCodeTextField, gbc);

		PLUenterButton = new JButton("Enter");
		PLUenterButton.addActionListener(e -> {
			String code = pluCodeTextField.getText();
			cioc.attemptSignIn(code);
		});

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		PluCodePanel.add(PLUenterButton, gbc);

		JOptionPane optPane = new JOptionPane(PluCodePanel,JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);

		JDialog dialog = optPane.createDialog(cioc.getDevice().getFrame(), "Enter Membership Numb");
		dialog.setModal(false);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Code to run when the JOptionPane is closed
				cioc.cancelSignInAsMember();
			}
		});

		dialog.setVisible(true);
	}
	private void showAddItemByLookup() {
		// Create a list to hold all products
		List<Product> allProducts = new ArrayList<>();

		// Add all barcoded products to the list
		allProducts.addAll(BARCODED_PRODUCT_DATABASE.values());

		// Add all PLU coded products to the list
		allProducts.addAll(PLU_PRODUCT_DATABASE.values());

		// Create a JList to display product descriptions
		listModel = new DefaultListModel<>();
		for (Product product : allProducts) {
			if (product instanceof BarcodedProduct) {
				listModel.addElement(((BarcodedProduct) product).getDescription());
			} else if (product instanceof PLUCodedProduct) {
				listModel.addElement(((PLUCodedProduct) product).getDescription());
			}
		}
		productList = new JList<>(listModel);
		productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Show a scrollable popup with the list of product descriptions
		productScrollPane = new JScrollPane(productList);
		showMessageDialog (productScrollPane, "Select a product");
		//JOptionPane.showMessageDialog(cioc.getDevice().getFrame(), scrollPane, "Select a product", JOptionPane.PLAIN_MESSAGE);

		// Get the selected product and add it to the transaction
		int selectedIndex = productList.getSelectedIndex();
		if (selectedIndex != -1) {
			selectedProduct = allProducts.get(selectedIndex);
			cioc.addItemByBrowsing(selectedProduct);

			// Prompt the user to bag the item
			// JOptionPane.showMessageDialog(null, "Please bag the item", "Bagging", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	private void showPurchaseBagsPane() {
		purchaseBagsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		purchaseBagsPanel.add(new JLabel("Please enter the number of bags you wish to purchase:"), gbc);

		bagQuantityTextField = new JTextField(10);
		gbc.gridx = 1;
		purchaseBagsPanel.add(bagQuantityTextField, gbc);

		purchaseBagsEnterButton = new JButton("Enter");
		purchaseBagsEnterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int bagQuantity = Integer.parseInt(bagQuantityTextField.getText());

					if (bagQuantity < 0) {
						showErrorMessage("Invalid quantity. Please enter a non-negative integer.");
						//JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a non-negative integer.", "Error", JOptionPane.ERROR_MESSAGE);
					} else {
						// Add the purchased bags to the order.
						cioc.purchaseBags(bagQuantity);

						// Update the order grid to display the bags.
						refreshOrderGrid();

						//System.out.println("here");

						Window window = SwingUtilities.getWindowAncestor(purchaseBagsEnterButton);
						if (window != null) {
							window.dispose();
						}

						System.out.println("Bags purchased: " + bagQuantity);
					}
				} catch (NumberFormatException ex) {
					showErrorMessage("Invalid input. Please enter a non-negative integer.");
					//JOptionPane.showMessageDialog(null, "Invalid input. Please enter a non-negative integer.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		purchaseBagsPanel.add(purchaseBagsEnterButton, gbc);

		optionDialog(purchaseBagsPanel, "Purchase Bags");
		//JOptionPane.showOptionDialog(cioc.getDevice().getFrame(), purchaseBagsPanel, "Purchase Bags", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
	}

	private void showAddOwnBagsPane() {
		addOwnBagsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		addOwnBagsPanel.add(new JLabel("Please add your own bags to the bagging area, and press \"Finished\" when you are done."), gbc);

		finishedAddOwnBagsButton = new JButton("Finished");
		finishedAddOwnBagsButton.addActionListener(e -> {
			cioc.notifyAttendantBagsAdded();

			Window window1 = SwingUtilities.getWindowAncestor(finishedAddOwnBagsButton);
			if (window1 != null) {
				window1.dispose();
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		addOwnBagsPanel.add(finishedAddOwnBagsButton, gbc);
		addOwnBagsOptionPane(addOwnBagsPanel);

//		JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
//		JDialog dialog = optionPane.createDialog(cioc.getDevice().getFrame(), "Add Own Bags");

//		dialog.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent e) {
//				// Code to run when the JOptionPane is closed
//				cioc.cancelAddOwnBags();
//			}
//		});
//
//		dialog.setVisible(true);
	}

	public void addOwnBagsOptionPane(JPanel panel) {
		JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
		JDialog dialog = optionPane.createDialog(cioc.getDevice().getFrame(), "Add Own Bags");
		dialog.setModal(false);
		cioc.getDevice().getFrame().setAlwaysOnTop(false);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Code to run when the JOptionPane is closed
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
	
	public void notifyItemAdded() {
		refreshOrderGrid();

		baggingGlassPane.setVisible(true);
	}


	public void initializeCashPromptGlassPane() {
		cashGlassPane = new JPanel(new GridBagLayout()) {

			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(new Color(0, 0, 0, 0)); // transparent
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		cashGlassPane.setOpaque(false);
		cashGlassPane.setBounds(0, 0, 800, 800); // Set the bounds to match the size of the CustomerStartPane
		cashGlassPane.setVisible(false);

		// Make the glass pane "absorb" the mouse events, so that nothing behind it (the buttons) can be clicked while it is displayed
		cashGlassPane.addMouseListener(new MouseAdapter() {
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

		add(cashGlassPane);

	}

	
	public void initializeBaggingPromptGlassPane() {
		baggingGlassPane = new JPanel(new GridBagLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(new Color(0, 0, 0, 0)); // transparent
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		baggingGlassPane.setOpaque(false);
		baggingGlassPane.setBounds(0, 0, 800, 800); // Set the bounds to match the size of the CustomerStartPane
		baggingGlassPane.setVisible(false);

		// Make the glass pane "absorb" the mouse events, so that nothing behind it (the buttons) can be clicked while it is displayed
		baggingGlassPane.addMouseListener(new MouseAdapter() {
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
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		panel.add(new JLabel("Please add that item to the bagging area."), gbc);

		JButton finishedButton = new JButton("Finished");
		finishedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Check if item was bagged
				if (cioc.isItemBagged()) {
					// Close pane
					baggingGlassPane.setVisible(false);
				} else {
					createBaggingWeightProblemPopup();

				}
			}
		});

		JButton doNotBagButton = new JButton("Do not bag this item");
		doNotBagButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.out.println("Do not bag this item pressed");
				cioc.selectDoNotBag();
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		panel.add(finishedButton, gbc);

		gbc.gridx = 1;
		panel.add(doNotBagButton, gbc);
		
		panel.setBackground(new Color(227, 241, 241, 255)); // light blue
		baggingGlassPane.add(panel);
		add(baggingGlassPane);
	}
	
	/**
	 * Creates a pop-up indicating that the bagging area weight is incorrect.
	 */
	public void createBaggingWeightProblemPopup() {
		// Create panel for the pop-up.
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// Create a label indicating no items found.
		JLabel label = new JLabel(Language.translate(language, "The bagging area weight does not match!"));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(label);
		
		// Show pop-up.
		BaggingWeightProblemDialog(panel, "Bagging Area Weight Discrepancy");
		//optionDialogPopup(panel, Language.translate(language, "Bagging Area Weight Discrepancy"));
	}
	
	public void BaggingWeightProblemDialog(JPanel panel, String header) {
		optionDialogPopup(panel, Language.translate(language, header));
	}

	/**
	 * Simple pop-up.
	 */
	public int optionDialogPopup(JPanel panel, String header) {
		return JOptionPane.showOptionDialog(cioc.getDevice().getFrame(), panel, header, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
	}

	public void notifyNoBagApproved() {
		baggingGlassPane.setVisible(false);
	}

	public void notifyItemRemoved() {
		refreshOrderGrid();
	}

	public int showPopup(JPanel panel, String header) {
		return JOptionPane.showOptionDialog(cioc.getDevice().getFrame(), panel, header, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
	}
	
	public int optionDialog(JPanel panel, String header) {
		return JOptionPane.showOptionDialog(cioc.getDevice().getFrame(), purchaseBagsPanel, "Purchase Bags", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
	}
	public void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}


	public void notifyAsMember(String name) {
		// Create panel for the pop-up.
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Create a label indicating no items found.
		JLabel label = new JLabel(Language.translate(language, "Hello "+name));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(label);

		// Show pop-up.
		BaggingWeightProblemDialog(panel, "Membership Verified");
		//optionDialogPopup(panel, Language.translate(language, "Bagging Area Weight Discrepancy"));
	}
}