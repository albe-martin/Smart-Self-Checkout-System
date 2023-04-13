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

package com.autovend.software.controllers;

import com.autovend.Barcode;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.observers.BarcodeScannerObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Controller for the barcode scanner, communicates with main checkout
 * controller to add items to order.
 */
public class BarcodeScannerController extends ItemAdderController<BarcodeScanner, BarcodeScannerObserver>
		implements BarcodeScannerObserver {
	private boolean isScanningItems;

	void setScanningItems(boolean val){isScanningItems=val;}
	public boolean getScanningItems(){return isScanningItems;}

	public BarcodeScannerController(BarcodeScanner scanner) {
		super(scanner);
		isScanningItems=true;
	}
	public void reactToBarcodeScannedEvent(BarcodeScanner barcodeScanner, Barcode barcode) {
		// if barcode is for a valid object, then add the product found to the order on
		// the main controller.
		// otherwise ignore the item.
		if (barcodeScanner != this.getDevice()) {
			return;
		}
		if (isScanningItems) {
			BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
			if (product != null) {
				this.getMainController().addItem(product);
			}
		} else {
			this.getMainController().validateMembership(String.join("",barcode.digits().stream().map(i->(Byte.valueOf(i.getValue())).toString()).collect(Collectors.toList())));
		}
	}
}
