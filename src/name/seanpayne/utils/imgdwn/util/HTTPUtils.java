/**
 * 
 */
package name.seanpayne.utils.imgdwn.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;

/**
 * @author Sean
 *
 */
public final class HTTPUtils {
	
	public static boolean downloadFile(URL url, OutputStream outStream, Map<String, String> headers) throws IOException {
		HttpURLConnection conn = null;
		InputStream is = null;
		BufferedInputStream bis = null;
		try {
			conn = (HttpURLConnection)url.openConnection();
			HTTPUtils.applyHeaders(conn, headers);
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

	public static boolean downloadFile(URL url, OutputStream outStream) throws IOException {
		Map<String, String> m = ImmutableMap.of();
		return downloadFile(url, outStream, m);
	}
	
	public static void applyHeaders(HttpURLConnection connection, Map<String, String> headers) {
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			connection.setRequestProperty(entry.getKey(), entry.getValue());
		}
	}

}
