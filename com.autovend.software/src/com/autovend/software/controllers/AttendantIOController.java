package com.autovend.software.controllers;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
            checkout.enableStation();
    	}
    }

    /**
     * Disables a specified checkout station's registered devices if the attendant is logged in
     * @param checkout
     * 		The checkout station controller to disable
     */
    public void disableStation(CheckoutController checkout) {
    	if(this.mainController.isLoggedIn()) {
            checkout.disableStation();
    	}
    }

    /**
     * Initializes startup of a controller if the attendant is logged in
     * @param checkout
     * 		The checkout station controller to start up
     */
    public void startupStation(CheckoutController checkout) {
    	if(this.mainController.isLoggedIn()) {
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
    public Set<Product> searchProductsByText(String input){
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

    public void addProductByText(CheckoutController controller, Product prod, BigDecimal count){
        //for items priced by unit, count is the number of items, otherwise it reads the scale.
        controller.addItem(prod, count);
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
        customerIOController.getMainController().approveAddingBags();
    }

    /**
     * Notifies the GUI that a customer wants to add bags.
     *
     * @param customerIOController the CustomerIOController of the customer who wants to add bags.
     */
    public void notifyAddBags(CustomerIOController customerIOController){
    	// Notify GUI to approve added bags.
        //might be better to not need to pass in the controller...
		AttendantOperationPane pane = (AttendantOperationPane) getDevice().getFrame().getContentPane();
		pane.notifyConfirmAddedBags(customerIOController);
    }

    public void approveWeightDiscrepancy(CheckoutController controller) {
        controller.attendantOverrideBaggingLock();
        //todo: GUI
    }
    
    /**
     * Method that will remove item from order from a specificed checkout station
     * @param checkout
     * 		The checkout station to remove from
     * @param item
     * 		The item to remove
     * @param amount
     * 		The amount of the item to remove
     */
    public void removeItemFromOrder(CheckoutController checkout, Product item, BigDecimal amount){
        checkout.removeItemFromOrder(item, amount);
    }
    
    /**
     * Method that will get the cart from a specificed checkout station
     * @param checkout
     * 		The checkout station to get the cart from
     * @return 
     * 		LinkedHashMap of order <Product, (Amount(units or by weight), total cost)>
     */
    public LinkedHashMap<Product, Number[]> getCart(CheckoutController checkout) {
    	return checkout.getOrder();
    }


    void notifyLowBillDenomination(CheckoutController checkout, ChangeDispenserController controller, BigDecimal denom) {
        //TODO: Signal GUI
    }

    void notifyLowCoinDenomination(CheckoutController checkout, ChangeDispenserController controller, BigDecimal denom) {
        //TODO: Signal GUI
    }

    //todo: add methods which let this controller modify the GUI on the screen

    void displayMessage(String message){
    /**
     * Notify the GUI that paper is low for a customer station.
     * 
     * TODO: Back-end functionality that calls this function needs to be implemented.
     * 
     * @param customerIOController
     * 			CustomerIOController that is low on paper.
     * @param printer
     * 			ReceiptPrinterController with the issue.
     */
    void notifyLowPaper(CustomerIOController customerIOController, ReceiptPrinterController printer) {
    	// Notify GUI about low paper.
    	AttendantOperationPane pane = (AttendantOperationPane) getDevice().getFrame().getContentPane();
    	pane.notifyLowPaper(customerIOController, printer);
    }

    /**
     * Receive notification from attendant GUI about low paper issue being acknowledged.
     *
     * @param customerIOController
     * 			CustomerIOController with the issue acknowledged.
     * @param printer
     * 			ReceiptPrinterController with the resolved issue.
     */
    public void receiveLowPaperAcknowledgement(CustomerIOController customerIOController, ReceiptPrinterController printer) {
    	// TODO: Connect to back-end
    }

    /**
     * Notify the GUI that a low paper issue was resolved.
     *
     * @param customerIOController
     * 			CustomerIOController with low paper resolved.
     */
    void notifyLowPaperResolved(CustomerIOController customerIOController) {
    	// Notify GUI about low paper resolved.
    	AttendantOperationPane pane = (AttendantOperationPane) getDevice().getFrame().getContentPane();
    	pane.notifyLowPaperResolved(customerIOController);
    }
    
    /**
     * Notify the GUI that ink is low for a customer station.
     * 
     * TODO: Back-end functionality that calls this function needs to be implemented.
     * 
     * @param customerIOController
     * 			CustomerIOController that is low on ink.
     * @param printer
     * 			ReceiptPrinterController with the issue.
     */
    void notifyLowInk(CustomerIOController customerIOController, ReceiptPrinterController printer) {
    	// Notify GUI about low ink.
    	AttendantOperationPane pane = (AttendantOperationPane) getDevice().getFrame().getContentPane();
    	pane.notifyLowInk(customerIOController, printer);
    }

    /**
     * Receive notification from attendant GUI about low ink issue being acknowledged.
     *
     * @param customerIOController
     * 			CustomerIOContoller with the issue acknowledged.
     * @param printer
     * 			ReceiptPrinterController with the acknowledged issue.
     */
    public void receiveLowInkAcknowledgement(CustomerIOController customerIOController, ReceiptPrinterController receiptPrinter) {
    	// TODO: Connect to back-end
    }

    /**
     * Notify the GUI that a low ink issue was resolved.
     *
     * @param customerIOController
     * 			CustomerIOController with low paper resolved.
     */
    void notifyLowInkResolved(CustomerIOController customerIOController) {
    	// Notify GUI about low paper resolved.
    	AttendantOperationPane pane = (AttendantOperationPane) getDevice().getFrame().getContentPane();
    	pane.notifyLowInkResolved(customerIOController);
    }
}
