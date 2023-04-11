package com.autovend.software.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class CustomerEventSimulator extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public void customerEventSimulator() {

        setTitle("Customer Event Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//
//        setContentPane(contentPane);
//        GridBagLayout gbl_contentPane = new GridBagLayout();
//        gbl_contentPane.columnWidths = new int[]{293, 293, 0};
//        gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
//        gbl_contentPane.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
//        gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
//        contentPane.setLayout(gbl_contentPane);
//
//
//        // Bag request events
//        JButton bagRequest1 = new JButton("Create Bag Confirmation Request (1)");
//        bagRequest1.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                ((AttendantOperationPane) attendantFrame.getContentPane()).notifyConfirmAddedBags(cioc1);
//            }
//        });
//        GridBagConstraints gbcBag1 = new GridBagConstraints();
//        gbcBag1.fill = GridBagConstraints.BOTH;
//        gbcBag1.gridx = 0;
//        gbcBag1.gridy = 0;
//        contentPane.add(bagRequest1, gbcBag1);
//
//
//        // Low coins events
//        JButton lowChange1 = new JButton("Create Low Coin Notification (1)");
//        lowChange1.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                ((AttendantOperationPane) attendantFrame.getContentPane()).notifyLowCoinDenomination(cioc1, new BigDecimal("0.25"));
//            }
//        });
//        GridBagConstraints gbcChange1 = new GridBagConstraints();
//        gbcChange1.fill = GridBagConstraints.BOTH;
//        gbcChange1.gridx = 0;
//        gbcChange1.gridy = 1;
//        contentPane.add(lowChange1, gbcChange1);
    }
}
