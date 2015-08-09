package at.spot.velocityeditor.service;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import at.spot.velocityeditor.constants.UiConstants;

public class ImageResourceService {
	private static ImageResourceService INSTANCE = new ImageResourceService();
	
	public Image getImageResource(String key, int imageSize) {
		return new Image(Display.getDefault(), UiConstants.RES_BASE_PATH + imageSize + "/" + key);
	}
	
	public static ImageResourceService getInstance() {
		return INSTANCE;
	}
}
