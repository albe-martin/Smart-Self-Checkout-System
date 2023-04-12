package com.autovend.software.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.Objects;
import java.util.Map.Entry;


import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.devices.TouchScreen;
import com.autovend.devices.observers.TouchScreenObserver;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.controllers.CheckoutController.completePaymentErrorEnum;
import com.autovend.software.swing.CustomerOperationPane;
import com.autovend.software.swing.CustomerStartPane;

import com.autovend.software.swing.Language;

import com.autovend.software.swing.ShutdownPane;

import javax.swing.*;


/**
 *
 */
public class CustomerIOController extends DeviceController<TouchScreen, TouchScreenObserver> implements TouchScreenObserver{

    public CustomerIOController(TouchScreen newDevice) {
        super(newDevice);
    }
    public final String getTypeName(){
        return "CustomerIOController";
    }


    //todo: add methods which let this controller modify the GUI on the screen


    /**
     *
     * @param pluCode
     * @return if the item was successfully added or not (returning true/false tells gui what to update)
     */
    public boolean addItemByPLU(String pluCode){
        Numeral[] code = new Numeral[pluCode.length()];
        for (int ii=0;ii<pluCode.length();ii++) {
            code[ii] = Numeral.valueOf((byte)Integer.parseInt(String.valueOf(pluCode.charAt(ii))));
        }

        PriceLookUpCode plu = new PriceLookUpCode(code);
        PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(plu);

        if (product!=null){
            this.getMainController().addItem(product);
            return true;
        } else {
            System.out.println("Product not in database");
            //stuff to the scale first before they do stuff for the PLU code
            return false;
            //todo: please figure out why calling addItemByPLU and subsequently calling getCart() does not have
            // an updated cart (GUI team request)
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
     * Called when an item has been added, and now needs to go to the bagging area
     */
    public void promptAddItemToBaggingArea() {}

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

    public void cancelPayment(){this.getMainController().disableCardReader();}
    public void finalizeOrder() {
    	completePaymentErrorEnum e = this.getMainController().completePayment();
    	//while (this.getMainController().checkoutStation.billInput.removeDanglingBill() != null);
		//((CustomerOperationPane)getDevice().getFrame().getContentPane()).showPaymentErrorPane(e);
    	//((CustomerOperationPane)getDevice().getFrame().getContentPane()).updateAmountPaid();
    }

    public void purchaseBags(int amountOfBagsToAdd) {
        //TODO: Add the specified number of bags to the order
        // technically, the GUI can get away with only knowing the amount of bags for the order elsewhere,
        // so that bag products don't actually have to be in the order, if that is easier
        this.getMainController().purchaseBags(amountOfBagsToAdd);
    }

    public void addOwnBags(){this.getMainController().setAddingBagsLock();}
    //todo: gui stuff
    public void cancelAddOwnBags(){this.getMainController().cancelAddingBagsLock();}

    /**
     * Called in response to the customer selecting the 'finished adding bags' option.
     */
    public void notifyAttendantBagsAdded(){this.getMainController().notifyAddBags();}
    //todo: more substance

    /**
     * Same thing as above with no product param, as the gui does not have the current product added when
     * the customer chooses to not bag that item
     */
    public void selectDoNotBag() {
        this.getMainController().notifyAttendantNoBagRequest();
        // todo: either make this work or tell Colton how it is meant to work with a product param
    }

    
    /**
     * Registers an Attendant's IO Controller into CustomerIO Controller if not already assigned one.
     * @param IOController
     * 		The Attendant Station's IO Controller to add.
     * @throws IllegalStateException
     * 		When a Checkout station is already assigned to an attendant station.
     */
    public void registerAttendant(AttendantIOController IOController) throws IllegalStateException{
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
    public void deregisterAttendant(AttendantIOController IOController) throws IllegalStateException{
    	if(this.getMainController().getSupervisor() != 0) {
    		if(this.getMainController().getControllersByType("AttendantIOController").contains(IOController)) {
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
     * Enables the JPane passed into it (JPanel as arg, only as common parent, but distinguished to either
     * customerStartPane or customerOperationPane inside the method to enable that
     * @param panel JPanel (JPane) to be enabled
     */
    public void enablePanel(JPanel panel) {
        if (panel instanceof CustomerOperationPane) {
            CustomerOperationPane pane = (CustomerOperationPane) panel;
            pane.enableStation();
        } else if (panel instanceof CustomerStartPane) {
            CustomerStartPane pane = (CustomerStartPane) panel;
            pane.enableStation();
        }
    }

    /**
     * Disables the JPane passed into it (JPanel as arg, only as common parent, but distinguished to either
     * customerStartPane or customerOperationPane inside the method to disable that
     * @param panel JPanel (JPane) to be disabled
     */
    public void disablePanel(JPanel panel) {
        if (panel instanceof CustomerOperationPane) {
            CustomerOperationPane pane = (CustomerOperationPane) panel;
            pane.disableStation();
        } else if (panel instanceof CustomerStartPane) {
            CustomerStartPane pane = (CustomerStartPane) panel;
            pane.disableStation();
        }
    }
    
    /**
     * Signals GUI that station is enabled.
     */
    void notifyEnabled() {
    	enablePanel((JPanel) getDevice().getFrame().getContentPane());
    }
    
    /**
     * Signals GUI that station is disabled.
     */
    void notifyDisabled() {
		disablePanel((JPanel) getDevice().getFrame().getContentPane());

    }
    
    /**
     * Signals GUI to terminate (since it is turning off).
     */
    void notifyShutdown() {
        getDevice().getFrame().setContentPane(new ShutdownPane(this));
        getDevice().getFrame().revalidate();
        getDevice().getFrame().repaint();

        getMainController().setInUse(false);
    }
    
    /**
     * Signals GUI to start GUI.
     */
    void notifyStartup() {
        startMenu();

        disablePanel((JPanel) getDevice().getFrame().getContentPane());
    }

    void startMenu(){
        getDevice().getFrame().setContentPane(new CustomerStartPane(this));
        getDevice().getFrame().revalidate();
        getDevice().getFrame().repaint();
        disablePanel((JPanel) getDevice().getFrame().getContentPane());
        enablePanel((JPanel) getDevice().getFrame().getContentPane());

    }

    /**
     * Signals start button was pressed.
     */
    public void startPressed() {
    	// Switch to operation screen.
    	getDevice().getFrame().setContentPane(new CustomerOperationPane(this));
    	getDevice().getFrame().revalidate();
    	getDevice().getFrame().repaint();

        getMainController().setInUse(true);
    }


    // don't think this is needed, as only the temporary customer gui exit button utilized it
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
    
    /**
     * Method that will notify that the station is out of order
     */
    void notifyOutOfOrder() {

    }

    void selectLanguage () {
        HashMap<String, HashMap<String, String>> language = new HashMap<>();
        
        for (Entry<String, HashMap<String, String>> lang :
        Language.getLanguageBank().entrySet()) {
            if (Objects.equals("English", lang.getKey())) {
                language.put(lang.getKey(), lang.getValue());
            }
        }
        
    }
    
    /**
     * Notify the customer that an item was added.
     */
    void notifyItemAdded() {
    	if (getMainController().isInUse()) {
    		((CustomerOperationPane)getDevice().getFrame().getContentPane()).notifyItemAdded();
    	}
    }
    
    /**
     * Check if bagging area matches expected weight.
     */
    public boolean isItemBagged() {
    	return !getMainController().baggingItemLock;
    }
    
    void notifyNoBagApproved() {
    	if (getMainController().isInUse()) {
    		((CustomerOperationPane)getDevice().getFrame().getContentPane()).notifyNoBagApproved();
    	}
    }
    
    void notifyItemRemoved() {
    	if (getMainController().isInUse()) {
    		((CustomerOperationPane)getDevice().getFrame().getContentPane()).notifyItemRemoved();
    	}
    }
}
