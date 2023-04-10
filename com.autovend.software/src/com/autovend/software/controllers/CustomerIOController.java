package com.autovend.software.controllers;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Set;

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
        System.out.println(ProductDatabases.PLU_PRODUCT_DATABASE);
        PriceLookUpCode plu = new PriceLookUpCode(code);
        PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(plu);
        if (product!=null){
            this.getMainController().addItem(product);
            return true;
        } else {
            System.out.println("Product not in database");

            //stuff to the scale first before they do stuff for the PLU code
            return false;

        }
    }
    
    public void addItemByBrowsing(Product selectedProduct) {
    	//product to add will already be selected from the catalogue here
    	//so it just adds the selected item, gets the product from UI
    	if (selectedProduct!=null) {
            this.getMainController().addItem(selectedProduct);
        }
    }
    
    public void addProduct(Product product){
        //since products have to be displayed for the catalogue already
        //it just adds the item here.
        if (product!=null) {
            this.getMainController().addItem(product);
        }
    }
    
    public void beginSignInAsMember(){
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
    public void choosePayByCard(CardIssuer bank, BigDecimal amount) {
        this.getMainController().payByCard(bank, amount);
    }
    
    void finalizeOrder(){
        this.getMainController().completePayment();
        //todo:
        // add stuff for GUI here, also modify that method to return stuff so we can
        // react to that to modify the GUI
    }

    public void addBagsToOrder(int amountOfBagsToAdd) {
        //TODO: Add the specified number of bags to the order
        // technically, the GUI can get away with only knowing the amount of bags for the order elsewhere,
        // so that bag products don't actually have to be in the order, if that is easier
    }

    void selectAddBags(){
        //todo: self explanatory
    }

    /**
     * Called in response to the customer selecting the 'purchase reusable bags' option.
     * Should trigger a prompt asking the customer how many bags they want to buy.
     */
    void selectPurchaseBags(){

    }

    /**
     * Called in response to the customer selecting the 'finished adding bags' option.
     */
    public void selectBagsAdded(){
        Set<DeviceController> baggingControllers = this.getMainController().getAllDeviceControllersRevised().get("BaggingAreaController");
        for (DeviceController baggingController : baggingControllers) {
            BaggingScaleController scale = (BaggingScaleController) baggingController;
            scale.setAddingBags(false);
            scale.setExpectedWeight(scale.getSavedWeight());
            if(scale.getExpectedWeight() != scale.getCurrentWeight()){
                this.getMainController().systemProtectionLock = true; // Lock the system
                this.getMainController().AttendantApproved = false; // Signal the attendant
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

        getMainController().setInUse(false);
    }
    
    /**
     * Signals GUI to start GUI.
     */
    void notifyStartup() {
    	
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
    	return this.getMainController().getCart();
    }


}
