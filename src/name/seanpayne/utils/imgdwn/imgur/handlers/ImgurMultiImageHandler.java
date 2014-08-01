package name.seanpayne.utils.imgdwn.imgur.handlers;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.seanpayne.utils.imgdwn.api.IMatchingHandler;
import name.seanpayne.utils.imgdwn.imgur.ImgurImage;
import name.seanpayne.utils.imgdwn.imgur.downloaders.ImgurImageDownloader;

public class ImgurMultiImageHandler implements IMatchingHandler {

	private static final String MULTIURL = "(https?:\\/\\/(www\\.)?)?imgur\\.com\\/(([a-zA-Z0-9]+)(,([a-zA-Z0-9]+))+)";
	private static final int IMAGEGROUP = 3;
	private static final int RETRYLIMIT = 2;
	private final Pattern multiUrlPattern = Pattern.compile(MULTIURL);
	
	@Override
	public boolean isMatch(URL url) {
		Matcher m = multiUrlPattern.matcher(url.toString());
		return m.matches();
	}
	
	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		Matcher m = multiUrlPattern.matcher(url.toString());
		if(m.matches()) {
			final String images = m.group(IMAGEGROUP);
			
			String[] imageHashes = images.split(",");
			for (String hash : imageHashes) {
				ImgurImage image = new ImgurImage(hash);
				threadService.execute(new ImgurImageDownloader(image, outputPath, null, RETRYLIMIT));
			}
		}
	}


}
