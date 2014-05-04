/**
 * 
 */
package name.seanpayne.utils.imgdwn.flickr;

import java.net.URL;

import name.seanpayne.utils.imgdwn.api.IDownloadHandler;
import name.seanpayne.utils.imgdwn.api.IImageService;
import name.seanpayne.utils.imgdwn.api.IMatchingHandler;
import name.seanpayne.utils.imgdwn.flickr.handlers.FlickrSetHandler;
import name.seanpayne.utils.imgdwn.flickr.handlers.FlickrSinglePhotoHandler;
import name.seanpayne.utils.imgdwn.flickr.handlers.FlickrUserHandler;

/**
 * @author Sean
 *
 */
public class FlickrService implements IImageService {

	IMatchingHandler[] handlers = {
		new FlickrSetHandler(),
		new FlickrSinglePhotoHandler(),
		new FlickrUserHandler()
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
