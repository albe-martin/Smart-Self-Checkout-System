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

import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.external.CardIssuer;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.Product;
import com.autovend.software.swing.CustomerOperationPane;
import com.autovend.software.utils.CardIssuerDatabases;
import com.autovend.software.utils.MiscProductsDatabase;

import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("rawtypes")

public class CheckoutController {
	// todo: comb through classes fields to update modifiers for them, getters and
	// setters
	// will be provided for testing purposes for fields where those are necessary.
	private static int IDcounter = 1;
	private final int stationID = IDcounter++;
	private LinkedHashMap<Product, Number[]> order;
	private final Map<Product, Double> latestItem = new HashMap<>();

	public BigDecimal cost;
	protected BigDecimal amountPaid;
	private HashMap<String, ArrayList<DeviceController>> registeredControllers;
	// A hashmap which maps the simple names of controller classes to sets of
	// that type of controller, this is just simpler and less tedious to program
	// with.

	public boolean baggingItemLock;
	public boolean systemProtectionLock;
	public boolean payingChangeLock;
	public boolean addingBagsLock;
	private boolean isDisabled = false;
	private boolean isShutdown = false;

	// need this variable to know if this station is being used or not.
	private boolean inUse = false;

	// Supervisor Station ID. 0 = not supervised
	private int supervisorID = 0;

	public SelfCheckoutStation checkoutStation;
	private boolean requireAdjustment;

	/**
	 * Constructors for CheckoutController
	 */

	public CheckoutController() {
		initControllers();
		clearOrder();
	}


	private void initControllers() {
		registeredControllers = new HashMap<>();
		registeredControllers.put("BaggingAreaController", new ArrayList<>());
		registeredControllers.put("ItemAdderController", new ArrayList<>());
		registeredControllers.put("PaymentController", new ArrayList<>());
		registeredControllers.put("ReceiptPrinterController", new ArrayList<>());
		registeredControllers.put("ChangeSlotController", new ArrayList<>());
		registeredControllers.put("ChangeDispenserController", new ArrayList<>());
		registeredControllers.put("ValidPaymentControllers", new ArrayList<>());
		registeredControllers.put("AttendantIOController", new ArrayList<>());
		registeredControllers.put("CustomerIOController", new ArrayList<>());
		registeredControllers.put("ScanningScaleController", new ArrayList<>());
		registeredControllers.put("ReusableBagDispenserController", new ArrayList<>());
	}

	public CheckoutController(SelfCheckoutStation checkout) {
		checkoutStation = checkout;
		// todo: getters and setters for checkout

		initControllers();
		BarcodeScannerController mainScannerController = new BarcodeScannerController(checkout.mainScanner);
		BarcodeScannerController handheldScannerController = new BarcodeScannerController(checkout.handheldScanner);
		this.registeredControllers.get("ItemAdderController")
				.addAll(List.of(mainScannerController, handheldScannerController));

		BaggingScaleController scaleController = new BaggingScaleController(checkout.baggingArea);
		this.registeredControllers.get("BaggingAreaController").add(scaleController);

		ReusableBagDispenser bagDispenser = new ReusableBagDispenser(105);
		ReusableBagDispenserController bagDispenserController = new ReusableBagDispenserController(bagDispenser);
		this.registeredControllers.get("ReusableBagDispenserController").add(bagDispenserController);

		this.registeredControllers.get("ReceiptPrinterController").add(new ReceiptPrinterController(checkout.printer));

		BillPaymentController billPayController = new BillPaymentController(checkout.billValidator);
		CoinPaymentController coinPaymentController = new CoinPaymentController(checkout.coinValidator);
		CardReaderController cardReaderController = new CardReaderController(checkout.cardReader);
		this.registeredControllers.get("ValidPaymentControllers")
				.addAll(List.of(billPayController, coinPaymentController, cardReaderController));

		BillChangeSlotController billChangeSlotController = new BillChangeSlotController(checkout.billOutput);
		CoinTrayController coinChangeSlotController = new CoinTrayController(checkout.coinTray);
		this.registeredControllers.get("ChangeSlotController")
				.addAll(List.of(billChangeSlotController, coinChangeSlotController));

		HashSet<ChangeDispenserController> changeDispenserControllers = new HashSet<>();
		for (int denom : checkout.billDispensers.keySet()) {
			changeDispenserControllers
					.add(new BillDispenserController(checkout.billDispensers.get(denom), BigDecimal.valueOf(denom)));
		}

		for (BigDecimal denom : checkout.coinDispensers.keySet()) {
			changeDispenserControllers.add(new CoinDispenserController(checkout.coinDispensers.get(denom), denom) {
			});
		}
		registeredControllers.get("ChangeDispenserController").addAll(changeDispenserControllers);

		// Added CustomerIOController initialization
		// NOTE: AttendantIOController should be added when and only when a checkout
		// station
		// is added to an attendant station as a checkout station can only be monitored
		// by at most one attendant station.

		CustomerIOController customerIOController = new CustomerIOController(checkout.screen);
		this.registeredControllers.get("CustomerIOController").add(customerIOController);

		//todo: add attendant controller
		ScanningScaleController scanningScaleController = new ScanningScaleController(checkout.scale);
		this.registeredControllers.get("ScanningScaleController").add(scanningScaleController);


		registerAll();
		clearOrder();
	}

