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

import com.autovend.ChipFailureException;
import com.autovend.CreditCard;
import com.autovend.DebitCard;
import com.autovend.GiftCard;
import com.autovend.InvalidPINException;
import com.autovend.MagneticStripeFailureException;
import com.autovend.devices.BillValidator;
import com.autovend.devices.CardReader;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.external.CardIssuer;
import com.autovend.software.controllers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;

public class CardPaymentTest {
    TestBank bankStub;
    SelfCheckoutStation station;
    CheckoutController checkout;
    CreditCard creditCard;
    DebitCard debitCard;
    GiftCard giftCard;
    
    Currency currency;
    int[] billDenominations;
    BigDecimal[] coinDenominations;
    
    boolean success;
    
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

    @Before
    public void setup(){
    	currency = Currency.getInstance("CAD");
		billDenominations = new int[] {5, 10, 20, 50, 100};
		coinDenominations = new BigDecimal[] {new BigDecimal(0.01), new BigDecimal(0.05), new BigDecimal(0.1), new BigDecimal(0.25), new BigDecimal(1), new BigDecimal(2)};
    	
        bankStub = new TestBank("TestBank");
        creditCard = new CreditCard("Credit Card", "12345", "Steve", "987", "1337", true, true);
        debitCard = new DebitCard("Debit Card", "12345", "Steve", "987", "1337", true, true);
        giftCard = new GiftCard("Gift Card", "12345", "1337", currency, BigDecimal.ONE);
        
		station = new SelfCheckoutStation(currency, billDenominations, coinDenominations, 200, 1);
        checkout = new CheckoutController(station);
        ((CustomerIOController)checkout.getControllersByType("CustomerIOController").get(0)).startPressed();
        
        success = false;
    }

    @Test
    public void testSuccessfulTransactionPayingByCreditInserting() {
        checkout.cost = BigDecimal.ONE;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
        while (!success) {
        	try {
            	checkout.payByBankCard(CardReaderControllerState.PAYINGBYCREDIT, bankStub, BigDecimal.ONE);
                station.cardReader.insert(creditCard, "1337");
        	} catch (ChipFailureException ex) {
        		// Randomly failed insert
        		continue;
            } catch (Exception ex){
                ex.printStackTrace();
                fail("Exception incorrectly thrown");
            }
        	success = true;
        }
        
        assertEquals(BigDecimal.ZERO, checkout.getRemainingAmount());
    }
    
    @Test
    public void testSuccessfulTransactionPayingByCreditTapping() {
        checkout.cost = BigDecimal.ONE;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
        while (!success) {
        	try {
            	checkout.payByBankCard(CardReaderControllerState.PAYINGBYCREDIT, bankStub, BigDecimal.ONE);
                station.cardReader.tap(creditCard);
        	} catch (ChipFailureException ex) {
        		// Randomly failed tap
        		continue;
            } catch (Exception ex){
                fail("Exception incorrectly thrown");
            }
        	success = true;
        }
        
        assertEquals(BigDecimal.ZERO, checkout.getRemainingAmount());
    }
    
    @Test
    public void testSuccessfulTransactionPayingByCreditSwiping() {
        checkout.cost = BigDecimal.ONE;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
        while (!success) {
        	try {
            	checkout.payByBankCard(CardReaderControllerState.PAYINGBYCREDIT, bankStub, BigDecimal.ONE);
                station.cardReader.swipe(creditCard, null);
        	} catch (MagneticStripeFailureException ex) {
        		// Randomly failed swipe
        		continue;
            } catch (Exception ex) {
                fail("Exception incorrectly thrown");
            }
        	success = true;
        }
        
        assertEquals(BigDecimal.ZERO, checkout.getRemainingAmount());
    }
    
    @Test
    public void testSuccessfulTransactionPayingByDebitInserting() {
        checkout.cost = BigDecimal.ONE;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
        while (!success) {
        	try {
            	checkout.payByBankCard(CardReaderControllerState.PAYINGBYDEBIT, bankStub, BigDecimal.ONE);
                station.cardReader.insert(debitCard, "1337");
        	} catch (ChipFailureException ex) {
        		// Randomly failed insert
        		continue;
            } catch (Exception ex){
                fail("Exception incorrectly thrown");
            }
        	success = true;
        }
        
        assertEquals(BigDecimal.ZERO, checkout.getRemainingAmount());
    }
    
