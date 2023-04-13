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
