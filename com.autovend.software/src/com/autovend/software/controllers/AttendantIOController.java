package com.autovend.software.controllers;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.autovend.devices.TouchScreen;
import com.autovend.devices.observers.TouchScreenObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.swing.AttendantLoginPane;
import com.autovend.software.swing.AttendantOperationPane;

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
    
    public void setMainAttendantController(AttendantStationController controller) {
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
    public void enableStation(CheckoutController checkout) {
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
    public void disableStation(CheckoutController checkout) {
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
    public void startupStation(CheckoutController checkout) {
    	if(this.mainController.isLoggedIn()) {
	        // TODO: Changed by Braedon, please verify.
    		checkout.startUp();
    	}
    }
    
    /**
     * Notifies Attendant GUI that the station has started up and is ready to be enabled.
     */
    void notifyStartup(CheckoutController checkout) {
    	System.out.println("notified startup");
    	for (DeviceController<?, ?> customerIOController : checkout.getControllersByType("CustomerIOController")) {
    		AttendantOperationPane pane = (AttendantOperationPane) getDevice().getFrame().getContentPane();
    		pane.notifyStartup((CustomerIOController) customerIOController);
    	}
    }
    
    /**
     * Initializes shut down of a controller if not in use  if the attendant is logged in
     * However, a signal will be sent back to the GUI to ask Attendant to force shut down.
     * @param checkout
     * 		The checkout station controller to shut down
     */
    public void shutdownStation(CheckoutController checkout) {
    	if(this.mainController.isLoggedIn()) {
	        if(checkout.isInUse()) {
	        	// Notify GUI back to confirm shut down
	        	for (DeviceController<?, ?> customerIOController : checkout.getControllersByType("CustomerIOController")) {
	        		AttendantOperationPane pane = (AttendantOperationPane) getDevice().getFrame().getContentPane();
	        		pane.notifyShutdownStationInUse((CustomerIOController) customerIOController);
	        	}
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
    public void forceShutDownStation(CheckoutController checkout) {
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
   public void login(String username, String password) {
    	this.mainController.login(username, password);
    }
    
    /**
     * Signals Attendant station controller ot log out if the attendant is logged in
     */
    public void logout() {
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
    	// Check validity
    	if (success) {
    		// Switch GUI to operation screen.
    		getDevice().getFrame().setContentPane(new AttendantOperationPane(this));
    		getDevice().getFrame().revalidate();
        	getDevice().getFrame().repaint();
    	} else {
    		// Handle bad login
    		AttendantLoginPane pane = (AttendantLoginPane) getDevice().getFrame().getContentPane();
    		pane.showLoginError();
    	}
    }
    
    /**
     * Simple method that will signal GUI that a user logged out
     * 
     * @param username
     * 		The username of the logged in user who wishes to log out.
     */
    void loggedOut(String username) {
    	// Switch GUI to login screen.
    	getDevice().getFrame().setContentPane(new AttendantLoginPane(this));
    	getDevice().getFrame().revalidate();
    	getDevice().getFrame().repaint();
    }
    
    
    /**
     * Method to add items by text search for attendants
     * 
     * @param input
     * 		The string to search with
     * @return
     * 		Set<Product>: its a set of products that are collected after the search is done.
     */
    public Set<Product> addItemByTextSearch(String input){
    	String[] filteredInput = input.split(" ");
    	Set<Product> productsToReturn = new HashSet<Product>();
    	
    	for(int b = 0; b < filteredInput.length; b++){
    		for(PLUCodedProduct p : ProductDatabases.PLU_PRODUCT_DATABASE.values()) {
    			if(p.getClass().getSimpleName().contains(filteredInput[b]) || p.getDescription().contains(filteredInput[b])) {
    				productsToReturn.add((Product) p);
    			}
    		}
    		for(BarcodedProduct p : ProductDatabases.BARCODED_PRODUCT_DATABASE.values()) {
    			if(p.getClass().getSimpleName().contains(filteredInput[b]) || p.getDescription().contains(filteredInput[b])) {
    				productsToReturn.add((Product) p);
    			}
    		}
    		
    	}
    	
    	return productsToReturn;
    	
    }
    
    /**
     * Simple method that will return the checkout station list from this IO's main attendant station in the form of IO controllers
     * 
     */
    public List<CustomerIOController> getAllStationsIOControllers() {
    	return mainController.getAllStationsIOControllers();
    }
    
    /**
     * Simple method that will return a list of disabled station io controllers
     * @return
     * 		List of disabled station IO controllers
     */
    public List<CustomerIOController> getDisabledStationsIOControllers() {
    	return mainController.getDisabledStationsIOControllers();
    }

    /**
     * Called when an attendant approves the customer's added bags. Unlocks the machine, terminates the attendant signal, and zeros the scale.
     * @param customerIOController the CustomerIOController of the customer who needs their bags approved.
     */
    public void approveAddedBags(CustomerIOController customerIOController){
        this.getMainController().systemProtectionLock = false;
        this.getMainController().AttendantApproved = true;
        Set<DeviceController> baggingControllers = customerIOController.getMainController().getAllDeviceControllers();
        for (DeviceController baggingController : baggingControllers) {
            BaggingScaleController scale = (BaggingScaleController) baggingController;
            scale.setExpectedWeight(scale.getCurrentWeight());
        }
    }

    /**
     * Notifies the GUI that a customer wants to add bags.
     * @param customerIOController the CustomerIOController of the customer who wants to add bags.
     */
    public void notifyAddBags(CustomerIOController customerIOController){
    	// Notify GUI to approve added bags.
		AttendantOperationPane pane = (AttendantOperationPane) getDevice().getFrame().getContentPane();
		pane.notifyConfirmAddedBags(customerIOController);
    }

    void notifyLowBillDenomination(CheckoutController checkout, ChangeDispenserController controller, BigDecimal denom) {
        //TODO: Signal GUI
    }

    void notifyLowCoinDenomination(CheckoutController checkout, ChangeDispenserController controller, BigDecimal denom) {
        //TODO: Signal GUI
    }

    //todo: add methods which let this controller modify the GUI on the screen
    
    


}
