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

package com.autovend.software.swing;

import java.math.BigDecimal;

import java.util.*;

import javax.swing.JFrame;

import com.autovend.*;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.devices.SupervisionStation;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CustomerIOController;
import com.autovend.software.utils.CardIssuerDatabases;

/**
 * Launches the customer and attendant GUIs.
 */
public class GUILauncher {
		
	/**
	 * Main runner.
	 */
	public static void launchGUI(SupervisionStation attendantStation) {
		
		// Get and set up screen
		JFrame attendantScreen = attendantStation.screen.getFrame();
		attendantScreen.setExtendedState(0);
		attendantScreen.setSize(800, 800);
		attendantScreen.setUndecorated(false);
		attendantScreen.setResizable(false);
		attendantScreen.setTitle("Attendant Screen");
		AttendantIOController aioc = new AttendantIOController(attendantStation.screen);
		
		AttendantStationController asc = new AttendantStationController();
		aioc.setMainAttendantController(asc);
		asc.registerController(aioc);
		
		// Add valid username and password for the attendant.
		asc.registerUser("", "");
		
		attendantScreen.setVisible(true);




		// Create list of checkout stations
		int num_stations = 2;
		ArrayList<CustomerIOController> ciocs = new ArrayList<>();
		for (int i = 0; i < num_stations; i++) {
			SelfCheckoutStation customerStation = new SelfCheckoutStation(Currency.getInstance(Locale.CANADA), 
					new int[] {5}, new BigDecimal[] {new BigDecimal(0.25)}, 1000, 1);
			
			for (int j = 0; j < SelfCheckoutStation.BILL_DISPENSER_CAPACITY-1; j++) {
				try {
					customerStation.billDispensers.get(5).load(new Bill(5, Currency.getInstance(Locale.CANADA)));
				} catch (SimulationException | OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (int j = 0; j < SelfCheckoutStation.COIN_DISPENSER_CAPACITY-1; j++) {
				try {
					customerStation.coinDispensers.get(BigDecimal.valueOf(0.25)).load(new Coin(BigDecimal.valueOf(0.25), Currency.getInstance(Locale.CANADA)));
				} catch (SimulationException | OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// Get and set up screen
			JFrame customerScreen = customerStation.screen.getFrame();
			customerScreen.setExtendedState(0);
			customerScreen.setSize(800, 800);
			customerScreen.setUndecorated(false);
			customerScreen.setResizable(false);
			customerScreen.setTitle("Customer Screen #" + (i + 1));
			// Create controller
			CustomerIOController cioc = new CustomerIOController(customerStation.screen);
			cioc.setMainController(new CheckoutController(customerStation));
			
			// Add to array
			ciocs.add(cioc);
			
			customerScreen.setContentPane(new CustomerStartPane(cioc));
			customerScreen.setVisible(true);
			
			// Register customer to attendant
			asc.registerController(cioc);
			cioc.getMainController().registerController("AttendantIOController", aioc);
		}
		for (int ii=0;ii<num_stations;ii++) {ciocs.get(ii).getMainController().shutDown();}
		
		// Create demo products.
		BarcodedProduct bcproduct1 = new BarcodedProduct(new Barcode(Numeral.three, Numeral.three), "box of chocolates",
				BigDecimal.valueOf(83.29), 359.0);
		BarcodedProduct bcproduct2 = new BarcodedProduct(new Barcode(Numeral.four, Numeral.five), "screwdriver",
				BigDecimal.valueOf(42), 60.0);

		// Add demo products to database.
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bcproduct1.getBarcode(), bcproduct1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bcproduct2.getBarcode(), bcproduct2);

		// Need scale set when added
		PLUCodedProduct pluCodedProduct1 = new PLUCodedProduct(new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.four), "apple" , BigDecimal.valueOf(1.00));
		PLUCodedProduct pluCodedProduct2 = new PLUCodedProduct(new PriceLookUpCode(Numeral.four, Numeral.three, Numeral.two, Numeral.one), "banana" , BigDecimal.valueOf(0.75));
		PLUCodedProduct pluCodedProduct3 = new PLUCodedProduct(new PriceLookUpCode(Numeral.one, Numeral.one, Numeral.one, Numeral.one), "bunch of jabuticaba" , BigDecimal.valueOf(15.00));

		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCodedProduct1.getPLUCode(), pluCodedProduct1);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCodedProduct2.getPLUCode(), pluCodedProduct2);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCodedProduct3.getPLUCode(), pluCodedProduct3);
		
		// Run customer event simulators.
		CustomerEventSimulator customerEventSimulatorFrame = new CustomerEventSimulator(aioc.getDevice().getFrame(),ciocs.get(0).getMainController().checkoutStation);
		customerEventSimulatorFrame.setVisible(true);
		customerEventSimulatorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		
		// Run attendant event simulator.
		AttendantEventSimulator attendantEventSimulatorFrame = new AttendantEventSimulator(aioc.getDevice().getFrame(), ciocs.get(0).getMainController(), ciocs.get(1).getMainController());
		attendantEventSimulatorFrame.setVisible(true);
		attendantEventSimulatorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	}
	
	public static void main(String[] args) {
		SupervisionStation attendantStation = new SupervisionStation();
		launchGUI(attendantStation);
	}
}
