/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgur.handlers;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;

import name.seanpayne.utils.imgdwn.api.IMatchingHandler;
import name.seanpayne.utils.imgdwn.defaults.DefaultDownloader;

/**
 * @author Sean
 *
 */
public class ImgurAbsoluteImageHandler implements IMatchingHandler {
	private static final String IMGURIMAGEURLABSOLUTE = "(https?:\\/\\/)?i.imgur\\.com\\/([a-zA-Z0-9]+\\.[a-zA-Z0-9]{3,4})";
	private static final int RETRYLIMIT = 2;

	@Override
	public boolean isMatch(URL url) {
		return url.toString().matches(IMGURIMAGEURLABSOLUTE);
	}

	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		threadService.execute(new DefaultDownloader(url, outputPath, RETRYLIMIT));
	}

}
