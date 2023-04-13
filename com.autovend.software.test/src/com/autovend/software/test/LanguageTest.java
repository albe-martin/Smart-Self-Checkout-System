package com.autovend.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import com.autovend.software.swing.Language;

public class LanguageTest {
	@Test
	public void testInstantiation() {
		assertTrue(new Language() instanceof Language);
	}
	
	@Test (expected = NullPointerException.class)
	public void testTranslateNullLanguage() {
		Language.translate(null, "hi");
	}
	
	@Test (expected = NullPointerException.class)
	public void testTranslateNullText() {
		Language.translate("engligh", null);
	}
	
	@Test
	public void testTranslateEnglish() {
		assertEquals("hi", Language.translate("English", "hi"));
	}
	
	@Test
	public void testTranslateNoLanguage() {
		assertEquals("hi", Language.translate("German", "hi"));
	}
	
	@Test
	public void testTranslateLanguageNoTranslation() {
		HashMap<String, String> french = new HashMap<>();
		french.put("hello", "bonjour");
		Language.addLanguage("French", french);
		assertEquals("hi", Language.translate("French", "hi"));
	}
	
	@Test
	public void testTranslateNewLanguage() {
		HashMap<String, String> french = new HashMap<>();
		french.put("hi", "bonjour");
		Language.addLanguage("French", french);
		assertEquals("bonjour", Language.translate("French", "hi"));
	}
	
	@Test (expected = NullPointerException.class)
	public void testAddLanguageNullLanguage() {
		Language.addLanguage(null, new HashMap<String, String>());
	}
	
	@Test (expected = NullPointerException.class)
	public void testAddLanguageNullTranslations() {
		Language.addLanguage("Spanish", null);
	}
	
	@Test
	public void testGetLanguageBank() {
		assertTrue(Language.getLanguageBank() instanceof HashMap<String, HashMap<String, String>>);
	}
}
