/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgur;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import name.seanpayne.utils.imgdwn.util.HTTPUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Sean
 *
 */
public class ImgurImage extends AbstractImgurElement {
	private static final String METHOD = "image";
	private static final String TARGET = "data";
	
	String hash;
	ImgurImageMetaData metadata;
	ImgurImageLinks links;
	byte[] imageData;
	
	public ImgurImage(String hash) {
		this.hash = hash;
	}
	
	public ImgurImage(JSONObject json) {
		parse(json);
	}
	
	public String getHash() {
		return hash;
	}
	
	public ImgurImageMetaData getMetadata() {
		if (metadata == null)
			refresh();

		return metadata;
	}
	
	public ImgurImageLinks getLinks() {
		if (links == null) 
			refresh();

		return links;
	}
	
	@Override
	protected void refresh() {
		refresh(METHOD, getHash());	
	}
	
	
	@Override
	protected void parse(JSONObject json) {
		try {
			//XXX Very kludgy to check if the passed JSON is in the right subtree
			if(json.has("status"))
				this.metadata = new ImgurImageMetaData(json.getJSONObject("data"));
			else
				this.metadata = new ImgurImageMetaData(json);
	
			this.links = new ImgurImageLinks(metadata);
		} catch (JSONException e) {
			e.printStackTrace(System.err);
		}
	}
	
	public InputStream getImageData() throws IOException {
		if (imageData == null) {
			long size = 1024*1024; //1MB Buffer
			if(getMetadata() != null) {
				size = getMetadata().getSize();
			}
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream((int)size);
			
			if(HTTPUtils.downloadFile(getLinks().getOriginal(), bos)) {
				imageData = bos.toByteArray();
			}
		}

		return new ByteArrayInputStream(imageData);
	}
	
	public void clearImageData() {
		imageData = null;
	}
}
