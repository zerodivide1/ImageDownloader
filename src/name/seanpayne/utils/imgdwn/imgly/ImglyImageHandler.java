/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgly;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.seanpayne.utils.imgdwn.api.IMatchingHandler;

/**
 * @author Sean
 *
 */
public class ImglyImageHandler implements IMatchingHandler {
	private static final String IMAGEURL = "(https?:\\/\\/(www\\.)?)?img\\.ly\\/([a-zA-Z0-9]+)";
	private static final int IMAGEURLGROUP = 3;
	private static final int RETRYLIMIT = 2;
	private static Pattern imageUrlPattern = Pattern.compile(IMAGEURL);
	
	/* (non-Javadoc)
	 * @see name.seanpayne.utils.imgdwn.api.IMatchingHandler#isMatch(java.net.URL)
	 */
	@Override
	public boolean isMatch(URL url) {
		Matcher m = imageUrlPattern.matcher(url.toString());
		
		return m.matches();
	}

	/* (non-Javadoc)
	 * @see name.seanpayne.utils.imgdwn.api.IDownloadHandler#download(java.net.URL, java.io.File, java.util.concurrent.ExecutorService)
	 */
	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		Matcher m = imageUrlPattern.matcher(url.toString());
		if(m.matches()) {
			final String imageHash = m.group(IMAGEURLGROUP);
			
			threadService.execute(new ImglyImageDownloader(imageHash, outputPath, null, RETRYLIMIT));
		}
	}

	

}
