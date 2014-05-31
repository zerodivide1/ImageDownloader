/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgur;

import java.net.URL;

import name.seanpayne.utils.imgdwn.api.IDownloadHandler;
import name.seanpayne.utils.imgdwn.api.IImageService;
import name.seanpayne.utils.imgdwn.api.IMatchingHandler;
import name.seanpayne.utils.imgdwn.imgur.handlers.ImgurAbsoluteImageHandler;
import name.seanpayne.utils.imgdwn.imgur.handlers.ImgurAccountAlbumsHandler;
import name.seanpayne.utils.imgdwn.imgur.handlers.ImgurAlbumHandler;
import name.seanpayne.utils.imgdwn.imgur.handlers.ImgurGalleryHandler;
import name.seanpayne.utils.imgdwn.imgur.handlers.ImgurImageHandler;

/**
 * @author Sean
 *
 */
public class ImgurService implements IImageService {
	IMatchingHandler[] handlers = {
		new ImgurGalleryHandler(),
		new ImgurAlbumHandler(),
		new ImgurImageHandler(),
		new ImgurAbsoluteImageHandler(),
		new ImgurAccountAlbumsHandler()
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
