package com.autovend.software.swing;

import javax.swing.JFrame;

import com.autovend.devices.SupervisionStation;
import com.autovend.software.controllers.AttendantIOController;
import com.autovend.software.controllers.AttendantStationController;

public class GUILauncher {
	public static void main(String[] args) {
		SupervisionStation attendantStation = new SupervisionStation();
		JFrame attendantScreen = attendantStation.screen.getFrame();
		
		AttendantIOController ioc = new AttendantIOController(attendantStation.screen);
		
		attendantScreen.setContentPane(AttendantGUIUtils.getLoginPane(ioc));
		
		
		AttendantStationController asc = new AttendantStationController();
		ioc.setMainAttendantController(asc);
		asc.registerController(ioc);
		
		asc.registerUser("abc", "123");
		
		attendantScreen.setVisible(true);
	
		System.out.println("running");
	}
}
