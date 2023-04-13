/*
 * SENG 300 Project Iteration 3 - Group P3-2
 * Braedon Haensel -         UCID: 30144363
 * Umar Ahmed -             UCID: 30145076
 * Bartu Okan -             UCID: 30150180
 * Arie Goud -                 UCID: 30163410
 * Abdul Biderkab -         UCID: 30156693
 * Hamza Khan -             UCID: 30157097
 * James Hayward -             UCID: 30149513
 * Christian Salvador -     UCID: 30089672
 * Fatema Chowdhury -         UCID: 30141268
 * Sankalp Bartwal -         UCID: 30132025
 * Avani Sharma -             UCID: 30125040
 * Albe Martin -             UCID: 30161964
 * Omar Khan -                 UCID: 30143707
 * Samantha Liu -             UCID: 30123255
 * Alex Chen -                 UCID: 30140184
 * Auric Adubofour-Poku -     UCID: 30143774
 * Grant Tkachyk -             UCID: 30077137
 * Amandeep Kaur -             UCID: 30153923
 * Tashi Labowka-Poulin -     UCID: 30140749
 * Daniel Chang -             UCID: 30110252
 * Jacob Braun -             UCID: 30124507
 * Omar Ragab -             UCID: 30148549
 * Artemy Gavrilov -         UCID: 30143698
 * Colton Gowans -             UCID: 30143979
 * Hada Rahadhi Hafiyyan -     UCID: 30186484
 *
 */

package com.autovend.software.controllers;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReceiptPrinter;
import com.autovend.devices.observers.ReceiptPrinterObserver;
import com.autovend.products.Product;

public class ReceiptPrinterController extends DeviceController<ReceiptPrinter, ReceiptPrinterObserver>
		implements ReceiptPrinterObserver {
	private ReceiptPrinter printer;

	// Flags/indicators that ink or paper levels are low
	public boolean inkLow = true;
	public boolean paperLow = true;

	public int estimatedInk = 0;
	public int estimatedPaper = 0;

	// Ink and Paper Threshold
	public static final int INK_THRESHOLD = 500;
	public static final int PAPER_THRESHOLD = 200;

	public ReceiptPrinterController(ReceiptPrinter newDevice) {
		super(newDevice);
	}


	public boolean getInkLow(){
		return inkLow;
	}

	public boolean getPaperLow() {
		return paperLow;
	}

	/**
	 * Function for software to keep track of how much ink printer has Since there
	 * are no sensors, whenever ink is added to printer, its incremented in the
	 * software using this function
	 * 
	 * @param inkAmount: amount of ink added to printer
	 */
	public void addedInk(int inkAmount) {
		if (inkAmount > 0) {
			estimatedInk += inkAmount;	
			lowInk();	
		}
		else
			System.out.println("Negative Ink Not Allowed to be Added");
	}

	/**
	 * Function for software to keep track of how much paper printer has Since there
	 * are no sensors, whenever paper is added to printer, its incremented in the
	 * software using this function
	 * 
	 * @param paperAmount: amount of paper added to printer
	 */
	public void addedPaper(int paperAmount) {
		if (paperAmount > 0){ 
			estimatedPaper += paperAmount;
			lowPaper();
		}
		else
			System.out.println("Negative Paper Not Allowed to be Added");
	}

	/**
	 * Method to notify when the ink in the printer is below the threshold
	 * 
	 * @return 
	 * 		inkLow printer status
	 */
	public boolean lowInk() {
		if (estimatedInk <= INK_THRESHOLD)
			inkLow = true;
		else 
			inkLow = false;
		return inkLow;
	}

	/**
	 * Method to notify when the paper in the printer is below the threshold
	 * 
	 * @return
	 * 		paperLow printer status
	 */
	public boolean lowPaper() {
		if (estimatedPaper <= PAPER_THRESHOLD)
			paperLow = true;
		else
			paperLow = false;
		return paperLow;
	}

	/**
	 * Method for creating a receipt
	 * @param order
	 * @param cost
	 * @return
	 */
	public StringBuilder createReceipt(LinkedHashMap<Product, Number[]> order, BigDecimal cost) {
		// initialize String Builder to build the receipt
		StringBuilder receipt = new StringBuilder();
		receipt.append("Purchase Details:\n");

		// loop through every product in the order, appending the appropriate strings to
		// the receipt
		int i = 1;

		for (Product product : order.keySet()) {
			Number[] productInfo = order.get(product);

			String productName = product.getClass().getSimpleName();
			String productString;
			if (product.isPerUnit()) {
				productString = String.format("%d $%.2f %dx %s\n", i, productInfo[1].doubleValue(), productInfo[0].intValue(),
						productName);
			} else {
				productString = String.format("%d $%.2f %.3fkg %s\n", i, productInfo[1].doubleValue(), productInfo[0].doubleValue(),
						productName);
			}
			int splitPos = 59;
			String splitterSubString = "-\n    -";
			while (splitPos < productString.length() - 1) {// -1 to not worry about the \n at the end.
				productString = productString.substring(0, splitPos) + splitterSubString
						+ productString.substring(splitPos);
				splitPos += 61;// 1 extra to account for \n being 1 character (prevents double-spacing of text)
			}

			receipt.append(productString);
			i++;
		}
		// append total cost at the end of the receipt
		receipt.append(String.format("Total: $%.2f\n", cost));
		return receipt;
	}

	/**
	 * Responsible for printing out a properly formatted Receipt
	 */
	public void printReceipt(StringBuilder receipt) {
		printer = getDevice();
		if (!lowInk() && !lowPaper()) {
			try {
				for (char c : receipt.toString().toCharArray()) {
					if (c == '\n') {
						estimatedPaper--;
					} else if (!Character.isWhitespace(c)) {
						estimatedInk--;
					}
					printer.print(c);
				}
				printer.cutPaper();
			} catch (OverloadException e) {
				System.out.println("The receipt is too long.");
			} catch (EmptyException e) {
				System.out.println("The printer is out of paper or ink.");
				this.getMainController().printerOutOfResources(receipt);
			}
		}

		else if (lowInk() && !lowPaper()) {
			// Inform the I/O for attendant from the error message about low ink
			inkLow = true;
			this.getMainController().printerOutOfResources(new StringBuilder());

		} 
		else if (!lowInk() && lowPaper()) {

			// Inform the I/O for attendant from the error message about low paper
			paperLow = true;
			this.getMainController().printerOutOfResources(new StringBuilder());

		}
		else if (lowInk() && lowPaper()) {

			//inform the I/O for attendant from the error message about low ink and paper
			inkLow = true;
			paperLow = true;
			this.getMainController().printerOutOfResources(new StringBuilder());

		}
		lowInk();
		lowPaper();
	}

	@Override
	public void reactToOutOfPaperEvent(ReceiptPrinter printer) {
		estimatedPaper = 0;
		this.getMainController().printerOutOfResources(new StringBuilder());
	}

	@Override
	public void reactToOutOfInkEvent(ReceiptPrinter printer) {
		estimatedInk = 0;
		this.getMainController().printerOutOfResources(new StringBuilder());
	}
	public final String getTypeName(){
		return "ReceiptPrinterController";
	}

	@Override
	public void reactToPaperAddedEvent(ReceiptPrinter printer) {
		this.getMainController().printerRefilled();
		lowPaper();
	}

	@Override
	public void reactToInkAddedEvent(ReceiptPrinter printer) {
		this.getMainController().printerRefilled();
		lowInk();
	}
}
