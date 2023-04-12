package com.autovend.software.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.devices.TouchScreen;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.controllers.DeviceController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class CustomerEventSimulator extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;


	public CustomerEventSimulator(JFrame attendantFrame, CheckoutController checkout1, CheckoutController checkout2) {

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
            			System.out.println("trying scan");
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
       
            	
                
        
        
    }
}
