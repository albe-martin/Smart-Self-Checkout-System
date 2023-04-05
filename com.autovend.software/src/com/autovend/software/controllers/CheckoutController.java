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
import java.util.*;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.CardIssuer;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;

@SuppressWarnings("rawtypes")

public class CheckoutController {
	//todo:
	//comb through classes fields to update modifiers for them, getters and setters
	//will be provided for testing purposes for fields where those are necessary.
	private static int IDcounter = 1;
	private int stationID = IDcounter++;
	private LinkedHashMap<Product, Number[]> order;
	public BigDecimal cost;
	protected BigDecimal amountPaid;
	private HashMap<String, Set<DeviceController>> registeredControllers;
	//A hashmap which maps the simple names of controller classes to sets of
	//that type of controller, this is just simpler and less tedious to program with.

	public boolean baggingItemLock;
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
		initControllers();
		clearOrder();
	}

	private void initControllers(){
		registeredControllers = new HashMap<String, Set<DeviceController>>();
		registeredControllers.put("BaggingAreaController", new HashSet<DeviceController>());
		registeredControllers.put("ItemAdderController", new HashSet<DeviceController>());
		registeredControllers.put("PaymentController", new HashSet<DeviceController>());
		registeredControllers.put("ReceiptPrinterController", new HashSet<DeviceController>());
		registeredControllers.put("ChangeSlotController", new HashSet<DeviceController>());
		registeredControllers.put("ChangeDispenserController", new HashSet<DeviceController>());
	}
	public CheckoutController(SelfCheckoutStation checkout) {
		initControllers();
		BarcodeScannerController mainScannerController = new BarcodeScannerController(checkout.mainScanner);
		BarcodeScannerController handheldScannerController = new BarcodeScannerController(checkout.handheldScanner);
		this.registeredControllers.get("ItemAdderController").addAll(List.of(mainScannerController, handheldScannerController));

		BaggingScaleController scaleController = new BaggingScaleController(checkout.baggingArea);
		this.registeredControllers.get("BaggingAreaController").add(scaleController);

		this.registeredControllers.get("ReceiptPrinterController").add(new ReceiptPrinterController(checkout.printer));

		BillPaymentController billPayController = new BillPaymentController(checkout.billValidator);
		CoinPaymentController coinPaymentController = new CoinPaymentController(checkout.coinValidator);
		CardReaderController cardReaderController = new CardReaderController(checkout.cardReader);
		this.registeredControllers.get("ValidPaymentControllers").addAll(List.of(billPayController, coinPaymentController, cardReaderController));

		BillChangeSlotController billChangeSlotController = new BillChangeSlotController(checkout.billOutput);
		CoinTrayController coinChangeSlotController = new CoinTrayController(checkout.coinTray);
		this.registeredControllers.get("ChangeSlotController").addAll(List.of(billChangeSlotController, coinChangeSlotController));

		HashSet<ChangeDispenserController> changeDispenserControllers = new HashSet<>();
		for (int denom : checkout.billDispensers.keySet()) {
			changeDispenserControllers.add(new BillDispenserController(checkout.billDispensers.get(denom), BigDecimal.valueOf(denom)));
		}

		for (BigDecimal denom : checkout.coinDispensers.keySet()) {
			changeDispenserControllers.add(new CoinDispenserController(checkout.coinDispensers.get(denom), denom) {});
		}
		registeredControllers.get("ChangeDispenserController").addAll(changeDispenserControllers);

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
		order = new LinkedHashMap<>();
		cost = BigDecimal.ZERO;
		amountPaid = BigDecimal.ZERO;
		baggingItemLock = false;
		systemProtectionLock = false; // If the order is cleared, then nothing is at risk of damaging the station.
		payingChangeLock = false;
		HashSet<DeviceController> controllers = (HashSet<DeviceController>)registeredControllers.get("BaggingAreaController");
		for (DeviceController controller : controllers) {
			((BaggingAreaController)controller).resetOrder();
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
	 * Methods to register and deregister peripherals
	 */
	public void deregisterController(String typeName, DeviceController controller) {
		Set<DeviceController> controllerSet = this.registeredControllers.get(typeName);
		if (controllerSet==null){return;}
		if (controllerSet.contains(controller)){controllerSet.remove(controller);}
	}
	public void registerController(String typeName, DeviceController controller) {
		Set<DeviceController> controllerSet = this.registeredControllers.get(typeName);
		if (controllerSet==null){return;}
		if (!controllerSet.contains(controller)){controllerSet.add(controller);}
	}
	void registerAll() {
		for (String key : this.registeredControllers.keySet()){
			Set<DeviceController> controllerSet = this.registeredControllers.get(key);
			for (DeviceController controller : controllerSet) {
				controller.setMainController(this);
			}
		}
	}
	public HashSet<DeviceController> getAllDeviceControllers() {
		HashSet<DeviceController> out = new HashSet<>();
		for (String key : this.registeredControllers.keySet()){
			out.addAll(this.registeredControllers.get(key));
		}
		return out;
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
	 * @param newBag  The product to be added to the current order
	 * @param weight  The weight of the product to update the weight in the bagging
	 *                area
	 * @param numBags The number of bags getting added
	 */
	public void purchaseBags(Product newBag, double weight, int numBags) {
		if (newBag == null || weight <= 0 || baggingItemLock || systemProtectionLock) {
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
		for (DeviceController baggingController : this.registeredControllers.get("BaggingAreaController")) {
			((BaggingAreaController) baggingController).updateExpectedBaggingArea(newBag, weight, true);
		}
		baggingItemLock = true;
		System.out.println("Reusable bag has been added, you may continue.");
	}

	/*
	 * Methods used by ItemAdderControllers
	 */

	/**
	 * Method to add items to the order
	 */
	public void addItem(Product newItem) {
		if (baggingItemLock || systemProtectionLock || newItem == null) {return;}
		//then go through the item and get its weight, either expected weight if it exists, or
		//get the scale controller in the checkout to give us the weight for the PLU code based
		//item.

		Number[] currentItemInfo = new Number[] { BigDecimal.ZERO, BigDecimal.ZERO };

		// Add item to order
		if (this.order.containsKey(newItem)) {
			currentItemInfo = this.order.get(newItem);
		}

		// Add the cost of the new item to the current cost.
		this.cost = this.cost.add(newItem.getPrice());


		double weight;
		if (newItem.isPerUnit()) {
			weight = ((BarcodedProduct) newItem).getExpectedWeight();
			if (weight<=0){return;}
			//if an item has a weight less than or equal to 0, then do nothing.
			//since such an item couldn't exist.

			//only items priced per unit weight are barcoded products, so this is fine.
			currentItemInfo[0] = (currentItemInfo[0].intValue()) + 1;
			currentItemInfo[1] = ((BigDecimal) currentItemInfo[1]).add(newItem.getPrice());
		} else {
			Set<DeviceController> scaleController = registeredControllers.get("ScanningScaleController");
			weight = ((ScanningScaleController) scaleController.stream().toList().get(0)).getCurrentWeight();
			//adding the recorded weight on the current scale to the current item information
			currentItemInfo[0] = ((BigDecimal) currentItemInfo[0]).add(BigDecimal.valueOf(weight));
			currentItemInfo[1] = ((BigDecimal) currentItemInfo[1]).add(
					newItem.getPrice().multiply(BigDecimal.valueOf(weight))
			);
		}
		//first number is amount (either kg or number of units), second is cumulative price.
		//TODO: Make changes to printer code to display kg for decimal values.

		this.order.put(newItem, currentItemInfo);

		for (DeviceController baggingController : registeredControllers.get("BaggingAreaController")) {
			((BaggingAreaController) baggingController).updateExpectedBaggingArea(newItem, weight, true);
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
	public void baggedItemsValid() {
		// looping over all bagging area controllers and checking if all of them say the
		// contents are valid
		// then we unlock the station.
		boolean unlockStation = true;
		for (DeviceController baggingController : registeredControllers.get("BaggingAreaController")) {
			if (!((BaggingAreaController)baggingController).getBaggingValid()) {
				unlockStation = false;
				break;
			}
		}
		baggingItemLock = unlockStation;
	}

	void baggedItemsInvalid(String ErrorMessage) {
		// inform the I/O for both customer and attendant from the error message, this
		// is a placeholder currently.
		System.out.println(ErrorMessage);
		// TODO: Lock system out of processing payments if error in bagging area occurs
	}

	void baggingAreaError(String ErrorMessage) {
		// inform the I/O for both customer and attendant from the error message
		System.out.println(ErrorMessage);
		this.systemProtectionLock = true;
	}

	// If the potential error which could have damaged the system is no longer a
	// threat
	// (eg: if the weight was reduced to below the threshold so its no longer at
	// risk of damaging the system)
	// then the error will be cleared.
	void baggingAreaErrorEnded(String OutOfErrorMessage) {
		this.systemProtectionLock = false;
	}

	/**
	 * Methods used to control the ReceiptPrinterController
	 */
	public void printReceipt() {
		// print receipt
		if (!registeredControllers.get("ReceiptPrinterController").isEmpty()) {
			// call print receipt method in the ReceiptPrinterController class with the
			// order details and cost
			((ReceiptPrinterController) this.registeredControllers.get("ReceiptPrinterController").iterator().next()).printReceipt(this.order, this.cost);
		}
		clearOrder();
	}
	public boolean needPrinterRefill = false;
	void printerOutOfResources() {
		this.needPrinterRefill = true;
	}
	void printerRefilled() {
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
			dispenseChange();
		} else {
			printReceipt();
		}
	}

	void dispenseChange() {
		if ((getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) && payingChangeLock == true) {
			ReceiptPrinterController printerController = (ReceiptPrinterController) this.registeredControllers.get("ReceiptPrinterController").iterator().next();
			printerController.printReceipt(this.order, this.cost);
		} else {
			TreeSet<ChangeDispenserController> controllers = new TreeSet<>();
			for (DeviceController devControl : registeredControllers.get("ChangeDispenserController")) {
				controllers.add((ChangeDispenserController) devControl);
			}
			ChangeDispenserController dispenser = controllers.last();
			while (dispenser != null) {
				if ((getRemainingAmount().negate()).compareTo(dispenser.getDenom()) >= 0) {
					amountPaid = amountPaid.subtract((dispenser.getDenom()));
					dispenser.emitChange();
					break;
				} else {
					if (controllers.lower(dispenser) != null) {
						dispenser = controllers.lower(dispenser);
					} else {
						dispenser = null;
					}
				}
			}
		}
	}
	public void changeDispenseFailed(ChangeDispenserController controller, BigDecimal denom) {
		//todo: have it notify attendant and then try dispensing lower decrements (if possible),
		if (controller instanceof BillDispenserController) {
			System.out.println(String.format("Bill dispenser with denomination %s out of bills.", denom.toString()));
		} else {
			System.out.println(String.format("Coin dispenser with denomination %s out of coins.", denom.toString()));
		}
		this.amountPaid = this.amountPaid.add(denom);
	}

	// since methods of paying by credit, debit, and gift cards are simulated the same way
	// only one method is needed which works for all of them. - Arie
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
		Set<DeviceController> controllers = this.registeredControllers.get("PaymentController");
		for (DeviceController controller : controllers) {
			if (controller instanceof CardReaderController) {
				((CardReaderController) controller).enablePayment(source, amount);
			}
		}
	}
	/*
	 * This method is called when the user indicates they want to add their own bags
	 */
	public void addOwnBags() {
		Set<DeviceController> baggingControllers = this.registeredControllers.get("BaggingAreaController");
		// store the current weight of items in the bagging controller
		for (DeviceController baggingController : baggingControllers) {
			BaggingScaleController scale = (BaggingScaleController) baggingController;
			double current = scale.getCurrentWeight();
			weight.put(((BaggingAreaController) baggingController), current);
			// let the scale know the customer is adding bags to prevent a weight
			// discrepancy
			scale.setAddingBags(true);
		}

		// let the customer know they can add their bags now
		System.out.print("Add bags now\n");
		// at this point, the customer IO must have signalled they are done adding bags
		// to proceed
		// GUI will implement this part to continue to next lines of code
		//todo: GUI responses.

		// store the new weight in bagging area with bags added
		for (DeviceController baggingController : baggingControllers) {
			BaggingScaleController scale = (BaggingScaleController) baggingController;
			// let the scale know the customer is done adding bags
			scale.setAddingBags(false);
			double current = scale.getCurrentWeight();
			weightWithBags.put(((BaggingAreaController) baggingController), current);
		}

		// at this point the system signals to the attendant IO and locks
		systemProtectionLock = true;
		// if the attendant approves adding bags, the system is unblocked
		if (AttendantApproved) {
			// get the weight of the bags and update the expected weight of the bagging area
			// to account for them
			for (DeviceController baggingController : baggingControllers) {
				double bagWeight = weightWithBags.get(baggingController) - weight.get(baggingController);
				BaggingScaleController scale = (BaggingScaleController) baggingController;
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
		return (HashSet) this.registeredControllers.get("BaggingAreaController");
	}//todo: yeet this method

	//todo:
	//memberships and stuff, if valid, tell scanners and card reader that membership has been validated
	//so they go back to normal function.
	public void validateMembership(String number){
	}

	public void removeItemFromOrder(Product item, BigDecimal amount){
		if (order.containsKey(item)){
			Number[] currentItemInfo = order.get(item);
			amount = amount.min((BigDecimal) currentItemInfo[0]);
			currentItemInfo[0] = ((BigDecimal) currentItemInfo[0]).subtract(amount);
			if (((BigDecimal)currentItemInfo[0]).compareTo(BigDecimal.ZERO)==1) {
				currentItemInfo[1] = ((BigDecimal) currentItemInfo[1]).subtract(item.getPrice().multiply(amount));
				order.put(item, currentItemInfo);
			} else {
				order.remove(item);
			}
			double weight = amount.doubleValue();
			if (item.isPerUnit()){
				weight*=amount.doubleValue();
			}
			for (DeviceController baggingController : registeredControllers.get("BaggingAreaController")) {
				((BaggingAreaController) baggingController).updateExpectedBaggingArea(item, weight, false);
			}
		}
	}
}
