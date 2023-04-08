package com.autovend.software.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.autovend.software.controllers.AttendantIOController;

/**
 * A class for the attendant login pane.
 */
public class AttendantLoginPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private AttendantIOController aioc;
	private String language = "English";
	private String[] languages = new String[] {"English", "French"};
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JButton loginButton;
	private JButton languageSelectButton;
	private JLabel errorLabel;
	
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
		usernameLabel = new JLabel(Language.translate(language, "Username:"));
		usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		usernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		usernameLabel.setBounds(118, 296, 202, 47);
		this.add(usernameLabel);
		
		// Create username text field.
		JTextField usernameTextField = new JTextField();
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
		JPasswordField passwordTextField = new JPasswordField();
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
                        usernameLabel.setText(Language.translate(language, "Username:"));
                        passwordLabel.setText(Language.translate(language, "Password:"));
                        loginButton.setText(Language.translate(language, "Log In"));
                        languageSelectButton.setText(Language.translate(language, "Change Language"));
                    }
                }
            }
        });
        languageSelectButton.setBounds(700, 700, 200, 50);
        this.add(languageSelectButton);

	}
	
	/**
	 * Display a login error message.
	 */
	public void showLoginError() {
		// Create error label.
		JLabel errorLabel = new JLabel(Language.translate(language, "Invalid username or password, try again."));
		errorLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		errorLabel.setForeground(Color.RED);
		errorLabel.setBounds(218, 375, 200, 47);
		this.add(errorLabel);
	}
}