	public int getID() {
		return stationID;
	}

	/**
	 * Returns supervisor ID
	 * 
	 * @return
	 *         The ID of the supervisor/attendant station
	 *         0 = No supervisor
	 */
	public int getSupervisor() {
		return supervisorID;
	}

	/**
	 * set supervisor ID
	 * 
	 * @param id
	 *           The ID of the supervisor/attendant station
	 */
	public void setSupervisor(int id) {
		this.supervisorID = id;
	}

	public ArrayList<DeviceController> getControllersByType(String type) {
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
		ArrayList<DeviceController> controllers = registeredControllers.get("BaggingAreaController");
		for (DeviceController controller : controllers) {
			((BaggingAreaController) controller).resetOrder();
		}
		this.latestItem.clear();
	}

	// Getters for the order and cost for this checkout controller's current order.
	public LinkedHashMap<Product, Number[]> getOrder() {
		return this.order;
	}

	public BigDecimal getCost() {
		return this.cost;
	}

	/**
	 * Methods to register and deregister peripherals
	 */
	public void deregisterController(String typeName, DeviceController controller) {
		ArrayList<DeviceController> controllerSet = this.registeredControllers.get(typeName);
		if (controllerSet == null) {
			return;
		}
		if (controllerSet.contains(controller)) {
			controllerSet.remove(controller);

		}
	}

	public void registerController(String typeName, DeviceController controller) {
		ArrayList<DeviceController> controllerSet = this.registeredControllers.get(typeName);
		if (controllerSet == null) {
			return;
		}
		if (!controllerSet.contains(controller)) {
			controllerSet.add(controller);
		}
	}

	void registerAll() {
		for (String key : this.registeredControllers.keySet()) {
			ArrayList<DeviceController> controllerSet = this.registeredControllers.get(key);
			for (DeviceController controller : controllerSet) {
				controller.setMainController(this);
			}
		}
	}

	public HashSet<DeviceController> getAllDeviceControllers() {
		HashSet<DeviceController> out = new HashSet<>();
		for (String key : this.registeredControllers.keySet()) {
			out.addAll(this.registeredControllers.get(key));
		}
		return out;
	}

	/**
	 * A different variant of getAllDeviceControllers
	 */
	public HashMap<String, ArrayList<DeviceController>> getAllDeviceControllersRevised() {
		return this.registeredControllers;
	}

