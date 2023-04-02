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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.CardIssuer;
import com.autovend.products.Product;

@SuppressWarnings("rawtypes")

public class CheckoutController {
	private static int IDcounter = 1;
	private int stationID = IDcounter++;

	private LinkedHashMap<Product, Number[]> order;
	public BigDecimal cost;
	protected BigDecimal amountPaid;

	// sets of valid sources of information to the main controller.
	private final HashSet<BaggingAreaController> validBaggingControllers;
	private final HashSet<ItemAdderController> validItemAdderControllers;
	private HashSet<PaymentController> validPaymentControllers;
	private ReceiptPrinterController receiptPrinter;
	private final LinkedHashSet<ChangeSlotController> changeSlotControllers;
	private TreeMap<BigDecimal, ChangeDispenserController> changeDispenserControllers;

	// Flag to prevent further addition of items if waiting to bag item or an
	// invalid item was found in the bagging area.
	public boolean baggingItemLock;

	// Flag to lock processing in case of damage to station
	// Specifically bagging area for this case, but could be used elsewhere if
	// needed.
	public boolean systemProtectionLock;

	private boolean payingChangeLock;

	/*
	 * Boolean that indicates if an attendant has approved a certain action
	 */
	public boolean AttendantApproved = false;

	// create map to store current weight in bagging area
	private Map<BaggingAreaController, Double> weight = new HashMap<>();
	// create map to store weight after bags added in bagging area
	private Map<BaggingAreaController, Double> weightWithBags = new HashMap<>();

	/**
	 * Constructors for CheckoutController
	 */

	public CheckoutController() {
		validBaggingControllers = new HashSet<>();
		validItemAdderControllers = new HashSet<>();
		validPaymentControllers = new HashSet<>();
		receiptPrinter = null;
		this.changeDispenserControllers = new TreeMap<>();
		this.changeSlotControllers = new LinkedHashSet<>();
		clearOrder();
	}

	public CheckoutController(SelfCheckoutStation checkout) {
		BarcodeScannerController mainScannerController = new BarcodeScannerController(checkout.mainScanner);
		BarcodeScannerController handheldScannerController = new BarcodeScannerController(checkout.handheldScanner);
		this.validItemAdderControllers = new HashSet<>(Arrays.asList(mainScannerController, handheldScannerController));

		ElectronicScaleController scaleController = new ElectronicScaleController(checkout.baggingArea);
		this.validBaggingControllers = new HashSet<>(List.of(scaleController));

		this.receiptPrinter = new ReceiptPrinterController(checkout.printer);

		BillPaymentController billPayController = new BillPaymentController(checkout.billValidator);
		CoinPaymentController coinPaymentController = new CoinPaymentController(checkout.coinValidator);
		CardReaderController cardReaderController = new CardReaderController(checkout.cardReader);

		this.validPaymentControllers = new HashSet<>(
				List.of(billPayController, coinPaymentController, cardReaderController));

		BillChangeSlotController billChangeSlotController = new BillChangeSlotController(checkout.billOutput);
		CoinTrayController coinChangeSlotController = new CoinTrayController(checkout.coinTray);

		// TODO: Finish Coin Tray Controller and add to controllers set
		this.changeSlotControllers = new LinkedHashSet<>(List.of(billChangeSlotController, coinChangeSlotController));
		this.changeDispenserControllers = new TreeMap<>();

		// TODO: Also add coin dispensers to changeDispenserControllers (once done)

		for (int denom : checkout.billDispensers.keySet()) {
			changeDispenserControllers.put(BigDecimal.valueOf(denom),
					new BillDispenserController(checkout.billDispensers.get(denom), BigDecimal.valueOf(denom)));
		}
		for (BigDecimal denom : checkout.coinDispensers.keySet()) {
			changeDispenserControllers.put(denom,
					new CoinDispenserController(checkout.coinDispensers.get(denom), denom) {
					});
		}

		// Add additional device peripherals for Customer I/O and Attendant I/O here
		registerAll();
		clearOrder();
	}

	public int getID() {
		return stationID;
	}

