/**
 * 
 */
package name.seanpayne.utils.imgdwn.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.google.common.io.ByteStreams;

/**
 * @author Sean
 *
 */
public final class HTTPUtils {

	public static boolean downloadFile(URL url, OutputStream outStream) throws IOException {
		URLConnection conn = null;
		InputStream is = null;
		BufferedInputStream bis = null;
		try {
			conn = url.openConnection();
			long len = conn.getContentLength();
			
			is = conn.getInputStream();
			bis = new BufferedInputStream(is);
			
			long write = ByteStreams.copy(bis, outStream);
			return len > 0 ? write == len : true;
		} catch (IOException e) {
			throw new IOException(String.format("Error encountered while downloading file from \"%s\"", url.toString()), e);
		} finally {
			try {
				if(is != null)
					is.close();
			} catch (IOException e) {
				//Ignore closure errors
			}
			try {
				if(bis != null)
					bis.close();
			} catch (IOException e) {
				//Ignore closure errors
			}
		}
	}
}