    @Test
    public void testSuccessfulTransactionPayingByDebitTapping() {
        checkout.cost = BigDecimal.ONE;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
        while (!success) {
        	try {
            	checkout.payByBankCard(CardReaderControllerState.PAYINGBYDEBIT, bankStub, BigDecimal.ONE);
                station.cardReader.tap(debitCard);
        	} catch (ChipFailureException ex) {
        		// Randomly failed tap
        		continue;
            } catch (Exception ex){
                fail("Exception incorrectly thrown");
            }
        	success = true;
        }
        
        assertEquals(BigDecimal.ZERO, checkout.getRemainingAmount());
    }
    
    @Test
    public void testSuccessfulTransactionPayingByDebitSwiping() {
        checkout.cost = BigDecimal.ONE;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
        while (!success) {
        	try {
            	checkout.payByBankCard(CardReaderControllerState.PAYINGBYDEBIT, bankStub, BigDecimal.ONE);
                station.cardReader.swipe(debitCard, null);
        	} catch (MagneticStripeFailureException ex) {
        		// Randomly failed swipe
        		continue;
            } catch (Exception ex) {
                fail("Exception incorrectly thrown");
            }
        	success = true;
        }
        
        assertEquals(BigDecimal.ZERO, checkout.getRemainingAmount());
    }
    
    @Test
    public void testSuccessfulTransactionPayingByGiftCard() throws InvalidPINException {
        checkout.cost = BigDecimal.ONE;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
        while (!success) {
        	try {
            	checkout.payByGiftCard();
                station.cardReader.insert(giftCard, "1337");
        	} catch (ChipFailureException ex) {
        		// Randomly failed insert
        		continue;
            } catch (Exception ex){
                fail("Exception incorrectly thrown");
            }
        	success = true;
        }
        
        assertEquals(BigDecimal.ZERO, checkout.getRemainingAmount());
        assertEquals(BigDecimal.ZERO, giftCard.createCardInsertData("1337").getRemainingBalance());
    }
    
    @Test
    public void testPartialPaymentPayingByGiftCard() throws InvalidPINException {
        checkout.cost = BigDecimal.valueOf(2);
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
        while (!success) {
        	try {
        		checkout.payByGiftCard();
                station.cardReader.insert(giftCard, "1337");
                station.cardReader.remove();
        	} catch (ChipFailureException ex) {
        		// Randomly failed insert
        		continue;
            } catch (Exception ex){
                ex.printStackTrace();

                fail("Exception incorrectly thrown");
            }
        	success = true;
        }
        
        assertEquals(BigDecimal.ONE, checkout.getRemainingAmount());
        assertEquals(BigDecimal.ZERO, giftCard.createCardInsertData("1337").getRemainingBalance());
    }
    
    @Test
    public void testTransactionNullBank() throws IOException {
    	checkout.cost = BigDecimal.ONE;
    	
    	while (!success) {
	        try {
	        	checkout.payByBankCard(CardReaderControllerState.PAYINGBYCREDIT, null, BigDecimal.ONE);
	            station.cardReader.insert(creditCard, "1337");
	        } catch (ChipFailureException ex) {
	        	// Randomly failed insert
	        	continue;
	        } catch (Exception ex) {
	            fail("Exception incorrectly thrown");
	        }
	        success = true;
    	}
    	
        assertEquals(BigDecimal.ONE, checkout.getRemainingAmount());
    }
    
    @Test
    public void testPayByCreditSystemProtectionLock() throws IOException {
    	checkout.cost = BigDecimal.ONE;
    	bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
    	checkout.systemProtectionLock = true;
    	
    	while (!success) {
	        try {
	        	checkout.payByBankCard(CardReaderControllerState.PAYINGBYCREDIT, bankStub, BigDecimal.ONE);
	            station.cardReader.insert(creditCard, "1337");
	        } catch (ChipFailureException ex) {
	        	// Randomly failed insert
	        	continue;
	        } catch (Exception ex) {
	            fail("Exception incorrectly thrown");
	        }
	        success = true;
    	}
    	
        assertEquals(BigDecimal.ONE, checkout.getRemainingAmount());
    }
    