	/**
	 * Method to add reusable bags to the order after the customer signals to buy
	 * bags
	 * 
	 * @param numBags The number of bags getting added
	 */
	public void purchaseBags(int numBags) {
		if (baggingItemLock || systemProtectionLock || isDisabled) {
			return;
		}
		ArrayList<DeviceController> bagDispenserController = registeredControllers
				.get("ReusableBagDispenserController");
		this.addItem(MiscProductsDatabase.MISC_DATABASE.get(MiscProductsDatabase.bagNumb), BigDecimal.valueOf(numBags));
		((ReusableBagDispenserController) bagDispenserController.get(0)).dispenseBags(numBags);
	}

	// if the user wants to cancel adding bags, this will do so.
	public void cancelAddingBagsLock() {
		addingBagsLock = false;
		boolean unlock = true;
		ArrayList<DeviceController> baggingControllers = this.registeredControllers.get("BaggingAreaController");
		for (DeviceController baggingController : baggingControllers) {
			BaggingScaleController scale = (BaggingScaleController) baggingController;
			if (!scale.getBaggingValid()) {
				unlock = false;
				break;
			}
		}
		baggingItemLock = unlock;
	}

	public void setAddingBagsLock() {
		this.addingBagsLock = true;
		this.baggingItemLock = true;
	}

	public void alertAttendant(String message) {
		ArrayList<DeviceController> io = registeredControllers.get("AttendantIOController");
		((AttendantIOController) io.get(0)).displayMessage(message);
	}

	public void notifyAddBags() {
		ArrayList<DeviceController> io = registeredControllers.get("AttendantIOController");
		((AttendantIOController) io.get(0)).notifyAddBags(this);
	}

	/**
	 * A method called by attendant I/O when they have approved adding bags
	 */
	public void approveAddingBags() {
			ArrayList<DeviceController> baggingControllers = this.registeredControllers.get("BaggingAreaController");
			for (DeviceController baggingController : baggingControllers) {
				BaggingScaleController scale = (BaggingScaleController) baggingController;
				scale.setExpectedWeight(scale.getCurrentWeight());
			}
			addingBagsLock = false;
			baggingItemLock = false;
	}

	/*
	 * Methods used by ItemAdderControllers
	 */

	/**
	 * Method to add items to the order
	 */
	public void addItem(Product newItem) {
		this.addItem(newItem, BigDecimal.ONE);
	}

	public void addItem(Product newItem, BigDecimal count) {
		if (baggingItemLock || systemProtectionLock || isDisabled || count.compareTo(BigDecimal.ZERO) <= 0 || newItem == null) {
			return;
		}
		// then go through the item and get its weight, either expected weight if it
		// exists, or
		// get the scale controller in the checkout to give us the weight for the PLU
		// code based item.
		Number[] currentItemInfo = new Number[] { BigDecimal.ZERO, BigDecimal.ZERO };

		// Add item to order
		if (this.order.containsKey(newItem)) {
			currentItemInfo = this.order.get(newItem);
		}

		double weight;
		if (newItem.isPerUnit()) {
			weight = ((BarcodedProduct) newItem).getExpectedWeight() * count.intValue();
			if (weight <= 0) {
				return;
			}
			// if an item has a weight less than or equal to 0, then do nothing.
			// since such an item couldn't exist.

			// only items priced per unit weight are barcoded products, so this is fine.
			currentItemInfo[0] = (currentItemInfo[0].intValue()) + (count.intValue());
			currentItemInfo[1] = ((BigDecimal) currentItemInfo[1]).add(newItem.getPrice().multiply(count));

			// Add the cost of the new item to the current cost.
			this.cost = this.cost.add(newItem.getPrice().multiply(count));
		} else {
			ArrayList<DeviceController> scaleController = registeredControllers.get("ScanningScaleController");
			try {
				weight = ((ScanningScaleController) scaleController.get(0)).getCurrentWeight() * count.intValue();
			} catch (IndexOutOfBoundsException e) {
				weight = 0;
			}

			if (weight <= 0) {
				return;
			}

			// adding the recorded weight on the current scale to the current item
			// information
			currentItemInfo[0] = ((BigDecimal) currentItemInfo[0]).add(BigDecimal.valueOf(weight));
			currentItemInfo[1] = ((BigDecimal) currentItemInfo[1]).add(
					newItem.getPrice().multiply(BigDecimal.valueOf(weight)));

			// Add the cost of the new item to the current cost.
			this.cost = this.cost.add(newItem.getPrice().multiply(BigDecimal.valueOf(weight)));

		}
		this.order.put(newItem, currentItemInfo);
		this.latestItem.clear();
		this.latestItem.put(newItem, currentItemInfo[1].doubleValue());
		for (DeviceController baggingController : registeredControllers.get("BaggingAreaController")) {
			((BaggingAreaController) baggingController).updateExpectedBaggingArea(newItem, weight, true);
		}
		// Notify customerIO
		for (DeviceController customerIOController : registeredControllers.get("CustomerIOController")) {
			((CustomerIOController) customerIOController).notifyItemAdded();
		}
		baggingItemLock = true;
	}

