package com.autovend.software.controllers;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.devices.TouchScreen;
import com.autovend.devices.observers.TouchScreenObserver;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.swing.CustomerOperationPane;
import com.autovend.software.swing.CustomerStartPane;

/**
 *
 */
public class

CustomerIOController extends DeviceController<TouchScreen, TouchScreenObserver> implements TouchScreenObserver{

    public CustomerIOController(TouchScreen newDevice) {
        super(newDevice);
    }
    final String getTypeName(){
        return "CustomerIOController";
    }


    //todo: add methods which let this controller modify the GUI on the screen
    public void addItemByPLU(String pluCode){
        Numeral[] code = new Numeral[pluCode.length()];
        for (int ii=0;ii<pluCode.length();ii++) {
            code[ii] = Numeral.valueOf((byte)Integer.parseInt(String.valueOf(pluCode.charAt(ii))));
        }
        PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(new PriceLookUpCode(code));
        if (product!=null){
            this.getMainController().addItem(product);
        } else {
            System.out.println("Product not in database");
            //todo: print stuff related to this on GUI, also make sure to notify customer to add
            //stuff to the scale first before they do stuff for the PLU code
        }
    }
    //this is also used for adding by browsing!!!!!
    public void addProduct(Product selectedProduct) {
    	//product to add will already be selected from the catalogue here,
    	//so it just adds the selected item, gets the product from UI
    	if (selectedProduct!=null) {
            this.getMainController().addItem(selectedProduct);
        }
    }
    //syntactic sugar method
    public void addItemByBrowsing(Product selectedProduct) {addProduct(selectedProduct);}

    /**
     * Methods for membership sign-in and stuff
     */
    
    public void beginSignInAsMember(){
        this.getMainController().signingInAsMember();
        //todo: Stuff with the GUI
    }
    void attemptSignIn(String number){
        this.getMainController().validateMembership(number);
    }
    void signedIn(){
        //todo: display stuff here for the GUI (and do whatever membership actually does)
    }

    public void cancelSignInAsMember(){
        this.getMainController().cancelSigningInAsMember();
        //todo: GUI
    }

    /**
     *  Methods for Payment
     */
    //since all card payment methods work the same here (basically), then this can just
    //be generically used by the I/O
    public void choosePayByBankCard(CardReaderControllerState state, CardIssuer bank, BigDecimal amount) {
        this.getMainController().payByBankCard(state, bank, amount);
    }
    public void choosePayByGiftCard() {
        this.getMainController().payByGiftCard();
    }

    void finalizeOrder(){
        this.getMainController().completePayment();
        //todo:
        // add stuff for GUI here, also modify that method to return stuff so we can
        // react to that to modify the GUI
    }

    public void purchaseBags(int amountOfBagsToAdd) {
        //TODO: Add the specified number of bags to the order
        // technically, the GUI can get away with only knowing the amount of bags for the order elsewhere,
        // so that bag products don't actually have to be in the order, if that is easier
        this.getMainController().purchaseBags(amountOfBagsToAdd);
    }

    void addOwnBags(){this.getMainController().setAddingBagsLock();}
    //todo: gui stuff
    void cancelAddOwnBags(){this.getMainController().cancelAddingBagsLock();}
    /**
     * Called in response to the customer selecting the 'finished adding bags' option.
     */
    void notifyAttendantBagsAdded(){this.getMainController().notifyAddBags();}
    //todo: more substance

    void selectDoNotBag(Product product){
        this.getMainController().doNotBagLatest();
        /* todo: update UI so it goes back to the normal order, also make the do not bag code
         * not trash you idiot
         */
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
    void notifyShutdown() {}
    
    /**
     * Signals GUI to start GUI.
     */
    void notifyStartup() {}
    
    /**
     * Signals start button was pressed.
     */
    public void startPressed() {
    	// Switch to operation screen.
    	getDevice().getFrame().setContentPane(new CustomerOperationPane(this));
    	getDevice().getFrame().revalidate();
    	getDevice().getFrame().repaint();
    }
    
    /**
     * Signals logout button was pressed.
     */
    public void logoutPressed() {
    	// Switch to start screen.
    	getDevice().getFrame().setContentPane(new CustomerStartPane(this));
    	getDevice().getFrame().revalidate();
    	getDevice().getFrame().repaint();
    }

    //this method is used to display that there is a bagging discrepancy
    void displayWeightDiscrepancyMessage() {}

    //method used to display there is a danger to the station due to weight
    //potentially damaging the bagging area
    void displayBaggingProtectionLock() {}
    
    /**
     * Check if this station is shut down.
     * @return
     * 		True if shut down, false otherwise.
     */
    public boolean isShutdown() {
    	return getMainController().isShutdown();
    }
    
    /**
     * Method that will get the cart from this controller's main checkout station
     * @return 
     * 		LinkedHashMap of order <Product, (Amount(units or by weight), total cost)>
     */
    public LinkedHashMap<Product, Number[]> getCart() {
    	return this.getMainController().getOrder();
    }


}
