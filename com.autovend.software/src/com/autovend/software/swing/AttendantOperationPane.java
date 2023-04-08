package com.autovend.software.swing;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import com.autovend.devices.SupervisionStation;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CustomerIOController;
import javax.swing.JScrollPane;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.Component;

/**
 * A class for the attendant operation pane.
 */
public class AttendantOperationPane extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private AttendantIOController aioc;
	public JButton logoutButton;
	public JPanel manageEnabledPane;
	public JPanel manageDisabledPane;
	
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
		logoutButton = new JButton("Log Out");
		logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		logoutButton.setBounds(586, 645, 120, 63);
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
	 * Initialize notifications pane.
	 */
	public void initializeNotificationsPane() {
		// Create label for notifications panel.
		JLabel notificationsLabel = new JLabel("Station Notifications:");
		notificationsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		notificationsLabel.setBounds(21, 30, 173, 14);
		this.add(notificationsLabel);
		
		// Create panel for notifications.
		JPanel notificationsPane = new JPanel();
		notificationsPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		notificationsPane.setBounds(21, 55, 299, 304);
		this.add(notificationsPane);
	}
	
	/**
	 * Initialize enabled and disabled station management panes.
	 */
	public void initializeManagementPanes() {
		// Create label for panel with all active stations.
		JLabel manageEnabledLabel = new JLabel("Manage Enabled Stations:");
		manageEnabledLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		manageEnabledLabel.setBounds(21, 536, 181, 23);
		this.add(manageEnabledLabel);
		
		// Create panel for managing enabled stations.
		manageEnabledPane = new JPanel();
		manageEnabledPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		manageEnabledPane.setBounds(21, 562, 181, 201);
		this.add(manageEnabledPane);
		manageEnabledPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// Create label for panel with all inactive stations.
		JLabel manageDisabledLabel = new JLabel("Manage Disabled Stations:");
		manageDisabledLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		manageDisabledLabel.setBounds(215, 536, 181, 23);
		this.add(manageDisabledLabel);
		
		// Create panel for managing disabled stations.
		manageDisabledPane = new JPanel();
		manageDisabledPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		manageDisabledPane.setBounds(215, 562, 181, 201);
		this.add(manageDisabledPane);
		manageDisabledPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// Populate the panes.
		populateManagementPanes();
	}
	
	/**
	 * Populates the enabled and disabled station management panes.
	 */
	public void populateManagementPanes() {
		// Add each station to enabled/disabled pane.
		for (CustomerIOController cioc : aioc.getAllStationsIOControllers()) {
			if (cioc.getMainController().isDisabled()) {
				// Add disabled station to disabled pane.
				JButton btn = new JButton("Station #" + cioc.getMainController().getID());
				addDisabledActionPopup(btn, cioc);
				manageDisabledPane.add(btn);
			} else {
				// Add enabled station to enabled pane.
				JButton btn = new JButton("Station #" + cioc.getMainController().getID());
				btn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Active station button pressed.
						System.out.println("Enabled Station pressed!");
					}
				});
				manageEnabledPane.add(btn);
			}
		}
	}
	
	/**
	 * Adds the action pop-up menu for an enabled station.
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
                for (String action : new String[] {"Enable Station"}) {
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
	 * Process an action on a customer station.
	 * 
	 * @param action
	 * 			Action to be performed.
	 * @param cioc
	 * 			CustomerIOController to perform the action on.
	 */
	public void processAction(String action, CustomerIOController cioc ) {
		if (action.equalsIgnoreCase("Enable Station")) {
			aioc.disableStation(cioc.getMainController());
			// Reinitialize management panes.
			initializeManagementPanes();
			
			// fixing bugs.
			
			System.out.println(cioc.getMainController().isDisabled());
			
//			System.out.println("logged in:" + aioc.getMainAttendantController().isLoggedIn());
		}
		
		// Refresh screen.
		this.revalidate();
		this.repaint();
		
	}
}
