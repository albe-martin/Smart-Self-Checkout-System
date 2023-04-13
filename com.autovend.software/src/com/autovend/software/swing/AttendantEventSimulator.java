/*
 * SENG 300 Project Iteration 3 - Group P3-2
 * Braedon Haensel -         UCID: 30144363
 * Umar Ahmed -             UCID: 30145076
 * Bartu Okan -             UCID: 30150180
 * Arie Goud -                 UCID: 30163410
 * Abdul Biderkab -         UCID: 30156693
 * Hamza Khan -             UCID: 30157097
 * James Hayward -             UCID: 30149513
 * Christian Salvador -     UCID: 30089672
 * Fatema Chowdhury -         UCID: 30141268
 * Sankalp Bartwal -         UCID: 30132025
 * Avani Sharma -             UCID: 30125040
 * Albe Martin -             UCID: 30161964
 * Omar Khan -                 UCID: 30143707
 * Samantha Liu -             UCID: 30123255
 * Alex Chen -                 UCID: 30140184
 * Auric Adubofour-Poku -     UCID: 30143774
 * Grant Tkachyk -             UCID: 30077137
 * Amandeep Kaur -             UCID: 30153923
 * Tashi Labowka-Poulin -     UCID: 30140749
 * Daniel Chang -             UCID: 30110252
 * Jacob Braun -             UCID: 30124507
 * Omar Ragab -             UCID: 30148549
 * Artemy Gavrilov -         UCID: 30143698
 * Colton Gowans -             UCID: 30143979
 * Hada Rahadhi Hafiyyan -     UCID: 30186484
 *
 */
package com.autovend.software.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.autovend.ReusableBag;
import com.autovend.devices.OverloadException;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.ReceiptPrinterController;
import com.autovend.software.controllers.ReusableBagDispenserController;

/**
 * GUI used to simulate events related to the Attendant.
 * Used for the project demonstration.
 */