	public void doNotBagLatest() {
		if (this.latestItem.size() == 0) {
			return;
		}
		Product latestProd = this.latestItem.keySet().iterator().next();
		if (baggingItemLock && this.latestItem.get(latestProd) > 0) {
			for (DeviceController baggingController : registeredControllers.get("BaggingAreaController")) {
				((BaggingAreaController) baggingController).updateExpectedBaggingArea(
						null, this.latestItem.get(latestProd), false);
				this.latestItem.clear();
				this.baggingItemLock = false;
				// todo: this is a bad way to handle this
			}
			for (DeviceController customerIOController : registeredControllers.get("CustomerIOController")) {
				((CustomerIOController) customerIOController).notifyNoBagApproved();
			}
		}
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
			if (!((BaggingAreaController) baggingController).getBaggingValid()) {
				unlockStation = false;
				break;
			}
		}
		baggingItemLock = !unlockStation;
	}

	void baggedItemsInvalid(boolean weightReduced) {
		if (!addingBagsLock) {
			this.baggingItemLock = true;
			((CustomerIOController) registeredControllers.get("CustomerIOController").get(0))
					.displayWeightDiscrepancyMessage();
			if (!weightReduced) {
				alertAttendant("Weight discrepancy at station " + this.getID());
			}
		}
	}

	void baggingAreaError() {
		((CustomerIOController) registeredControllers.get("CustomerIOController").get(0))
				.displayBaggingProtectionLock();
		alertAttendant("Overload for scale at station " + this.getID());
		this.systemProtectionLock = true;
	}

	void baggingAreaErrorEnded() {
		this.systemProtectionLock = false;
		alertAttendant("Overload for scale at station " + this.getID() + " has ended.");
	}

	void attendantOverrideBaggingLock() {
		ArrayList<DeviceController> baggingControllers = this.registeredControllers.get("BaggingAreaController");
		for (DeviceController baggingController : baggingControllers) {
			BaggingScaleController scale = (BaggingScaleController) baggingController;
			scale.setExpectedWeight(scale.getCurrentWeight());
		}
		baggingItemLock = false;
	}

	/**
	 * Methods used to control the ReceiptPrinterController
	 */
	public void printReceipt() {
		// print receipt
		if (!registeredControllers.get("ReceiptPrinterController").isEmpty()) {
			StringBuilder receipt = ((ReceiptPrinterController) this.registeredControllers.get("ReceiptPrinterController").get(0)).createReceipt(this.order, this.cost);
			// call print receipt method in the ReceiptPrinterController class with the
			// order details and cost

			//check ink and paper level after printing
			boolean lowInk = ((ReceiptPrinterController) this.registeredControllers.get("ReceiptPrinterController").get(0)).lowInk();
			boolean lowPaper = ((ReceiptPrinterController) this.registeredControllers.get("ReceiptPrinterController").get(0)).lowPaper();
			if (lowInk || lowPaper) {
				// if either ink or paper is low then the station will be disabled
				for(DeviceController io : this.registeredControllers.get("AttendantIOController")) {
					((AttendantIOController) io).disableStation(this);
					((AttendantIOController) io).rePrintReceipt(this, receipt);
				}
			}
			else {
				((ReceiptPrinterController) this.registeredControllers.get("ReceiptPrinterController").get(0)).printReceipt(receipt);
			}


		}
		clearOrder();
		CustomerIOController cioc = (CustomerIOController) this.registeredControllers.get("CustomerIOController").get(0);
		if (!isDisabled) {cioc.startMenu();} else {cioc.notifyStartup();}
	}

	public boolean needPrinterRefill = false;

	void printerOutOfResources(StringBuilder receipt) {
		this.needPrinterRefill = true;
		AttendantIOController aioc = (AttendantIOController) this.registeredControllers.get("AttendantIOController").get(0);
		if (receipt.isEmpty()){
			alertAttendant("Printer low on resources");
		}else if (this.order.size() > 0){
			aioc.notifyRePrintReceipt(this, receipt);
		}
		else {
			alertAttendant("Printing Error at station, no receipt contents");
		}
		CustomerIOController cioc = (CustomerIOController) this.registeredControllers.get("CustomerIOController").get(0);
		cioc.notifyEnabled();
		disableStation();
	}

	void printerRefilled() {
		this.needPrinterRefill = false;
	}

	public void setOrder(LinkedHashMap<Product, Number[]> newOrd) {
		this.order = newOrd;
		for (Map.Entry<Product, Number[]> entry : this.order.entrySet()) {
			Product product = entry.getKey();
			this.cost = this.cost.add(product.getPrice());
		}
	}





	/**
	 * Methods to control the PaymentController
	 */
	public void addToAmountPaid(BigDecimal val) {
		amountPaid = amountPaid.add(val);
		if (this.registeredControllers.get("CustomerIOController").size()>0) {
			CustomerIOController cioc = (CustomerIOController) this.registeredControllers.get("CustomerIOController").get(0);
			((CustomerOperationPane) (cioc.getDevice().getFrame().getContentPane())).updateAmountPaid();
		}
	}

	public BigDecimal getRemainingAmount() {
		return getCost().subtract(amountPaid);
	}

	public enum completePaymentErrorEnum {LOCKED, DUE, EMPTYORDER, CHANGE, RECEIPT}
	public completePaymentErrorEnum completePayment() {
		if (this.baggingItemLock || this.systemProtectionLock || isDisabled) {
			return completePaymentErrorEnum.LOCKED;
		}
		if (this.cost.compareTo(this.amountPaid) > 0) {
			System.out.println("You haven't paid enough money yet.");
			return completePaymentErrorEnum.DUE;
		}
		if (this.order.keySet().size() == 0) {
			System.out.println("Your order is empty.");
			return completePaymentErrorEnum.EMPTYORDER;
		}
		if (this.cost.compareTo(this.amountPaid) < 0) {
			this.payingChangeLock = true;
			// This code is inefficient and could be better, too bad!
			dispenseChange();
			return completePaymentErrorEnum.CHANGE;
		} else {
			printReceipt();
			return completePaymentErrorEnum.RECEIPT;
		}
	}

	public void dispenseChange() {
		if (!payingChangeLock) {
			return;
		}
		CustomerIOController cioc = (CustomerIOController) this.registeredControllers.get("CustomerIOController").get(0);
		((CustomerOperationPane) (cioc.getDevice().getFrame().getContentPane())).updateAmountPaid();
		if ((getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) && payingChangeLock) {

			printReceipt();

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
		changeDenomLow(controller, denom);
		alertAttendant("Out of Change!!!!!");
		disableStation();
		if (controller instanceof BillDispenserController) {
			System.out.println(String.format("Bill dispenser with denomination %s out of bills.", denom.toString()));
		} else {
			System.out.println(String.format("Coin dispenser with denomination %s out of coins.", denom.toString()));
		}
		this.amountPaid = this.amountPaid.add(denom);
	}

	// when bill/coin dispenser signals that a certain denomination is low (for now
	// low is defined as <= 5)
	public void changeDenomLow(ChangeDispenserController controller, BigDecimal denom) {
		if (controller instanceof BillDispenserController) {
			for (DeviceController io : this.registeredControllers.get("AttendantIOController")) {
				((AttendantIOController) io).notifyLowBillDenomination(this, controller, denom);
			}
		} else {
			for (DeviceController io : this.registeredControllers.get("AttendantIOController")) {
				((AttendantIOController) io).notifyLowCoinDenomination(this, controller, denom);
			}
		}
		this.requireAdjustment = true;
	}

	// generic method used to control how payment by credit/debit card is handled.
	public void payByBankCard(CardReaderControllerState state, CardIssuer source, BigDecimal payAmount) {
		if (baggingItemLock || systemProtectionLock || payingChangeLock || isDisabled || source == null) {
			return;
		}
		if (payAmount.compareTo(getRemainingAmount()) > 0) {
			return;
			// only reason to pay more than the order with card is to mess with the amount
			// of change the system has for some reason
			// so preventing stuff like this would be a good idea.
		}
		ArrayList<DeviceController> controllers = this.registeredControllers.get("PaymentController");
		for (DeviceController controller : controllers) {
			if (controller instanceof CardReaderController) {
				((CardReaderController) controller).setState(state, source, payAmount);
			}
		}
	}

	public void payByGiftCard() {
		if (baggingItemLock || systemProtectionLock || payingChangeLock || isDisabled) {
			return;
		}
		ArrayList<DeviceController> controllers = this.registeredControllers.get("PaymentController");
		for (DeviceController controller : controllers) {
			if (controller instanceof CardReaderController) {
				((CardReaderController) controller).setState(CardReaderControllerState.PAYINGBYGIFTCARD, null,
						this.getRemainingAmount());
			}
		}
	}

	/**
	 * A method used to remove an item from a customers order of some given
	 * quantity.
	 *
	 * @param item
	 * @param amount
	 */
	public void removeItemFromOrder(Product item, BigDecimal amount) {
		if (order.containsKey(item)) {
			Number[] currentItemInfo = order.get(item);
			amount = amount.min(BigDecimal.valueOf(currentItemInfo[0].doubleValue()));
			currentItemInfo[0] = (BigDecimal.valueOf(currentItemInfo[0].doubleValue())).subtract(amount);
			if ((BigDecimal.valueOf(currentItemInfo[0].doubleValue())).compareTo(BigDecimal.ZERO) == 1) {
				currentItemInfo[1] = ((BigDecimal) currentItemInfo[1]).subtract(item.getPrice().multiply(amount));
				order.put(item, currentItemInfo);
				this.cost = this.cost.subtract(item.getPrice().multiply(amount));
			} else {
				order.remove(item);
				this.cost = this.cost.subtract(item.getPrice().multiply(amount));
			}
			double weight = amount.doubleValue();
			if (item.isPerUnit()) {
				weight *= ((BarcodedProduct) item).getExpectedWeight();
			}
			for (DeviceController baggingController : registeredControllers.get("BaggingAreaController")) {
				((BaggingAreaController) baggingController).updateExpectedBaggingArea(item, weight, false);
			}

			for (DeviceController cioc : registeredControllers.get("CustomerIOController")) {
				((CustomerIOController) cioc).notifyItemRemoved();
			}


		}
	}

	// When sign in starts, tells card reader and barcode scanner
	// to be ready to scan a membership card.
	public void signingInAsMember() {
		for (DeviceController cardReaderController : registeredControllers.get("ValidPaymentControllers")) {
			if (cardReaderController instanceof  CardReaderController){
			((CardReaderController) cardReaderController).setState(CardReaderControllerState.REGISTERINGMEMBERS);
			}
		}

		for (DeviceController barcodeScannerController : registeredControllers.get("ItemAdderController")) {
			((BarcodeScannerController) barcodeScannerController).setScanningItems(false);
		}
	}


	public void validateMembership(String number){
		boolean isValid = CardIssuerDatabases.MEMBERSHIP_DATABASE.containsKey(number);

		if (isValid) {
			for (DeviceController cardReaderController : registeredControllers.get("ValidPaymentControllers")) {
				if (cardReaderController instanceof  CardReaderController) {

					((CardReaderController) cardReaderController).setState(CardReaderControllerState.NOTINUSE);
				}
			}
			for (DeviceController barcodeScannerController : registeredControllers.get("ItemAdderController")) {
				((BarcodeScannerController) barcodeScannerController).setScanningItems(true);
			}
			((CustomerIOController) registeredControllers.get("CustomerIOController").get(0)).signedIn(CardIssuerDatabases.MEMBERSHIP_DATABASE.get(number));
		} else {
			// todo: GUI methods to notify failed sign-in
		}
	}


	public void cancelSigningInAsMember(){
		for (DeviceController cardReaderController : registeredControllers.get("ValidPaymentControllers")) {
			if (cardReaderController instanceof CardReaderController) {
				((CardReaderController) cardReaderController).setState(CardReaderControllerState.NOTINUSE);
			}
		}
		for (DeviceController barcodeScannerController : registeredControllers.get("ItemAdderController")) {
			((BarcodeScannerController) barcodeScannerController).setScanningItems(true);
		}
	}

	/**
	 * A method that enables all devices registered. It also sets disabled flag to
	 * false
	 */
	public void enableAllDevices() {
		for (String controllerType : registeredControllers.keySet()) {
			for (DeviceController device : registeredControllers.get(controllerType)) {
				device.enableDevice();
			}
		}
		isDisabled = false;
	}

	/**
	 * A method that disabled all devices registered. It also sets disabled flag to
	 * true;
	 */
	public void disableAllDevices() {
		for (String controllerType : registeredControllers.keySet()) {
			for (DeviceController device : registeredControllers.get(controllerType)) {
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

		for (DeviceController io : registeredControllers.get("CustomerIOController")) {
			((CustomerIOController) io).notifyShutdown();
		}
		disableAllDevices();

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

	public void disableStation() {

		if (!isShutdown) {
			clearOrder();
			this.isDisabled = true;
			inUse = false;
			
			// Notify customerIO
			for (DeviceController io : registeredControllers.get("CustomerIOController")) {
				((CustomerIOController) io).notifyDisabled();
			}
			disableAllDevices();
			for (DeviceController io : registeredControllers.get("AttendantIOController")) {
				((AttendantIOController) io).updateAttendantGUI();
			}
		} else {
		}
	}

	public void enableStation() {
		if (!isShutdown) {
			enableAllDevices();
			clearOrder();
			this.isDisabled = false;
			
			// Notify customerIO
			for (DeviceController io : registeredControllers.get("CustomerIOController")) {
				((CustomerIOController) io).notifyEnabled();
			}
			for (DeviceController io : registeredControllers.get("AttendantIOController")) {
				((AttendantIOController) io).updateAttendantGUI();
			}
		}
	}
	
	/**
	 * Method that notifies an attendant for a no bag request
	 */
	public void notifyAttendantNoBagRequest() {
		baggingItemLock = true;
		for(DeviceController io: this.registeredControllers.get("AttendantIOController")) {
			((AttendantIOController) io).notifyNoBagRequest(this);
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
	 * 
	 * @return
	 *         True if shut down, false otherwise.
	 */
	public boolean isShutdown() {
		return isShutdown;
	}

	public void disableCardReader(){
		ArrayList<DeviceController> controllers = this.registeredControllers.get("PaymentController");
		for (DeviceController controller : controllers) {
			if (controller instanceof CardReaderController) {
				((CardReaderController) controller).setState(CardReaderControllerState.NOTINUSE);
			}
		}

	}
}