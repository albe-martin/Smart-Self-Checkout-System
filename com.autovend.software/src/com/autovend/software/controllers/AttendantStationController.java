package com.autovend.software.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;

@SuppressWarnings("rawtypes")
public class AttendantStationController {
	
	/**
	 * A hash map containing a set of all IO Controllers with either key 
	 * "AttendantIOController" or "CustomerIOController" and values being a set of DeviceController objects
	 */
	private HashMap<String, Set<DeviceController>> registeredIOControllers = new HashMap<>();
	
	/**
	 * A hashmap containing login credentials for attendants. The key being a username and value being password.
	 * 
	 * https://d2l.ucalgary.ca/d2l/le/497088/discussions/threads/1878152/View
	 * This will suffice despite being a security concern.
	 * 
	 */
	private HashMap<String, String> credentials = new HashMap<>();
	
	//The station that this software controller is to be installed on
	private SupervisionStation attendantStation;
	
	
	//boolean to check if the attendant has successfully logged in.
	private boolean loggedIn;
	
	//string ot hold username of current logged in user
	private String currentUser;

	private static int IDcounter = 1;
	private int stationID = IDcounter++;
	
	
	
	/**
	 * Basic controller without parameters. Can be removed if not needed
	 * It will simply make sure logout() is called.
	 * Controller hashmaps will be initialized but thats it.
	 */
	public AttendantStationController() {
		initControllers();
		logout();
	}
	
	/**
	 * Constructor to assign this controller a station.
	 * @param tation
	 * 		The supervision station to assign this controller to.
	 * 		
	 */
	public AttendantStationController(SupervisionStation station) {
		initControllers();
		attendantStation = station;
		
		Set<DeviceController> attendantIOControllers = registeredIOControllers.get("AttendantIOController");
		
		//Creates IO controller for attendant and adds it to the list
		AttendantIOController controller = new AttendantIOController(attendantStation.screen);
		controller.setMainAttendantController(this);
		
		attendantIOControllers.add(controller);
		
		//Ensure logged out
		logout();
	}
	
	
	
	/**
	 * Creates sets for each controller type in the Hashmap registeredIOControllers
	 */
	public void initControllers() {
		registeredIOControllers = new HashMap<String, Set<DeviceController>>();
		registeredIOControllers.put("AttendantIOController", new HashSet<DeviceController>());
		registeredIOControllers.put("CustomerIOController", new HashSet<DeviceController>());
	}
	
	/**
	 * Getter for stationID
	 * @return
	 * 		Returns the station ID
	 */
	public int getID() {
		return stationID;
	}
	
	/**
	 * Registers a controller and places it in its respective set in the hashmap registieredIOControllers
	 * 
	 * @param controller
	 * 		The controller to be added if it is of type "AttendantIOController" or "CustomerIOController"
	 * 		and is not already in its respective set.
	 * 		Otherwise, do nothing and return;
	 */
	public void registerController(DeviceController controller) {
		if(controller.getTypeName().equals("AttendantIOController")) {
			Set<DeviceController> attendantIOControllers = registeredIOControllers.get("AttendantIOController");
			if(!attendantIOControllers.contains(controller))
				attendantIOControllers.add(controller);
		} 
		else if(!controller.getTypeName().equals("CustomerIOController")) {
			Set<DeviceController> customerIoControllers = registeredIOControllers.get("CustomerIOController");
			if(!customerIoControllers.contains(controller))
				customerIoControllers.add(controller);
		}
		
		return;
	}
	
	/**
	 * Deregisters a controller and removes it from its respective set in the hashmap registieredIOControllers
	 * 
	 * @param controller
	 * 		The controller to be removed if it is of type "AttendantIOController" or "CustomerIOController"
	 * 		and its respective set contains it.
	 * 		Otherwise, do nothing and return;
	 */
	public void deregisterController(DeviceController controller) {
		if(controller.getTypeName().equals("AttendantIOController")) {
			Set<DeviceController> attendantIOControllers = registeredIOControllers.get("AttendantIOController");
			if(attendantIOControllers.contains(controller))
				attendantIOControllers.add(controller);
		} 
		else if(!controller.getTypeName().equals("CustomerIOController")) {
			Set<DeviceController> customerIoControllers = registeredIOControllers.get("CustomerIOController");
			if(customerIoControllers.contains(controller))
				customerIoControllers.add(controller);
		}
		
		return;
	}
	
	/**
	 *  A simple method to add a station to this attendant station's list of stations and its controller.
	 *  Catches IllegalArgumentException of station is null, do nothing.
	 *  Catches IllegalStateException if station is already being supervised, do nothing
	 *  
	 *  @param station
	 *  		The self checkout station
	 *  @param IOController
	 *  		The IO controller of the selfcheckout station.
	 */
	public void addStation(SelfCheckoutStation station, CustomerIOController IOController) {
		attendantStation.add(station);
		registeredIOControllers.get("CustomerIOController").add(IOController);
		//Signal Customer IO to register AttendantIOs of which this station is the main station of
		for(DeviceController io : this.registeredIOControllers.get("AttendantIOController")) {
			if(((AttendantIOController)io).getMainAttendantController().getID() == getID())
				IOController.registerAttendant((AttendantIOController)io);
		}
		return;
	}
	
