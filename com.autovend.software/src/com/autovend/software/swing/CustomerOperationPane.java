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

        
        
        // Initialize exit button.
        // TODO: Note: might be removed.
        initializeExitButton();
       
        
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
				// Add more items here...
		};
		DefaultTableModel items = new DefaultTableModel(data, columnNames);
		table = new JTable(items);
		table.setRowHeight(25);
		table.setRowSelectionAllowed(false);
		table.setRequestFocusEnabled(false);
		table.setFocusable(false);

		// Customize table appearance
		//table.setGridColor(Color.BLACK);
		//table.setIntercellSpacing(new Dimension(1, 1));
		//table.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		table.setShowGrid(true);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(27, 97, 339, 468);

		// Customize scroll pane appearance
		//scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
//		scrollPane.setBackground(new Color(240, 240, 240));
//		scrollPane.getViewport().setBackground(Color.WHITE);
//		scrollPane.getVerticalScrollBar().setBackground(new Color(224, 224, 224));
//		scrollPane.getHorizontalScrollBar().setBackground(new Color(224, 224, 224));

		// Customize table header appearance
		//table.getTableHeader().setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));
		//table.getTableHeader().setBackground(new Color(240, 240, 240));
		//4table.getTableHeader().setOpaque(false);
		//table.getTableHeader().setForeground(Color.BLACK);

		// Adjust the table header border to account for grid lines
		//table.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
		//table.getTableHeader().setReorderingAllowed(false);
		//table.getTableHeader().setResizingAllowed(false);

		add(scrollPane);
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
        logoutButton.setBounds(614, 689, 120, 63);
        this.add(logoutButton);
	}
}
