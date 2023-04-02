/*
SENG 300 Project Iteration 2
Group 7
Niran Malla 30086877
Saksham Puri 30140617
Fatema Chowdhury 30141268
Janet Tesgazeab 30141335
Fabiha Fairuzz Subha 30148674
Ryan Janiszewski 30148838
Umesh Oad 30152293
Manvi Juneja 30153525
Daniel Boettcher 30153811
Zainab Bari 30154224
Arie Goud 30163410
Amasil Rahim Zihad 30164830
*/

package com.autovend.software.pojo;

import java.util.ArrayList;

	public class Cart {
	    private ArrayList<CartLineItem> cartLineItems;
	    private double subtotal;
	    private double taxRate1;
	    private String taxName1;
	    private double taxAmount1;
	    private double taxRate2;
	    private String taxName2;
	    private double taxAmount2;
	    private double total;
	    private boolean compoundTaxes;

	    public String getTaxName1() {
	        return taxName1;
	    }

	    public double getTaxAmount1() {
	        return taxAmount1;
	    }

	    public String getTaxName2() {
	        return taxName2;
	    }

	    public double getTaxAmount2() {
	        return taxAmount2;
	    }

	    public boolean isCompoundTaxes() {
	        return compoundTaxes;
	    }

	    public double getSubtotal() {
	        return subtotal;
	    }

	    public double getTaxRate1() {
	        return taxRate1;
	    }

	    public double getTaxRate2() {
	        return taxRate2;
	    }

	    public double getTotal() {
	        return total;
	    }

	    public ArrayList<CartLineItem> getCartLineItems() {
	        return cartLineItems;
	    }

	    public Cart(String taxName1, double taxRate1, String taxName2, double taxRate2, boolean compoundTaxes) {
	        cartLineItems = new ArrayList<>();
	        this.taxName1 = taxName1;
	        this.taxRate1 = taxRate1;
	        this.taxName2 = taxName2;
	        this.taxRate2 = taxRate2;
	        this.compoundTaxes = compoundTaxes;
	        this.subtotal = 0.0;
	        this.total = 0.0;
	        if (this.taxName1 != null && this.taxName1.trim().length() > 0 && this.taxRate1 <= 0) {
	        	throw new IllegalArgumentException("Invalid tax rate provided.");
	        }
	        if (this.taxName2 != null && this.taxName2.trim().length() > 0 && this.taxRate2 <= 0) {
	        	throw new IllegalArgumentException("Invalid tax rate provided.");
	        }
	        if (this.taxRate1 == 0 && this.taxRate2 > 0) {
	        	throw new IllegalArgumentException("Tax Rate2 cannot be provided when Tax Rate1 is 0");
	        }
	    }

	    public boolean addCartItem(CartLineItem lineItem) {
	        this.cartLineItems.add(lineItem);
	        recalculate();
	        return true;
	    }

	    public boolean removeLineItem(int lineIndex) {
	        this.cartLineItems.remove(lineIndex);
	        recalculate();
	        return true;
	    }

	    private void recalculate() {
	        double linesSubtotal = 0.0;
	        double cartTotal = 0.0;
	        for (CartLineItem lineItem: this.cartLineItems) {
	            linesSubtotal += lineItem.getLineTotalPrice();
	        }
	        cartTotal = linesSubtotal;
	        if (taxRate1 > 0) {
	            this.taxAmount1 = cartTotal * this.taxRate1;
	        }
	        if (taxRate2 > 0) {
	            if (compoundTaxes) {
	                cartTotal += this.taxAmount1;
	            }
	            this.taxAmount2 = cartTotal * this.taxRate2;
	        }
	        cartTotal = linesSubtotal + this.taxAmount1 + this.taxAmount2;
	        this.subtotal = linesSubtotal;
	        this.total = cartTotal;
	    }
	}
