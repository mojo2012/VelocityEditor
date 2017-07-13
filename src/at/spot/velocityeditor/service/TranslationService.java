package at.spot.velocityeditor.service;

import java.util.Locale;

public interface TranslationService {

	/**
	 * Returns a translation for the given key using the default locale.
	 * 
	 * @param key
	 * @return
	 */
	String getTranslation(String key);

	/**
	 * Returns a translation for the given key.
	 * 
	 * @param key
	 * @return
	 */
	String getTranslation(String key, Locale locale);
}