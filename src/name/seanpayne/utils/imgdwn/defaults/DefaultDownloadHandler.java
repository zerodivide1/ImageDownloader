/**
 * 
 */
package name.seanpayne.utils.imgdwn.defaults;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;

import name.seanpayne.utils.imgdwn.api.IMatchingHandler;

/**
 * @author Sean
 *
 */
public class DefaultDownloadHandler implements IMatchingHandler {
	private static final int RETRYLIMIT = 1;

	@Override
	public boolean isMatch(URL url) {
		return true;
	}

	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		threadService.execute(new DefaultDownloader(url, outputPath, RETRYLIMIT));
	}

}
