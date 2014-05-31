/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgur.handlers;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.seanpayne.utils.imgdwn.api.IMatchingHandler;
import name.seanpayne.utils.imgdwn.imgur.ImgurGallery;
import name.seanpayne.utils.imgdwn.imgur.ImgurImage;
import name.seanpayne.utils.imgdwn.imgur.downloaders.ImgurImageDownloader;
import name.seanpayne.utils.imgdwn.util.DirectoryUtils;

/**
 * @author Sean
 *
 */
public class ImgurGalleryHandler implements IMatchingHandler {
	private static final String GALLERYURL = "(https?:\\/\\/(www\\.)?)?imgur\\.com\\/gallery\\/([a-zA-Z0-9]+)"; 
	private static final int GALLERYURLGROUP = 3;
	private static final int RETRYLIMIT = 2;
	private final Pattern galleryUrlPattern = Pattern.compile(GALLERYURL);

	@Override
	public boolean isMatch(URL url) {
		Matcher m = galleryUrlPattern.matcher(url.toString());
		return m.matches();
	}

	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		Matcher m = galleryUrlPattern.matcher(url.toString());
		if(m.matches()) {
			final String galleryHash = m.group(GALLERYURLGROUP);
			
			ImgurGallery gallery = new ImgurGallery(galleryHash);
			List<ImgurImage> images = gallery.getImages();
			if(images != null) {
				File outputSubdir = DirectoryUtils.createOutputDirectory(outputPath, galleryHash);
				if(outputSubdir != null) {
					for(int i=0; i < images.size(); i++) {
						ImgurImage image = images.get(i);
						
						threadService.execute(new ImgurImageDownloader(image, outputSubdir, i, RETRYLIMIT));
					}
				}
			}
		}
	}

}
