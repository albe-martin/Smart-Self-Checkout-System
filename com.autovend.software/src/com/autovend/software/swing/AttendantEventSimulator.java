package com.autovend.software.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.autovend.software.controllers.CustomerIOController;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * GUI used to simulate events related to the Attendant.
 * Used for the project demonstration.
 * 
 * TODO: Remove for final submission after demonstration is complete.
 * 
 * Should not be tested.
 */
public class AttendantEventSimulator extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public AttendantEventSimulator(JFrame attendantFrame, CustomerIOController cioc1, CustomerIOController cioc2) {
		
		this.setTitle("Attendant Event Simulator");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{240, 240, 0};
		gbl_contentPane.rowHeights = new int[]{0, 50, 50, 50, 50, 50, 50, 50};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JButton bagRequestButton1 = new JButton("Create Bag Confirmation Request (1)");
		bagRequestButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Simulate bag request
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyConfirmAddedBags(cioc1);
			}
		});
		GridBagConstraints gbc_bagRequestButton1 = new GridBagConstraints();
		gbc_bagRequestButton1.fill = GridBagConstraints.BOTH;
		gbc_bagRequestButton1.gridx = 0;
		gbc_bagRequestButton1.gridy = 0;
		contentPane.add(bagRequestButton1, gbc_bagRequestButton1);
		
		JButton bagRequestButton2 = new JButton("Create Bag Confirmation Request (2)");
		bagRequestButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Simulate bag request
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyConfirmAddedBags(cioc2);
			}
		});
		GridBagConstraints gbc_bagRequestButton2 = new GridBagConstraints();
		gbc_bagRequestButton2.fill = GridBagConstraints.BOTH;
		gbc_bagRequestButton2.gridx = 1;
		gbc_bagRequestButton2.gridy = 0;
		contentPane.add(bagRequestButton2, gbc_bagRequestButton2);
	}

}
