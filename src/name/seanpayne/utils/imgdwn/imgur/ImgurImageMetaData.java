/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgur;

import java.net.URL;

import org.json.JSONObject;

/**
 * @author Sean
 * 
 */
public class ImgurImageMetaData extends BaseImgurElement {
	String title;
	String caption;
	String id;
	Long datetime; // XXX Type?
	String type;
	boolean animated;
	long width;
	long height;
	long size;
	long views;
	long bandwidth;
	boolean favorite;
	boolean nsfw;
	String section;
	URL link;

	ImgurImageMetaData(JSONObject json) {
		parse(json);
	}

	@Override
	protected void parse(JSONObject json) {
		this.title = parseString(json, "title");
		
		this.caption = parseString(json, "caption");
		
		this.id = parseString(json, "id");

		this.datetime = parseLong(json, "datetime", -1L);

		this.type = parseString(json, "type");

		this.animated = parseBoolean(json, "animated", false);

		this.width = parseLong(json, "width", -1L);

		this.height = parseLong(json, "height", -1L);

		this.size = parseLong(json, "size", -1L);

		this.views = parseLong(json, "views", -1L);

		this.bandwidth = parseLong(json, "bandwidth", -1L);
		
		this.favorite = parseBoolean(json, "favorite", false);
		
		this.nsfw = parseBoolean(json, "nsfw", false);
		
		this.section = parseString(json, "section");
		
		this.link = parseUrl(json, "link");
	}

	public String getTitle() {
		return title;
	}

	public String getCaption() {
		return caption;
	}

	public String getId() {
		return id;
	}

	public Long getDatetime() {
		return datetime;
	}

	public String getType() {
		return type;
	}

	public boolean isAnimated() {
		return animated;
	}

	public long getWidth() {
		return width;
	}

	public long getHeight() {
		return height;
	}

	public long getSize() {
		return size;
	}

	public long getViews() {
		return views;
	}

	public long getBandwidth() {
		return bandwidth;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public boolean isNsfw() {
		return nsfw;
	}

	public String getSection() {
		return section;
	}

	public URL getLink() {
		return link;
	}
}
