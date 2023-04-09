package com.autovend.software.swing;

import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.autovend.devices.SupervisionStation;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;

/**
 * A class for the attendant login pane.
 */
public class AttendantLoginPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private AttendantIOController aioc;
	public String language = "English";
	// TODO: Have English be the only built in language
	public String[] languages = new String[] {"English", "French"};
	public JLabel usernameLabel;
	public JTextField usernameTextField;
	public JLabel passwordLabel;
	public JPasswordField passwordTextField;
	public JButton loginButton;
	public JButton languageSelectButton;
	public JLabel errorLabel;
    public JOptionPane options;    
    public ButtonGroup group;


	
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
		attendantScreen.setContentPane(new AttendantLoginPane(aioc));
		
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
	public AttendantLoginPane(AttendantIOController aioc) {
		super();
		this.aioc = aioc;
		initializeLoginPane();
	}
	
	/**
	 * Initializes attendant login pane.
	 */
	private void initializeLoginPane() {
		// Create login screen pane.
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(null);
		
		// Initialize login controls.
		initializeLoginControls();
		
		// Initialize language select button
		initializeLanguageSelectButton();
	}
	
	/**
	 * Initialize login controls.
	 */
	private void initializeLoginControls() {
		// Create username label.
		usernameLabel = new JLabel(Language.translate(language, "Username:"));
		usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		usernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		usernameLabel.setBounds(118, 296, 202, 47);
		this.add(usernameLabel);
		
		// Create username text field.
		usernameTextField = new JTextField();
		usernameTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		usernameTextField.setBounds(330, 301, 192, 38);
		this.add(usernameTextField);
		usernameTextField.setColumns(10);
		
		// Create password label.
		passwordLabel = new JLabel(Language.translate(language, "Password:"));
		passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		passwordLabel.setBounds(118, 345, 202, 47);
		this.add(passwordLabel);
		
		// Create password text field.
		passwordTextField = new JPasswordField();
		passwordTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		passwordTextField.setColumns(10);
		passwordTextField.setBounds(330, 350, 192, 38);
		passwordTextField.setEchoChar('*');
		this.add(passwordTextField);
		
		// Create login button.
		loginButton = new JButton(Language.translate(language, "Log In"));
		loginButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		loginButton.setBounds(311, 424, 146, 63);
		loginButton.setActionCommand("login");
		loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Login button pressed

            	// Make call for password verification
            	aioc.login(usernameTextField.getText(), String.valueOf(passwordTextField.getPassword()));
            }
        });
		this.add(loginButton);	
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
                if (optionDialogPopup(panel) == JOptionPane.OK_OPTION) {
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
                        usernameLabel.setText(Language.translate(language, "Username:"));
                        passwordLabel.setText(Language.translate(language, "Password:"));
                        loginButton.setText(Language.translate(language, "Log In"));
                        languageSelectButton.setText(Language.translate(language, "Change Language"));
                    }
                }
            }
        });
        languageSelectButton.setBounds(291, 642, 200, 50);
        this.add(languageSelectButton);
	}
	
	public int optionDialogPopup(JPanel panel) {
        return JOptionPane.showOptionDialog(null, panel, "Language Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
	}
	
	/**
	 * Display a login error message.
	 */
	public void showLoginError() {
		// Create error label.
		JLabel errorLabel = new JLabel(Language.translate(language, "Invalid username or password, try again."));
		errorLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		errorLabel.setForeground(Color.RED);
		errorLabel.setBounds(293, 393, 209, 20);
		this.add(errorLabel);
		
		// Refresh screen
		this.revalidate();
		this.repaint();
	}
}
