/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgur;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.json.JSONObject;

import com.google.common.io.Files;

/**
 * @author Sean
 *
 */
public class ImgurImageLinks extends BaseImgurElement {
	URL original;
	URL imgur_page;
	URL small_square;
	URL large_thumbnail;
    URL animatedVideo;
	
	public ImgurImageLinks(ImgurImageMetaData metadata) {
		parse(metadata);
	}
	
	public URL getOriginal() {
		return original;
	}

	public URL getImgur_page() {
		return imgur_page;
	}

	public URL getSmall_square() {
		return small_square;
	}

	public URL getLarge_thumbnail() {
		return large_thumbnail;
	}

    public URL getAnimatedVideo() {
        return animatedVideo;
    }


	@Override
	protected void parse(JSONObject json) {
		ImgurImageMetaData metadata = new ImgurImageMetaData(json);
		
		parse(metadata);
	}
	
	protected void parse(ImgurImageMetaData metadata) {
		URL link = metadata.getLink();
		
		try {
			this.original = new URL(link.getProtocol(), link.getHost(), link.getFile());
		} catch (MalformedURLException e) {
			e.printStackTrace(System.err);
		}
		
		try {
			this.imgur_page = new URL(link.getProtocol(), "imgur.com", String.format("/%s", metadata.getId()));
		} catch (MalformedURLException e) {
			e.printStackTrace(System.err);
		}
		
		
		try {
			this.small_square = new URL(link.getProtocol(), link.getHost(), String.format("/%s", reformatFilename(link.getFile(), "s")));
		} catch (MalformedURLException e) {
			e.printStackTrace(System.err);
		}
		
		try {
			this.large_thumbnail = new URL(link.getProtocol(), link.getHost(), String.format("/%s", reformatFilename(link.getFile(), "l")));
		} catch (MalformedURLException e) {
			e.printStackTrace(System.err);
		}

        this.animatedVideo = Optional
                .fromNullable(metadata.getMp4())
                .or(Optional.fromNullable(metadata.getWebm()))
                .transform(new Function<URL, URL>() {
                    @Override
                    public URL apply(URL input) {
                        try {
                            return new URL(input.getProtocol(), input.getHost(), input.getFile());
                        } catch (MalformedURLException e) {
                            e.printStackTrace(System.err);
                            return null;
                        }
                    }
                })
                .orNull();
	}
	
	private String reformatFilename(String name, String suffix) {
		String filename = Files.getNameWithoutExtension(name.substring(name.lastIndexOf('/')));
		String fileext = Files.getFileExtension(name);
		
		return String.format("%s%s.%s", filename, suffix, fileext);
	}
}
