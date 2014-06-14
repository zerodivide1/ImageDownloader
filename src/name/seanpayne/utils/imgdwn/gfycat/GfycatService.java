package name.seanpayne.utils.imgdwn.gfycat;

import java.net.URL;

import name.seanpayne.utils.imgdwn.api.IDownloadHandler;
import name.seanpayne.utils.imgdwn.api.IImageService;
import name.seanpayne.utils.imgdwn.api.IMatchingHandler;

/**
 * 
 * @author Sean
 *
 */
public class GfycatService implements IImageService {
	IMatchingHandler[] handlers = {
		new GfycatHandler()
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
