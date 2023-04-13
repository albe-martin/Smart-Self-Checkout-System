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

package com.autovend.software.swing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class used for translating the programmers (english) text to the customer's desired language
 */
public class Language {
	public static ArrayList<String> languages = new ArrayList<>(List.of("English"));
	public static String defaultLanguage = "English";

	// Map looks like {
	//   	language: {
	//   		text1: translation,
	//   		text2: translation
	// 		}
	// 	 	language2: {
	// 	 		text1: translation,
	// 	 		text2: translation
	// 		}
	// 	 }
	private static final HashMap<String, HashMap<String, String>> languageBank = new HashMap<>() {};

	/**
	 * Translates the programmers (english) text to the translation of said language, if it exists.
	 * @param language
	 * 			The language to translate to.
	 * @param text
	 * 			The text to be translated. Must be provided in English.
	 * @return
	 * 			The translated text.
	 */
	public static String translate(String language, String text) {
		if (language == null || text == null) throw new NullPointerException("Language and text params cannot be null!");
		// we don't translate english to english!
		if (language.equals("English")) return text;
		// that language has no translations! return original text (can change depending on what should happen)
		if (!languageBank.containsKey(language)) return text;
		// that translation for that language doesn't exist! return original text (can change depending on what should happen)
		if (!languageBank.get(language).containsKey(text)) return text;
		return languageBank.get(language).get(text);
	}

	/**
	 * Adds a new language to the languageBank, with all of its translations
	 * @param language the language the translations are in
	 * @param translations the HashMap of all the translations
	 */
	public static void addLanguage(String language, HashMap<String, String> translations) {
		if (language == null || translations == null) throw new NullPointerException("language and translations params cannot be null!");
		languageBank.put(language, translations);
	}
	
	/**
	 * Get the language bank.
	 */
	public static HashMap<String, HashMap<String, String>> getLanguageBank() {
		return languageBank;
	}
}
