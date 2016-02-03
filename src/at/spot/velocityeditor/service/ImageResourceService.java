package at.spot.velocityeditor.service;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.springframework.stereotype.Service;

import at.spot.velocityeditor.constants.UiConstants;

@Service("imageResourceService")
public class ImageResourceService {
	public Image getImageResource(String key, int imageSize) {
		return new Image(Display.getDefault(), UiConstants.RES_BASE_PATH + imageSize + "/" + key);
	}
}
