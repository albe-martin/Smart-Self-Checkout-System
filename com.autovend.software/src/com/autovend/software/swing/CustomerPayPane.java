package com.autovend.software.swing;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.autovend.software.controllers.CustomerIOController;

public class CustomerPayPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton cancelButton;
	private CustomerIOController cioc;

	public CustomerPayPane(CustomerIOController cioc) {
		super();
		this.cioc = cioc;
		initializePayPane();
	}

	private void initializePayPane() {
		// Create operation screen pane.
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setLayout(null);
        
        initializeCancelButton();

	}

	private void initializeCancelButton() {
		// Create exit button.
		cancelButton = new JButton("Cancel Pay");
		cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Cancel pay button pressed.

                // Notify controller that cancel pay is requested.
//                cioc.cancelPayPressed();
            }
        });
		cancelButton.setBounds(511, 503, 120, 63);
        this.add(cancelButton);
	}
	
}