	/**
	 * Method for clearing the current order, to be used for testing purposes,
	 * resetting our order after payment, and to simplify our constructor code as
	 * well.
	 */
	void clearOrder() {
		// garbage collection will throw away the old objects, so implementing this way
		// lets us re-use this for
		// our constructor as well.
		this.order = new LinkedHashMap<>();
		this.cost = BigDecimal.ZERO;
		this.amountPaid = BigDecimal.ZERO;
		this.baggingItemLock = false;
		systemProtectionLock = false; // If the order is cleared, then nothing is at risk of damaging the station.
		payingChangeLock = false;
		for (BaggingAreaController controller : this.validBaggingControllers) {
			controller.resetOrder();
		}
	}

	// Getters for the order and cost for this checkout controller's current order.
	public HashMap<Product, Number[]> getOrder() {
		return this.order;
	}

	public BigDecimal getCost() {
		return this.cost;
	}

	/**
	 * Methods to register and deregister peripherals for controlling the bagging
	 * area and scanning and printer and methods of payment.
	 */

	void registerBaggingAreaController(BaggingAreaController controller) {
		if (validBaggingControllers.contains(controller)) {
			return;
		}
		this.validBaggingControllers.add(controller);
	}

	void deregisterBaggingAreaController(BaggingAreaController controller) {
		if (!validBaggingControllers.contains(controller)) {
			return;
		}
		this.validBaggingControllers.remove(controller);
	}

	void registerItemAdderController(ItemAdderController adder) {
		if (validItemAdderControllers.contains(adder)) {
			return;
		}
		this.validItemAdderControllers.add(adder);
	}

	void deregisterItemAdderController(ItemAdderController adder) {
		if (!validItemAdderControllers.contains(adder)) {
			return;
		}
		this.validItemAdderControllers.remove(adder);
	}

	public void registerPaymentController(PaymentController controller) {
		if (validPaymentControllers.contains(controller)) {
			return;
		}
		this.validPaymentControllers.add(controller);
	}

	void deregisterPaymentController(PaymentController controller) {
		if (!validPaymentControllers.contains(controller)) {
			return;
		}
		this.validPaymentControllers.remove(controller);
	}

	public void registerReceiptPrinter(ReceiptPrinterController printer) {
		if (receiptPrinter == null) {
			this.receiptPrinter = printer;
		}
	}

	void deregisterReceiptPrinter(ReceiptPrinterController printer) {
		if (receiptPrinter.equals(printer)) {
			this.receiptPrinter = null;
		}
	}

	void registerChangeSlotController(ChangeSlotController controller) {
		if (!this.changeSlotControllers.contains(controller)) {
			this.changeSlotControllers.add(controller);
		}
	}

	void deregisterChangeSlotController(ChangeSlotController controller) {
		if (this.changeSlotControllers.contains(controller)) {
			this.changeSlotControllers.remove(controller);
		}
	}

	void registerChangeDispenserController(BigDecimal denom, ChangeDispenserController controller) {
		if (!this.changeDispenserControllers.containsValue(controller)
				&& !changeDispenserControllers.containsKey(denom)) {
			this.changeDispenserControllers.put(denom, controller);
		}
	}

	void deregisterChangeDispenserController(BigDecimal denom, ChangeDispenserController controller) {
		if (changeDispenserControllers.containsValue(controller) && changeDispenserControllers.containsKey(denom)) {
			this.changeDispenserControllers.remove(denom, controller);
		}
	}

	void registerAll() {
		for (ItemAdderController controller : validItemAdderControllers) {
			controller.setMainController(this);
		}
		for (BaggingAreaController controller : validBaggingControllers) {
			controller.setMainController(this);
		}
		for (PaymentController controller : validPaymentControllers) {
			controller.setMainController(this);
		}
		receiptPrinter.setMainController(this);
		for (ChangeSlotController controller : changeSlotControllers) {
			controller.setMainController(this);
		}
		for (BigDecimal denom : changeDispenserControllers.keySet()) {
			changeDispenserControllers.get(denom).setMainController(this);
		}
	}

	/**
	 * Methods to get the device controllers for peripherals TODO: Add other methods
	 * for payment controllers to these methods
	 */
	public HashSet<BaggingAreaController> getAllBaggingControllers() {
		return validBaggingControllers;
	}

	public HashSet<ItemAdderController> getAllItemAdders() {
		return this.validItemAdderControllers;
	}

