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
