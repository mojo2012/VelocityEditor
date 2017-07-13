package at.spot.velocityeditor.service;

import org.eclipse.swt.graphics.Image;

public interface ImageResourceService {

	/**
	 * Loads an image resource with the given size.
	 * 
	 * @param key
	 * @param imageSize
	 * @return
	 */
	Image getImageResource(String key, int imageSize);

}