/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgur.handlers;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.seanpayne.utils.imgdwn.api.IMatchingHandler;
import name.seanpayne.utils.imgdwn.imgur.ImgurImage;
import name.seanpayne.utils.imgdwn.imgur.downloaders.ImgurImageDownloader;

/**
 * @author Sean
 *
 */
public class ImgurImageHandler implements IMatchingHandler {
	private static final String IMAGEURL = "(https?:\\/\\/(www\\.)?)?imgur\\.com\\/([a-zA-Z0-9]+)"; 
	private static final int IMAGEURLGROUP = 3;
	private static final int RETRYLIMIT = 2;
	private final Pattern imageUrlPattern = Pattern.compile(IMAGEURL);

	@Override
	public boolean isMatch(URL url) {
		Matcher m = imageUrlPattern.matcher(url.toString());
		return m.matches();
	}

	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		Matcher m = imageUrlPattern.matcher(url.toString());
		if(m.matches()) {
			final String imageHash = m.group(IMAGEURLGROUP);
			
			ImgurImage image = new ImgurImage(imageHash);
			threadService.execute(new ImgurImageDownloader(image, outputPath, null, RETRYLIMIT));
		}
	}

}
