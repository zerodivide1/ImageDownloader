package name.seanpayne.utils.imgdwn.imgur;

import java.net.URL;

import name.seanpayne.utils.imgdwn.json.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Sean
 *
 */
public abstract class BaseImgurElement {

	public BaseImgurElement() {
		super();
	}

	protected abstract void parse(JSONObject json);

	protected String parseString(JSONObject json, String key) {
		try {
			return JSONUtils.getNonNullString(json, key);
		} catch (JSONException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	protected URL parseUrl(JSONObject json, String key) {
		try {
			return JSONUtils.getURL(json, key);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	protected Boolean parseBoolean(JSONObject json, String key, boolean defaultValue) {
		try {
			return JSONUtils.getNonNullBoolean(json, key, defaultValue);
		} catch (JSONException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	protected Long parseLong(JSONObject json, String key, long defaultValue) {
		try {
			return JSONUtils.getNonNullLong(json, key, defaultValue);
		} catch (JSONException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
}