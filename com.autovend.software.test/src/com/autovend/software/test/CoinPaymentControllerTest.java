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
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.CoinValidator;
import com.autovend.devices.TouchScreen;
import com.autovend.software.controllers.CheckoutController;
import com.autovend.software.controllers.CoinPaymentController;
import com.autovend.software.controllers.CustomerIOController;

public class CoinPaymentControllerTest {
	private boolean result;
	
	@Before
	public void setup() {
		result = false;
	}
	@Test
	public void testInstantiation() {
		assertTrue(new CoinPaymentController(new CoinValidator(Currency.getInstance(Locale.CANADA), Arrays.asList(BigDecimal.ONE))) instanceof CoinPaymentController);
	}
	
	@Test
	public void testReactDifferentDevice() {
		CoinPaymentController controller = new CoinPaymentController(new CoinValidator(Currency.getInstance(Locale.CANADA), Arrays.asList(BigDecimal.ONE)));
		controller.reactToValidCoinDetectedEvent(new CoinValidator(Currency.getInstance(Locale.CANADA), Arrays.asList(BigDecimal.ONE)), BigDecimal.ONE);
	}
	
	@Test
	public void testReactValid() {
		CoinValidator validator = new CoinValidator(Currency.getInstance(Locale.CANADA), Arrays.asList(BigDecimal.ONE));
		CoinPaymentController controller = new CoinPaymentController(validator);
		CheckoutController checkout = new CheckoutController() {
			@Override
			public void addToAmountPaid(BigDecimal val) {
				result = true;
			};
		};
		controller.setMainController(checkout);
		controller.reactToValidCoinDetectedEvent(validator, BigDecimal.ONE);
		assertTrue(result);
	}
	
	@Test
	public void testReactInvalid() {
		CoinPaymentController controller = new CoinPaymentController(new CoinValidator(Currency.getInstance(Locale.CANADA), Arrays.asList(BigDecimal.ONE)));
		controller.reactToInvalidCoinDetectedEvent(new CoinValidator(Currency.getInstance(Locale.CANADA), Arrays.asList(BigDecimal.ONE)));
	}
}
