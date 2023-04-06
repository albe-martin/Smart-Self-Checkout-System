package com.autovend.software.controllers;

import com.autovend.devices.AbstractDevice;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.TouchScreen;
import com.autovend.devices.observers.KeyboardObserver;
import com.autovend.devices.observers.TouchScreenObserver;

import java.util.ArrayList;

//need to decide whether the keyboard should get its own controller or not,
//might be excessive to be honest, but it would be consistent....
public class AttendantIOController extends DeviceController<TouchScreen, TouchScreenObserver> implements TouchScreenObserver {
    public AttendantIOController(TouchScreen newDevice) {
        super(newDevice);
    }

    //I don't like that we have to maintain 2 arraylists for controllers
    //but this will be necessary, annoyingly.
    private ArrayList<CheckoutController> controllers;

    @Override
    String getTypeName() {
        return "AttendantIOController";
    }


    void enableStation(CheckoutController checkout) {
        checkout.setMaintenence(false);
        checkout.enableAllDevices();
    }

    void disableStation(CheckoutController checkout) {
        checkout.setMaintenence(true);
        checkout.disableAllDevices();
    }

    void startupStation(CheckoutController checkout) {
        checkout.setShutdown(false);
        checkout.enableAllDevices();
    }

    void shutdownStation(CheckoutController checkout) {
        checkout.setShutdown(false);
        checkout.enableAllDevices();
    }

    //todo: add methods which let this controller modify the GUI on the screen
    


}