	HashSet<PaymentController> getAllPaymentControllers() {
		return this.validPaymentControllers;
	}

	ReceiptPrinterController getReceiptPrinter() {
		return this.receiptPrinter;
	}

	HashSet<ChangeSlotController> getChangeSlotController() {
		return this.changeSlotControllers;
	}

	public HashSet<DeviceController> getAllDeviceControllers() {
		HashSet<DeviceController> newSet = new HashSet<>(this.validItemAdderControllers);
		newSet.addAll(this.validBaggingControllers);
		newSet.addAll(this.validPaymentControllers);
		newSet.add(this.receiptPrinter);
		newSet.addAll(this.changeSlotControllers);
		newSet.addAll(this.changeDispenserControllers.values());
		newSet.remove(null);
		return newSet;
	}

	/**
	 * A method to get the number of bags from the customer response
	 * 
	 * @return number of bags
	 */
	public int getBagNumber() {
		// Asking the customer to give the number of bags
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.println("Number of bags to purchase?");
		String response = scan.nextLine();

		// If customer gives 0 then return
		if (response.equals("0")) {
			System.out.println("No bags added!");
			return 0;
		} else {
			// Otherwise record the customer response
			int bagNumber = Integer.parseInt(response);
			return bagNumber;
		}

	}

	/**
	 * Method to add reusable bags to the order after the customer signals to buy
	 * bags TODO: Implement the bags being dispensed by the bag dispenser
	 * 
	 * @param adder   The ItemAdderController used to add the bag to the order
	 * @param newBag  The product to be added to the current order
	 * @param weight  The weight of the product to update the weight in the bagging
	 *                area
	 * @param numBags The number of bags getting added
	 */
	public void purchaseBags(ItemAdderController adder, Product newBag, double weight, int numBags) {

		if ((!this.validItemAdderControllers.contains(adder)) || newBag == null) {
			return;
		}
		if (weight <= 0) {
			return;
		}
		if (baggingItemLock || systemProtectionLock) {
			return;
		}
		// If customer gives 0 then return
		if (numBags == 0) {
			System.out.println("No bags added!");
			return;
		}

		// Add the cost of the new bag to the current cost.
		BigDecimal bagCost = newBag.getPrice().multiply(BigDecimal.valueOf(numBags));
		this.cost = this.cost.add(bagCost);

		// Putting the bag information to the order
		Number[] currentBagInfo = new Number[] { numBags, bagCost };
		if (this.order.containsKey(newBag)) {
			Number[] existingBagInfo = this.order.get(newBag);
			int totalNumBags = existingBagInfo[0].intValue() + numBags;
			BigDecimal totalBagCost = ((BigDecimal) existingBagInfo[1]).add(bagCost);
			currentBagInfo = new Number[] { totalNumBags, totalBagCost };
		}
		this.order.put(newBag, currentBagInfo);

		for (BaggingAreaController baggingController : this.validBaggingControllers) {
			baggingController.updateExpectedBaggingArea(newBag, weight);
		}

		baggingItemLock = true;

		System.out.println("Reusable bag has been added, you may continue.");

	}

	/*
	 * Methods used by ItemAdderControllers
	 */

	/**
	 * Method to add items to the order TODO: Make this general to handle objects
	 * priced by weight instead of just by unit
	 */
	public void addItem(ItemAdderController adder, Product newItem, double weight) {
		if ((!this.validItemAdderControllers.contains(adder)) || newItem == null) {
			return;
		}
		if (weight <= 0) {
			return;
		}
		if (baggingItemLock || systemProtectionLock) {
			return;
		}

		Number[] currentItemInfo = new Number[] { BigDecimal.ZERO, BigDecimal.ZERO };

		// Add item to order
		if (this.order.containsKey(newItem)) {
			currentItemInfo = this.order.get(newItem);
		}

		// Add the cost of the new item to the current cost.
		this.cost = this.cost.add(newItem.getPrice());

		currentItemInfo[0] = (currentItemInfo[0].intValue()) + 1;
		currentItemInfo[1] = ((BigDecimal) currentItemInfo[1]).add(newItem.getPrice());

		this.order.put(newItem, currentItemInfo);

		for (BaggingAreaController baggingController : this.validBaggingControllers) {
			baggingController.updateExpectedBaggingArea(newItem, weight);
		}

		baggingItemLock = true;
	}

