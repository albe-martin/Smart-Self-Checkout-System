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
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;

@SuppressWarnings("rawtypes")

public class CheckoutController {
	//todo: comb through classes fields to update modifiers for them, getters and setters
	//will be provided for testing purposes for fields where those are necessary.
	private static int IDcounter = 1;
	private int stationID = IDcounter++;
	private LinkedHashMap<Product, Number[]> order;
	private double latestWeight;
	public BigDecimal cost;
	protected BigDecimal amountPaid;
	private HashMap<String, Set<DeviceController>> registeredControllers;
	//A hashmap which maps the simple names of controller classes to sets of
	//that type of controller, this is just simpler and less tedious to program with.

	public boolean baggingItemLock;
	public boolean systemProtectionLock;
	private boolean payingChangeLock;
	public boolean addingBagsLock;
	private boolean isDisabled = false;
	private boolean isShutdown = false;

	//need this variable to know if this station is being used or not.
	private boolean inUse = false;

	//Supervisor Station ID. 0 = not supervised
	private int supervisorID = 0;

	private SelfCheckoutStation checkoutStation;

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
		registeredControllers.put("ValidPaymentControllers", new HashSet<DeviceController>());
		registeredControllers.put("AttendantIOController", new HashSet<DeviceController>());
		registeredControllers.put("CustomerIOController", new HashSet<DeviceController>());
	}
	public CheckoutController(SelfCheckoutStation checkout) {
		checkoutStation=checkout;
		//todo: getters and setters for checkout

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

		// Added CustomerIOController initialization
		//NOTE: AttendantIOController should be added when and only when a checkout station
		// is added to an attendant station as a checkout station can only be monitored by at most one attendant station.

		CustomerIOController customerIOController = new CustomerIOController(checkout.screen);
		this.registeredControllers.get("CustomerIOController").add(customerIOController);

		registerAll();
		clearOrder();
	}

	public int getID() {
		return stationID;
	}

	/**
	 * Returns supervisor ID
	 * @return
	 * 		The ID of the supervisor/attendant station
	 * 		0 = No supervisor
	 */
	public int getSupervisor() {
		return supervisorID;
	}

	/**
	 * set supervisor ID
	 * @param id
	 * 		The ID of the supervisor/attendant station
	 */
	public void setSupervisor(int id) {
		this.supervisorID = id;
	}

	public Set<DeviceController> getControllersByType(String type) {
		return this.registeredControllers.get(type);
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
	 * A different variant of getAllDeviceControllers
	 */
	public HashMap<String, Set<DeviceController>> getAllDeviceControllersRevised(){
		return this.registeredControllers;
	}

	/**
	 * Method to add reusable bags to the order after the customer signals to buy
	 * bags
	 * @param numBags The number of bags getting added
	 */
	public void purchaseBags(int numBags) {
		if (baggingItemLock || systemProtectionLock) {return;}
		//TODO: Update how Add Bags works to use the right database, right now this is temporary!!!!
		BigDecimal bagCost = ProductDatabases.PLU_PRODUCT_DATABASE.get("BagNumb").getPrice();
		Set<DeviceController> bagDispenserController = registeredControllers.get("ReusableBagDispenserController");
		this.addItem(ProductDatabases.PLU_PRODUCT_DATABASE.get("BagNumb"), BigDecimal.valueOf(numBags));
		((ReusableBagDispenserController) bagDispenserController.iterator().next()).dispenseBags(numBags);
	}

	//if the user wants to cancel adding bags, this will do so.
	public void cancelAddingBagsLock(){
		addingBagsLock = false;
		boolean unlock=true;
		Set<DeviceController> baggingControllers = this.registeredControllers.get("BaggingAreaController");
		for (DeviceController baggingController : baggingControllers) {
			BaggingScaleController scale = (BaggingScaleController) baggingController;
			if (!scale.getBaggingValid()) {
				unlock=false;
				break;
			}
		}
		baggingItemLock=unlock;
	}

	public void setAddingBagsLock(){
		this.addingBagsLock=true;
		this.baggingItemLock=true;
		//using this just do I don't need to make a bunch of changes to if-statements because i'm lazy.
		//todo: stuff with GUIs
	}

	//TODO: COMPLETE THIS METHOD!!!!!!
	public void alertAttendant(String message){}

	/**
	 * A method called by attendant I/O when they have approved adding bags
	 */
	public void approveAddingBags() {
		if (addingBagsLock) {
			Set<DeviceController> baggingControllers = this.registeredControllers.get("BaggingAreaController");
			for (DeviceController baggingController : baggingControllers) {
				BaggingScaleController scale = (BaggingScaleController) baggingController;
				scale.setExpectedWeight(scale.getCurrentWeight());
			}
			addingBagsLock=false;
			baggingItemLock=false;
		}
	}


	/*
	 * Methods used by ItemAdderControllers
	 */

	/**
	 * Method to add items to the order
	 */
	public void addItem(Product newItem, BigDecimal count) {
		if (baggingItemLock || systemProtectionLock || newItem == null) {return;}
		//then go through the item and get its weight, either expected weight if it exists, or
		//get the scale controller in the checkout to give us the weight for the PLU code based
		//item.

		Number[] currentItemInfo = new Number[] { BigDecimal.ZERO, BigDecimal.ZERO };

		// Add item to order
		if (this.order.containsKey(newItem)) {
			currentItemInfo = this.order.get(newItem);
		}


		double weight;
		if (newItem.isPerUnit()) {
			weight = ((BarcodedProduct) newItem).getExpectedWeight();
			if (weight<=0){return;}
			//if an item has a weight less than or equal to 0, then do nothing.
			//since such an item couldn't exist.

			//only items priced per unit weight are barcoded products, so this is fine.
			currentItemInfo[0] = (currentItemInfo[0].intValue())+(count.intValue());
			currentItemInfo[1] = ((BigDecimal) currentItemInfo[1]).add(newItem.getPrice().multiply(count));

			// Add the cost of the new item to the current cost.
			this.cost = this.cost.add(newItem.getPrice());
		} else {
			Set<DeviceController> scaleController = registeredControllers.get("ScanningScaleController");
			weight = ((ScanningScaleController) scaleController.stream().toList().get(0)).getCurrentWeight();
			//adding the recorded weight on the current scale to the current item information
			currentItemInfo[0] = ((BigDecimal) currentItemInfo[0]).add(BigDecimal.valueOf(weight));
			currentItemInfo[1] = ((BigDecimal) currentItemInfo[1]).add(
					newItem.getPrice().multiply(BigDecimal.valueOf(weight))
			);

			// Add the cost of the new item to the current cost.
			this.cost = this.cost.add(newItem.getPrice().multiply(BigDecimal.valueOf(weight)));
		}

		this.order.put(newItem, currentItemInfo);
		this.latestWeight= currentItemInfo[1].doubleValue();
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
		if (!addingBagsLock) {
			this.baggingItemLock = true;
			System.out.println(ErrorMessage);
		}
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

	// generic method used to control how payment by credit/debit card is handled.
	public void payByBankCard(CardReaderControllerState state, CardIssuer source, BigDecimal payAmount) {
		if (baggingItemLock || systemProtectionLock || payingChangeLock || source == null) {
			return;
		}
		if (payAmount.compareTo(getRemainingAmount()) > 0) {
			return;
			// only reason to pay more than the order with card is to mess with the amount
			// of change the system has for some reason
			// so preventing stuff like this would be a good idea.
		}
		Set<DeviceController> controllers = this.registeredControllers.get("PaymentController");
		for (DeviceController controller : controllers) {
			if (controller instanceof CardReaderController) {
				((CardReaderController) controller).setState(state, source, payAmount);
			}
		}
	}

	public void payByGiftCard() {
		if (baggingItemLock || systemProtectionLock || payingChangeLock) {
			return;
		}
		Set<DeviceController> controllers = this.registeredControllers.get("PaymentController");
		for (DeviceController controller : controllers) {
			if (controller instanceof CardReaderController) {
				((CardReaderController) controller).setState(CardReaderControllerState.PAYINGBYGIFTCARD, null, this.getRemainingAmount());
			}
		}
	}

	public Map<BaggingAreaController, Double> getWeight() {
		return this.weight;
	}

	/**
	 * A method used to remove an item from a customers order of some given quantity.
	 * @param item
	 * @param amount
	 */
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
				weight*=((BarcodedProduct) item).getExpectedWeight();
			}
			for (DeviceController baggingController : registeredControllers.get("BaggingAreaController")) {
				((BaggingAreaController) baggingController).updateExpectedBaggingArea(item, weight, false);
			}
		}
	}

	// When sign in starts, tells card reader and barcode scanner 
	// to be ready to scan a membership card.
	public void signingInAsMember() {
		for (DeviceController cardReaderController : registeredControllers.get("CardReaderController")) {
			((CardReaderController) cardReaderController).enableMemberReg();
		}
		
		for (DeviceController barcodeScannerController : registeredControllers.get("BarcodeScannerController")) {
			((BarcodeScannerController) barcodeScannerController).setScanningItems(false);
		}
		
	}
	//todo:
	//memberships and stuff, if valid, tell scanners and card reader that membership has been validated
	//so they go back to normal function.
	public void validateMembership(String number){
		// Since we do not know what validates a membership number, a temporary local variable
		// will be used for testing purposes, which assumes the membership number is correct
		boolean isValid = true;
		
		if (isValid) {
			for (DeviceController cardReaderController : registeredControllers.get("CardReaderController")) {
				((CardReaderController) cardReaderController).disableMemberReg();
			}
			
			for (DeviceController barcodeScannerController : registeredControllers.get("BarcodeScannerController")) {
				((BarcodeScannerController) barcodeScannerController).setScanningItems(true);
			}
		} else {
			// ????
		}
	}

	/**
	 * A method that enables all devices registered. It also sets disabled flag to false
	 */
	public void enableAllDevices() {
		//note: change behaviour depending on whether it was shut down or not for this
		//and below method, also change how it starts up depending on those flags;

		//Temp solution
		for(String controllerType : registeredControllers.keySet()) {
			for(DeviceController device : registeredControllers.get(controllerType)) {
				device.enableDevice();
			}
		}
		isDisabled = false;
	}

	/**
	 * A method that disabled all devices registered. It also sets disabled flag to true;
	 */
	public void disableAllDevices() {
		for(String controllerType : registeredControllers.keySet()) {
			for(DeviceController device : registeredControllers.get(controllerType)) {
				device.disableDevice();
			}
		}
		isDisabled = true;
	}

	/**
	 * Inititiates shut down.
	 * Sets shutdown flag to true.
	 * Clears order.
	 * Disables devices
	 * Notifies all customer IO of shut down
	 */
	public void shutDown() {
		setShutdown(true);
		clearOrder();
		disableAllDevices();

		for (DeviceController io : registeredControllers.get("CustomerIOController")) {
			((CustomerIOController) io).notifyShutdown();
		}
	}

	/**
	 * Initiates Startup.
	 * Sets shutdown flag to false.
	 * Ensures devices are STILL disabled. Must be re-enabled
	 * Clears order.
	 * Notifies both Customer IO controller and Attendant Controller of startup.
	 */
	public void startUp() {
		setShutdown(false);
		if (!isDisabled) {
			enableAllDevices();
		}

		for (DeviceController io : registeredControllers.get("CustomerIOController")) {
			((CustomerIOController) io).notifyStartup();
		}
		for (DeviceController io : registeredControllers.get("AttendantIOController")) {
			// Notify attendant about startup.
			((AttendantIOController) io).notifyStartup(this);
		}
	}

	public void disableStation(){
		if (!isShutdown && !inUse) {
			disableAllDevices();
			clearOrder();
			this.isDisabled = true;
			inUse=false;
		}
	}

	public void enableStation(){
		if (!isShutdown) {
			enableAllDevices();
			clearOrder();
			this.isDisabled = false;
		}
	}


	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean set) {
		inUse = set;
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void setShutdown(boolean b) {
		this.isShutdown = b;
	}
	
	/**
	 * Check if the station is currently shut down.
	 * @return
	 * 			True if shut down, false otherwise.
	 */
	public boolean isShutdown() {
		return isShutdown;
	}
}
