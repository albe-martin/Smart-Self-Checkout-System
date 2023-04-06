package com.autovend.software.swing;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class CustomerGUI extends JFrame {

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
					CustomerGUI frame = new CustomerGUI("English");
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
	 * Create the customer GUI frame.
	 */
	public CustomerGUI(String language) {
		super("Customer GUI");
		
		// Set language.
		this.language = language;
		
		// Set frame properties.
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 800, 800);

		// Start on the login screen.
		showStartScreen();
	}
	
	/**
	 * Switches to the start screen.
	 */
	public void showStartScreen() {
		// TODO: Language selector.
		
		// Create start screen pane.
		JPanel startContentPane = new JPanel();
		startContentPane.setLayout(null);
		startContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		ImageIcon startImage = new ImageIcon(getClass().getResource("resources/start.png"));
		
		// Create login button.
		JButton startButton = new JButton(startImage);
		startButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Start button pressed.
				
				// Switch to regular operation screen.
				showOperationScreen();
			}
		});

		startButton.setBounds(200, 200, startImage.getIconWidth(), startImage.getIconHeight());
//		startButton.setIcon(new ImageIcon("~/Desktop/start.png"));
		startContentPane.add(startButton);

		JButton languageSelectButton = new JButton(Language.translate(language, "Select Language"));
		languageSelectButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		languageSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// set users language
				// pop up to give different languages to select from?
				String newLanguage = "English";

				language = newLanguage;
				// will have to update select language button and start button text's with new language text after switching
			}
		});
		languageSelectButton.setBounds(this.getWidth()/2 - 75, 500, 150, 50);

		startContentPane.add(languageSelectButton);
		
		// Change frame pane to login.
		setContentPane(startContentPane);
		
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
		
		// Create exit button. TODO: Replace with cart functionalities
		JButton logoutButton = new JButton(Language.translate(language, "Exit"));
		logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Logout button pressed.
				
				// Switch to login screen.
				showStartScreen();
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
