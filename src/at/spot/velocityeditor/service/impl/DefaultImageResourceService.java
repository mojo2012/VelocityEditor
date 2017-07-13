package at.spot.velocityeditor.service.impl;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.springframework.stereotype.Service;

import at.spot.velocityeditor.constants.UiConstants;
import at.spot.velocityeditor.service.ImageResourceService;

@Service("imageResourceService")
public class DefaultImageResourceService implements ImageResourceService {
	/* (non-Javadoc)
	 * @see at.spot.velocityeditor.service.impl.ImageResourceService#getImageResource(java.lang.String, int)
	 */
	@Override
	public Image getImageResource(String key, int imageSize) {
		return new Image(Display.getDefault(), UiConstants.RES_BASE_PATH + imageSize + "/" + key);
	}
}
