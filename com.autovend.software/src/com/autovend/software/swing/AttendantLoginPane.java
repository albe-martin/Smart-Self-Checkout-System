package com.autovend.software.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.autovend.software.controllers.AttendantIOController;

/**
 * A class for the attendant login pane.
 */
public class AttendantLoginPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private AttendantIOController aioc;
	
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
		
		// Create username label.
		JLabel usernameLabel = new JLabel("Username");
		usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		usernameLabel.setBounds(218, 296, 102, 47);
		this.add(usernameLabel);
		
		// Create username text field.
		JTextField usernameTextField = new JTextField();
		usernameTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		usernameTextField.setBounds(330, 301, 192, 38);
		this.add(usernameTextField);
		usernameTextField.setColumns(10);
		
		// Create password label.
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		passwordLabel.setBounds(218, 345, 102, 47);
		this.add(passwordLabel);
		
		// Create password text field.
		JPasswordField passwordTextField = new JPasswordField();
		passwordTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		passwordTextField.setColumns(10);
		passwordTextField.setBounds(330, 350, 192, 38);
		passwordTextField.setEchoChar('*');
		this.add(passwordTextField);
		
		// Create login button.
		JButton loginButton = new JButton("Log In");
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
	 * Display a login error message.
	 */
	public void showLoginError() {
		// Create error label.
		JLabel passwordLabel = new JLabel("Invalid username or password, try again.");
		passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		passwordLabel.setForeground(Color.RED);
		passwordLabel.setBounds(218, 375, 200, 47);
		this.add(passwordLabel);
	}
}
