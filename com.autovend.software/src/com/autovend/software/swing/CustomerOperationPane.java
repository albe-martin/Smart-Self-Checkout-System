package com.autovend.software.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.software.controllers.CustomerIOController;

/**
 * A class for  the customer start pane.
 */
public class CustomerOperationPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private CustomerIOController cioc;
	public JButton logoutButton;
	private JTable table;
	private JLabel totalCostLabel;
	
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


		initializeButtons();


		// Initialize exit button.
        // TODO: Note: might be removed.
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
		Object[][] data = {
				{"Item 1", new BigDecimal("10.00")},
				{"Item 2", new BigDecimal("20.00")},
				{"Item 3", new BigDecimal("30.00")},
		};
		DefaultTableModel items = new DefaultTableModel(data, columnNames);
		table = new JTable(items);
		table.setRowHeight(25);
		table.setRowSelectionAllowed(false);
		table.setRequestFocusEnabled(false);
		table.setFocusable(false);
		table.setShowGrid(true);

		JScrollPane scrollPane = new JScrollPane(table);
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
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(new Object[]{itemName, itemPrice});
		updateTotalCost();
	}

	private void removeItemFromGrid(int rowIndex) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		if (rowIndex >= 0 && rowIndex < model.getRowCount()) {
			model.removeRow(rowIndex);
			updateTotalCost();
		}
	}





	private void updateTotalCost() {
		BigDecimal totalCost = BigDecimal.ZERO;
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowCount = model.getRowCount();

		for (int i = 0; i < rowCount; i++) {
			BigDecimal itemPrice = (BigDecimal) model.getValueAt(i, 1);
			totalCost = totalCost.add(itemPrice);
		}

		totalCostLabel.setText("Total Cost: $" + totalCost.toString());
	}


	private void initializeButtons() {

		JButton enterMembershipNumberButton = new JButton("Enter Membership \nNumber");
		enterMembershipNumberButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		enterMembershipNumberButton.setBounds(370, 663, 188, 76);
		add(enterMembershipNumberButton);

		JButton addItemByPluCodeButton = new JButton("Add Item by PLU Code");
		addItemByPluCodeButton.setBounds(589, 236, 173, 60);
		add(addItemByPluCodeButton);

		JButton addItemByLookupButton = new JButton("Add Item by Lookup");
		addItemByLookupButton.setBounds(382, 236, 188, 60);
		add(addItemByLookupButton);

		JButton payForItemsButton = new JButton("Pay for Items");
		payForItemsButton.setBounds(480, 363, 173, 60);
		add(payForItemsButton);

		JButton selectLanguageButton = new JButton("Select Language");
		selectLanguageButton.setBounds(589, 663, 173, 76);
		add(selectLanguageButton);
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
        logoutButton.setBounds(121, 649, 120, 63);
        this.add(logoutButton);
	}
}
