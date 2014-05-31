/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgur;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Sean
 *
 */
public class ImgurAlbum extends AbstractImgurElement {
	private static final String METHODKEY = "album";
	private static final String TARGET = "data";
	
	String id;
	
	String title;
	String description;
	String cover;
	String layout;
	List<ImgurImage> images;
	URL link;
	String privacy;
	Boolean favorite;
	String section;
	Boolean nsfw;
	Long views;
	
	public ImgurAlbum(String id) {
		this.id = id;
	}
	
	public ImgurAlbum(JSONObject json) {
		parse(json);
	}
	
	public String getId() {
		return id;
	}
	
	public String getTitle() {
		if (title == null)
			refresh();

		return title;
	}
	
	public String getDescription() {
		if (description == null)
			refresh();

		return description;
	}

	public String getCover() {
		if (cover == null)
			refresh();	

		return cover;
	}

	public String getLayout() {
		if (layout == null)
			refresh();

		return layout;
	}

	public List<ImgurImage> getImages() {
		if (images == null)
			refresh();

		return images;
	}
	
	public URL getLink() {
		if(link == null)
			refresh();
		return link;
	}

	public String getPrivacy() {
		if(privacy == null)
			refresh();
		return privacy;
	}

	public Boolean getFavorite() {
		if(favorite == null)
			refresh();
		
		return favorite;
	}

	public String getSection() {
		if(section == null)
			refresh();
		
		return section;
	}

	public Boolean getNsfw() {
		if(section == null)
			refresh();
		
		return nsfw;
	}

	public Long getViews() {
		if(views == null)
			refresh();
		
		return views;
	}

	@Override
	protected void refresh() {
		refresh(METHODKEY, getId());	
	}

	@Override
	protected void parse(JSONObject json) {
		try {
			JSONObject data = json.getJSONObject(TARGET);
			this.title = parseString(data, "title");
			this.description = parseString(data, "description");
			this.cover = parseString(data, "cover");
			this.layout = parseString(data, "layout");
			this.link = parseUrl(data, "link");
			this.privacy = parseString(data, "privacy");
			this.favorite = parseBoolean(data, "favorite", false);
			this.section = parseString(data, "section");
			this.nsfw = parseBoolean(data, "nsfw", false);
			this.views = parseLong(data, "views", -1L);
			
			try {
				JSONArray images = data.getJSONArray("images");
				this.images = new ArrayList<ImgurImage>(images.length());
				for(int i=0; i < images.length(); i++) {
					try {
						JSONObject image = images.getJSONObject(i);
						
						ImgurImage img = new ImgurImage(image);
						this.images.add(img);
					} catch (JSONException e) {
						e.printStackTrace(System.err);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace(System.err);
			}
		} catch (JSONException e) {
			e.printStackTrace(System.err);
		}
	}

}
