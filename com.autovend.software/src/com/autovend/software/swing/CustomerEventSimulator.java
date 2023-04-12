package com.autovend.software.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.autovend.Barcode;
import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.Numeral;
import com.autovend.devices.DisabledException;
import com.autovend.devices.OverloadException;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.controllers.BaggingScaleController;
import com.autovend.software.controllers.BarcodeScannerController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.controllers.DeviceController;
import com.autovend.software.controllers.ScanningScaleController;

public class CustomerEventSimulator extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
	public CustomerIOController cioc;


	public CustomerEventSimulator(JFrame attendantFrame, CheckoutController checkout) {


		for (DeviceController<?, ?> controller : checkout.getControllersByType("CustomerIOController")) {
    		cioc = (CustomerIOController) controller;
    	}
		
		// Create sample items
		BarcodedProduct bcproduct1 = new BarcodedProduct(new Barcode(Numeral.five, Numeral.seven), "toy car",
				BigDecimal.valueOf(83.29), 3.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bcproduct1.getBarcode(), bcproduct1);
		
		BarcodedProduct bcproduct2 = new BarcodedProduct(new Barcode(Numeral.five, Numeral.eight), "lamp",
				BigDecimal.valueOf(50.29), 10.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bcproduct2.getBarcode(), bcproduct2);
		
		
		
        setTitle("Customer #" + checkout.getID() + " Event Simulator");
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


         
        JButton scanItem = new JButton("Scan Item #1");
        scanItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	for (DeviceController<?, ?> controller : checkout.getControllersByType("ItemAdderController")) {
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
        
        JButton scanItem2 = new JButton("Scan Item #2");
        scanItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	for (DeviceController<?, ?> controller : checkout.getControllersByType("ItemAdderController")) {
            		if (controller instanceof BarcodeScannerController barcodeScanner) {
            			barcodeScanner.reactToBarcodeScannedEvent(barcodeScanner.getDevice(), bcproduct2.getBarcode());
            		}
            	}
            	// Clear weighting scale weight
            	for (DeviceController<?, ?> controller : checkout.getControllersByType("ScanningScaleController")) {
	        		if (controller instanceof ScanningScaleController ssc) {
	        			double expWeight = ssc.getCurrentWeight();
	        			ssc.reactToWeightChangedEvent(ssc.getDevice(), 0);
	        		}
	        	}
            	
           }
        });
        GridBagConstraints gbcScan2 = new GridBagConstraints();
        gbcScan2.fill = GridBagConstraints.BOTH;
        gbcScan2.gridx = 1;
        gbcScan2.gridy = 0;
        contentPane.add(scanItem2, gbcScan2);
        
        JButton addWeight = new JButton("Add item to bagging area");
        addWeight.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
	        	for (DeviceController<?, ?> controller : checkout.getControllersByType("BaggingAreaController")) {
	        		if (controller instanceof BaggingScaleController bsc) {
	        			double expWeight = bsc.getExpectedWeight();
	        			bsc.reactToWeightChangedEvent(bsc.getDevice(), expWeight);
	        		}
	        	}
           }
        });
        GridBagConstraints gbcaddWeight = new GridBagConstraints();
        gbcaddWeight.fill = GridBagConstraints.BOTH;
        gbcaddWeight.gridx = 0;
        gbcaddWeight.gridy = 1;
        contentPane.add(addWeight, gbcaddWeight);

        JButton addItemToBaggingArea = new JButton("Add Weight To Weighing Scale");
        addItemToBaggingArea.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
	        	for (DeviceController<?, ?> controller : checkout.getControllersByType("ScanningScaleController")) {
	        		if (controller instanceof ScanningScaleController ssc) {
	        			ssc.reactToWeightChangedEvent(ssc.getDevice(), 1);
	        		}
	        	}
           }
        });
        GridBagConstraints gbcAddItemToBaggingArea = new GridBagConstraints();
        gbcAddItemToBaggingArea.fill = GridBagConstraints.BOTH;
        gbcAddItemToBaggingArea.gridx = 1;
        gbcAddItemToBaggingArea.gridy = 1;
        contentPane.add(addItemToBaggingArea, gbcAddItemToBaggingArea);
       
        JButton scanMembership = new JButton("Scan membership");
        scanMembership.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        GridBagConstraints gbcMembership = new GridBagConstraints();
        gbcMembership.fill = GridBagConstraints.BOTH;
        gbcMembership.gridx = 0;
        gbcMembership.gridy = 2;
        contentPane.add(scanMembership, gbcMembership);
        
        
        JButton inputBill = new JButton("Input 1$ Bill");
        inputBill.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	try {
        		checkout.checkoutStation.billInput.accept(new Bill(1, Currency.getInstance(Locale.CANADA)));
			} catch (DisabledException | OverloadException e1) {
				e1.printStackTrace();
			}
        	((CustomerOperationPane)(checkout.checkoutStation.screen.getFrame().getContentPane())).updateAmountPaid();;
           }
        });
        GridBagConstraints gbcInputBill = new GridBagConstraints();
        gbcInputBill.fill = GridBagConstraints.BOTH;
        gbcInputBill.gridx = 0;
        gbcInputBill.gridy = 3;
        contentPane.add(inputBill, gbcInputBill);  
        
        
        JButton inputCoin = new JButton("Input 0.25$ Coin");
        inputCoin.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	try {
        		checkout.checkoutStation.coinSlot.accept(new Coin(BigDecimal.valueOf(0.25), Currency.getInstance(Locale.CANADA)));
			} catch (DisabledException e1) {
				e1.printStackTrace();
			}
        	((CustomerOperationPane)(checkout.checkoutStation.screen.getFrame().getContentPane())).updateAmountPaid();;
           }
        });
        GridBagConstraints gbcInputCoin = new GridBagConstraints();
        gbcInputCoin.fill = GridBagConstraints.BOTH;
        gbcInputCoin.gridx = 1;
        gbcInputCoin.gridy = 3;
        contentPane.add(inputCoin, gbcInputCoin);  
    }
}
