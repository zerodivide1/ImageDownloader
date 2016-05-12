/**
 * 
 */
package name.seanpayne.utils.imgdwn.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import name.seanpayne.utils.imgdwn.util.HTTPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;

import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author Sean
 * Modified from: http://stackoverflow.com/a/4308662
 * 
 */
public class JSONReader {
	private static final int REDIRECT_THRESHOLD = 5;

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(URL url) throws IOException, JSONException {
		return readJsonFromUrl(url, ImmutableMap.<String,String>of());
	}

	public static JSONObject readJsonFromUrl(URL url, Map<String, String> headers) throws IOException, JSONException {
		return readJsonFromUrl(url, url, headers, 0);
	}
	
	private static JSONObject readJsonFromUrl(URL originalUrl, URL url, Map<String, String> headers, int redirectCount) throws IOException, JSONException {
		if(redirectCount > REDIRECT_THRESHOLD) {
			throw new IllegalStateException(String.format("Unable to download URL \"%s\" - too many redirects.", originalUrl.toString()));
		}
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		try {
			HTTPUtils.applyHeaders(conn, headers);
			
			int responseCode = conn.getResponseCode();
			InputStream is = null;
			try {
				switch(responseCode) {
					case HTTP_OK: //OK
						{
							is =  conn.getInputStream();
							JSONObject json = readFromStream(is);
							return json;
						}
					case HTTP_MOVED_PERM: //Permanent redirect
					case HTTP_MOVED_TEMP: //Temporary redirect
						{
							final String redirectLocation = conn.getHeaderField("Location");
							System.err.format("URL \"%s\" redirecting to \"%s\" (%d)\n", url.toString(), redirectLocation, redirectCount+1);

							return readJsonFromUrl(originalUrl, new URL(redirectLocation), headers, redirectCount+1);
						}
					default:
						is = conn.getErrorStream();
						if(is == null) {
							is = conn.getInputStream();
						}
						if(is == null) {
							throw new IllegalStateException(String.format("Unable to download URL \"%s\". Response code = %d Reason: Unknown (No data)", url.toString(), responseCode));
						} else {
							throw new IllegalStateException(String.format("Unable to download URL \"%s\". Response code = %d Reason:\n%s", url.toString(), responseCode, readAll(new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8"))))));
						}
				}
			} finally {
				if(is != null) {
					is.close();
				} else {
					System.err.format("Error: JSONReader#readJsonFromUrl InputStream is null; responseCode is %d\n", responseCode);
				}
			}
		} finally {
			conn.disconnect();
		}
		
	}
	
	private static JSONObject readFromStream(InputStream is) throws IOException, JSONException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		String jsonText = readAll(rd);
		JSONObject json = new JSONObject(jsonText);
		return json;
	}
	
	

}
