/**
 * 
 */
package name.seanpayne.utils.imgdwn.api;

import java.net.URL;

/**
 * @author Sean
 *
 */
public interface IImageService {
	IDownloadHandler getHandler(URL url);
}
