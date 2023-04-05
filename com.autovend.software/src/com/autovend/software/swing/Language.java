package com.autovend.software.swing;

import java.util.HashMap;

/**
 * Class used for translating text to the customer's desired language.
 */
public class Language {
	/**
	 * HashMap containing each language and the direct text translations of that language.
	 */
	private static HashMap<String, HashMap<String, String>> translator = new HashMap<>();
	/**
	 * Translates the text to the desired language.
	 * 
	 * @param language
	 * 			The language to translate to.
	 * @param text
	 * 			The text to be translated. Must be provided in English.
	 */
	public static String translate(String language, String text) {
		if (language.equalsIgnoreCase("English")) {
			// If language is English, translation is not required.
			return text;
			
		} else if (translator.containsKey(language.toLowerCase())) {
			// Translate to language.
			String translation = translator.get(language.toLowerCase()).get(text);
			
			if (translation == null) {
				// Translation not found, return original english text.
				return text;
			} else {
				// Return translation.
				return translation;
			}
			
		} else {
			// Language not supported.
			throw new IllegalArgumentException("Language not supported.");
		}
	}
	
	/**
	 * Add a new language to the translator HashMap.
	 * 
	 * @param language
	 * 			Name of the new language.
	 * @param translations
	 * 			HashMap of direct translations from English to the given language.
	 */
	public static void addLanguage(String language, HashMap<String, String> translations) {
		translator.put(language.toLowerCase(), translations);
	}
}