public class AttendantEventSimulator extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public JButton bagRequest1;
	public JButton bagRequest2;
	public JButton lowChange1;
	public JButton lowChange2;
	public JButton lowBill1;
	public JButton lowBill2;
	public JButton lowink1;
	public JButton lowink2;
	public JButton lowinkResolve1;
	public JButton lowinkResolve2;
	public JButton lowPaper1;
	public JButton lowPaper2;
	public JButton lowPaperResolve1;
	public JButton lowPaperResolve2;
	public JButton noBag1;
	public JButton noBag2;
	public JButton weight1;
	public JButton weight2;
	public JButton reprint1;
	public JButton reprint2;
	public JButton fillBagsResolve1;
	public JButton fillBagsResolve2;
	
	/**
	 * Create the frame.
	 */      
	public AttendantEventSimulator(JFrame attendantFrame, CheckoutController checkout1, CheckoutController checkout2) {


		setTitle("Attendant Event Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{293, 293, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		// Bag request events
		bagRequest1 = new JButton("Create Bag Confirmation Request (1)");
		bagRequest1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyConfirmAddedBags(checkout1);
			}
		});
		GridBagConstraints gbcBag1 = new GridBagConstraints();
		gbcBag1.fill = GridBagConstraints.BOTH;
		gbcBag1.gridx = 0;
		gbcBag1.gridy = 0;
		contentPane.add(bagRequest1, gbcBag1);
		
		bagRequest2 = new JButton("Create Bag Confirmation Request (2)");
		bagRequest2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyConfirmAddedBags(checkout2);
			}
		});
		GridBagConstraints gbcBag2 = new GridBagConstraints();
		gbcBag2.fill = GridBagConstraints.BOTH;
		gbcBag2.gridx = 1;
		gbcBag2.gridy = 0;
		contentPane.add(bagRequest2, gbcBag2);
		
		// Low coins events
		lowChange1 = new JButton("Create Low Coin Notification (1)");
		lowChange1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowCoinDenomination(checkout1, new BigDecimal("0.25"));
			}
		});
		GridBagConstraints gbcChange1 = new GridBagConstraints();
		gbcChange1.fill = GridBagConstraints.BOTH;
		gbcChange1.gridx = 0;
		gbcChange1.gridy = 1;
		contentPane.add(lowChange1, gbcChange1);
		
		lowChange2 = new JButton("Create Low Coin Notification (2)");
		lowChange2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowCoinDenomination(checkout2, new BigDecimal("0.10"));
			}
		});
		GridBagConstraints gbcCoin2 = new GridBagConstraints();
		gbcCoin2.fill = GridBagConstraints.BOTH;
		gbcCoin2.gridx = 1;
		gbcCoin2.gridy = 1;
		contentPane.add(lowChange2, gbcCoin2);
		
		// Low bills events
		lowBill1 = new JButton("Create Low Bill Notification (1)");
		lowBill1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowBillDenomination(checkout2, new BigDecimal("5"));
			}
		});
		GridBagConstraints gbcBill1 = new GridBagConstraints();
		gbcBill1.fill = GridBagConstraints.BOTH;
		gbcBill1.gridx = 0;
		gbcBill1.gridy = 2;
		contentPane.add(lowBill1, gbcBill1);
		
		lowBill2 = new JButton("Create Low Bill Notification (2)");
		lowBill2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowBillDenomination(checkout2, new BigDecimal("20"));
			}
		});
		GridBagConstraints gbcBill2 = new GridBagConstraints();
		gbcBill2.fill = GridBagConstraints.BOTH;
		gbcBill2.gridx = 1;
		gbcBill2.gridy = 2;
		contentPane.add(lowBill2, gbcBill2);
		
		// Low ink events.
		lowink1 = new JButton("Create Low Ink Notification (1)");
		lowink1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowInk(checkout1, null);
			}
		});
		GridBagConstraints gbcink1 = new GridBagConstraints();
		gbcink1.fill = GridBagConstraints.BOTH;
		gbcink1.gridx = 0;
		gbcink1.gridy = 3;
		contentPane.add(lowink1, gbcink1);
		
		lowink2 = new JButton("Create Low Ink Notification (2)");
		lowink2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowInk(checkout2, null);
			}
		});
		GridBagConstraints gbcink2 = new GridBagConstraints();
		gbcink2.fill = GridBagConstraints.BOTH;
		gbcink2.gridx = 1;
		gbcink2.gridy = 3;
		contentPane.add(lowink2, gbcink2);
		
		// Resolve low ink events.
		lowinkResolve1 = new JButton("Resolve Low Ink Issue (1)");
		lowinkResolve1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					checkout1.checkoutStation.printer.addInk(1000);
					((ReceiptPrinterController)checkout1.getControllersByType("ReceiptPrinterController").get(0)).addedInk(1000);
				} catch (OverloadException ex) {
					throw new RuntimeException(ex);
				}
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowInkResolved(checkout1);
			}
		});
		GridBagConstraints gbcinkResolve1 = new GridBagConstraints();
		gbcinkResolve1.fill = GridBagConstraints.BOTH;
		gbcinkResolve1.gridx = 0;
		gbcinkResolve1.gridy = 4;
		contentPane.add(lowinkResolve1, gbcinkResolve1);
		
		lowinkResolve2 = new JButton("Resolve Low Ink Issue (2)");
		lowinkResolve2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					checkout2.checkoutStation.printer.addInk(1000);
					((ReceiptPrinterController)checkout2.getControllersByType("ReceiptPrinterController").get(0)).addedInk(1000);
				} catch (OverloadException ex) {
					throw new RuntimeException(ex);
				}
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowInkResolved(checkout2);
			}
		});
		GridBagConstraints gbcinkResolve2 = new GridBagConstraints();
		gbcinkResolve2.fill = GridBagConstraints.BOTH;
		gbcinkResolve2.gridx = 1;
		gbcinkResolve2.gridy = 4;
		contentPane.add(lowinkResolve2, gbcinkResolve2);

		// Low paper events.
		lowPaper1 = new JButton("Create Low Paper Notification (1)");
		lowPaper1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowPaper(checkout1, null);
			}
		});
		GridBagConstraints gbcpaper1 = new GridBagConstraints();
		gbcpaper1.fill = GridBagConstraints.BOTH;
		gbcpaper1.gridx = 0;
		gbcpaper1.gridy = 5;
		contentPane.add(lowPaper1, gbcpaper1);
		
		lowPaper2 = new JButton("Create Low Paper Notification (2)");
		lowPaper2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowPaper(checkout2, null);
			}
		});
		GridBagConstraints gbcPaper2 = new GridBagConstraints();
		gbcPaper2.fill = GridBagConstraints.BOTH;
		gbcPaper2.gridx = 1;
		gbcPaper2.gridy = 5;
		contentPane.add(lowPaper2, gbcPaper2);

		// Resolve low paper events.
		lowPaperResolve1 = new JButton("Resolve Low Paper Issue (1)");
		lowPaperResolve1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					checkout1.checkoutStation.printer.addPaper(1000);
					((ReceiptPrinterController)checkout1.getControllersByType("ReceiptPrinterController").get(0)).addedPaper(1000);
				} catch (OverloadException ex) {
					throw new RuntimeException(ex);
				}
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowPaperResolved(checkout1);
			}
		});
		GridBagConstraints gbcPaperResolve1 = new GridBagConstraints();
		gbcPaperResolve1.fill = GridBagConstraints.BOTH;
		gbcPaperResolve1.gridx = 0;
		gbcPaperResolve1.gridy = 6;
		contentPane.add(lowPaperResolve1, gbcPaperResolve1);

		lowPaperResolve2 = new JButton("Resolve Low Paper Issue (2)");
		lowPaperResolve2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					checkout2.checkoutStation.printer.addPaper(1000);
					((ReceiptPrinterController)checkout2.getControllersByType("ReceiptPrinterController").get(0)).addedPaper(1000);
				} catch (OverloadException ex) {
					throw new RuntimeException(ex);
				}
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowPaperResolved(checkout2);
			}
		});
		GridBagConstraints gbcPaperResolve2 = new GridBagConstraints();
		gbcPaperResolve2.fill = GridBagConstraints.BOTH;
		gbcPaperResolve2.gridx = 1;
		gbcPaperResolve2.gridy = 6;
		contentPane.add(lowPaperResolve2, gbcPaperResolve2);
		
		// No bag request events.
		noBag1 = new JButton("Create No Bag Request (1)");
		noBag1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyNoBag(checkout1);
			}
		});
		GridBagConstraints gbcnoBag1 = new GridBagConstraints();
		gbcnoBag1.fill = GridBagConstraints.BOTH;
		gbcnoBag1.gridx = 0;
		gbcnoBag1.gridy = 7;
		contentPane.add(noBag1, gbcnoBag1);
		
		noBag2 = new JButton("Create No Bag Request (2)");
		noBag2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyNoBag(checkout2);
			}
		});
		GridBagConstraints gbcnoBag2 = new GridBagConstraints();
		gbcnoBag2.fill = GridBagConstraints.BOTH;
		gbcnoBag2.gridx = 1;
		gbcnoBag2.gridy = 7;
		contentPane.add(noBag2, gbcnoBag2);
		
		// Weight discrepancy events.
		weight1 = new JButton("Create Weight Discrepancy (1)");
		weight1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyWeightDiscrepancy(checkout1);
			}
		});
		GridBagConstraints gbcweight1 = new GridBagConstraints();
		gbcweight1.fill = GridBagConstraints.BOTH;
		gbcweight1.gridx = 0;
		gbcweight1.gridy = 8;
		contentPane.add(weight1, gbcweight1);
		
		weight2 = new JButton("Create Weight Discrepancy (2)");
		weight2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyWeightDiscrepancy(checkout2);
			}
		});
		GridBagConstraints gbcweight2 = new GridBagConstraints();
		gbcweight2.fill = GridBagConstraints.BOTH;
		gbcweight2.gridx = 1;
		gbcweight2.gridy = 8;
		contentPane.add(weight2, gbcweight2);
		
		// Receipt reprint events.
		reprint1 = new JButton("Create Receipt Reprint Notification (1)");
		reprint1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyReceiptRePrint(checkout1, new StringBuilder("receipt"));
			}
		});
		GridBagConstraints gbcreprint1 = new GridBagConstraints();
		gbcreprint1.fill = GridBagConstraints.BOTH;
		gbcreprint1.gridx = 0;
		gbcreprint1.gridy = 9;
		contentPane.add(reprint1, gbcreprint1);
		
		reprint2 = new JButton("Create Receipt Reprint Notification (2)");
		reprint2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((AttendantOperationPane) attendantFrame.getContentPane()).notifyReceiptRePrint(checkout2, new StringBuilder("receipt"));
			}
		});
		GridBagConstraints gbcreprint2 = new GridBagConstraints();
		gbcreprint2.fill = GridBagConstraints.BOTH;
		gbcreprint2.gridx = 1;
		gbcreprint2.gridy = 9;
		contentPane.add(reprint2, gbcreprint2);


		// Resolve low bags events.
		fillBagsResolve1 = new JButton("Resolve Low Bags Issue (1)");
		lowinkResolve1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List<ReusableBag> bags = Collections.nCopies(20, new ReusableBag());
					checkout1.checkoutStation.bagDispenser.load(bags.toArray(new ReusableBag[0]));
				} catch (OverloadException ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		GridBagConstraints gbcFillBagsResolve1= new GridBagConstraints();
		gbcFillBagsResolve1.fill = GridBagConstraints.BOTH;
		gbcFillBagsResolve1.gridx = 0;
		gbcFillBagsResolve1.gridy = 10;
		contentPane.add(fillBagsResolve1, gbcFillBagsResolve1);

		fillBagsResolve2 = new JButton("Resolve Low Bags Issue (2)");
		lowinkResolve1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List<ReusableBag> bags = Collections.nCopies(20, new ReusableBag());
					checkout2.checkoutStation.bagDispenser.load(bags.toArray(new ReusableBag[0]));
				} catch (OverloadException ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		GridBagConstraints gbcFillBagsResolve2= new GridBagConstraints();
		gbcFillBagsResolve2.fill = GridBagConstraints.BOTH;
		gbcFillBagsResolve2.gridx = 1;
		gbcFillBagsResolve2.gridy = 10;
		contentPane.add(fillBagsResolve2, gbcFillBagsResolve2);


	}
}
