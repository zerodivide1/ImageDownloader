package name.seanpayne.utils.imgdwn.imgur;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableList;

public class ImgurGallery extends AbstractImgurElement {
	private static final String METHOD = "gallery";
	
	String id;
	BaseImgurElement item;

	public ImgurGallery(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public BaseImgurElement getItem() {
		if(item == null)
			refresh();
		
		return item;
	}
	
	public List<ImgurImage> getImages() {
		BaseImgurElement galleryItem = getItem();
		
		if(galleryItem instanceof ImgurAlbum) {
			return ((ImgurAlbum) galleryItem).getImages();
		} else {
			return ImmutableList.of((ImgurImage)galleryItem);
		}
	}

	@Override
	protected void refresh() {
		refresh(METHOD, getId());
	}

	@Override
	protected void parse(JSONObject json) {
		try {
			if(json.has("status")) {
				JSONObject data = json.getJSONObject("data");
				if(data.has("is_album")) {
					boolean isAlbum = data.getBoolean("is_album");
					if(isAlbum) {
						this.item = new ImgurAlbum(json);
					} else {
						this.item = new ImgurImage(json);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace(System.err);
		}
	}

}
