package name.seanpayne.utils.imgdwn.json;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Sean
 *
 */
public final class JSONUtils {
	public static String getNonNullString(JSONObject object, String fieldName) throws JSONException {
		if(!object.has(fieldName))
			return "";
		else if(object.isNull(fieldName))
			return "";
		else
			return object.getString(fieldName);
	}
	public static boolean getNonNullBoolean(JSONObject object, String fieldName, boolean defaultValue) throws JSONException {
		if(!object.has(fieldName))
			return defaultValue;
		else if(object.isNull(fieldName))
			return defaultValue;
		else
			return object.getBoolean(fieldName);
	}
	public static long getNonNullLong(JSONObject object, String fieldName, long defaultValue) throws JSONException {
		if(!object.has(fieldName))
			return defaultValue;
		else if(object.isNull(fieldName))
			return defaultValue;
		else
			return object.getLong(fieldName);
	}
	public static URL getURL(JSONObject object, String fieldName) throws JSONException, MalformedURLException {
		if(!object.has(fieldName))
			return null;
		else if (object.isNull(fieldName))
			return null;
		else
			return new URL(object.getString(fieldName));
	}
}
