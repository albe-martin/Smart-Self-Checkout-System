package com.autovend.software.swing;

import java.util.HashMap;

/**
 * Class used for translating the programmers (english) text to the customer's desired language.
 */
public class Language {
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
		put("Select Language", new HashMap<>(){{
			put("English", "Select Language");
		}});
	}};

	/**
	 * Translates the english text for a gui element into the language specified
	 * @param language the language used for an instance of the calling gui
	 * @param text the text to be translated
	 * @return the translated text
	 */
	public static String translate(String language, String text) {
		return languageBank.get(text).get(language);
	}
}
