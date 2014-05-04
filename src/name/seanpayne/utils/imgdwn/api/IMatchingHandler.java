/**
 * 
 */
package name.seanpayne.utils.imgdwn.api;

import java.net.URL;

/**
 * @author Sean
 *
 */
public interface IMatchingHandler extends IDownloadHandler {
	boolean isMatch(URL url);
}
