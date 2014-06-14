/**
 * 
 */
package name.seanpayne.utils.imgdwn.flickr;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.xml.sax.SAXException;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;

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
			
		}

		return PROPERTIES;
	}
	
	private static void copyProperty(Properties target, String key) {
		if(!System.getProperties().containsKey(key))
			throw new IllegalArgumentException(String.format("Required property not set: %s", key));
		target.setProperty(key, System.getProperty(key));
	}
	
	
	private static Auth loadAuth(Flickr api) throws IOException, SAXException, FlickrException, Exception {
		if(System.getProperty("flickr.token", null) != null && System.getProperty("flickr.tokensecret", null) != null) {
			Token token = new Token(System.getProperty("flickr.token"), System.getProperty("flickr.tokensecret"));
			
			Auth auth = api.getAuthInterface().checkToken(token);
			
			return auth;
		} else {
			if(promptForToken(api))
				System.exit(-1);
		}
		throw new Exception("Unauthed");
	}
	
	public static boolean promptForToken(Flickr api) throws IOException, SAXException, FlickrException {
		Scanner scanner = new Scanner(System.in);
		
		AuthInterface authInterface = api.getAuthInterface();


        Token token = authInterface.getRequestToken();
        System.out.println("token: " + token);

        String url = authInterface.getAuthorizationUrl(token, Permission.READ);
        System.out.println("Follow this URL to authorise yourself on Flickr");
        System.out.println(url);
        System.out.println("Paste in the token it gives you:");
        System.out.print(">>");

        String tokenKey = scanner.nextLine();

        Token requestToken = authInterface.getAccessToken(token, new Verifier(tokenKey));
        System.out.println("Authentication success");

        Auth auth = authInterface.checkToken(requestToken);

        // This token can be used until the user revokes it.
        System.out.println("Token: " + requestToken.getToken());
        System.out.println("nsid: " + auth.getUser().getId());
        System.out.println("Realname: " + auth.getUser().getRealName());
        System.out.println("Username: " + auth.getUser().getUsername());
        System.out.println("Permission: " + auth.getPermission().getType());
		
		System.out.format("Restart application with the following argument:\n-Dflickr.token=%s -Dflickr.tokensecret=%s\n", requestToken.getToken(), requestToken.getSecret());
		return true;
		
	}
}
