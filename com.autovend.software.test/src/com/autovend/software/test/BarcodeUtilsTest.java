package com.autovend.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.software.utils.BarcodeUtils;

public class BarcodeUtilsTest {
	@Test
	public void testStringBarcodeToBarcode() {
		Barcode actual= BarcodeUtils.stringBarcodeToBarcode("1234");
		Barcode expected = new Barcode(Numeral.one, Numeral.two, Numeral.three, Numeral.four);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testStringPLUToPLU() {
		PriceLookUpCode actual = BarcodeUtils.stringPLUToPLU("1234");
		PriceLookUpCode expected = new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.four);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testInstantiate() {
		assertTrue(new BarcodeUtils() instanceof BarcodeUtils);
	}
}
