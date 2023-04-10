package com.autovend.software.controllers;

import com.autovend.devices.AbstractDevice;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.ReusableBagDispenserObserver;

public class ReusableBagDispenserController extends DeviceController<ReusableBagDispenser, ReusableBagDispenserObserver> implements ReusableBagDispenserObserver {
    public ReusableBagDispenserController(ReusableBagDispenser newDevice) {
        super(newDevice);
    }

    @Override
    String getTypeName() {
        return "ReusableBagDispenserController";
    }

    @Override
    public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {

    }

    @Override
    public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {

    }

    @Override
    public void bagDispensed(ReusableBagDispenser dispenser) {

    }

    @Override
    public void outOfBags(ReusableBagDispenser dispenser) {
        this.getMainController().AttendantApproved = false; // signal attendant
        this.getMainController().systemProtectionLock = true; // lock station
        dispenser.disable();
    }

    @Override
    public void bagsLoaded(ReusableBagDispenser dispenser, int count) {
        this.getMainController().AttendantApproved = true;
        this.getMainController().systemProtectionLock = false;
        dispenser.enable();
    }
}
