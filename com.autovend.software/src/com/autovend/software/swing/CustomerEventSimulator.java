package com.autovend.software.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.devices.TouchScreen;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.controllers.DeviceController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;

public class CustomerEventSimulator extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
	public CustomerIOController cioc1;
	public CustomerIOController cioc2;


	public CustomerEventSimulator(JFrame attendantFrame, CheckoutController checkout1, CheckoutController checkout2) {


		for (DeviceController<?, ?> controller : checkout1.getControllersByType("CustomerIOController")) {
    		cioc1 = (CustomerIOController) controller;
    	}
		
		for (DeviceController<?, ?> controller : checkout2.getControllersByType("CustomerIOController")) {
    		cioc2 = (CustomerIOController) controller;
    	}
		
		// Create sample items
		BarcodedProduct bcproduct1 = new BarcodedProduct(new Barcode(Numeral.five, Numeral.seven), "toy car",
				BigDecimal.valueOf(83.29), 359.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bcproduct1.getBarcode(), bcproduct1);
		
		
		
        setTitle("Customer Event Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        GridBagLayout si1_contentPane = new GridBagLayout();
        si1_contentPane.columnWidths = new int[]{293, 293, 0};
        si1_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        si1_contentPane.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        si1_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(si1_contentPane);


         
        JButton scanItem = new JButton("Scan Item (Station 1)");
        scanItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	for (DeviceController<?, ?> controller : checkout1.getControllersByType("ItemAdderController")) {
            		if (controller instanceof BarcodeScannerController barcodeScanner) {
            			barcodeScanner.reactToBarcodeScannedEvent(barcodeScanner.getDevice(), bcproduct1.getBarcode());
            		}
            	}
            	
           }
        });
        GridBagConstraints gbcScan = new GridBagConstraints();
        gbcScan.fill = GridBagConstraints.BOTH;
        gbcScan.gridx = 0;
        gbcScan.gridy = 0;
        contentPane.add(scanItem, gbcScan);


       
        JButton scanMembership = new JButton("Scan membership");
        scanMembership.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        GridBagConstraints gbcMembership = new GridBagConstraints();
        gbcMembership.fill = GridBagConstraints.BOTH;
        gbcMembership.gridx = 1;
        gbcMembership.gridy = 0;
        contentPane.add(scanMembership, gbcMembership);
        
        JButton doNotBag = new JButton("Do not bag item");
        doNotBag.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            	
                
           }
        });
        GridBagConstraints gbcNoBag = new GridBagConstraints();
        gbcNoBag.fill = GridBagConstraints.BOTH;
        gbcNoBag.gridx = 0;
        gbcNoBag.gridy = 1;
        contentPane.add(doNotBag, gbcNoBag);
        
        
        JButton addOwnBag = new JButton("Use own bag");
        addOwnBag.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            	
                
           }
        });
        GridBagConstraints gbcAddOwnBag = new GridBagConstraints();
        gbcAddOwnBag.fill = GridBagConstraints.BOTH;
        gbcAddOwnBag.gridx = 1;
        gbcAddOwnBag.gridy = 1;
        contentPane.add(addOwnBag, gbcAddOwnBag);
        
        
        JButton input5Bill = new JButton("Input 5$ Bill");
        input5Bill.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            checkout1.addToAmountPaid(BigDecimal.valueOf(5));
        	ArrayList<DeviceController> bpcs = checkout1.getControllersByType("BillPaymentController");
            for (DeviceController bpc : bpcs) {
				
			}
           }
        });
        GridBagConstraints gbcInput5Bill = new GridBagConstraints();
        gbcInput5Bill.fill = GridBagConstraints.BOTH;
        gbcInput5Bill.gridx = 0;
        gbcInput5Bill.gridy = 2;
        contentPane.add(input5Bill, gbcInput5Bill);
        
        
        JButton addItemToBaggingArea = new JButton("Add item to bagging area");
        addItemToBaggingArea.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            cioc1.itemWasAddedToTheBaggingArea();
           }
        });
        GridBagConstraints gbcAddItemToBaggingArea = new GridBagConstraints();
        gbcAddItemToBaggingArea.fill = GridBagConstraints.BOTH;
        gbcAddItemToBaggingArea.gridx = 0;
        gbcAddItemToBaggingArea.gridy = 3;
        contentPane.add(addItemToBaggingArea, gbcAddItemToBaggingArea);
       
            	
                
        
        
    }
}
