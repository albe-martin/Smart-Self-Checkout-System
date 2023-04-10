package com.autovend.software.swing;

import java.util.HashMap;

/**
 * Class used for translating the programmers (english) text to the customer's desired language
 */
public class Language {
	// public static String[] languages = new String[] {"English", "French"};

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
	private static final HashMap<String, HashMap<String, String>> languageBank = new HashMap<>() {{
			put("English", new HashMap<>(){{
				put("Log In", "Log In");
				put("Log Out", "Log Out");
				put("Username:", "Username:");
				put("Password:", "Password:");
				put("Exit", "Exit");
				put("Select Language", "Select Language");
		}});
	}};

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
