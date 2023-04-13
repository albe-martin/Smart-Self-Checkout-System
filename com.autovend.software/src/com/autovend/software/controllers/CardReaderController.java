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

// todo: card lock use cases need to be handled.

public class CardReaderController extends PaymentController<CardReader, CardReaderObserver>
		implements CardReaderObserver {
	public CardReaderControllerState state;
	/**
	 * The number of times to retry a data read in case of a fail.
	 */
	private static final int RETRIES = 5;

	public CardIssuer bank;
	private BigDecimal amount;

	public CardReaderController(CardReader newDevice) {
		super(newDevice);
		state = CardReaderControllerState.NOTINUSE;
	}

	public void setState(CardReaderControllerState newstate) {
		if ((newstate == CardReaderControllerState.NOTINUSE)) {
			this.disableDevice();
		} else {
			this.enableDevice();
		}
		this.state = newstate;
	}

	public void setState(CardReaderControllerState newstate, CardIssuer newbank, BigDecimal newamount) {
		this.bank = newbank;
		this.amount = newamount;
		setState(newstate);
	}

	public void setState(CardReaderControllerState newstate, BigDecimal newamount) {
		this.amount = newamount;
		setState(newstate);
	}

	// TODO: Add Messages for the GUI to reactToCardInserted/Removed/Tapped/Swiped
	@Override
	public void reactToCardInsertedEvent(CardReader reader) {
	}

	@Override
	public void reactToCardRemovedEvent(CardReader reader) {
	}

	@Override
	public void reactToCardTappedEvent(CardReader reader) {
	}

	@Override
	public void reactToCardSwipedEvent(CardReader reader) {
	}

	@Override
	public void reactToCardDataReadEvent(CardReader reader, Card.CardData data) {
		if (reader != this.getDevice()) {
			return;
		}
		if (state == CardReaderControllerState.PAYINGBYGIFTCARD) {
			reactToGiftCardDataRead((GiftCard.GiftCardInsertData) data);
		} else if (state == CardReaderControllerState.PAYINGBYCREDIT
				|| state == CardReaderControllerState.PAYINGBYDEBIT) { // Credit and Debit cards
			reactToBankCardRead(reader, data);
		} else if (state == CardReaderControllerState.REGISTERINGMEMBERS) { // Membership is being dealt with
			this.getMainController().validateMembership(data.getNumber());
		}

	}

	/**
	 * Attempts to make a bank on the debit/credit card with the provided data.
	 * 
	 * @param reader the bank card reader, used to update the screen for the card
	 *               reader for display text.
	 * @param data   The GiftCardInsertData for the card.
	 */
	private void reactToBankCardRead(CardReader reader, Card.CardData data) {
		if (this.bank != null) {
			int holdNum = bank.authorizeHold(data.getNumber(), this.amount);
			if (holdNum != -1 && (bank.postTransaction(data.getNumber(), holdNum, this.amount))) {
				getMainController().addToAmountPaid(this.amount);
				this.disableDevice();
				this.amount = BigDecimal.ZERO;
				this.bank = null;
				this.state = CardReaderControllerState.NOTINUSE;
			}
		} else {
			// TODO: inform customer that card read failed
			return;
		}
	}

	/**
	 * Attempts to make a payment on the gift card with the provided data.
	 * 
	 * @param data The GiftCardInsertData for the card.
	 */
	private void reactToGiftCardDataRead(GiftCard.GiftCardInsertData data) {
		BigDecimal balance = data.getRemainingBalance();
		if (balance == null) { // If reading failed, try again to get balance RETRIES times
			int attempts = 0;
			while (balance == null && attempts <= RETRIES) {
				balance = data.getRemainingBalance();
				attempts++;
			}

			if (balance == null) { // Should only happen if a card wasn't properly initialized
				// TODO: inform customer
				return;
			}
		}

		if (balance.compareTo(BigDecimal.ZERO) > 0) {
			Currency cardCurr = data.getCurrency();
			// TODO: make sure currency matches the station's, reject and inform customer if
			// not
			// It doesn't specify the currency for the station anywhere soooooo lol...
		} else {
			// TODO: inform customer
			return;
		}

		try {
			// The return flag on the deductions is ignored, as the below checks
			// mean that the deduction will always either return true or throw an exception.
			if (this.amount.compareTo(balance) > 0) {
				BigDecimal diff = amount.subtract(balance);
				// Balance is less than amount to be paid. Use the rest of the balance.
				data.deduct(balance);

				// Subtract amount paid from amount due
				this.amount = this.amount.subtract(balance);
				getMainController().addToAmountPaid(diff);
				this.disableDevice();
				this.amount = BigDecimal.ZERO;
			} else {
				// Balance is greater than or equal to amount, pay entire cost.
				data.deduct(this.amount);
				getMainController().addToAmountPaid(amount);
				// Payment is complete
				this.disableDevice();
				this.amount = BigDecimal.ZERO;
			}
		} catch (ChipFailureException e) {
			// TODO: inform customer
			return;
		}

	}
}
