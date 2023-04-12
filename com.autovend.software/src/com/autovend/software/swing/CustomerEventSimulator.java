package com.autovend.software.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import com.autovend.*;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.utils.CardIssuerDatabases;

public class CustomerEventSimulator extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;


	public CustomerEventSimulator(JFrame attendantFrame, SelfCheckoutStation checkout) {

        // Create sample items
        BarcodedProduct bcproduct1 = new BarcodedProduct(new Barcode(Numeral.five, Numeral.seven), "toy car",
                BigDecimal.valueOf(20.25), 3.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bcproduct1.getBarcode(), bcproduct1);

        BarcodedProduct bcproduct2 = new BarcodedProduct(new Barcode(Numeral.five, Numeral.eight), "lamp",
        		BigDecimal.valueOf(10.50), 10.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bcproduct2.getBarcode(), bcproduct2);

        final int[] numbAdded = {0};
        final SellableUnit[] latestUnit = {null};
        ArrayList<SellableUnit> orderItems = new ArrayList<>();

        CardIssuer cibc = new CardIssuer("CIBC");
        CardIssuerDatabases.ISSUER_DATABASE.put("CIBC", cibc);
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, 100);

        DebitCard testCard = new DebitCard("Test", "123", "Bob", "111", "1337", true, true);
        cibc.addCardData("123", "Bob", calendar, "111", BigDecimal.valueOf(200));


        setTitle("Customer # 1 Event Simulator");
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
        scanItem.addActionListener(e -> {
            BarcodedUnit bcItem1 = new BarcodedUnit(new Barcode(Numeral.five, Numeral.seven), 3.0);
            checkout.mainScanner.scan(bcItem1);
            latestUnit[0] = bcItem1;
            numbAdded[0] = 0;
        });
        GridBagConstraints gbcScan = new GridBagConstraints();
        gbcScan.fill = GridBagConstraints.BOTH;
        gbcScan.gridx = 0;
        gbcScan.gridy = 0;
        contentPane.add(scanItem, gbcScan);

        JButton scanItem2 = new JButton("Scan Item #2");
        scanItem2.addActionListener(e -> {
            BarcodedUnit bcItem2 = new BarcodedUnit(new Barcode(Numeral.five, Numeral.eight), 10.0);
            checkout.mainScanner.scan(bcItem2);
            latestUnit[0] = bcItem2;
            numbAdded[0] = 0;
        });


        GridBagConstraints gbcScan2 = new GridBagConstraints();
        gbcScan2.fill = GridBagConstraints.BOTH;
        gbcScan2.gridx = 1;
        gbcScan2.gridy = 0;
        contentPane.add(scanItem2, gbcScan2);

        JButton addBagging = new JButton("Add item to bagging area");
        addBagging.addActionListener(e -> {
            if (numbAdded[0]>0) {
                checkout.scale.remove(latestUnit[0]);
            }
            checkout.baggingArea.add(latestUnit[0]);
            orderItems.add(latestUnit[0]);
            latestUnit[0] = null;
            numbAdded[0] = 0;
        });
        GridBagConstraints gbcaddBagging = new GridBagConstraints();
        gbcaddBagging.fill = GridBagConstraints.BOTH;
        gbcaddBagging.gridx = 0;
        gbcaddBagging.gridy = 1;
        contentPane.add(addBagging, gbcaddBagging);

        JButton addScale = new JButton("Add Weight To Weighing Scale");
        addScale.addActionListener(e -> {
            if (numbAdded[0] > 0) {
                checkout.scale.remove(latestUnit[0]);
            }
            numbAdded[0]++;
            PriceLookUpCodedUnit pluItem1 = new PriceLookUpCodedUnit(new PriceLookUpCode(Numeral.one, Numeral.three, Numeral.three, Numeral.four), 1.0 * numbAdded[0]);
            latestUnit[0] = pluItem1;
            checkout.scale.add(latestUnit[0]);
        });
        GridBagConstraints gbcaddScale = new GridBagConstraints();
        gbcaddScale.fill = GridBagConstraints.BOTH;
        gbcaddScale.gridx = 1;
        gbcaddScale.gridy = 1;
        contentPane.add(addScale, gbcaddScale);

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


        JButton input5Bill = new JButton("Input 5$ Bill");
        input5Bill.addActionListener(e -> {
            try {
                checkout.billInput.accept(new Bill(5, Currency.getInstance(Locale.CANADA)));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ((CustomerOperationPane) (checkout.screen.getFrame().getContentPane())).updateAmountPaid();
            ;
        });
        GridBagConstraints gbcInputBill = new GridBagConstraints();
        gbcInputBill.fill = GridBagConstraints.BOTH;
        gbcInputBill.gridx = 0;
        gbcInputBill.gridy = 3;
        contentPane.add(input5Bill, gbcInputBill);


        JButton inputCoin = new JButton("Input 0.25$ Coin");
        inputCoin.addActionListener(e -> {
            try {
                checkout.coinSlot.accept(new Coin(BigDecimal.valueOf(0.25), Currency.getInstance(Locale.CANADA)));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ((CustomerOperationPane) (checkout.screen.getFrame().getContentPane())).updateAmountPaid();
            ;
        });
        GridBagConstraints gbcInputCoin = new GridBagConstraints();
        gbcInputCoin.fill = GridBagConstraints.BOTH;
        gbcInputCoin.gridx = 1;
        gbcInputCoin.gridy = 3;
        contentPane.add(inputCoin, gbcInputCoin);


        JButton tapCard = new JButton("Tap Card");
        tapCard.addActionListener(e -> {
            try {
                checkout.cardReader.tap(testCard);
                System.out.println("IN");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ((CustomerOperationPane) (checkout.screen.getFrame().getContentPane())).updateAmountPaid();
        });
        GridBagConstraints gbcTapCard = new GridBagConstraints();
        gbcTapCard.fill = GridBagConstraints.BOTH;
        gbcTapCard.gridx = 0;
        gbcTapCard.gridy = 4;
        contentPane.add(tapCard, gbcTapCard);

        JButton removeItems = new JButton("Remove Items from Bagging Area");
        removeItems.addActionListener(e -> {
            try {
                for (SellableUnit unit : orderItems) {
                    checkout.baggingArea.remove(unit);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        GridBagConstraints gbcRemoveItemsFromBaggingArea = new GridBagConstraints();
        gbcRemoveItemsFromBaggingArea.fill = GridBagConstraints.BOTH;
        gbcRemoveItemsFromBaggingArea.gridx = 0;
        gbcRemoveItemsFromBaggingArea.gridy = 5;
        contentPane.add(removeItems, gbcRemoveItemsFromBaggingArea);



        JButton removeChange = new JButton("Remove Change");
        removeChange.addActionListener(e -> {
            try {
                while (checkout.billOutput.removeDanglingBill()!=null);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        GridBagConstraints gbcremoveChange = new GridBagConstraints();
        gbcremoveChange.fill = GridBagConstraints.BOTH;
        gbcremoveChange.gridx = 1;
        gbcremoveChange.gridy = 5;
        contentPane.add(removeChange, gbcremoveChange);
    }
}
