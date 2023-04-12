package com.autovend.software.controllers;

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
        } catch (Exception ex) {
            //TODO: Inform checkout controller of problem or something
        }
    }
}
