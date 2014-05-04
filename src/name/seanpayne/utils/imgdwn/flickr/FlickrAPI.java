/**
 * 
 */
package name.seanpayne.utils.imgdwn.flickr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;

/**
 * @author Sean
 *
 */
public final class FlickrAPI {
	private static Flickr FLICKR_API = null;
	private static Properties PROPERTIES = null;
	
	public static Flickr getAPIInstance() {
		if (FLICKR_API == null) {
			try {
				FLICKR_API = new Flickr(getProperties().getProperty("flickr.apikey"), getProperties().getProperty("flickr.apisecret"), new REST());
				
				Auth auth = loadAuth(FLICKR_API);
				if(auth != null)
					RequestContext.getRequestContext().setAuth(auth);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
			
		}

		return FLICKR_API;
	}
	
	private static Properties getProperties() {
		if (PROPERTIES == null) {
			PROPERTIES = new Properties();
			
			copyProperty(PROPERTIES, "flickr.apikey");
			copyProperty(PROPERTIES, "flickr.apisecret");
			copyProperty(PROPERTIES, "flickr.appauthurl");
			
		}

		return PROPERTIES;
	}
	
	private static void copyProperty(Properties target, String key) {
		if(!System.getProperties().containsKey(key))
			throw new IllegalArgumentException(String.format("Required property not set: %s", key));
		target.setProperty(key, System.getProperty(key));
	}
	
	public static Auth getFullAuth(Flickr api, String minitoken) {
		if(minitoken == null || (minitoken != null && minitoken.trim().isEmpty())) {
			return null;
		}
		
		try {
			final AuthInterface authInterface = FLICKR_API.getAuthInterface();
			return authInterface.getFullToken(minitoken);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
		return null;
	}
	
	private static Auth loadAuth(Flickr api) throws IOException, SAXException, FlickrException, Exception {
		if(System.getProperty("flickr.token", null) != null) {
			Auth auth = new Auth();
			auth.setToken(System.getProperty("flickr.token"));
			
			return auth;
		} else {
			if(promptForToken(api))
				System.exit(-1);
		}
		throw new Exception("Unauthed");
	}
	
	public static boolean promptForToken(Flickr api) throws IOException, SAXException, FlickrException {
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.format("Visit %s. Enter minitoken: ", getAppAuthURL());
		String minitoken = cin.readLine();
		if(minitoken == null || (minitoken != null && !minitoken.isEmpty())) {
			Auth fullToken = api.getAuthInterface().getFullToken(minitoken);
			
			System.out.format("Restart application with the following argument:\n-Dflickr.token=%s\n", fullToken.getToken());
			return true;
		}
		return false;
	}

	private static String getAppAuthURL() {
		return getProperties().getProperty("flickr.appauthurl");
	}
}
