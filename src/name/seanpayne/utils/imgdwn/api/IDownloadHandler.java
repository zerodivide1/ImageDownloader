package name.seanpayne.utils.imgdwn.api;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public interface IDownloadHandler {

	public abstract void download(URL url, File outputPath,
			ExecutorService threadService);

}