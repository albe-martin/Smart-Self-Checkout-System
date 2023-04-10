package com.autovend.software.controllers;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.ReusableBag;
import com.autovend.devices.TouchScreen;
import com.autovend.devices.observers.TouchScreenObserver;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
class CustomerIOController extends DeviceController<TouchScreen, TouchScreenObserver> implements TouchScreenObserver{

    public CustomerIOController(TouchScreen newDevice) {
        super(newDevice);
    }
    final String getTypeName(){
        return "CustomerIOController";
    }


    //todo: add methods which let this controller modify the GUI on the screen




    void addItemByPLU(String pluCode){
        Numeral[] code = new Numeral[pluCode.length()];
        for (int ii=0;ii<pluCode.length();ii++) {
            code[ii] = Numeral.valueOf((byte)Integer.parseInt(String.valueOf(pluCode.charAt(ii))));
        }
        PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(code);
        if (product!=null){
            this.getMainController().addItem(product);
        } else {
            System.out.println("Product not in database");
            //todo: print stuff related to this on GUI, also make sure to notify customer to add
            //stuff to the scale first before they do stuff for the PLU code
        }
    }



    void addProduct(Product product){
        //since products have to be displayed for the catalogue already
        //it just adds the item here.
        if (product!=null) {
            this.getMainController().addItem(product);
        }
    }
    void beginSignInAsMember(){
        this.getMainController().signingInAsMember();
        //Stuff with the GUI
    }


    void attemptSignIn(String number){
        this.getMainController().validateMembership(number);
    }

    void signedIn(){
        //todo: display stuff here for the GUI (and do whatever membership actually does)
    }


    //since all card payment methods work the same here (basically), then this can just
    //be generically used by the I/O
    void choosePayByCard(CardIssuer bank, BigDecimal amount) {
        this.getMainController().payByCard(bank, amount);
    }
    void finalizeOrder(){
        this.getMainController().completePayment();
        //todo:
        // add stuff for GUI here, also modify that method to return stuff so we can
        // react to that to modify the GUI
    }

    void selectAddBags(){
        //todo: self explanatory
    }

    /**
     * Called in response to the customer selecting the 'purchase reusable bags' option.
     * Adds quantity of reusableBags to 'order' and dispenses quantity of reusableBags.
     * @param quantity The number of bags the customer wants to buy.
     * @param price The price of a reusableBag.
     */
    public void selectPurchaseBags(int quantity, BigDecimal price){
        // reusableBags are SellableUnits, so they cannot be added to 'order'. Need to make a product (?):
        ReusableBag bag = new ReusableBag(); // Create a bag just to get its weight.
        Numeral[] numerals = {Numeral.one, Numeral.two}; // Not even sure if these values matter.
        Barcode barcode = new Barcode(numerals);
        String description = "ReusableBag";
        BarcodedProduct reusableBagProduct = new BarcodedProduct(barcode, description, price, bag.getWeight());

        this.getMainController().purchaseBags(reusableBagProduct, reusableBagProduct.getExpectedWeight(), quantity);
    }

    /**
     * Called in response to the customer selecting the 'finished adding bags' option.
     */
    public void selectBagsAdded(){
        for (DeviceController<?, ?> baggingController : this.getMainController().getControllersByType("BaggingAreaController")) {
            if(baggingController instanceof BaggingScaleController){
                BaggingScaleController scale = (BaggingScaleController) baggingController;
                scale.setAddingBags(false);
                scale.setExpectedWeight(scale.getSavedWeight());
                if(scale.getExpectedWeight() != scale.getCurrentWeight()){
                    this.getMainController().systemProtectionLock = true; // Lock the system
                    this.getMainController().AttendantApproved = false; // Signal the attendant
                }
            }
        }
    }

    void selectDoNotBag(Product product){
        // todo:
        // tell main controller to not bag a certain product, need to modify checkout controller
        // for this
    }
    
    /**
     * Registers an Attendant's IO Controller into CustomerIO Controller if not already assigned one.
     * @param IOController
     * 		The Attendant Station's IO Controller to add.
     * @throws IllegalStateException
     * 		When a Checkout station is already assigned to an attendant station.
     */
    void registerAttendant(AttendantIOController IOController) throws IllegalStateException{
    	if(this.getMainController().getSupervisor() == 0) {
    		this.getMainController().registerController("AttendantIOController", IOController);
    		this.getMainController().setSupervisor(IOController.getID());
    	} else {
    		throw new IllegalStateException("Checkout Station is already assigned to an Attendant Station.");
    	}
    }
    
    /**
     * Deregisters an Attendant's IO Controller into CustomerIO Controller.
     * @param IOController
     * 		The attendant station's IO controller
     * @throws IllegalStateException
     * 		If the attendant station is not supervising this checkout station OR
     * 		if this checkout station is not being supervised.
     * 
     */
    void deregisterAttendant(AttendantIOController IOController) throws IllegalStateException{
    	if(this.getMainController().getSupervisor() != 0) {
    		if(this.getMainController().getControllersByType("AttendantIOControllers").contains(IOController)) {
    	    	this.getMainController().deregisterController("AttendantIOController", IOController);
        		this.getMainController().setSupervisor(0);
    		}
    		else {
    			throw new IllegalStateException("This Checkout Station is not assigned to this Attendant Station");
    		}
    	} else {
    		throw new IllegalStateException("Checkout Station is not assigned to an Attendant Station."); 
    	}
    }
    
    /**
     * Signals GUI to terminate (since it is turning off).
     */
    void notifyShutdown() {
    	
    }
    
    /**
     * Signals GUI to start GUI.
     */
    void notifyStartup() {
    	
    }

    //this method is used to display that there is a bagging discrepancy
    void displayWeightDiscrepancyMessage() {}

    //method used to display there is a danger to the station due to weight
    //potentially damaging the bagging area
    void displayBaggingProtectionLock() {}


}