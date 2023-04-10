/*
SENG 300 Project Iteration 2
Group 7
Niran Malla 30086877
Saksham Puri 30140617
Fatema Chowdhury 30141268
Janet Tesgazeab 30141335
Fabiha Fairuzz Subha 30148674
Ryan Janiszewski 30148838
Umesh Oad 30152293
Manvi Juneja 30153525
Daniel Boettcher 30153811
Zainab Bari 30154224
Arie Goud 30163410
Amasil Rahim Zihad 30164830
*/

package com.autovend.software.controllers;

import com.autovend.Card;
import com.autovend.ChipFailureException;
import com.autovend.GiftCard;
import com.autovend.devices.CardReader;
import com.autovend.devices.observers.CardReaderObserver;
import com.autovend.external.CardIssuer;

import java.math.BigDecimal;
import java.util.Currency;


/**
 * A class used to describe a controller for the card reader for this station.
 * Since all cards behave the same way in this simulation, we only need one
 * class which handles every card instance the same way.
 */

//todo: card lock use cases need to be handled.
public class CardReaderController extends PaymentController<CardReader, CardReaderObserver>
		implements CardReaderObserver {
	/**
	 * The number of times to retry a data read in case of a fail.
	 */
	private static final int RETRIES = 5;
	public boolean isPaying;
	public boolean registeringMembers;
	public CardReaderController(CardReader newDevice) {
		super(newDevice);
	}

	public CardIssuer bank;
	private BigDecimal amount;

	// TODO: Add Messages for the GUI to reactToCardInserted/Removed/Tapped/Swiped
	@Override
	public void reactToCardInsertedEvent(CardReader reader) {
		this.isPaying = true;
	}

	@Override
	public void reactToCardRemovedEvent(CardReader reader) {
		this.isPaying = false;
		//technically not required since the data read method does this, but
		//better safe than sorry.
	}

	// Don't need to implement below yet (use case only asks for insertion so far)
	@Override
	public void reactToCardTappedEvent(CardReader reader) {
		if (!registeringMembers) {
			this.isPaying = true;
		}
	}

	@Override
	public void reactToCardSwipedEvent(CardReader reader) {
		if (!registeringMembers) {
			this.isPaying = true;
		}
	}

	@Override
	public void reactToCardDataReadEvent(CardReader reader, Card.CardData data) {
		if (reader != this.getDevice()) {
			return;
		}

		
		if (registeringMembers == false) {	// If a payment is being made
			if (data.getType().equalsIgnoreCase("giftcard")) {
				reactToGiftCardDataRead((GiftCard.GiftCardInsertData) data);
			} else if (data.getType().equalsIgnoreCase("credit") || data.getType().equalsIgnoreCase("debit")) {	// Credit and Debit cards
				if (this.isPaying && this.bank != null) {
					int holdNum = bank.authorizeHold(data.getNumber(), this.amount);
					if (holdNum != -1 && (bank.postTransaction(data.getNumber(), holdNum, this.amount))) {
						getMainController().addToAmountPaid(this.amount);
					}

					this.disableDevice();

					this.amount = BigDecimal.ZERO;
					this.bank = null;

					this.isPaying = false;

				}
			} else {
				// TODO: inform customer that card read failed
				return;
			}
		} else {	// Membership is being dealt with
			this.getMainController().validateMembership(data.getNumber());
		}
	}
	
	/**
	 * Attempts to make a payment on the gift card with the provided data.
	 * 
	 * @param data The GiftCardInsertData for the card.
	 */
	private void reactToGiftCardDataRead(GiftCard.GiftCardInsertData data) {
		BigDecimal balance = data.getRemainingBalance();
		if (balance == null) {	// If reading failed, try again to get balance RETRIES times
			int attempts = 0;
			while (balance == null && attempts <= RETRIES) {
				balance = data.getRemainingBalance();
				attempts++;
			}
			
			if (balance == null) {	// Should only happen if a card wasn't properly initialized
				// TODO: inform customer
				return;
			}
		}
		
		if (balance.compareTo(BigDecimal.ZERO) > 0) {
			Currency cardCurr = data.getCurrency();
			// TODO: make sure currency matches the station's, reject and inform customer if not
		} else {
			// TODO: inform customer
			return;
		}
		
		try {
			// The return flag on the deductions is ignored, as the below checks
			// mean that the deduction will always either return true or throw an exception.
			if (this.amount.compareTo(balance) > 0) {
				// Balance is less than amount to be paid. Use the rest of the balance.
				data.deduct(balance);
				
				// Subtract amount paid from amount due
				this.amount = this.amount.subtract(balance);
			} else {
				// Balance is greater than or equal to amount, pay entire cost.
				data.deduct(this.amount);
				
				// Payment is complete
				this.disableDevice();
				this.amount = BigDecimal.ZERO;
			}
		} catch (ChipFailureException e) {
			// TODO: inform customer
			return;
		}
		
	}

	public void enableMemberReg(){
		registeringMembers=true;
	}
	
	public void disableMemberReg(){
		registeringMembers=false;
	}

	public void enablePayment(CardIssuer issuer, BigDecimal amount) {
		this.enableDevice();
		this.bank = issuer;
		this.amount = amount;
	}
}
