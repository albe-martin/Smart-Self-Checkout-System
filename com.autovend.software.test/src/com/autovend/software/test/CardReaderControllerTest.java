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

package com.autovend.software.test;

import com.autovend.*;
import com.autovend.devices.CardReader;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.devices.TouchScreen;
import com.autovend.external.CardIssuer;
import com.autovend.software.controllers.CardReaderController;
import com.autovend.software.controllers.CardReaderControllerState;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertEquals;

public class CardReaderControllerTest {

    private class TestBank extends CardIssuer {
        public boolean held;
        public boolean posted;
        public boolean noPostCall;
        public boolean noHoldCall;
        public boolean holdAuthorized;
        public boolean canPostTransaction;
        /**
         * Create a card provider.
         *
         * @param name The company's name.
         * @throws SimulationException If name is null.
         */
        public TestBank(String name) {
            super(name);
            noHoldCall=true;
            noPostCall=true;
        }
        public int authorizeHold(String cardNumber, BigDecimal amount) {
            if (holdAuthorized) {
                held=true;
                return 1;
            } else {
                held=false;
                return -1;
            }
        }
        public boolean postTransaction(String cardNumber, int holdNumber, BigDecimal actualAmount) {
            noPostCall=false;
            if (holdNumber == 1 && canPostTransaction) {
                this.posted = true;
                return true;
            } else {
                this.posted=false;
                return false;
            }
        }
    }

    SelfCheckoutStation testCheck;
    CheckoutController checkoutController;
    CardReader reader;
    TestBank bankStub;
    CreditCard creditCard;
    GiftCard giftCard;
    CardReaderController testController;
    Currency currency;

    @Before
    public void setup(){
        checkoutController = new CheckoutController();
        reader = new CardReader();
        bankStub = new TestBank("TestBank");
        currency = Currency.getInstance("CAD");
        creditCard = new CreditCard("Credit Card", "12345", "Steve", "987", "1337", true, true);
        giftCard = new GiftCard("Gift Card", "12345", "1337", currency, BigDecimal.ONE);
        testController = new CardReaderController(reader);
        testController.setMainController(checkoutController);
        checkoutController.registerController("CardReaderController", testController);
        testController.bank = bankStub;
        CustomerIOController customer = new CustomerIOController(new TouchScreen());
        checkoutController.registerController("CustomerIOController", customer);
        customer.setMainController(checkoutController);
        customer.startPressed();
        
    }

    @After
    public void tearDown(){
        checkoutController = null;
        reader = null;
        bankStub = null;
        creditCard = null;
        giftCard = null;
        testController = null;
        currency = null;
    }

    @Test
    public void reactToCartRemovedEvent() {
    	testController.reactToCardRemovedEvent(reader);
    }
    @Test
    public void setStateTest(){
        testController.setState(CardReaderControllerState.NOTINUSE, BigDecimal.valueOf(1));
        assertEquals(CardReaderControllerState.NOTINUSE, testController.state);
    }

    @Test
    public void reactToCardDataReadEventTest() throws InvalidPINException {
        testController.setState(CardReaderControllerState.REGISTERINGMEMBERS);
        testController.reactToCardDataReadEvent(reader, creditCard.createCardInsertData("1337"));
        assertEquals(CardReaderControllerState.REGISTERINGMEMBERS, testController.state);
    }

    @Test
    public void reactToBankCardReadTest() throws InvalidPINException {
        bankStub.holdAuthorized = true;
        bankStub.canPostTransaction = true;
        testController.setState(CardReaderControllerState.PAYINGBYDEBIT, BigDecimal.valueOf(10));
        testController.reactToCardDataReadEvent(reader, creditCard.createCardInsertData("1337"));
        assertEquals(CardReaderControllerState.NOTINUSE, testController.state); // this is just a placeholder
    }
    
    @Test
    public void reactCardDataReadWrongDevice() {
    	testController.reactToCardDataReadEvent(new CardReader(), null);
    }

    @Test
    public void reactToGiftCardDataReadTest() throws InvalidPINException {
        testController.setState(CardReaderControllerState.PAYINGBYGIFTCARD, BigDecimal.valueOf(10));
        testController.reactToCardDataReadEvent(reader, giftCard.createCardInsertData("1337"));
    }
}
