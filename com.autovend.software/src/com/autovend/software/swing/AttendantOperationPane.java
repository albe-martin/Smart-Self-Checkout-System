package com.autovend.software.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.autovend.devices.SupervisionStation;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CustomerIOController;

/**
 * A class for the attendant operation pane.
 */
public class AttendantOperationPane extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private AttendantIOController aioc;
	private String language = "English";
	// TODO: Have English be the only built in language
	private String[] languages = new String[] {"English", "French"};
	public JButton logoutButton;
	public JPanel manageEnabledPane;
	public JLabel manageEnabledLabel;
	public JPanel manageDisabledPane;
	public JLabel manageDisabledLabel;
	public JPanel manageShutdownPane;
	public JLabel manageShutdownLabel;
	public JButton languageSelectButton;
	public JLabel notificationsLabel;
	public JPanel notificationsPane;
	
	/**
	 * TODO: Delete for final submission.
	 * 
	 * Quick GUI Launcher. Used to allow window builder to work.
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
		
		// Create attendant station.
		SupervisionStation attendantStation = new SupervisionStation();
		
		// Get and set up screen
		JFrame attendantScreen = attendantStation.screen.getFrame();
		attendantScreen.setExtendedState(0);
		attendantScreen.setSize(800, 800);
		attendantScreen.setUndecorated(false);
		attendantScreen.setResizable(false);
		AttendantIOController aioc = new AttendantIOController(attendantStation.screen);
		attendantScreen.setContentPane(new AttendantOperationPane(aioc));
		
		AttendantStationController asc = new AttendantStationController();
		aioc.setMainAttendantController(asc);
		asc.registerController(aioc);
		
		// Add valid username and password.
		asc.registerUser("abc", "123");
		
		attendantScreen.setVisible(true);	
	}
	
	/**
	 * Basic constructor.
	 * 
	 * @param aioc
	 * 			Linked AttendantIOController.
	 */
	public AttendantOperationPane(AttendantIOController aioc) {
		super();
		this.aioc = aioc;
		initializeOperationPane();
	}
	
	/**
	 * Initializes attendant operation pane.
	 */
	private void initializeOperationPane() {
		// Create operation screen pane.
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(null);
		
		// Initialize logout button
		initializeLogoutButton();
		
		// Initialize language select button
		initializeLanguageSelectButton();
		
		// Initialize notifications pane.
		initializeNotificationsPane();

		// Initialize station management panes.
		initializeManagementPanes();
	}
	
	/**
	 * Initialize logout button.
	 */
	private void initializeLogoutButton() {
		// Create logout button.
		logoutButton = new JButton(Language.translate(language, "Log Out"));
		logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		logoutButton.setBounds(631, 19, 118, 50);
		logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Login button pressed

            	// Request logout
            	aioc.logout();
            }
        });
		this.add(logoutButton);
	}
	
	/**
	 * Initialize language select button.
	 */
	public void initializeLanguageSelectButton() {
		// Create language select button.
		languageSelectButton = new JButton(Language.translate(language, "Change Language"));
        languageSelectButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        languageSelectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a panel to hold the language select pop-up
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
                    for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        if (button.isSelected()) {
                            newLanguage = button.getText();
                            break;
                        }
                    }

                    if (newLanguage != null) {
                        // Update the language variable
                        language = newLanguage;

                        // Update texts to new language
                        notificationsLabel.setText(Language.translate(language, "Station Notifications:"));
                        manageEnabledLabel.setText(Language.translate(language, "Manage Enabled Stations:"));
                        manageDisabledLabel.setText(Language.translate(language, "Manage Disabled Stations:"));
                        logoutButton.setText(Language.translate(language, "Log Out"));
                        languageSelectButton.setText(Language.translate(language, "Change Language"));
                        populateManagementPanes();
                    }
                }
            }
        });
        languageSelectButton.setBounds(402, 19, 200, 50);
        this.add(languageSelectButton);
	}
	
	/**
	 * Initialize notifications pane.
	 */
	public void initializeNotificationsPane() {
		// Create label for notifications panel.
		notificationsLabel = new JLabel(Language.translate(language, "Station Notifications:"));
		notificationsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		notificationsLabel.setBounds(21, 30, 173, 14);
		this.add(notificationsLabel);
		
		// Create panel for notifications.
		notificationsPane = new JPanel();
		notificationsPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		notificationsPane.setBounds(21, 55, 299, 304);
		this.add(notificationsPane);
	}
	
	/**
	 * Initialize enabled and disabled station management panes.
	 */
	public void initializeManagementPanes() {
		// Create label for panel with all enabled stations.
		manageEnabledLabel = new JLabel(Language.translate(language, "Manage Enabled Stations:"));
		manageEnabledLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		manageEnabledLabel.setBounds(21, 536, 181, 23);
		add(manageEnabledLabel);
		
		// Create panel for managing enabled stations.
		manageEnabledPane = new JPanel();
		manageEnabledPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		manageEnabledPane.setBounds(21, 562, 226, 179);
		add(manageEnabledPane);
		manageEnabledPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// Create label for panel with all disabled stations.
		manageDisabledLabel = new JLabel(Language.translate(language, "Manage Disabled Stations:"));
		manageDisabledLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		manageDisabledLabel.setBounds(280, 536, 181, 23);
		add(manageDisabledLabel);
		
		// Create panel for managing disabled stations.
		manageDisabledPane = new JPanel();
		manageDisabledPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		manageDisabledPane.setBounds(280, 562, 226, 179);
		add(manageDisabledPane);
		manageDisabledPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// Create label for managing shutdown stations.
		manageShutdownLabel = new JLabel(Language.translate(language, "Manage Shutdown Stations:"));
		manageShutdownLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		manageShutdownLabel.setBounds(542, 536, 227, 23);
		add(manageShutdownLabel);
		
		// Create panel for managing shutdown stations.
		manageShutdownPane = new JPanel();
		manageShutdownPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		manageShutdownPane.setBounds(542, 562, 226, 179);
		add(manageShutdownPane);
		manageShutdownPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// Populate the panes.
		populateManagementPanes();
	}
	
	/**
	 * Populates the enabled and disabled station management panes.
	 */
	public void populateManagementPanes() {
		// Clear panes.
		manageEnabledPane.removeAll();
		manageDisabledPane.removeAll();
		manageShutdownPane.removeAll();
		
		// Add each station to enabled/disabled/shutdown pane.
		for (CustomerIOController cioc : aioc.getAllStationsIOControllers()) {
			if (cioc.getMainController().isDisabled()) {
				if (cioc.isShutdown()) {
					// Add to shutdown pane.
					JButton btn = new JButton(Language.translate(language, "Station") + " #" + cioc.getMainController().getID());
					addShutdownActionPopup(btn, cioc);
					manageShutdownPane.add(btn);
				} else {
					// Add to disabled pane.
					JButton btn = new JButton(Language.translate(language, "Station") + " #" + cioc.getMainController().getID());
					addDisabledActionPopup(btn, cioc);
					manageDisabledPane.add(btn);
				}
			} else {
				// Add enabled station to enabled pane.
				JButton btn = new JButton(Language.translate(language, "Station") + " #" + cioc.getMainController().getID());
				addEnabledActionPopup(btn, cioc);
				manageEnabledPane.add(btn);
			}
		}
	}
	
	/**
	 * Adds the action pop-up menu for a shut down station.
	 * 
	 * @param btn
	 * 			Button that causes the pop-up.
	 * @param cioc
	 * 			CustomerIOController performing the action on.
	 */
	public void addShutdownActionPopup(JButton btn, CustomerIOController cioc) {
		btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a panel to hold the actions pop-up.
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                // Create a label for the action selection.
                JLabel label = new JLabel("Select an action:");
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(label);
                
                // Create a group of radio buttons for the available actions.
                ButtonGroup group = new ButtonGroup();
                for (String action : new String[] {Language.translate(language, "Startup Station")}) {
                    JRadioButton radioButton = new JRadioButton(action);
                    radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    group.add(radioButton);
                    panel.add(radioButton);
                }

                // Show the action pop-up and get the selected action.
                int result = JOptionPane.showOptionDialog(null, panel, "Action Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (result == JOptionPane.OK_OPTION) {
                    String chosenAction = null;
                    // Determine selected action's text.
                    for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        if (button.isSelected()) {
                            chosenAction = button.getText();
                            break;
                        }
                    }
                    
                    // Process the selected action.
                    processAction(chosenAction, cioc);
                }
            }
        });
		
	}
	
	/**
	 * Adds the action pop-up menu for a disabled station.
	 * 
	 * @param btn
	 * 			Button that causes the pop-up.
	 * @param cioc
	 * 			CustomerIOController performing the action on.
	 */
	public void addDisabledActionPopup(JButton btn, CustomerIOController cioc) {
		btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a panel to hold the actions pop-up.
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                // Create a label for the action selection.
                JLabel label = new JLabel("Select an action:");
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(label);
                
                // Create a group of radio buttons for the available actions.
                ButtonGroup group = new ButtonGroup();
                for (String action : new String[] {Language.translate(language, "Enable Station"),
                		Language.translate(language, "Shutdown Station")}) {
                    JRadioButton radioButton = new JRadioButton(action);
                    radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    group.add(radioButton);
                    panel.add(radioButton);
                }

                // Show the action pop-up and get the selected action.
                int result = JOptionPane.showOptionDialog(null, panel, "Action Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (result == JOptionPane.OK_OPTION) {
                    String chosenAction = null;
                    // Determine selected action's text.
                    for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        if (button.isSelected()) {
                            chosenAction = button.getText();
                            break;
                        }
                    }
                    
                    // Process the selected action.
                    processAction(chosenAction, cioc);
                }
            }
        });
		
	}
	
	/**
	 * Adds the action pop-up menu for an enabled station.
	 * 
	 * @param btn
	 * 			Button that causes the pop-up.
	 * @param cioc
	 * 			CustomerIOController performing the action on.
	 */
	public void addEnabledActionPopup(JButton btn, CustomerIOController cioc) {
		btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a panel to hold the actions pop-up.
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                // Create a label for the action selection.
                JLabel label = new JLabel(Language.translate(language, "Select an action:"));
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(label);
                
                // Create a group of radio buttons for the available actions.
                ButtonGroup group = new ButtonGroup();
                for (String action : new String[] {Language.translate(language, "Disable Station"),
                		Language.translate(language, "Shutdown Station"),
                		Language.translate(language, "Approve Custom Bags"),
                		Language.translate(language,  "Add Item By Text Search"),
                		Language.translate(language, "Remove Item")}) {
                    JRadioButton radioButton = new JRadioButton(action);
                    radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    group.add(radioButton);
                    panel.add(radioButton);
                }

                // Show the action pop-up and get the selected action.
                int result = JOptionPane.showOptionDialog(null, panel, Language.translate(language, "Action Selection"), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (result == JOptionPane.OK_OPTION) {
                    String chosenAction = null;
                    // Determine selected action's text.
                    for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        if (button.isSelected()) {
                            chosenAction = Language.translate(language, button.getText());
                            break;
                        }
                    }
                    
                    // Process the selected action.
                    processAction(chosenAction, cioc);
                }
            }
        });
		
	}
	
	/**
	 * Process an action on a customer station.
	 * 
	 * @param action
	 * 			Action to be performed.
	 * @param cioc
	 * 			CustomerIOController to perform the action on.
	 */
	public void processAction(String action, CustomerIOController cioc ) {
		if (action == null) {
			// Ignore null actions.
			return;
		}
		if (action.equalsIgnoreCase("Enable Station")) {
			// Enable station.
			aioc.enableStation(cioc.getMainController());
			// Repopulate management panes.
			populateManagementPanes();
		} else if (action.equalsIgnoreCase("Disable Station")) {
			// Disable station.
			aioc.disableStation(cioc.getMainController());
			// Repopulate management panes.
			populateManagementPanes();
		} else if (action.equalsIgnoreCase("Shutdown Station")) {
			// Shut down station. May receive an in use notification.
			aioc.shutdownStation(cioc.getMainController());
			// Repopulate management panes.
			populateManagementPanes();
		} else if (action.equalsIgnoreCase("Startup Station")) {
			// Request station start up.
			aioc.startupStation(cioc.getMainController());
		} else if (action.equalsIgnoreCase("Approve Custom Bags")) {
			// TODO: Notify station about approval
			System.out.println("Bags approved");
			// Remove from notifications.
		} else if (action.equalsIgnoreCase("Add Item By Text Search")) {
			// Create text search pop-up.
			createTextSearchPopup(cioc);
		} else if (action.equalsIgnoreCase("Remove Item")) {
			createRemoveItemPopup(cioc);
		}
		
		// Refresh screen.
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Create a text search pop-up for the attendant to add items to the chosen customer.
	 * 
	 * @param cioc
	 * 			CustomerIOController to add an item to.
	 */
	public void createTextSearchPopup(CustomerIOController cioc) {
		// Create a panel to hold the text search pop-up.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create a label asking to search for item.
        JLabel label = new JLabel(Language.translate(language, "Enter key words to search for an item:"));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        // Create a text field to search in.
        JTextField searchField = new JTextField();
        panel.add(searchField);
        
        // Show pop-up and get result.
        int result = JOptionPane.showOptionDialog(null, panel, Language.translate(language, "Add Item By Text Search"), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if (result == JOptionPane.OK_OPTION) {
        	Set<Product> foundProducts = aioc.addItemByTextSearch(searchField.getText());
        	System.out.println("found:" + foundProducts.toString());
        	createFoundProductsPopup(cioc, foundProducts);
        }
	}
	
	/**
	 * Creates another pop-up for the attendant to select which found products they want to add.
	 * 
	 * @param cioc
	 * 			CustomerIOController to add an item to.
	 * @param foundProducts
	 * 			Set of products found by the text search.
	 */
	public void createFoundProductsPopup(CustomerIOController cioc, Set<Product> foundProducts) {
		if (foundProducts.size() == 0) {
			// No products found, try again.
			createNoFoundProductsPopop(cioc);
		} else {
			// Display found products pop-up.
			
			// Create panel
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			// Create a label indicating to select product.
			JLabel label = new JLabel(Language.translate(language, "Select a product to add:"));
			label.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(label);
			
			// Create a group of radio buttons for the found products.
            ButtonGroup group = new ButtonGroup();
            
            // Add each found product.
            for (Product product : foundProducts) {
    			if (product instanceof BarcodedProduct) {
    				// Add barcoded product to options.
    				BarcodedProduct bcproduct = (BarcodedProduct) product;
    				JRadioButton radioButton = new JRadioButton(bcproduct.getDescription() + " for $" + bcproduct.getPrice());
                    radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    group.add(radioButton);
                    panel.add(radioButton);
    			} else if (product instanceof PLUCodedProduct) {
    				// Add PLU product to options.
    				PLUCodedProduct pluproduct = (PLUCodedProduct) product;
    				JRadioButton radioButton = new JRadioButton(pluproduct.getDescription() + " for $" + pluproduct.getPrice());
                    radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    group.add(radioButton);
                    panel.add(radioButton);
    			}
            }
            
            // Show pop-up.
            int result = JOptionPane.showOptionDialog(null, panel, "Choose found product", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
            if (result == JOptionPane.OK_OPTION) {
                String selectedProductDescription = null;
                // Determine selected button's text
                for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                    AbstractButton button = buttons.nextElement();
                    if (button.isSelected()) {
                        selectedProductDescription = button.getText();
                        break;
                    }
                }

                if (selectedProductDescription != null) {
                    // Get the selected product
                	for (Product product : foundProducts) {
            			if (product instanceof BarcodedProduct) {
            				// Add barcoded product to options.
            				BarcodedProduct bcproduct = (BarcodedProduct) product;
            				if (selectedProductDescription.equals(bcproduct.getDescription() + " for $" + bcproduct.getPrice())) {
            					// Product found
            					cioc.addProduct(product);
            					break;
            				}
            			} else if (product instanceof PLUCodedProduct) {
            				// Add PLU product to options.
            				PLUCodedProduct pluproduct = (PLUCodedProduct) product;
            				if (selectedProductDescription.equals(pluproduct.getDescription() + " for $" + pluproduct.getPrice())) {
            					// Product found
            					cioc.addProduct(product);
            					break;
            				}
            			}
                    }
                } else {
                	// No product selected, attempt another search.
                	createTextSearchPopup(cioc);
                }
            }
		}
	}
	
	/**
	 * Creates another pop-up indicating that no products were found, and to try again.
	 * 
	 * @param cioc
	 * 			CustomerIOController to add an item to. (When trying again).
	 */
	public void createNoFoundProductsPopop(CustomerIOController cioc) {
		// Create panel for the pop-up.
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// Create a label indicating no items found.
		JLabel label = new JLabel(Language.translate(language, "No products found, try again."));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(label);
		
		// Show pop-up.
		int result = JOptionPane.showOptionDialog(null, panel, Language.translate(language, "No Products Found"), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		if (result == JOptionPane.OK_OPTION) {
			// Create new text search pop-up.
			createTextSearchPopup(cioc);
		}
	}
	
	/**
	 * Create a pop-up allowing the attendant to remove a customer's items.
	 * 
	 * @param cioc
	 * 			CustomerIOController to remove item from.
	 */
	public void createRemoveItemPopup(CustomerIOController cioc) {
		// Create pop-up panel.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create pop-up label.
        JLabel label = new JLabel(Language.translate(language, "Select an item to remove:"));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        
        // Create a group of radio buttons for the available items to remove.
        ButtonGroup group = new ButtonGroup();
        
        // TODO: Loop through each item in customer's cart (includes bags, what product type are they? for description).
//        for (String language : languages) {
//            JRadioButton radioButton = new JRadioButton(language);
//            radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//            group.add(radioButton);
//            panel.add(radioButton);
//        }

        // Show pop-up.
        int result = JOptionPane.showOptionDialog(null, panel, Language.translate(language, "Remove Item"), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if (result == JOptionPane.OK_OPTION) {
            // TODO: Determine item and remove from customer's cart.
        }
	}
	
	// TODO: Link up shutdown notification from attendantIOController.
	
	/**
	 * Notify the attendant screen that a station is in use.
	 * Occurs after an attempt to shutdown a station.
	 * 
	 * @param cioc
	 * 			CustomerIOController being shut down.
	 */
	public void notifyShutdownStationInUse(CustomerIOController cioc) {
		// Create confirmation pop-up.
		
		// Create panel for the pop-up.
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// Create a label asking for confirmation.
		JLabel label = new JLabel(Language.translate(language, "Station is in use. Do you want to force a shutdown?"));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(label);
		
		// Show pop-up.
        int result = JOptionPane.showOptionDialog(null, panel, Language.translate(language, "Remove Item"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if (result == JOptionPane.YES_OPTION) {
            // Force shutdown.
        	aioc.forceShutDownStation(cioc.getMainController());
        	// Repopulate management panes
        	populateManagementPanes();
        }
	}
	
	/**
	 * Notify the attendant that a station has been started up.
	 * 
	 * @param cioc
	 * 			CustomerIOController that started up.
	 */
	public void notifyStartup(CustomerIOController cioc) {
		// Update management panes.
		populateManagementPanes();
		
		System.out.println("processed startup");
	}
}
