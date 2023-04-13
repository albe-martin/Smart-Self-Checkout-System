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

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.ReceiptPrinterController;
import com.autovend.software.utils.MiscProductsDatabase;

/**
 * A class for the attendant operation pane.
 */
public class AttendantOperationPane extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private AttendantIOController aioc;

	private final ArrayList<String> languages = Language.languages;
	public String language = Language.defaultLanguage;

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
	public JLabel activeIssuesLabel;
	public JPanel activeIssuesPane;
	// Array of [label, button] for notifications.
	public ArrayList<JComponent[]> notificationsData;
	public ArrayList<String> activeIssues;
	private JLabel manageNotificationsLabel;
    public ButtonGroup group;
    public JTextField searchField;
    public JButton button;
	
	/**
	 * Basic constructor.
	 * 
	 * @param aioc
	 * 			Linked AttendantIOController.
	 */
	public AttendantOperationPane(AttendantIOController aioc) {
		super();
		this.aioc = aioc;
		
		notificationsData = new ArrayList<>();
		activeIssues = new ArrayList<>();
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
		
		// Initialize active issues pane.
		initializeActiveIssuesPane();
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
                // Logout button pressed

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
                group = new ButtonGroup();
                for (String language : languages) {
                    JRadioButton radioButton = new JRadioButton(language);
                    radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    group.add(radioButton);
                    panel.add(radioButton);
                }

                // Show the language selection dialog and get the selected language
                if (optionDialogPopup(panel, "Language Selection") == JOptionPane.OK_OPTION) {
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
                        manageNotificationsLabel.setText(Language.translate(language, "Station Notifications:"));
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
	
	public int optionDialogPopup(JPanel panel, String header) {
		return JOptionPane.showOptionDialog(aioc.getDevice().getFrame(), panel, header, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
	}

	/**
	 * Initialize notifications pane.
	 */
	public void initializeNotificationsPane() {
		// Create manage notifications label.
		manageNotificationsLabel = new JLabel("Manage Notifications:");
		manageNotificationsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		manageNotificationsLabel.setBounds(22, 22, 181, 23);
		add(manageNotificationsLabel);
		
		// Create notifications scroll pane.
		JScrollPane notificationsScrollPane = new JScrollPane();
		notificationsScrollPane.setBounds(21, 49, 358, 462);
		add(notificationsScrollPane);
		
		// Add pane to scroll pane.
		notificationsPane = new JPanel();
		notificationsPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		notificationsScrollPane.setViewportView(notificationsPane);
		
		populateNotificationsPane();
	}
	
	/**
	 * Populates the notifications management pane.
	 */
	public void populateNotificationsPane() {
		// Wipe pane.
		notificationsPane.removeAll();
		
		// Create layout.
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[]{227, 124, 0};
		
		// Fill row heights with 30, extra element is 0.
		int[] rowHeights = new int[notificationsData.size() + 1];
		Arrays.fill(rowHeights, 30);
		rowHeights[notificationsData.size()] = 0;
		layout.rowHeights = rowHeights;
		
		layout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		
		// Fill row weights with 0.0, extra element is Double.MIN_VALUE.
		double[] rowWeights = new double[notificationsData.size() + 1];
		rowWeights[notificationsData.size()] = Double.MIN_VALUE;
		layout.rowWeights = rowWeights;
		notificationsPane.setLayout(layout);	

		// Populate grid.
		for (int row = 0; row < notificationsData.size(); row++) {
			for (int col = 0; col < 2; col++) {
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.BOTH;
				gbc.insets = new Insets(0, 0, 5, 5);
				gbc.gridx = col;
				gbc.gridy = row;
				notificationsPane.add(notificationsData.get(row)[col], gbc);
			}
		}
		
		repaint();
		revalidate();
	}
	
	/**
	 * Initialize active issues pane.
	 */
	public void initializeActiveIssuesPane() {
		// Create active issues label.
		activeIssuesLabel = new JLabel("Active Issues:");
		activeIssuesLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		activeIssuesLabel.setBounds(415, 304, 227, 23);
		add(activeIssuesLabel);
		
		// Create active issues pane.
		activeIssuesPane = new JPanel();
		activeIssuesPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		activeIssuesPane.setBounds(411, 332, 358, 179);
		add(activeIssuesPane);
		activeIssuesPane.setLayout(new BoxLayout(activeIssuesPane, BoxLayout.Y_AXIS));
		
		populateActiveIssuesPane();
	}
	
	/**
	 * Populates the active issues pane.
	 */
	public void populateActiveIssuesPane() {
		// Clear pane.
		activeIssuesPane.removeAll();
		
		// Create labels for active issues.
		for (String issue : activeIssues) {
			JLabel label = new JLabel(issue);
			activeIssuesPane.add(label);
		}
		
		
		revalidate();
		repaint();
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
		for (CheckoutController checkout : aioc.getAllStationsControllers()) {
			if (checkout.isDisabled()) {
				if (checkout.isShutdown()) {
					// Add to shutdown pane.
					JButton btn = new JButton(Language.translate(language, "Station") + " #" + checkout.getID());
					addShutdownActionPopup(btn, checkout);
					manageShutdownPane.add(btn);
				} else {
					// Add to disabled pane.
					JButton btn = new JButton(Language.translate(language, "Station") + " #" + checkout.getID());
					addDisabledActionPopup(btn, checkout);
					manageDisabledPane.add(btn);
				}
			} else {
				// Add enabled station to enabled pane.
				JButton btn = new JButton(Language.translate(language, "Station") + " #" + checkout.getID());
				addEnabledActionPopup(btn, checkout);
				manageEnabledPane.add(btn);
			}
		}
		
		repaint();
		revalidate();
	}
	
	/**
	 * Adds the action pop-up menu for a shut down station.
	 * 
	 * @param btn
	 * 			Button that causes the pop-up.
	 * @param checkout
	 * 			CheckoutController performing the action on.
	 */
	public void addShutdownActionPopup(JButton btn, CheckoutController checkout) {
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
                group = new ButtonGroup();
                for (String action : new String[] {Language.translate(language, "Startup Station")}) {
                    JRadioButton radioButton = new JRadioButton(action);
                    radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    group.add(radioButton);
                    panel.add(radioButton);
                }

                // Show the action pop-up and get the selected action.
                if (optionDialogPopup(panel, "Action Selection") == JOptionPane.OK_OPTION) {
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
                    processAction(chosenAction, checkout);
                }
            }
        });
		
	}
	
	
	/**
	 * Adds the action pop-up menu for a disabled station.
	 * 
	 * @param btn
	 * 			Button that causes the pop-up.
	 * @param checkout
	 * 			CheckoutController performing the action on.
	 */
	public void addDisabledActionPopup(JButton btn, CheckoutController checkout) {
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
                group = new ButtonGroup();
                for (String action : new String[] {Language.translate(language, "Enable Station"),
                		Language.translate(language, "Shutdown Station")}) {
                    JRadioButton radioButton = new JRadioButton(action);
                    radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    group.add(radioButton);
                    panel.add(radioButton);
                }

                // Show the action pop-up and get the selected action.
                if (optionDialogPopup(panel, "Action Selection") == JOptionPane.OK_OPTION) {
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
                    processAction(chosenAction, checkout);
                }
            }
        });
		
	}
	
	/**
	 * Adds the action pop-up menu for an enabled station.
	 * 
	 * @param btn
	 * 			Button that causes the pop-up.
	 * @param checkout
	 * 			CheckoutController performing the action on.
	 */
	public void addEnabledActionPopup(JButton btn, CheckoutController checkout) {
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
                group = new ButtonGroup();
                for (String action : new String[] {Language.translate(language, "Disable Station"),
                		Language.translate(language, "Shutdown Station"),
                		Language.translate(language,  "Add Item By Text Search"),
                		Language.translate(language, "Remove Item")}) {
                    JRadioButton radioButton = new JRadioButton(action);
                    radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    group.add(radioButton);
                    panel.add(radioButton);
                }

                // Show the action pop-up and get the selected action.
                if (optionDialogPopup(panel, "Action Selection") == JOptionPane.OK_OPTION) {
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
                    processAction(chosenAction, checkout);
                }
            }
        });
		
	}
	
	/**
	 * Process an action on a customer station.
	 * 
	 * @param action
	 * 			Action to be performed.
	 * @param checkout
	 * 			CheckoutController to perform the action on.
	 */
	public void processAction(String action, CheckoutController checkout ) {
		if (action == null) {
			// Ignore null actions.
			return;
		}
		if (action.equalsIgnoreCase("Enable Station")) {
			// Enable station.
			aioc.enableStation(checkout);
			// Repopulate management panes.
			populateManagementPanes();
		} else if (action.equalsIgnoreCase("Disable Station")) {
			// Disable station.
			aioc.disableStation(checkout);
			// Repopulate management panes.
			populateManagementPanes();
		} else if (action.equalsIgnoreCase("Shutdown Station")) {
			// Shut down station. May receive an in use notification.
			aioc.shutdownStation(checkout);
			// Repopulate management panes.
			populateManagementPanes();
		} else if (action.equalsIgnoreCase("Startup Station")) {
			// Request station start up.
			aioc.startupStation(checkout);
		} else if (action.equalsIgnoreCase("Add Item By Text Search")) {
			// Create text search pop-up.
			createTextSearchPopup(checkout);
		} else if (action.equalsIgnoreCase("Remove Item")) {
			// Create remove item pop-up.
			createRemoveItemPopup(checkout);
		}
		
		// Refresh screen.
		this.revalidate();
		this.repaint();
	}

	/**
	 * Create a text search pop-up for the attendant to add items to the chosen customer.
	 * 
	 * @param checkout
	 * 			CheckoutController to add an item to.
	 */
	public void createTextSearchPopup(CheckoutController checkout) {
		// Create a panel to hold the text search pop-up.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create a label asking to search for item.
        JLabel label = new JLabel(Language.translate(language, "Enter key words to search for an item:"));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        // Create a text field to search in.
        searchField = new JTextField();
        panel.add(searchField);
        
        // Show pop-up and get result.
        if (optionDialogPopup(panel, Language.translate(language, "Add Item By Text Search")) == JOptionPane.OK_OPTION) {
        	Set<Product> foundProducts = aioc.searchProductsByText(searchField.getText());
        	createFoundProductsPopup(checkout, foundProducts);
        }
	}
	
	/**
	 * Creates another pop-up for the attendant to select which found products they want to add.
	 * 
	 * @param checkout
	 * 			CheckoutController to add an item to.
	 * @param foundProducts
	 * 			Set of products found by the text search.
	 */
	public void createFoundProductsPopup(CheckoutController checkout, Set<Product> foundProducts) {
		if (foundProducts.size() == 0) {
			// No products found, try again.
			createNoFoundProductsPopop(checkout);
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
            group = new ButtonGroup();
            
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
            if (optionDialogPopup(panel, "Choose found product") == JOptionPane.OK_OPTION) {
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
            					aioc.addProductByText(checkout, product, BigDecimal.ONE);
            					break;
            				}
            			} else if (product instanceof PLUCodedProduct) {
            				// Add PLU product to options.
            				PLUCodedProduct pluproduct = (PLUCodedProduct) product;
            				if (selectedProductDescription.equals(pluproduct.getDescription() + " for $" + pluproduct.getPrice())) {
            					// Product found
            					aioc.addProductByText(checkout, product, BigDecimal.ONE);
            					break;
            				}
            			}
                    }
                } else {
                	// No product selected, attempt another search.
                	createTextSearchPopup(checkout);
                }
            }
		}
	}
	
	/**
	 * Creates another pop-up indicating that no products were found, and to try again.
	 * 
	 * @param checkout
	 * 			CheckoutController to add an item to. (When trying again).
	 */
	public void createNoFoundProductsPopop(CheckoutController checkout) {
		// Create panel for the pop-up.
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// Create a label indicating no items found.
		JLabel label = new JLabel(Language.translate(language, "No products found, try again."));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(label);
		
		// Show pop-up.
		if (optionDialogPopup(panel, Language.translate(language, "No Products Found")) == JOptionPane.OK_OPTION) {
			// Create new text search pop-up.
			createTextSearchPopup(checkout);
		}
	}
	
	/**
	 * Create a pop-up allowing the attendant to remove a customer's items.
	 * 
	 * @param checkout
	 * 			CheckoutController to remove item from.
	 */
	public void createRemoveItemPopup(CheckoutController checkout) {
		// Create pop-up panel.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create pop-up label.
        JLabel label = new JLabel(Language.translate(language, "Select an item to remove:"));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        
        // Create a group of radio buttons for the available items to remove.
        group = new ButtonGroup();

        // Loop through each item in customer's cart.
        for (Map.Entry<Product, Number[]> entry : aioc.getCart(checkout).entrySet()) {
        	JRadioButton radioButton;
			Product product = entry.getKey();
			if (product instanceof PLUCodedProduct pluProduct) {
				// Handle PLU product.
				radioButton = new JRadioButton(entry.getValue()[0] + " " + pluProduct.getDescription() + " for " + entry.getValue()[1]);
				radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	            group.add(radioButton);
	            panel.add(radioButton);
			} else if (product instanceof BarcodedProduct barcodeProduct) {
				// Handle barcode product.
				radioButton = new JRadioButton(entry.getValue()[0] + " " + barcodeProduct.getDescription() + " for " + entry.getValue()[1]);
				radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	            group.add(radioButton);
	            panel.add(radioButton);
			} else if (product instanceof MiscProductsDatabase.Bag bagProduct){
				// Handle bags.
				radioButton = new JRadioButton(entry.getValue()[0] + " bags for " + entry.getValue()[1]);
				radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	            group.add(radioButton);
	            panel.add(radioButton);
			}
		}

        // Show pop-up.
        if (optionDialogPopup(panel, Language.translate(language, "Remove Item")) == JOptionPane.OK_OPTION) {
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
                // Match the product.
            	// Loop through each item in customer's cart.
                for (Map.Entry<Product, Number[]> entry : aioc.getCart(checkout).entrySet()) {
        			Product product = entry.getKey();
        			if (product instanceof PLUCodedProduct pluProduct) {
        				// Handle PLU product.
        				if (selectedProductDescription.equals(entry.getValue()[0] + " " + pluProduct.getDescription() + " for " + entry.getValue()[1])) {
        					aioc.removeItemFromOrder(checkout, product, new BigDecimal(entry.getValue()[0].toString()));
        					break;
        				}
        			} else if (product instanceof BarcodedProduct barcodeProduct) {
        				// Handle barcode product.
        				if (selectedProductDescription.equals(entry.getValue()[0] + " " + barcodeProduct.getDescription() + " for " + entry.getValue()[1])) {
        					aioc.removeItemFromOrder(checkout, product, new BigDecimal(entry.getValue()[0].toString()));
        					break;
        				}
        			} else if (product instanceof MiscProductsDatabase.Bag bagProduct){
        				// Handle bags.
        				if (selectedProductDescription.equals(entry.getValue()[0] + " bags for " + entry.getValue()[1])) {
        					aioc.removeItemFromOrder(checkout, product, new BigDecimal(entry.getValue()[0].toString()));
        					break;
        				}
        			}
        		}
            }
        }
	}

	/**
	 * Notify the attendant screen that a station is in use.
	 * Occurs after an attempt to shutdown a station.
	 * 
	 * @param checkout
	 * 			CheckoutController being shut down.
	 */
	public void notifyShutdownStationInUse(CheckoutController checkout) {
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
        if (yesNoPopup(panel) == JOptionPane.YES_OPTION) {
            // Force shutdown.
        	aioc.forceShutDownStation(checkout);
        	// Repopulate management panes
        	populateManagementPanes();
        }
	}
	
	/**
	 * Shows popup for yes/no selection
	 * @param panel
	 * @return int for whether the user clicked yes (0) or no (1)
	 */
	public int yesNoPopup(JPanel panel) {
		return JOptionPane.showOptionDialog(aioc.getDevice().getFrame(), panel, Language.translate(language, "Remove Item"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
	}
	
	/**
	 * Notify the attendant that a station has been started up.
	 * 
	 * @param checkout
	 * 			CheckoutController that started up.
	 */
	public void notifyStartup(CheckoutController checkout) {
		// Update management panes.
		populateManagementPanes();
	}
	
	/**
	 * Notify the attendant to confirm a customer's added bags.
	 * 
	 * @param checkout
	 * 			CheckoutController requesting confirmation.
	 */
	public void notifyConfirmAddedBags(CheckoutController checkout) {
		// Create notification data.
		JLabel label = new JLabel("Station #" + checkout.getID() + " needs bag confirmation!");
		button = new JButton("Confirm");
		JComponent[] data = new JComponent[] {label, button};
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// Approve bags.
            	aioc.approveAddedBags(checkout);
            	// Remove notification.
            	notificationsData.remove(data);
            	populateNotificationsPane();
            }
		});
		notificationsData.add(data);
		
		populateNotificationsPane();
	}
	
	/**
	 * Notify the attendant that a bill denomination is low.
	 * 
	 * @param checkout
	 * 			CheckoutController making request.
	 */
	public void notifyLowBillDenomination(CheckoutController checkout, BigDecimal denom) {
		// Create notification data.
		JLabel label = new JLabel("Station #" + checkout.getID() + " low bills: ($" + denom + ")");
		button = new JButton("Refilled");
		JComponent[] data = new JComponent[] {label, button};
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// Denomination refilled.
            	
            	// Remove notification.
            	notificationsData.remove(data);
            	populateNotificationsPane();
            }
		});
		notificationsData.add(data);
		
		populateNotificationsPane();
	}
	
	/**
	 * Notify the attendant that a coin denomination is low.
	 * 
	 * @param checkout CheckoutController making request.
	 */
	public void notifyLowCoinDenomination(CheckoutController checkout, BigDecimal denom) {
		// Create notification data.
		JLabel label = new JLabel("Station #" + checkout.getID() + " low coins: ($" + denom + ")");
		button = new JButton("Refilled");
		JComponent[] data = new JComponent[] {label, button};
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// Denomination refilled.
            	
            	// Remove notification.
            	notificationsData.remove(data);
            	populateNotificationsPane();
            }
		});
		notificationsData.add(data);
		
		populateNotificationsPane();
	}
	
	/**
	 * Notify the attendant that paper is low.
	 * 
	 * @param checkout
	 * 			CheckoutController requesting confirmation.
	 * @param printer
	 * 			ReceiptPrinterController with the issue.
	 */
	public void notifyLowPaper(CheckoutController checkout, ReceiptPrinterController printer) {
		// Create notification data.
		String issueText = "Station #" + checkout.getID() + " is low on paper!";
		JLabel label = new JLabel(issueText);
		button = new JButton("Acknowledge");
		JComponent[] data = new JComponent[] {label, button};
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// Notification acknowledged.
            	
            	// Remove notification.
            	notificationsData.remove(data);
            	populateNotificationsPane();
            	
            	// Add to active issues.
            	activeIssues.add(issueText);
            	populateActiveIssuesPane();

            	// Send acknowledgement
            	aioc.receiveLowPaperAcknowledgement(checkout, printer);
            }
		});
		notificationsData.add(data);
		
		populateNotificationsPane();
	}
	
	/**
	 * Notify the attendant that a low paper issue has been resolved.
	 *
	 * @param checkout
	 * 			CheckoutController with the resolved issue.
	 */
	public void notifyLowPaperResolved(CheckoutController checkout) {
		// Remove issue text from activeIssues.
		String issueText = "Station #" + checkout.getID() + " is low on paper!";
		activeIssues.remove(issueText);
		populateActiveIssuesPane();
	}

	/**
	 * Notify the attendant that ink is low.
	 * 
	 * @param checkout
	 * 			CheckoutController requesting confirmation.
	 * @param printer
	 * 			ReceiptPrinterController with the issue.
	 */
	public void notifyLowInk(CheckoutController checkout, ReceiptPrinterController printer) {
		// Create notification data.
		String issueText = "Station #" + checkout.getID() + " is low on ink!";
		JLabel label = new JLabel(issueText);
		button = new JButton("Acknowledge");
		JComponent[] data = new JComponent[] {label, button};
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// Notification acknowledged.
            	
            	// Remove notification.
            	notificationsData.remove(data);
            	populateNotificationsPane();
            	
            	// Add to active issues.
            	activeIssues.add(issueText);
            	populateActiveIssuesPane();

            	// Send acknowledgement
            	aioc.receiveLowInkAcknowledgement(checkout, printer);
            }
		});
		notificationsData.add(data);
		
		populateNotificationsPane();
	}

	/**
	 * Notify the attendant that a low ink issue has been resolved.
	 *
	 * @param checkout
	 * 			CheckoutController with the resolved issue.
	 */
	public void notifyLowInkResolved(CheckoutController checkout) {
		// Remove issue text from activeIssues.
		String issueText = "Station #" + checkout.getID() + " is low on ink!";
		activeIssues.remove(issueText);
		populateActiveIssuesPane();
	}
	
	/**
	 * Receive a message.
	 * 
	 * @param message
	 * 			Message being received.
	 */
	public void receiveMessage(String message) {
		// Create notification data.
		JLabel label = new JLabel(message);
		button = new JButton("Resolve");
		JComponent[] data = new JComponent[] {label, button};
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// Notification resolved.
            	
            	// Remove notification.
            	notificationsData.remove(data);
            	populateNotificationsPane();
            }
		});
		notificationsData.add(data);
		
		populateNotificationsPane();
	}
	
	/**
	 * Notify the attendant about a no bag request.
	 * 
	 * @param checkout
	 * 			CheckoutController requesting confirmation.
	 */
	public void notifyNoBag(CheckoutController checkout) {
		// Create notification data.
		String issueText = "Station #" + checkout.getID() + " made a no bag request!";
		JLabel label = new JLabel(issueText);
		button = new JButton("Approve");
		JComponent[] data = new JComponent[] {label, button};
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// Notification approved.
            	
            	// Remove notification.
            	notificationsData.remove(data);
            	populateNotificationsPane();

            	// Send approval.
            	aioc.approveNoBagRequest(checkout);
            }
		});
		notificationsData.add(data);
		
		populateNotificationsPane();
	}
	
	/**
	 * Notify the attendant about a weight discrepancy.
	 * 
	 * @param checkout
	 * 			CheckoutController requesting confirmation.
	 */
	public void notifyWeightDiscrepancy(CheckoutController checkout) {
		// Create notification data.
		String issueText = "Station #" + checkout.getID() + " has a weight discrepancy!";
		JLabel label = new JLabel(issueText);
		button = new JButton("Approve");
		JComponent[] data = new JComponent[] {label, button};
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// Notification approved.
            	
            	// Remove notification.
            	notificationsData.remove(data);
            	populateNotificationsPane();

            	// Send approval.
            	aioc.approveWeightDiscrepancy(checkout);
            }
		});
		notificationsData.add(data);
		
		populateNotificationsPane();
	}
	
	/**
	 * Notify the attendant about a need for a receipt reprint.
	 * 
	 * @param checkout
	 * 			CheckoutController requesting confirmation.
	 */
	public void notifyReceiptRePrint(CheckoutController checkout, StringBuilder receipt) {
		// Create notification data.
		String issueText = "Station #" + checkout.getID() + " needs a receipt reprint!";
		JLabel label = new JLabel(issueText);
		button = new JButton("Reprint");
		JComponent[] data = new JComponent[] {label, button};
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// Notification approved.
            	
            	// Remove notification.
            	notificationsData.remove(data);
            	populateNotificationsPane();

            	// Send reprint
            	aioc.rePrintReceipt(checkout, receipt);
            }
		});
		notificationsData.add(data);
		
		populateNotificationsPane();
	}
}