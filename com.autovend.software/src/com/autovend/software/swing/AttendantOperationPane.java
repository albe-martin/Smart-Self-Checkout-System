package com.autovend.software.swing;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.autovend.software.controllers.AttendantIOController;

/**
 * A class for the attendant operation pane.
 */
public class AttendantOperationPane extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private AttendantIOController aioc;
	
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
		
		// Create logout button.
		JButton logoutButton = new JButton("Log Out");
		logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		logoutButton.setBounds(324, 455, 120, 63);
		logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Login button pressed

            	// Request logout
            	aioc.logout();
            }
        });
		this.add(logoutButton);
	}
}
