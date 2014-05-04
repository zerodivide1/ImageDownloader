/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgly;

import java.net.URL;

import name.seanpayne.utils.imgdwn.api.IDownloadHandler;
import name.seanpayne.utils.imgdwn.api.IImageService;
import name.seanpayne.utils.imgdwn.api.IMatchingHandler;

/**
 * @author Sean
 *
 */
public class ImglyService implements IImageService {
	IMatchingHandler[] handlers = {
		new ImglyImageHandler()
	};

	@Override
	public IDownloadHandler getHandler(URL url) {
		for (IMatchingHandler handler : handlers) {
			if(handler.isMatch(url))
				return handler;
		}
		return null;
	}

}
