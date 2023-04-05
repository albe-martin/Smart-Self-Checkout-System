package com.autovend.software.swing;

/**
 * Class used for translating text to the customer's desired language.
 */
public class Language {
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
		} else {
			// Language not supported.
			throw new IllegalArgumentException("Language not supported.");
		}
	}
}
