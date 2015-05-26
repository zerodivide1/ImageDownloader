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
    String description;
    String account_url;
    long account_id;
    URL gifv;
    URL webm;
    URL mp4;
    boolean looping;

    ImgurImageMetaData(JSONObject json) {
		parse(json);
	}

	@Override
	protected void parse(JSONObject json) {
        this.id = parseString(json, "id");
        this.title = parseString(json, "title");
        this.caption = parseString(json, "caption");
        this.description = parseString(json, "description");
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
		this.account_url = parseString(json, "account_url");
        this.account_id = parseLong(json, "account_id", -1L);
        //TODO Comment preview
        this.gifv = parseUrl(json, "gifv");
        this.webm = parseUrl(json, "webm");
        this.mp4 = parseUrl(json, "mp4");
        this.link = parseUrl(json, "link");
        this.looping = parseBoolean(json, "looping", false);
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

    public String getDescription() {
        return description;
    }

    public String getAccount_url() {
        return account_url;
    }

    public long getAccount_id() {
        return account_id;
    }

    public URL getGifv() {
        return gifv;
    }

    public URL getWebm() {
        return webm;
    }

    public URL getMp4() {
        return mp4;
    }

    public boolean isLooping() {
        return looping;
    }
}
