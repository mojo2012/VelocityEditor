package at.spot.velocityeditor.service;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.stereotype.Service;

import at.spot.velocityeditor.constants.ApplicationConstants;

/**
 * A very primitive translation service that fetches translations from a translation properties file.
 * @author matthias
 *
 */
@Service("translationService")
public class TranslationService {
	protected Locale			locale;
	protected ResourceBundle	translation;

	public TranslationService() {
		this.locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));
		
		try {
			this.translation = ResourceBundle.getBundle(ApplicationConstants.TRANSLATION_BASE_PATH + ApplicationConstants.TRANSLATION_BASE_FILENAME,
					this.locale);
		} catch (MissingResourceException e) {
			this.translation = ResourceBundle.getBundle(ApplicationConstants.TRANSLATION_BASE_PATH + ApplicationConstants.TRANSLATION_BASE_FILENAME, 
					Locale.US);
		}
	}

	public String getTranslation(String key) {
		return translation.getString(key);
	}
}