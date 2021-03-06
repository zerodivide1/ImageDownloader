/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgur;

import java.net.URL;

import name.seanpayne.utils.imgdwn.api.IDownloadHandler;
import name.seanpayne.utils.imgdwn.api.IImageService;
import name.seanpayne.utils.imgdwn.api.IMatchingHandler;
import name.seanpayne.utils.imgdwn.imgur.handlers.*;

/**
 * @author Sean
 *
 */
public class ImgurService implements IImageService {
	IMatchingHandler[] handlers = {
		new ImgurGalleryHandler(),
		new ImgurAlbumHandler(),
		new ImgurImageHandler(),
		new ImgurMultiImageHandler(),
        new ImgurGifvImageHandler(),
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
