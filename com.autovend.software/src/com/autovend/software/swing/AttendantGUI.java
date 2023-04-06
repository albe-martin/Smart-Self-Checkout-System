package com.autovend.software.swing;

import java.awt.EventQueue;
//github.com/KittenInAMitten/SENG300-iteration-3
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Class for the Attendant GUI screen.
 */
public class AttendantGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private String[] languages;
	private String language;
	
	// Action commands
	protected final static String LOGIN = "login";
	protected final static String CHOOSE_LANGUAGE = "choose_language";
	protected final static String LOGOUT = "logout";
	
	/**
	 * Attendant screen modes.
	 */
	private enum Modes {
		LOGIN_SCREEN,
		OPERATION_SCREEN
	}
	
	// Current attendant screen mode.
	private Modes mode = Modes.LOGIN_SCREEN;
	
	// Components
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	private LanguageSelectorPopup languageSelector;

	/**
	 * TODO: Delete this method for final product.
	 * 
	 * Used to launch GUI when run.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// French language demo (to be removed for final project).
//					HashMap<String, String> french = new HashMap<>();
//					french.put("Username:", "Baguette:");
//					french.put("Password:", "Bonjour:");
//					french.put("Log In", "Paris");
//					Language.addLanguage("French", french);
					
					// Create attendant GUI
					AttendantGUI frame = new AttendantGUI("English", new String[] {"English", "French"});
					// Center attendant GUI
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the attendant GUI frame.
	 */
	public AttendantGUI(String language, String[] languages) {
		super("Attendant GUI");
		
		// Set language.
		this.languages = languages;
		this.language = language;
		
		// Set frame properties.
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 800, 800);

		// Start on the login screen.
		showLoginScreen();
	}
	
	/**
	 * Switches to the login screen.
	 */
	public void showLoginScreen() {
		// Create login screen pane.
		JPanel loginContentPane = new JPanel();
		loginContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		loginContentPane.setLayout(null);
		
		// Create username label.
		JLabel usernameLabel = new JLabel(Language.translate(language, "Username:"));
		usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		usernameLabel.setBounds(218, 296, 102, 47);
		loginContentPane.add(usernameLabel);
		
		// Create username text field.
		usernameTextField = new JTextField();
		usernameTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		usernameTextField.setBounds(330, 301, 192, 38);
		loginContentPane.add(usernameTextField);
		usernameTextField.setColumns(10);
		
		// Create password label.
		JLabel passwordLabel = new JLabel(Language.translate(language, "Password:"));
		passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		passwordLabel.setBounds(218, 345, 102, 47);
		loginContentPane.add(passwordLabel);
		
		// Create password text field.
		passwordTextField = new JPasswordField();
		passwordTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		passwordTextField.setColumns(10);
		passwordTextField.setBounds(330, 350, 192, 38);
		passwordTextField.setEchoChar('*');
		loginContentPane.add(passwordTextField);
		
		// Create login button.
		JButton loginButton = new JButton(Language.translate(language, "Log In"));
		loginButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		loginButton.setBounds(311, 424, 146, 63);
		loginButton.setActionCommand(LOGIN);
		loginButton.addActionListener(this);
		loginContentPane.add(loginButton);
		
		// Create choose language button.
		JButton changeLanguageButton = new JButton("Change Language");
		changeLanguageButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		changeLanguageButton.setBounds(530, 671, 162, 30);
		changeLanguageButton.setActionCommand(CHOOSE_LANGUAGE);
		changeLanguageButton.addActionListener(this);
		loginContentPane.add(changeLanguageButton);
		
		// Change frame pane to login.
		setContentPane(loginContentPane);
				
		// Refresh frame.
		revalidate();
		repaint();
		
		// Set mode
		mode = Modes.LOGIN_SCREEN;
	}
	
	/**
	 * Switches to the regular operation screen.
	 */
	public void showOperationScreen() {
		// Create login screen pane.
		JPanel operationContentPane = new JPanel();
		operationContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		operationContentPane.setLayout(null);
		
		// Create logout button.
		JButton logoutButton = new JButton(Language.translate(language, "Log Out"));
		logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		logoutButton.setBounds(324, 455, 120, 63);
		logoutButton.setActionCommand(LOGOUT);
		logoutButton.addActionListener(this);
		operationContentPane.add(logoutButton);
		
		// Change frame pane to regular operation.
		setContentPane(operationContentPane);
		
		// Refresh frame.
		revalidate();
		repaint();
		
		// Set mode
		mode = Modes.OPERATION_SCREEN;
	}
	
	/**
	 * Handle GUI events.
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals(LOGIN)) {
			// Login button pressed.
			
			// Clear text fields.
			usernameTextField.setText("");
			passwordTextField.setText("");
			
			// TODO: Verify login information 
			
			// Switch to regular operation screen (login successful).
			showOperationScreen();
		} else if (command.equals(CHOOSE_LANGUAGE)) {
			// Choose language button pressed
			
			// Create language selector pop-up.
			createLanguageSelector();
		} else if (command.equals(LanguageSelectorPopup.LANGUAGE_CHANGED)) {
			// Language changed
			
			// Update new language.
			language = languageSelector.getLanguage();
			
			// Close pop-up.
			languageSelector.setVisible(false);
            languageSelector.dispose();
            
            // Redraw screen.
            if (mode == Modes.LOGIN_SCREEN) {
            	showLoginScreen();
            }
            // TODO: Redraw screen for operation mode.
		} else if (command.equals(LOGOUT)) {
			// Logout button pressed.
			
			// TODO: Confirmation message.
			
			// Switch to login screen.
			showLoginScreen();
		}
	}
	
	/**
	 * Creates language selector pop-up.
	 */
	public void createLanguageSelector() {
		// Create pop-up
		languageSelector = new LanguageSelectorPopup(this, language, languages);
		
		// Set pup-up position
		Point position = getLocation();
		position.x += 250;
		position.y += 250;
		languageSelector.setLocation(position);
		
        // Show pup-up
        languageSelector.setVisible(true);
	}
	
	
}
