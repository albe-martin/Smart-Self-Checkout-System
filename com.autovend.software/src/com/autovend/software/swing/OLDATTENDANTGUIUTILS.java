// package com.autovend.software.swing;
//package com.autovend.software.swing;
//
//import java.awt.Container;
//import java.awt.Font;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JPasswordField;
//import javax.swing.JTextField;
//import javax.swing.border.EmptyBorder;
//
//import com.autovend.software.controllers.AttendantIOController;
//
///**
// * AttendantGUI utility class. Provides methods that return different attendant screens.
// */
//public class AttendantGUIUtils {
//	
//	/**
//	 * Creates an attendant login screen pane.
//	 * @param aioc
//	 * 			AttendantIOController to signal events to.
//	 * @return
//	 * 			Login screen pane.
//	 */
//	public static Container getLoginPane(AttendantIOController aioc) {
//			// Create login screen pane.
//			JPanel loginContentPane = new JPanel();
//			loginContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//			loginContentPane.setLayout(null);
//			
//			// Create username label.
//			JLabel usernameLabel = new JLabel("Username");
//			usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
//			usernameLabel.setBounds(218, 296, 102, 47);
//			loginContentPane.add(usernameLabel);
//			
//			// Create username text field.
//			JTextField usernameTextField = new JTextField();
//			usernameTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
//			usernameTextField.setBounds(330, 301, 192, 38);
//			loginContentPane.add(usernameTextField);
//			usernameTextField.setColumns(10);
//			
//			// Create password label.
//			JLabel passwordLabel = new JLabel("Password:");
//			passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
//			passwordLabel.setBounds(218, 345, 102, 47);
//			loginContentPane.add(passwordLabel);
//			
//			// Create password text field.
//			JPasswordField passwordTextField = new JPasswordField();
//			passwordTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
//			passwordTextField.setColumns(10);
//			passwordTextField.setBounds(330, 350, 192, 38);
//			passwordTextField.setEchoChar('*');
//			loginContentPane.add(passwordTextField);
//			
//			// Create login button.
//			JButton loginButton = new JButton("Log In");
//			loginButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
//			loginButton.setBounds(311, 424, 146, 63);
//			loginButton.setActionCommand("login");
//			loginButton.addActionListener(new ActionListener() {
//	            public void actionPerformed(ActionEvent e) {
//	                // Login button pressed
//
//	            	// Make call for password verification
//	            	aioc.login(usernameTextField.getText(), String.valueOf(passwordTextField.getPassword()));
//	            }
//	        });
//			loginContentPane.add(loginButton);
//			
//			return loginContentPane;		
//	}
//	
//	/**
//	 * Creates an attendant operation screen pane.
//	 * @param aioc
//	 * 			AttendantIOController to signal events to.
//	 * @return
//	 * 			Operation screen pane.
//	 */
//	public static Container getOperationPane(AttendantIOController aioc) {
//		// Create login screen pane.
//		JPanel operationContentPane = new JPanel();
//		operationContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//		operationContentPane.setLayout(null);
//		
//		// Create logout button.
//		JButton logoutButton = new JButton("Log Out");
//		logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
//		logoutButton.setBounds(324, 455, 120, 63);
//		logoutButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                // Login button pressed
//
//            	// Request logout
//            	aioc.logout();
//            	System.out.println("logout requested");
//            }
//        });
//		operationContentPane.add(logoutButton);
//		
//		// Return regular operation pane
//		return operationContentPane;
//	}
//}
