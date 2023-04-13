package com.autovend.software.test;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Currency;

import org.junit.Test;

import com.autovend.Coin;
import com.autovend.devices.CoinTray;
import com.autovend.devices.DisabledException;
import com.autovend.devices.OverloadException;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CoinTrayController;

public class CoinTrayControllerTest {
	@Test
	public void testInstantiation() {
		assertTrue(new CoinTrayController(new CoinTray(1)) instanceof CoinTrayController);
	}
	
	@Test
	public void testReactDifferentTray() {
		CoinTrayController controller = new CoinTrayController(new CoinTray(1));
		controller.reactToCoinAddedEvent(new CoinTray(3));
	}
	
	@Test
	public void testReactNoSpace() throws DisabledException, OverloadException {
		CoinTray tray = new CoinTray(1);
		tray.accept(new Coin(BigDecimal.valueOf(0.1), Currency.getInstance(Locale.CANADA)));
		CoinTrayController controller = new CoinTrayController(tray);
		controller.reactToCoinAddedEvent(tray);
	}
	
	@Test
	public void testReactSpace() {
		CoinTray tray = new CoinTray(1);
		CoinTrayController controller = new CoinTrayController(tray);
		controller.setMainController(new CheckoutController());
		controller.reactToCoinAddedEvent(tray);
		
	}
}
