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

	public JButton scanItem;
	public JButton scanItem2;
	public JButton addBagging;
	public JButton addScale;
	public JButton addItem1Direct;
	public JButton addItem2Direct;
	public JButton scanMembership;
	public JButton addPurchasedBags;
	public JButton input5Bill;
	public JButton inputCoin;
	public JButton tapCard;
	public JButton swipeCard;
	public JButton rightPinCardInsert;
	public JButton wrongPinCardInsert;
	public JButton removeItems;
	public JButton removeLatestFromBaggingArea;
	public JButton giftCardPay;
	public JButton removeChange;
	public JButton removeReceipt;



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
        LinkedList<SellableUnit> orderItems = new LinkedList<>();

        CardIssuer cibc = new CardIssuer("CIBC");
        CardIssuerDatabases.ISSUER_DATABASE.put("CIBC", cibc);
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, 100);

        DebitCard testCard = new DebitCard("Debit", "123", "Bob", "111", "1337", true, true);
        cibc.addCardData("123", "Bob", calendar, "111", BigDecimal.valueOf(200));

        MembershipCard testMembershipCard = new MembershipCard("Membership", "4678", "Bob", false);
        CardIssuerDatabases.MEMBERSHIP_DATABASE.put("4678", "Bob");


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
        scanItem = new JButton("Scan Item #1");
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

        scanItem2 = new JButton("Scan Item #2");
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

        addBagging = new JButton("Add scanned item to bagging area");
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

        addScale = new JButton("Add Weight To Weighing Scale");
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


        addItem1Direct = new JButton("Directly add Item #1 to bagging area");
        addItem1Direct.addActionListener(e -> {
            BarcodedUnit bcItem1 = new BarcodedUnit(new Barcode(Numeral.five, Numeral.seven), 3.0);
            orderItems.add(bcItem1);
            checkout.baggingArea.add(bcItem1);
            try {
                System.out.println(checkout.baggingArea.getCurrentWeight());
            } catch (OverloadException ex) {
                throw new RuntimeException(ex);
            }

            latestUnit[0] = null;
            numbAdded[0] = 0;
        });
        GridBagConstraints gbcAddDir1 = new GridBagConstraints();
        gbcAddDir1.fill = GridBagConstraints.BOTH;
        gbcAddDir1.gridx = 0;
        gbcAddDir1.gridy = 2;
        contentPane.add(addItem1Direct, gbcAddDir1);


        addItem2Direct = new JButton("Directly add Item #2 to bagging area");
        addItem2Direct.addActionListener(e -> {
            BarcodedUnit bcItem2 = new BarcodedUnit(new Barcode(Numeral.five, Numeral.eight), 10.0);
            orderItems.add(bcItem2);
            checkout.baggingArea.add(bcItem2);

            latestUnit[0] = null;
            numbAdded[0] = 0;
        });
        GridBagConstraints gbcAddDir2 = new GridBagConstraints();
        gbcAddDir2.fill = GridBagConstraints.BOTH;
        gbcAddDir2.gridx = 1;
        gbcAddDir2.gridy = 2;
        contentPane.add(addItem2Direct, gbcAddDir2);









        scanMembership = new JButton("Scan membership");
        scanMembership.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkout.handheldScanner.scan(testMembershipCard);
            }
        });
        GridBagConstraints gbcMembership = new GridBagConstraints();
        gbcMembership.fill = GridBagConstraints.BOTH;
        gbcMembership.gridx = 0;
        gbcMembership.gridy = 3;
        contentPane.add(scanMembership, gbcMembership);


        addPurchasedBags = new JButton("Add reusable bags");
        addPurchasedBags.addActionListener(e -> {
            ReusableBag newBag = new ReusableBag();
            orderItems.add(newBag);
            checkout.baggingArea.add(newBag);
            latestUnit[0] = null;
            numbAdded[0] = 0;
        });
        GridBagConstraints gbcAddBag = new GridBagConstraints();
        gbcAddBag.fill = GridBagConstraints.BOTH;
        gbcAddBag.gridx = 1;
        gbcAddBag.gridy = 3;
        contentPane.add(addPurchasedBags, gbcAddBag);




        input5Bill = new JButton("Input 5$ Bill");
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
        gbcInputBill.gridy = 4;
        contentPane.add(input5Bill, gbcInputBill);


        inputCoin = new JButton("Input 0.25$ Coin");
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
        gbcInputCoin.gridy = 4;
        contentPane.add(inputCoin, gbcInputCoin);


        tapCard = new JButton("Tap Card");
        tapCard.addActionListener(e -> {
            try {
                checkout.cardReader.tap(testCard);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ((CustomerOperationPane) (checkout.screen.getFrame().getContentPane())).updateAmountPaid();
        });
        GridBagConstraints gbcTapCard = new GridBagConstraints();
        gbcTapCard.fill = GridBagConstraints.BOTH;
        gbcTapCard.gridx = 0;
        gbcTapCard.gridy = 5;
        contentPane.add(tapCard, gbcTapCard);

        swipeCard = new JButton("Swipe Card");
        swipeCard.addActionListener(e -> {
            try {
                checkout.cardReader.swipe(testCard,null);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ((CustomerOperationPane) (checkout.screen.getFrame().getContentPane())).updateAmountPaid();
        });
        GridBagConstraints gbcSwipeCard = new GridBagConstraints();
        gbcSwipeCard.fill = GridBagConstraints.BOTH;
        gbcSwipeCard.gridx = 1;
        gbcSwipeCard.gridy = 5;
        contentPane.add(swipeCard, gbcSwipeCard);

        rightPinCardInsert = new JButton("Insert Card with correct PIN");
        rightPinCardInsert.addActionListener(e -> {
            try {
                checkout.cardReader.insert(testCard, "1337");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            checkout.cardReader.remove();

            ((CustomerOperationPane) (checkout.screen.getFrame().getContentPane())).updateAmountPaid();
        });
        GridBagConstraints gbcCorrectInsertCard = new GridBagConstraints();
        gbcCorrectInsertCard.fill = GridBagConstraints.BOTH;//
        gbcCorrectInsertCard.gridx = 0;
        gbcCorrectInsertCard.gridy = 6;
        contentPane.add(rightPinCardInsert, gbcCorrectInsertCard);

        wrongPinCardInsert = new JButton("Insert Card with incorrect PIN");
        wrongPinCardInsert.addActionListener(e -> {
            try {
                checkout.cardReader.insert(testCard, "69420");
                System.out.println("IN");
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            checkout.cardReader.remove();
            ((CustomerOperationPane) (checkout.screen.getFrame().getContentPane())).updateAmountPaid();
        });
        GridBagConstraints gbcIncorrectInsertCard = new GridBagConstraints();
        gbcIncorrectInsertCard.fill = GridBagConstraints.BOTH;
        gbcIncorrectInsertCard.gridx = 1;
        gbcIncorrectInsertCard.gridy = 6;
        contentPane.add(wrongPinCardInsert, gbcIncorrectInsertCard);







        removeItems = new JButton("Remove Items from Bagging Area");
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
        gbcRemoveItemsFromBaggingArea.gridy = 7;
        contentPane.add(removeItems, gbcRemoveItemsFromBaggingArea);

        removeLatestFromBaggingArea = new JButton("Remove Latest Item");
        removeLatestFromBaggingArea.addActionListener(e -> {
            try {
                checkout.baggingArea.remove(orderItems.getLast());
                orderItems.removeLast();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        GridBagConstraints gbcRemoveLatestFromBaggingArea = new GridBagConstraints();
        gbcRemoveLatestFromBaggingArea.fill = GridBagConstraints.BOTH;
        gbcRemoveLatestFromBaggingArea.gridx = 1;
        gbcRemoveLatestFromBaggingArea.gridy = 7;
        contentPane.add(removeLatestFromBaggingArea, gbcRemoveLatestFromBaggingArea);

        GiftCard testGiftCard = new GiftCard("GiftCard","400","3456",Currency.getInstance(Locale.CANADA),BigDecimal.valueOf(25));

        giftCardPay = new JButton("Pay Gift Card");
        giftCardPay.addActionListener(e -> {
            try {
                checkout.cardReader.insert(testGiftCard,"3456");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ((CustomerOperationPane) (checkout.screen.getFrame().getContentPane())).updateAmountPaid();
        });
        GridBagConstraints gbcGiftCard = new GridBagConstraints();
        gbcGiftCard.fill = GridBagConstraints.BOTH;
        gbcGiftCard.gridx = 0;
        gbcGiftCard.gridy = 8;
        contentPane.add(giftCardPay, gbcGiftCard);




        removeChange = new JButton("Remove Change");
        removeChange.addActionListener(e -> {
            try {
                while (checkout.billOutput.removeDanglingBill()!=null) {
                    System.out.println("Bill Removed");
                }
                checkout.coinTray.collectCoins();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        GridBagConstraints gbcremoveChange = new GridBagConstraints();
        gbcremoveChange.fill = GridBagConstraints.BOTH;
        gbcremoveChange.gridx = 0;
        gbcremoveChange.gridy = 9;
        contentPane.add(removeChange, gbcremoveChange);

        removeReceipt = new JButton("Remove Receipt");
        removeReceipt.addActionListener(e -> {
            System.out.println(checkout.printer.removeReceipt());
        });
        GridBagConstraints gbcremoveReceipt = new GridBagConstraints();
        gbcremoveReceipt.fill = GridBagConstraints.BOTH;
        gbcremoveReceipt.gridx = 1;
        gbcremoveReceipt.gridy = 9;
        contentPane.add(removeReceipt, gbcremoveReceipt);
    }
}
