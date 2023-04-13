package com.autovend.software.test;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.BillSlot;
import com.autovend.software.controllers.BillChangeSlotController;
import com.autovend.software.controllers.CheckoutController;

public class BillChangeSlotControllerTest {
	private boolean result;
	
	@Before
	public void setup() {
		result = false;
	}
	
	@Test
	public void TestInstantiation() {
		assertTrue(new BillChangeSlotController(new BillSlot(false)) instanceof BillChangeSlotController);
	}
	
	@Test
	public void testReactInserted() {
		new BillChangeSlotController(new BillSlot(false)).reactToBillInsertedEvent(null);
	}
	
	@Test
	public void testReactEjected() {
		new BillChangeSlotController(new BillSlot(false)).reactToBillEjectedEvent(null);
	}
	
	@Test
	public void testReactRemoved() {
		CheckoutController checkout = new CheckoutController() {
			@Override
			public void dispenseChange() {
				result = true;
			}
		};
		BillChangeSlotController controller = new BillChangeSlotController(new BillSlot(false));
		controller.setMainController(checkout);
		controller.reactToBillRemovedEvent(null);
		assertTrue(result);
	}
}
