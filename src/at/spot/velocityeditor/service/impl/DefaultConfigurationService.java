package at.spot.velocityeditor.service.impl;

import java.util.Properties;

import org.springframework.stereotype.Service;

import at.spot.velocityeditor.service.ConfigurationService;

@Service("configurationService")
public class DefaultConfigurationService implements ConfigurationService {
	
	Properties prop = new Properties();

	public DefaultConfigurationService() {
		
//		prop.load(inStream);
	}
	
	/* (non-Javadoc)
	 * @see at.spot.velocityeditor.service.impl.ConfigurationService#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String key) {
		return null;
	}
}
