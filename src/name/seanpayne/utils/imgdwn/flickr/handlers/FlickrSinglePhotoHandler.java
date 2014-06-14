/**
 * 
 */
package name.seanpayne.utils.imgdwn.flickr.handlers;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.seanpayne.utils.imgdwn.api.IMatchingHandler;
import name.seanpayne.utils.imgdwn.flickr.FlickrAPI;
import name.seanpayne.utils.imgdwn.flickr.downloaders.FlickrLargestImageDownloader;
import name.seanpayne.utils.imgdwn.flickr.utils.FlickrBase58;

import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotosInterface;

/**
 * 
 * @author Sean
 *
 */
public class FlickrSinglePhotoHandler implements IMatchingHandler {
	private static final String PHOTOURL = "(https?:\\/\\/(www\\.)?)?flickr.com\\/photos\\/([^\\/]+)\\/(\\d+)\\/?";
	private static final int PHOTOURL_GROUP = 4;
	private final Pattern photoUrlPattern = Pattern.compile(PHOTOURL);
	private static final String PHOTOURL_SHORT = "(https?:\\/\\/(www\\.)?)?flic.kr\\/p\\/([^\\/]+)";
	private static final int PHOTOURL_SHORT_GROUP = 3;
	private final Pattern photoUrlShortPattern = Pattern.compile(PHOTOURL_SHORT);
	
	private final static int RETRYLIMIT = 2;

	@Override
	public boolean isMatch(URL url) {
		final String urlString = url.toString();
		
		return urlString.matches(PHOTOURL) || urlString.matches(PHOTOURL_SHORT);
	}

	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		final String photoId = getPhotoId(url);
		if(photoId == null) {
			System.err.format("Photo ID could not be parsed: %s\n", url.toString());
			return; //TODO throw error that the url can't be parsed properly (maybe new URL scheme?)
		}
		
		final PhotosInterface photosInterface = FlickrAPI.getAPIInstance().getPhotosInterface();
		
		try {
			final Photo photo = photosInterface.getPhoto(photoId);
			if(photo != null) {
				FlickrLargestImageDownloader downloader = new FlickrLargestImageDownloader(FlickrAPI.getAPIInstance(), photo, outputPath, null, RETRYLIMIT);
				downloader.run();
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.format("Unable to retrieve information for photo: %s", photoId);
		}
		
	}
	
	private String getPhotoId(URL url) {
		String ret = null;
		Matcher m = photoUrlPattern.matcher(url.toString());
		if(m.matches()) {
			ret = m.group(PHOTOURL_GROUP);
		} else {
			m = photoUrlShortPattern.matcher(url.toString());
			if(m.matches())
				ret = (new Long(FlickrBase58.decode(m.group(PHOTOURL_SHORT_GROUP)))).toString();
		}
		return ret;
	}
}
