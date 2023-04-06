package com.autovend.software.swing;

import java.util.HashMap;

/**
 * Class used for translating text to the customer's desired language.
 */
public class Language {
	public static HashMap<String, HashMap<String, String>> languageBank = new HashMap<>() {{
		put("Log In", new HashMap<>(){{
			put("English", "Log In");
		}});
		put("Log Out", new HashMap<>(){{
			put("English", "Log Out");
		}});
		put("Username:", new HashMap<>(){{
			put("English", "Username:");
		}});
		put("Password:", new HashMap<>(){{
			put("English", "Password:");
		}});
		put("Exit", new HashMap<>(){{
			put("English", "Exit");
		}});
	}};

	/**
	 * Translates the text to the desired language.
	 *
	 * @param text
	 * 			The text to be translated. Must be provided in English.
	 */
	public static String translate(String language, String text) {
//		if (selfCheckoutLanguage.equalsIgnoreCase("English")) {
//			// If language is English, translation is not required.
//			return text;
//		} else {
//			// Language not supported.
//			throw new IllegalArgumentException("Language not supported.");
//		}
		return languageBank.get(text).get(language);
	}
}
