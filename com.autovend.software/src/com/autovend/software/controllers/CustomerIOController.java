package com.autovend.software.controllers;

import com.autovend.Numeral;
import com.autovend.devices.TouchScreen;
import com.autovend.devices.observers.TouchScreenObserver;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

import java.math.BigDecimal;

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

    void selectDoNotBag(Product product){
        // todo:
        // tell main controller to not bag a certain product, need to modify checkout controller
        // for this
    }


    //this method is used to display that there is a bagging discrepancy
    void displayWeightDiscrepancyMessage() {}

    //method used to display there is a danger to the station due to weight
    //potentially damaging the bagging area
    void displayBaggingProtectionLock() {}


}