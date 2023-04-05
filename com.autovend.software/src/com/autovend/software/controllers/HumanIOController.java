package com.autovend.software.controllers;

import com.autovend.devices.AbstractDevice;
import com.autovend.devices.observers.AbstractDeviceObserver;

/**
 * An abstract superclass of GUIs, used to implement some generic methods
 * Used by a bunch for both attendant and customer GUIs, also is helpful
 *
 * @param <D>
 * @param <O>
 */

abstract class HumanIOController<D extends AbstractDevice<O>, O extends AbstractDeviceObserver>
        extends DeviceController<D, O> {

    final String getTypeName(){
        return "HumanIOController";
    }
    public HumanIOController(D newDevice) {
        super(newDevice);
    }

}