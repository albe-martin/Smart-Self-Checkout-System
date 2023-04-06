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
import com.autovend.devices.CardReader;
import com.autovend.devices.observers.CardReaderObserver;
import com.autovend.external.CardIssuer;

import java.math.BigDecimal;


/**
 * A class used to describe a controller for the card reader for this station.
 * Since all cards behave the same way in this simulation, we only need one
 * class which handles every card instance the same way.
 */

//todo: card lock use cases need to be handled.
public class CardReaderController extends PaymentController<CardReader, CardReaderObserver>
		implements CardReaderObserver {
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
		if (registeringMembers==false) {
			if (this.isPaying && this.bank!=null);
			{
				// TODO: Given the data, handle stuff with the transaction
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
			this.getMainController().validateMembership(data.getNumber());
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
