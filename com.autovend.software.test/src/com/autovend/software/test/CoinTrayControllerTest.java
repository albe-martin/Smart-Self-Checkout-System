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
