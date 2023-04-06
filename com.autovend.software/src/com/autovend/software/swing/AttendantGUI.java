package com.autovend.software.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Class for the Attendant GUI screen.
 */
public class AttendantGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private String language;

	/**
	 * TODO: Delete this method for final product.
	 * 
	 * Used to launch GUI when run.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AttendantGUI frame = new AttendantGUI("English");
					// Center it
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
	public AttendantGUI(String language) {
		super("Attendant GUI");
		
		// Set language.
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
		// TODO: Language selector.
		
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
		JTextField usernameTextField = new JTextField();
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
		JPasswordField passwordTextField = new JPasswordField();
		passwordTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		passwordTextField.setColumns(10);
		passwordTextField.setBounds(330, 350, 192, 38);
		passwordTextField.setEchoChar('*');
		loginContentPane.add(passwordTextField);
		
		// Create login button.
		JButton loginButton = new JButton(Language.translate(language, "Log In"));
		loginButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Login button pressed.
				
				// Clear text fields.
				usernameTextField.setText("");
				passwordTextField.setText("");
				
				// TODO: Verify login information 
				
				// Switch to regular operation screen (login successful).
				showOperationScreen();
			}
		});
		loginButton.setBounds(324, 424, 120, 63);
		loginContentPane.add(loginButton);
		
		// Change frame pane to login.
		setContentPane(loginContentPane);
		
		// Refresh frame.
		revalidate();
		repaint();
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
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Logout button pressed.
				
				// TODO: Confirmation message.
				
				// Switch to login screen.
				showLoginScreen();
			}
		});
		logoutButton.setBounds(324, 455, 120, 63);
		operationContentPane.add(logoutButton);
		
		// Change frame pane to regular operation.
		setContentPane(operationContentPane);
		
		// Refresh frame.
		revalidate();
		repaint();
	}
	
}
