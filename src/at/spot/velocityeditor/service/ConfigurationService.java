package at.spot.velocityeditor.service;

public interface ConfigurationService {

	/**
	 * Returns the configuration value for the given key.
	 * 
	 * @param key
	 * @return
	 */
	String getProperty(String key);

}