package name.seanpayne.utils.imgdwn.imgur;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import name.seanpayne.utils.imgdwn.json.JSONReader;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Sean
 *
 */
public abstract class AbstractImgurElement extends BaseImgurElement {
	private static Supplier<String> CLIENT_ID = Suppliers.memoize(new Supplier<String>() {

		@Override
		public String get() {
			String val = System.getProperty("imgur.clientid");
			if(val == null)
				throw new IllegalStateException("imgur.clientid not supplied to application");
			return val;
		}
		
	});

	public AbstractImgurElement() {
		super();
	}

	protected abstract void refresh();
	
	protected void refresh(String apiMethod, String id) {
		final String imgurAPI = "https://api.imgur.com/3";
		String fullURL = String.format("%s/%s/%s", imgurAPI, apiMethod, id);
		URL url;
		try {
			url = new URL(fullURL);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("album URL invalid");
		}
		
		try {
			JSONObject jsonResult = JSONReader.readJsonFromUrl(url, getRequestHeaders());
			
			parse(jsonResult);
			
		} catch (IOException e) {
			e.printStackTrace(System.err);
		} catch (JSONException e) {
			e.printStackTrace(System.err);
		}
		
		
	}
	
	private Map<String, String> getRequestHeaders() {
		return new ImmutableMap.Builder<String, String>()
				.put("Authorization", String.format("Client-ID %s", CLIENT_ID.get()))
				.build();
	}

}