	public void addToAmountPaid(BigDecimal val) {
		amountPaid = amountPaid.add(val);
	}

	public BigDecimal getRemainingAmount() {
		return getCost().subtract(amountPaid);
	}
	/*
	 * Methods used by BaggingAreaControllers
	 */

	/**
	 * Method called by bagging area controllers which says to remove the lock on
	 * the station if all controllers for that area agree the items in it are valid.
	 */
	public void baggedItemsValid(BaggingAreaController controller) {
		if (!(this.validBaggingControllers.contains(controller))) {
			return;
		}
		// looping over all bagging area controllers and checking if all of them say the
		// contents are valid
		// then we unlock the station.
		boolean unlockStation = true;
		for (BaggingAreaController baggingController : validBaggingControllers) {
			if (!baggingController.getBaggingValid()) {
				unlockStation = false;
				break;
			}
		}
		baggingItemLock = unlockStation;
	}

	void baggedItemsInvalid(BaggingAreaController controller, String ErrorMessage) {
		if (!(this.validBaggingControllers.contains(controller))) {
			return;
		}
		// inform the I/O for both customer and attendant from the error message, this
		// is a placeholder currently.
		System.out.println(ErrorMessage);
		// TODO: Lock system out of processing payments if error in bagging area occurs
	}

	void baggingAreaError(BaggingAreaController controller, String ErrorMessage) {

		if (!(this.validBaggingControllers.contains(controller))) {
			return;
		}
		// inform the I/O for both customer and attendant from the error message, this
		// is specifically
		// for cases where further action might damage the station (eg: the weight on an
		// electronic scale
		// which is used to validate the order is at its limit and further item addition
		// might cause damage).
		// also is a placeholder
		System.out.println(ErrorMessage);
		this.systemProtectionLock = true;
	}

	// If the potential error which could have damaged the system is no longer a
	// threat
	// (eg: if the weight was reduced to below the threshold so its no longer at
	// risk of damaging the system)
	// then the error will be cleared.
	void baggingAreaErrorEnded(BaggingAreaController controller, String OutOfErrorMessage) {
		if (!(this.validBaggingControllers.contains(controller))) {
			return;
		}
		this.systemProtectionLock = false;
	}

	/**
	 * Methods used to control the ReceiptPrinterController
	 */
	public void printReceipt() {
		// print receipt
		if (this.receiptPrinter != null) {
			// call print receipt method in the ReceiptPrinterController class with the
			// order details and cost
			this.receiptPrinter.printReceipt(this.order, this.cost);
		}
		clearOrder();
	}

	public boolean needPrinterRefill = false;

	void printerOutOfResources(ReceiptPrinterController controller) {
		if (controller != this.receiptPrinter) {
			return;
		}
		this.needPrinterRefill = true;
	}

	void printerRefilled(ReceiptPrinterController controller) {
		if (controller != this.receiptPrinter) {
			return;
		}
		this.needPrinterRefill = false;
	}

	public void setOrder(LinkedHashMap<Product, Number[]> newOrd) {
		this.order = newOrd;
		
		for (Map.Entry<Product, Number[]> entry: this.order.entrySet()) {
			   Product product = entry.getKey();
			   this.cost = this.cost.add(product.getPrice());
			}
	}

	/**
	 * Methods to control the PaymentController
	 */

	void completePayment() {
		if (this.baggingItemLock || this.systemProtectionLock) {
			return;
		}
		if (this.cost.compareTo(this.amountPaid) > 0) {
			System.out.println("You haven't paid enough money yet.");
			return;
		}
		if (this.order.keySet().size() == 0) {
			System.out.println("Your order is empty.");
			return;
		}
		if (this.cost.compareTo(this.amountPaid) < 0) {
			this.payingChangeLock = true;
			// This code is inefficient and could be better, too bad!
			ChangeSlotController[] temp = this.changeSlotControllers.toArray(new ChangeSlotController[2]);
			dispenseChange(temp[0]);
		} else {
			printReceipt();
		}
	}

