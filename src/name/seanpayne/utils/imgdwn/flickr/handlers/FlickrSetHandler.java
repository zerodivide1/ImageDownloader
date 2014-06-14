/**
 * 
 */
package name.seanpayne.utils.imgdwn.flickr.handlers;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.seanpayne.utils.imgdwn.api.IMatchingHandler;
import name.seanpayne.utils.imgdwn.flickr.FlickrAPI;
import name.seanpayne.utils.imgdwn.flickr.downloaders.FlickrLargestImageDownloader;
import name.seanpayne.utils.imgdwn.flickr.utils.FlickrBase58;
import name.seanpayne.utils.imgdwn.util.DirectoryUtils;

import org.xml.sax.SAXException;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.PhotosetsInterface;

/**
 * 
 * @author Sean
 *
 */
public class FlickrSetHandler implements IMatchingHandler {
	private static final String SETSURL = "(https?:\\/\\/(www\\.)?)?flickr.com\\/photos\\/([^\\/]+)\\/sets/(\\d+)\\/?"; //User: 4; set: 5
	private static final int SETSURL_GROUP_USER = 3;
	private static final int SETSURL_GROUP = 4;
	private final Pattern setsURLPattern = Pattern.compile(SETSURL);
	private static final String SETSURL_SHORT = "(https?:\\/\\/(www\\.)?)?flic.kr\\/s\\/([^\\/]+)"; //set:3
	private static final int SETSURL_SHORT_GROUP = 3;
	private final Pattern setsURL_ShortPattern = Pattern.compile(SETSURL_SHORT);
	
	private final static int NUMPERPAGE = 50;
	private final static int RETRYLIMIT = 2;

	@Override
	public boolean isMatch(URL url) {
		final String urlString = url.toString();
		
		return urlString.matches(SETSURL) || urlString.matches(SETSURL_SHORT);
	}

	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		final String photosetId = getPhotosetId(url);
		if(photosetId == null) {
			System.err.format("Photoset ID could not be parsed: %s\n", url.toString());
			return; //TODO throw error that the url can't be parsed properly (maybe new URL scheme?)
		}
		
		final PhotosetsInterface photosetInterface = FlickrAPI.getAPIInstance().getPhotosetsInterface();
		
		Photoset photoSetInfo = null;
		try {
			photoSetInfo = photosetInterface.getInfo(photosetId);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.format("Unable to get info about photoset: %s\n", photosetId);
			return;
		}
		int photoCount = photoSetInfo.getPhotoCount();
		
		if(photoCount > 0) {
			List<Photo> photos = null;
			try {
				photos = getPhotos(photosetInterface, photosetId, photoCount, NUMPERPAGE);
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.err.format("Unable to retrieve list of photos for set: %s\n", photosetId);
				return;
			}
			
			File outputSubdir = DirectoryUtils.createOutputDirectory(outputPath, photosetId);
			
			for(int i=0; i < photos.size(); i++) {
				Photo photo = (Photo)photos.get(i);
				FlickrLargestImageDownloader downloader = new FlickrLargestImageDownloader(FlickrAPI.getAPIInstance(), photo, outputSubdir, i, RETRYLIMIT);
				downloader.run();
			}
			
			writeSetInfo(photoSetInfo, photos, outputSubdir);
		}
		
		
	}
	
	protected String getPhotosetId(URL url) {
		Matcher m = setsURLPattern.matcher(url.toString());
		String ret = null;
		if(m.matches()) {
			ret = m.group(SETSURL_GROUP);
		} else {
			m = setsURL_ShortPattern.matcher(url.toString());
			if(m.matches()) {
				ret = (new Long(FlickrBase58.decode(m.group(SETSURL_SHORT_GROUP)))).toString();
			}
		}
		return ret;
	}

	protected List<Photo> getPhotos(final PhotosetsInterface photoSetsInterface, final String photosetId, final int count, final int numPerPage) throws IOException, SAXException, FlickrException {
		ArrayList<Photo> photos = new ArrayList<Photo>();

		int pages = (int)Math.ceil((double)count/(double)numPerPage);
		
		for(int p = 0; p < pages; p++) {
			PhotoList<Photo> setPage = photoSetsInterface.getPhotos(photosetId, numPerPage, p);
			for(int i=0; i < setPage.size(); i++) {
				photos.add((Photo)setPage.get(i));
			}
		}
		
		return photos;
	}

	protected void writeSetInfo(Photoset info, List<Photo> photos, File outputDirectory) {
		File f = new File(outputDirectory, "set.txt");
		
		PrintStream ps = null;
		try {
			ps = new PrintStream(f);
			
			ps.format("Title: %s\n", info.getTitle());
			ps.format("Description: %s\n", info.getDescription() == null ? "" : info.getDescription());
			ps.format("Owner: %s - %s (%s)\n", info.getOwner().getUsername(), info.getOwner().getRealName(), info.getId());
			ps.format("Count: %d\n", info.getPhotoCount());
			ps.format("URL: %s\n", info.getUrl());
			ps.println();
			for(int i=0; i < photos.size(); i++) {
				Photo item = photos.get(i);
				ps.format("Photo %d: %s \"%s\" \"%s\" %s\n", i+1, item.getId(), item.getTitle(), item.getDescription(), item.getUrl());
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			try {
				if(ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}
}
