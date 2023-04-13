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

import com.autovend.devices.EmptyException;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.observers.ReusableBagDispenserObserver;

public class ReusableBagDispenserController extends DeviceController<ReusableBagDispenser, ReusableBagDispenserObserver> implements ReusableBagDispenserObserver {
    public ReusableBagDispenserController(ReusableBagDispenser newDevice) {
        super(newDevice);
    }

    @Override
    public String getTypeName() {
        return "ReusableBagDispenserController";
    }

    @Override
    public void bagDispensed(ReusableBagDispenser dispenser) {
    }

    @Override
    public void outOfBags(ReusableBagDispenser dispenser) {
        this.getMainController().systemProtectionLock = true; // lock station
        dispenser.disable();
    }

    @Override
    public void bagsLoaded(ReusableBagDispenser dispenser, int count) {
        this.getMainController().systemProtectionLock = false;
        dispenser.enable();
    }

    public void dispenseBags(int numBags) {
        try {
            for (int ii = 0; ii < numBags; ii++) {
                this.getDevice().dispense();
            }
        } catch (EmptyException ex) {}
    }
}
