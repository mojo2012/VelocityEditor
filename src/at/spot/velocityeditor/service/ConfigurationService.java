package at.spot.velocityeditor.service;

import java.util.Properties;

import org.springframework.stereotype.Service;

@Service("configurationService")
public class ConfigurationService {
	
	Properties prop = new Properties();

	public ConfigurationService() {
		
//		prop.load(inStream);
	}
	
	public String getProperty(String key) {
		return null;
	}
}
