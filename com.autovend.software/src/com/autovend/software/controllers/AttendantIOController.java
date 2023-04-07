package com.autovend.software.controllers;

import com.autovend.devices.AbstractDevice;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.TouchScreen;
import com.autovend.devices.observers.KeyboardObserver;
import com.autovend.devices.observers.TouchScreenObserver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//need to decide whether the keyboard should get its own controller or not,
//might be excessive to be honest, but it would be consistent....
public class AttendantIOController extends DeviceController<TouchScreen, TouchScreenObserver> implements TouchScreenObserver {
    public AttendantIOController(TouchScreen newDevice) {
        super(newDevice);
    }

    //I don't like that we have to maintain 2 arraylists for controllers
    //but this will be necessary, annoyingly.
    private ArrayList<CheckoutController> controllers;
    
    //mainController changed to AttendantStatioNController instead.
    private AttendantStationController mainController;

    @Override
    String getTypeName() {
        return "AttendantIOController";
    }
    
    void setMainAttendantController(AttendantStationController controller) {
    	this.mainController = controller;
    }
    
    AttendantStationController getMainAttendantController() {
    	return this.mainController;
    }

    //Enable and disable stations methods can be used by permit/prevent use case
    
    /**
     * Enables a specified checkout station's registered devices if the attendant is logged in
     * @param checkout
     * 		The checkout station controller to enable
     */
    void enableStation(CheckoutController checkout) {
    	if(this.mainController.isLoggedIn()) {
            checkout.setMaintenence(false);
            checkout.enableAllDevices();
    	}
    }

    /**
     * Disables a specified checkout station's registered devices if the attendant is logged in
     * @param checkout
     * 		The checkout station controller to disable
     */
    void disableStation(CheckoutController checkout) {
    	if(this.mainController.isLoggedIn()) {
	        checkout.setMaintenence(true);
	        checkout.disableAllDevices();
    	}
    }

    /**
     * Initializes startup of a controller if the attendant is logged in
     * @param checkout
     * 		The checkout station controller to start up
     */
    void startupStation(CheckoutController checkout) {
    	if(this.mainController.isLoggedIn()) {
	        checkout.setShutdown(false);
	        checkout.enableAllDevices();
    	}
    }
    
    /**
     * Notifies Attendant GUI that the station has started up and is ready to be enabled.
     */
    void notifyStartup() {
    	//TODO: GUI signal attendant that this station is ready to be enabled
    }
    
    /**
     * Initializes shut down of a controller if not in use  if the attendant is logged in
     * However, a signal will be sent back to the GUI to ask Attendant to force shut down.
     * @param checkout
     * 		The checkout station controller to shut down
     */
    void shutdownStation(CheckoutController checkout) {
    	if(this.mainController.isLoggedIn()) {
	        if(checkout.isInUse()) {
	        	//TODO: Notify GUI back to confirm shut down
	        } else {
	        	checkout.shutDown();
	        }
    	}
    }
    
    /**
     * Forces to initialize shutdown of controller if the attendant is logged in
     * @param checkout
     * 		The checkout station controller to shut down
     */
    void forceShutDownStation(CheckoutController checkout) {
    	if(this.mainController.isLoggedIn()) {
    		checkout.shutDown();
    	}
    }
    
    /**
     * Gets the ID of the main controller of this IO controller.
     * @return
     * 		The ID of the station.
     */
    int getID() {
    	return this.mainController.getID();
    }
    
    /**
     * Passes credentials to Attendant Station Controller
     * @param username
     * 		The username
     * @param password
     * 		The password
     */
    void login(String username, String password) {
    	this.mainController.login(username, password);
    }
    
    /**
     * Signals Attendant station controller ot log out if the attendant is logged in
     */
    void logout() {
    	if(this.mainController.isLoggedIn()) {
    		this.mainController.logout();
    	}
    }
    
    /**
     * Simple method that will signal GUI with success or failure.
     * 
     * @param success
     * 		True if login was successful,
     * 		False if login was a failure
     * @param username
     * 		The username of the logged in user.
     * 		Will be blank "" if fail
     */
    void loginValidity(boolean success, String username) {
    	//TODO: signal GUI
    }
    
    /**
     * Simple method that will signal GUI that a user logged out
     * 
     * @param username
     * 		The username of the logged in user who wishes to log out.
     */
    void loggedOut(String username) {
    	//TODO: signal GUI
    }
    
    /**
     * Simple method that will return the checkout station list from this IO's main attendant station in the form of IO controllers
     * 
     */
    List<CustomerIOController> getAllStationsIOControllers() {
    	return mainController.getAllStationsIOControllers();
    }
    
    /**
     * Simple method that will return a list of disabled station io controllers
     * @return
     * 		List of disabled station IO controllers
     */
    List<CustomerIOController> getDisabledStationsIOControllers() {
    	return mainController.getDisabledStationsIOControllers();
    }

    /**
     * Called when an attendant approves the customer's added bags. Unlocks the machine, terminates the attendant signal, and zeros the scale.
     * @param controller the BaggingScaleController of the main system logic.
     */
    void approveAddedBags(BaggingScaleController controller){
        this.getMainController().systemProtectionLock = false;
        this.getMainController().AttendantApproved = true;
        controller.setExpectedWeight(controller.getCurrentWeight());
    }

    //todo: add methods which let this controller modify the GUI on the screen
    


}
