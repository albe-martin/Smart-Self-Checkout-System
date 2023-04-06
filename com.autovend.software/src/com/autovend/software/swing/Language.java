package com.autovend.software.swing;

import java.util.HashMap;

/**
 * Class used for translating the programmers (english) text to the customer's desired language
 */
public class Language {
	// Map looks like: {language: "English text", "translation"}
	public static HashMap<String, HashMap<String, String>> languageBank = new HashMap<>();

	/**
	 * Translates the text to the desired language.
	 * 
	 * @param language
	 * 			The language to translate to.
	 * @param text
	 * 			The text to be translated. Must be provided in English.
	 * @return
	 * 			The translated text.
	 */
	public static String translate(String language, String text) {
		if (language.equalsIgnoreCase("English")) {
			// If language is English, translation is not required.
			return text;
			
		} else if (languageBank.containsKey(language.toLowerCase())) {
			String translation = languageBank.get(language.toLowerCase()).get(text);
			if (translation == null) {
				// Translation not found, return original text.
				return text;
			} else {
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
		languageBank.put(language.toLowerCase(), translations);
	}
}
