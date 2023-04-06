package com.autovend.software.swing;

import java.util.HashMap;

/**
 * Class used for translating the programmers (english) text to the customer's desired language
 */
public class Language {
	public static String[] languages = new String[] {"English", "French"};

	// Map looks like {
	//   	programmer's text: {
	//   		language: that language's text,
	//   		language2: another language's text
	// 		}
	// 	 	another text for another component: {
	// 	 		language: that language's text,
	// 	 		language2: another language's text
	// 		}
	// 	 }
	public static HashMap<String, HashMap<String, String>> languageBank = new HashMap<>() {{
		put("Log In", new HashMap<>(){{
			put("English", "Log In");
			put("French", "Paris");
		}});
		put("Log Out", new HashMap<>(){{
			put("English", "Log Out");
		}});
		put("Username:", new HashMap<>(){{
			put("English", "Username:");
			put("French", "Baguette");
		}});
		put("Password:", new HashMap<>(){{
			put("English", "Password:");
			put("French:", "Bonjour:");
		}});
		put("Exit", new HashMap<>(){{
			put("English", "Exit");
		}});
		put("Select Language", new HashMap<>(){{
			put("English", "Select Language");
		}});



	}};

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
		if (language == null || text == null) throw new NullPointerException("Language and text cannot be null!");
		// that text has no translations! return original text (can change depending on what should happen)
		if (!languageBank.containsKey(text)) return text;
		// that translation for that text doesn't exist! return original text (can change depending on what should happen)
		if (!languageBank.get(text).containsKey(language)) return text;
		return languageBank.get(text).get(language);
	}
	
	/**
	 * Add a new language to the translator HashMap.
	 * 
	 * @param language
	 * 			Name of the new language.
	 * @param translations
	 * 			HashMap of direct translations from English to the given language.
	 */
//	public static void addLanguage(String language, HashMap<String, String> translations) {
//		languageBank.put(language.toLowerCase(), translations);
//	}
}