	void dispenseChange(ChangeSlotController controller) {
		if (!changeSlotControllers.contains(controller)) {
			return;
		}
		if ((getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) && payingChangeLock == true) {
			this.receiptPrinter.printReceipt(this.order, this.cost);
		} else {
			BigDecimal denom = changeDispenserControllers.lastKey();
			while (denom.compareTo(BigDecimal.ZERO) > 0) {
				if ((getRemainingAmount().negate()).compareTo(denom) >= 0) {
					amountPaid = amountPaid.subtract((denom));
					changeDispenserControllers.get(denom).emitChange();
					break;
				} else {
					if (changeDispenserControllers.lowerKey(denom) != null) {
						denom = changeDispenserControllers.lowerKey(denom);
					} else {
						denom = BigDecimal.ZERO;
					}
				}
			}
		}
	}

	public void changeDispenseFailed(ChangeDispenserController controller, BigDecimal denom) {
		if (!this.changeDispenserControllers.containsValue(controller)) {
			return;
		}

		if (controller instanceof BillDispenserController) {
			System.out.println(String.format("Bill dispenser with denomination %s out of bills.", denom.toString()));
		} else {
			System.out.println(String.format("Coin dispenser with denomination %s out of coins.", denom.toString()));
		}
		this.amountPaid = this.amountPaid.add(denom);
	}

	// since both methods of paying by credit and debit cards are simulated the same
	// way
	// only one method is needed. - Arie
	public void payByCard(CardIssuer source, BigDecimal amount) {
		if (baggingItemLock || systemProtectionLock || payingChangeLock || source == null) {
			return;
		}
		if (amount.compareTo(getRemainingAmount()) > 0) {
			return;
			// only reason to pay more than the order with card is to mess with the amount
			// of change the system has for some reason
			// so preventing stuff like this would be a good idea.
		}
		for (PaymentController controller : validPaymentControllers) {
			if (controller instanceof CardReaderController) {
				((CardReaderController) controller).enablePayment(source, amount);
			}
		}
		// TODO: If this fails then do stuff idk
	}

	/*
	 * This method is called when the user indicates they want to add their own bags
	 */
	public void addOwnBags() {
		// store the current weight of items in the bagging controller
		for (BaggingAreaController baggingController : validBaggingControllers) {
			ElectronicScaleController scale = (ElectronicScaleController) baggingController;
			double current = scale.getCurrentWeight();
			weight.put(baggingController, current);
			// let the scale know the customer is adding bags to prevent a weight
			// discrepancy
			scale.setAddingBags(true);
		}

		// let the customer know they can add their bags now
		System.out.print("Add bags now\n");

		// at this point, the customer IO must have signalled they are done adding bags
		// to proceed
		// GUI will implement this part to continue to next lines of code

		// store the new weight in bagging area with bags added
		for (BaggingAreaController baggingController : validBaggingControllers) {
			ElectronicScaleController scale = (ElectronicScaleController) baggingController;
			// let the scale know the customer is done adding bags
			scale.setAddingBags(false);
			double current = scale.getCurrentWeight();
			weightWithBags.put(baggingController, current);
		}
		// at this point the system signals to the attendant IO and locks
		systemProtectionLock = true;
		// if the attendant approves adding bags, the system is unblocked
		if (AttendantApproved) {
			// get the weight of the bags and update the expected weight of the bagging area
			// to account for them
			for (BaggingAreaController baggingController : validBaggingControllers) {
				double bagWeight = weightWithBags.get(baggingController) - weight.get(baggingController);
				ElectronicScaleController scale = (ElectronicScaleController) baggingController;
				scale.updateWithBagWeight(bagWeight);
			}
			systemProtectionLock = false;
		} else {
			return;
		}
		// if the attendant has not approved, the request is cancelled
		// thus the current weight of the scale returns to what it was before

		// placeholder for system to tell customer to continue
		System.out.print("You may now continue\n");
	}

	public Map<BaggingAreaController, Double> getWeight() {
		return this.weight;
	}

	public Map<BaggingAreaController, Double> getWeightWithBags() {
		return this.weightWithBags;
	}

	public HashSet<BaggingAreaController> getValidBaggingControllers() {
		return this.validBaggingControllers;
	}
}
