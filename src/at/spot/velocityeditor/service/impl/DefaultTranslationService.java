package at.spot.velocityeditor.service.impl;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import at.spot.velocityeditor.constants.ApplicationConstants;
import at.spot.velocityeditor.service.TranslationService;

/**
 * A very primitive translation service that fetches translations from a
 * translation properties file.
 * 
 * @author matthias
 *
 */
@Service("translationService")
public class DefaultTranslationService implements TranslationService {
	protected Locale locale;
	protected ResourceBundle translation;

	public DefaultTranslationService() {
		this.locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));

		try {
			this.translation = ResourceBundle.getBundle(
					ApplicationConstants.TRANSLATION_BASE_PATH + ApplicationConstants.TRANSLATION_BASE_FILENAME,
					this.locale);
		} catch (final MissingResourceException e) {
			this.translation = ResourceBundle.getBundle(
					ApplicationConstants.TRANSLATION_BASE_PATH + ApplicationConstants.TRANSLATION_BASE_FILENAME,
					Locale.US);
		}
	}

	@Override
	public String getTranslation(final String key) {
		return translation.getString(key);
	}

	@Override
	public String getTranslation(final String key, final Locale locale) {
		throw new NotImplementedException();
	}
}