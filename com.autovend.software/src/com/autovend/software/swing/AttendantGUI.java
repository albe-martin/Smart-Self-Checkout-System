package com.autovend.software.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
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
	private JPanel contentPane;
	private JTextField usernameTextField;
	private JTextField passwordTextField;

	/**
	 * Launch the AttendantGUI application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AttendantGUI frame = new AttendantGUI();
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
	 * Create the AttendantGUI frame.
	 */
	public AttendantGUI() {
		// TODO: Have way to select language.
		String language = "English";
		
		
		// Frame properties
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Content frame
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Username label
		JLabel usernameLabel = new JLabel(Language.translate(language, "Username:"));
		usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		usernameLabel.setBounds(218, 296, 102, 47);
		contentPane.add(usernameLabel);
		
		// Username text field
		usernameTextField = new JTextField();
		usernameTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		usernameTextField.setBounds(330, 301, 192, 38);
		contentPane.add(usernameTextField);
		usernameTextField.setColumns(10);
		
		// Password label
		JLabel passwordLabel = new JLabel(Language.translate(language, "Password:"));
		passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		passwordLabel.setBounds(218, 345, 102, 47);
		contentPane.add(passwordLabel);
		
		// Password text field
		passwordTextField = new JTextField();
		passwordTextField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		passwordTextField.setColumns(10);
		passwordTextField.setBounds(330, 350, 192, 38);
		contentPane.add(passwordTextField);
		
		// Login button
		JButton loginButton = new JButton(Language.translate(language, "Log In"));
		loginButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Login button pressed
				
				// Clear text fields
				usernameTextField.setText("");
				passwordTextField.setText("");
				
				// TODO: Verify login information and move to logged in screen
			}
		});
		loginButton.setBounds(324, 424, 120, 63);
		contentPane.add(loginButton);
	}
}
