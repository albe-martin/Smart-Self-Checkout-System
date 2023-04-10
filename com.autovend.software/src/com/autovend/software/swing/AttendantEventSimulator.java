package com.autovend.software.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.autovend.software.controllers.CustomerIOController;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

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
		
		setTitle("Attendant Event Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{293, 293, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		// Bag request events
		JButton bagRequest1 = new JButton("Create Bag Confirmation Request (1)");
		bagRequest1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyConfirmAddedBags(cioc1);
			}
		});
		GridBagConstraints gbcBag1 = new GridBagConstraints();
		gbcBag1.fill = GridBagConstraints.BOTH;
		gbcBag1.gridx = 0;
		gbcBag1.gridy = 0;
		contentPane.add(bagRequest1, gbcBag1);
		
		JButton bagRequest2 = new JButton("Create Bag Confirmation Request (2)");
		bagRequest2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyConfirmAddedBags(cioc2);
			}
		});
		GridBagConstraints gbcBag2 = new GridBagConstraints();
		gbcBag2.fill = GridBagConstraints.BOTH;
		gbcBag2.gridx = 1;
		gbcBag2.gridy = 0;
		contentPane.add(bagRequest2, gbcBag2);
		
		// Low coins events
		JButton lowChange1 = new JButton("Create Low Coin Notification (1)");
		lowChange1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowCoinDenomination(cioc1, new BigDecimal("0.25"));
			}
		});
		GridBagConstraints gbcChange1 = new GridBagConstraints();
		gbcChange1.fill = GridBagConstraints.BOTH;
		gbcChange1.gridx = 0;
		gbcChange1.gridy = 1;
		contentPane.add(lowChange1, gbcChange1);
		
		JButton lowChange2 = new JButton("Create Low Coin Notification (2)");
		lowChange2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowCoinDenomination(cioc2, new BigDecimal("0.10"));
			}
		});
		GridBagConstraints gbcCoin2 = new GridBagConstraints();
		gbcCoin2.fill = GridBagConstraints.BOTH;
		gbcCoin2.gridx = 1;
		gbcCoin2.gridy = 1;
		contentPane.add(lowChange2, gbcCoin2);
		
		// Low bills events
		JButton lowBill1 = new JButton("Create Low Bill Notification (1)");
		lowBill1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowBillDenomination(cioc1, new BigDecimal("5"));
			}
		});
		GridBagConstraints gbcBill1 = new GridBagConstraints();
		gbcBill1.fill = GridBagConstraints.BOTH;
		gbcBill1.gridx = 0;
		gbcBill1.gridy = 2;
		contentPane.add(lowBill1, gbcBill1);
		
		JButton lowBill2 = new JButton("Create Low Bill Notification (2)");
		lowBill2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowBillDenomination(cioc2, new BigDecimal("20"));
			}
		});
		GridBagConstraints gbcBill2 = new GridBagConstraints();
		gbcBill2.fill = GridBagConstraints.BOTH;
		gbcBill2.gridx = 1;
		gbcBill2.gridy = 2;
		contentPane.add(lowBill2, gbcBill2);
		
		// Low ink events.
		JButton lowink1 = new JButton("Create Low Ink Notification (1)");
		lowink1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowInk(cioc1);
			}
		});
		GridBagConstraints gbcink1 = new GridBagConstraints();
		gbcink1.fill = GridBagConstraints.BOTH;
		gbcink1.gridx = 0;
		gbcink1.gridy = 3;
		contentPane.add(lowink1, gbcink1);
		
		JButton lowink2 = new JButton("Create Low Ink Notification (2)");
		lowink2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowInk(cioc2);
			}
		});
		GridBagConstraints gbcink2 = new GridBagConstraints();
		gbcink2.fill = GridBagConstraints.BOTH;
		gbcink2.gridx = 1;
		gbcink2.gridy = 3;
		contentPane.add(lowink2, gbcink2);
		
		
		
		// TODO: Resolve low ink, low paper.
		
		
		
		
		
		
		// Low paper events.
		JButton lowPaper1 = new JButton("Create Low Paper Notification (1)");
		lowPaper1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowPaper(cioc1);
			}
		});
		GridBagConstraints gbcpaper1 = new GridBagConstraints();
		gbcpaper1.fill = GridBagConstraints.BOTH;
		gbcpaper1.gridx = 0;
		gbcpaper1.gridy = 5;
		contentPane.add(lowPaper1, gbcpaper1);
		
		JButton lowPaper2 = new JButton("Create Low Paper Notification (2)");
		lowPaper2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowPaper(cioc2);
			}
		});
		GridBagConstraints gbcPaper2 = new GridBagConstraints();
		gbcPaper2.fill = GridBagConstraints.BOTH;
		gbcPaper2.gridx = 1;
		gbcPaper2.gridy = 5;
		contentPane.add(lowPaper2, gbcPaper2);
	}

}
