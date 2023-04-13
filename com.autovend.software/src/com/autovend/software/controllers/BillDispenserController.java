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

import com.autovend.Bill;
import com.autovend.devices.BillDispenser;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.observers.BillDispenserObserver;

import java.math.BigDecimal;

public class BillDispenserController extends ChangeDispenserController<BillDispenser, BillDispenserObserver>
		implements BillDispenserObserver {
	public BillDispenserController(BillDispenser newDevice, BigDecimal denom) {
		super(newDevice, denom);
	}

	@Override
	public void emitChange() {
		try {
			this.getDevice().emit();
			if (this.getDevice().size() <= 5) {
				this.getMainController().changeDenomLow(this, this.getDenom());
			}
		} catch (EmptyException ex) {
			this.getMainController().changeDispenseFailed(this, this.getDenom());
		} catch (OverloadException ex) {}
	}

	@Override
	public void reactToBillsFullEvent(BillDispenser dispenser) {
	}

	@Override
	public void reactToBillsEmptyEvent(BillDispenser dispenser) {

	}

	@Override
	public void reactToBillAddedEvent(BillDispenser dispenser, Bill bill) {
	}

	@Override
	public void reactToBillRemovedEvent(BillDispenser dispenser, Bill bill) {
	}

	@Override
	public void reactToBillsLoadedEvent(BillDispenser dispenser, Bill... bills) {
	}

	@Override
	public void reactToBillsUnloadedEvent(BillDispenser dispenser, Bill... bills) {
	}
}