    @Test
    public void testPayByCreditBaggingItemLock() throws IOException {
    	checkout.cost = BigDecimal.ONE;
    	bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
    	checkout.baggingItemLock=true;
    	
    	while (!success) {
	        try {
	        	checkout.payByBankCard(CardReaderControllerState.PAYINGBYCREDIT, bankStub, BigDecimal.ONE);
	            station.cardReader.insert(creditCard, "1337");
	        } catch (ChipFailureException ex) {
	        	// Randomly failed insert
	        	continue;
	        } catch (Exception ex) {
	            fail("Exception incorrectly thrown");
	        }
	        success = true;
    	}
    	
        assertEquals(BigDecimal.ONE, checkout.getRemainingAmount());
    }
    
    @Test
    public void testPayByCreditPayingChangeLock() throws IOException {
    	checkout.cost = BigDecimal.ONE;
    	bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
    	checkout.payingChangeLock = true;
    	
    	while (!success) {
	        try {
	        	checkout.payByBankCard(CardReaderControllerState.PAYINGBYCREDIT, bankStub, BigDecimal.ONE);
	            station.cardReader.insert(creditCard, "1337");
	        } catch (ChipFailureException ex) {
	        	// Randomly failed insert
	        	continue;
	        } catch (Exception ex) {
	            fail("Exception incorrectly thrown");
	        }
	        success = true;
    	}
    	
        assertEquals(BigDecimal.ONE, checkout.getRemainingAmount());
    }
    
    @Test
    public void testPayByCreditMoreThanOrderCost() throws IOException {
    	checkout.cost = BigDecimal.ONE;
    	bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        
    	while (!success) {
	        try {
	        	checkout.payByBankCard(CardReaderControllerState.PAYINGBYCREDIT, bankStub, BigDecimal.valueOf(5));
	            station.cardReader.insert(creditCard, "1337");
	        } catch (ChipFailureException ex) {
	        	// Randomly failed insert
	        	continue;
	        } catch (Exception ex) {
	            fail("Exception incorrectly thrown");
	        }
	        success = true;
    	}
    	
        assertEquals(BigDecimal.ONE, checkout.getRemainingAmount());
    }
    
    @Test
    public void testPayingByGiftCardBaggingItemLock() throws InvalidPINException {
        checkout.cost = BigDecimal.ONE;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        checkout.baggingItemLock = true;
        
        while (!success) {
        	try {
            	checkout.payByGiftCard();
                station.cardReader.insert(giftCard, "1337");
        	} catch (ChipFailureException ex) {
        		// Randomly failed insert
        		continue;
            } catch (Exception ex){
                fail("Exception incorrectly thrown");
            }
        	success = true;
        }
        
        assertEquals(BigDecimal.ONE, checkout.getRemainingAmount());
        assertEquals(BigDecimal.ONE, giftCard.createCardInsertData("1337").getRemainingBalance());
    }
    
    @Test
    public void testPayingByGiftCardSystemProtectionLock() throws InvalidPINException {
        checkout.cost = BigDecimal.ONE;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        checkout.systemProtectionLock = true;
        
        while (!success) {
        	try {
            	checkout.payByGiftCard();
                station.cardReader.insert(giftCard, "1337");
        	} catch (ChipFailureException ex) {
        		// Randomly failed insert
        		continue;
            } catch (Exception ex){
                fail("Exception incorrectly thrown");
            }
        	success = true;
        }
        
        assertEquals(BigDecimal.ONE, checkout.getRemainingAmount());
        assertEquals(BigDecimal.ONE, giftCard.createCardInsertData("1337").getRemainingBalance());
    }
    
    @Test
    public void testPayingByGiftCardPayingChangeLock() throws InvalidPINException {
        checkout.cost = BigDecimal.ONE;
        bankStub.canPostTransaction = true;
        bankStub.holdAuthorized = true;
        checkout.payingChangeLock = true;
        
        while (!success) {
        	try {
            	checkout.payByGiftCard();
                station.cardReader.insert(giftCard, "1337");
        	} catch (ChipFailureException ex) {
        		// Randomly failed insert
        		continue;
            } catch (Exception ex){
                fail("Exception incorrectly thrown");
            }
        	success = true;
        }
        
        assertEquals(BigDecimal.ONE, checkout.getRemainingAmount());
        assertEquals(BigDecimal.ONE, giftCard.createCardInsertData("1337").getRemainingBalance());
    }
}