	/**
	 * A simple method to remove  a station to this attendant station's list of stations along with its controller.
	 */
	public void removeStation(SelfCheckoutStation station, CustomerIOController IOController) {
		boolean result = attendantStation.remove(station);
		//if it was successfully removed, remove its IOController as well.
		if(result) {
			registeredIOControllers.get("CustomerIOController").remove(IOController);
			

			//Signal Customer IO to deregister AttendantIOs of which this station is the main station of
			for(DeviceController io : this.registeredIOControllers.get("AttendantIOController")) {
				if(((AttendantIOController)io).getMainAttendantController().getID() == getID())
					IOController.deregisterAttendant((AttendantIOController)io);
			}
		}
		return;
	}
	
	/**
	 * A simple login method that takes in a username and password and ensures that it matches.
	 * https://d2l.ucalgary.ca/d2l/le/497088/discussions/threads/1878152/View
	 * We do not need to implement a login database and can keep it simple, so a HashMap within this controller
	 * will suffice.
	 * 
	 * The method should signal to AttendantIO of failure or success.
	 * 
	 * @param username
	 * @param password
	 */
	public void login(String username, String password) {
		//Will check if username is valid, then password. If either of those fail, loggedIn is set to false and return
		//Otherwise, loggedIn is set to true and return.
		//Both will signal AttendantIO
		if(credentials.containsKey(username)) {
			if(credentials.get(username).equals(password)) {
				loggedIn = true;
				currentUser = username;
				//Signals AttendantIOController of success
				for(DeviceController io : this.registeredIOControllers.get("AttendantIOController")) {
					((AttendantIOController) io).loginValidity(true, username);
				}
				return;
			}
		}
			
		logout();
		
		//Signals AttendantIOController of failure
		for(DeviceController io : this.registeredIOControllers.get("AttendantIOController")) {
			((AttendantIOController) io).loginValidity(false, "");
		}
		
		return;
	}
	
	/**
	 * Simply resets the login credentials of this station
	 */
	public void logout() {
		loggedIn = false;
		currentUser = "";
		return;
	}
	
	/**
	 * A method to register a username and password for login. 
	 * https://d2l.ucalgary.ca/d2l/le/497088/discussions/threads/1878152/View
	 * Major security concern but due to time constraints, this will suffice
	 * 
	 * If the username does not already exist, add it to credentials.
	 * Otherwise, it will not be added and console will be notified of error.
	 * 
	 * @param username
	 * 		The username for login
	 * @param password
	 * 		The password for login
	 */
	public void registerUser(String username, String password) {
		if(!credentials.containsKey(username)) {
			credentials.put(username, password);
			System.out.println("SUCCESS: User added.");
		}
		else {
			System.out.println("ERROR: Username already exists.");
		}
		return;
	}
	
	/**
	 * A method to register a username and password for login. 
	 * https://d2l.ucalgary.ca/d2l/le/497088/discussions/threads/1878152/View
	 * Major security concern but due to time constraints, this will suffice
	 * 
	 * If the username exists, remove it from credentials.
	 * Otherwise, it will not be removed and console will be notified of error.
	 * 
	 * @param username
	 * 		The username for login credential
	 */
	public void deregisterUser(String username) {
		if(credentials.containsKey(username)) {
			credentials.remove(username);
			System.out.println("SUCCESS: User removed.");
		} else {
			System.out.println("ERROR: Username does not exist.");
		}
		return;
	}
	
	/**
	 * A simple method that will return an immutable list of stations monitored by this attendant station
	 * 
	 * @return 
	 * 		immutable list  of checkout stations.
	 * 		Returns null if not logged in
	 */
	public List<CustomerIOController> getAllStationsIOControllers() {
		List<CustomerIOController> controllers = new ArrayList<>();
		if(loggedIn) {
			//Loop through Customer IO Controllers and add
			for(DeviceController io : this.registeredIOControllers.get("CustomerIOController")) {
				controllers.add((CustomerIOController) io);
			}
			return controllers;
		}
		else {
			return null;
		}
	}
	
	/**
	 * A simple method that will return a list of disabled stations monitored by this attendant station.
	 * 
	 * @return 
	 * 		List of disabled checkout station IO controllers.
	 * 		Returns null if not logged in.
	 */
	public List<CustomerIOController> getDisabledStationsIOControllers() {
		List<CustomerIOController> disabledControllers = new ArrayList<>();
		if(loggedIn) {
			//Loop through Customer IO Controllers
			//If main controller of that io controller is disabled, add io controller to disabled controllers
			for(DeviceController io : this.registeredIOControllers.get("CustomerIOController")) {
				if(((CustomerIOController)io).getMainController().isDisabled()) {
					disabledControllers.add((CustomerIOController) io);
				}
			}
			return disabledControllers;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Getter for current logged in username
	 * @return
	 * 		Username of logged in attendant
	 */
	public String getCurrentUser() {
		return currentUser;
	}
	
	/**
	 * Getter for current logged in username
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}
}
