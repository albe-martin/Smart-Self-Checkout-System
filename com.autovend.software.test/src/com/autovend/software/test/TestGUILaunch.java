package com.autovend.software.test;

import static org.junit.Assert.fail;
import org.junit.Test;
import com.autovend.devices.SupervisionStation;
import com.autovend.software.swing.GUILauncher;

public class TestGUILaunch {

	/**
	 * Simple test to ensure that the GUILauncher does not throw any exceptions.
	 */
	@Test
	public void test() {
		try {
			SupervisionStation attendantStation = new SupervisionStation();
			GUILauncher.launchGUI(attendantStation);
		} catch (Exception e) {
			fail("No exceptions expection. Stack trace: " + e);
		}
	}
	
}
