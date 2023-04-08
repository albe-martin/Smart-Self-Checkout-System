package com.autovend.software.swing;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.autovend.software.controllers.CustomerIOController;

/**
 * A class for  the customer start pane.
 */
public class CustomerStartPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private CustomerIOController cioc;
	
	/**
	 * Basic constructor.
	 * 
	 * @param cioc
	 * 			Linked CustomerIOController.
	 */
	public CustomerStartPane(CustomerIOController cioc) {
		super();
		this.cioc = cioc;
		initializeStartPane();
	}
	
	/**
	 * Initialize customer start pane.
	 */
	private void initializeStartPane() {
		// Create start screen pane.
        this.setLayout(null);
        this.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Create login button.
        JButton startButton = new JButton("START");
        startButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Start button pressed.

                // Notify controller that start was pressed.
            	cioc.startPressed();
            }
        });
        startButton.setBounds(290, 200, 200, 200);
        this.add(startButton);
	}
}
