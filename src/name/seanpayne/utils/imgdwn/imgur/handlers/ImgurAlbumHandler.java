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
import name.seanpayne.utils.imgdwn.imgur.ImgurAlbum;
import name.seanpayne.utils.imgdwn.imgur.ImgurImage;
import name.seanpayne.utils.imgdwn.imgur.downloaders.ImgurImageDownloader;
import name.seanpayne.utils.imgdwn.util.DirectoryUtils;

/**
 * @author Sean
 *
 */
public class ImgurAlbumHandler implements IMatchingHandler {
	private static final String ALBUMURL = "(https?:\\/\\/(www\\.)?)?imgur\\.com\\/a\\/([a-zA-Z0-9]+)(#.*)?";
	private static final int ALBUMURLGROUP = 3;
	private static final int RETRYLIMIT = 2;
	private final Pattern albumUrlPattern = Pattern.compile(ALBUMURL);

	@Override
	public boolean isMatch(URL url) {
		Matcher m = albumUrlPattern.matcher(url.toString());
		return m.matches();
	}

	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		Matcher m = albumUrlPattern.matcher(url.toString());
		if(m.matches()) {
			final String albumHash = m.group(ALBUMURLGROUP);
			
			final ImgurAlbum album = new ImgurAlbum(albumHash);
			List<ImgurImage> images = album.getImages();
			if(images != null) {
				File outputSubdir = DirectoryUtils.createOutputDirectory(outputPath, albumHash);
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
