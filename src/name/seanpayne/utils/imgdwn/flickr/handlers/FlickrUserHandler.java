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

import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;

/**
 * 
 * @author Sean
 *
 */
public class FlickrUserHandler implements IMatchingHandler {
	private static final String USERPHOTOS = "(https?:\\/\\/(www\\.)?)?flickr.com\\/photos\\/([^\\/]+)\\/?";
	private static final int USERPHOTOS_GROUP = 3;
	private final Pattern userPhotosPattern = Pattern.compile(USERPHOTOS);
	private static final String PERSONPROFILE = "(https?:\\/\\/(www\\.)?)?flickr.com\\/people\\/([^\\/]+)\\/?";
	private static final int PERSONPROFILE_GROUP = 3;
	private final Pattern personProfilePattern = Pattern.compile(PERSONPROFILE);
	
	private static final String PHOTOSETS_SHORT = "(https?:\\/\\/(www\\.)?)?flic.kr\\/ps\\/([^\\/]+)";
	private static final int PHOTOSETS_SHORT_GROUP = 3;
	private final Pattern photoSetsShortPattern = Pattern.compile(PHOTOSETS_SHORT);
	
	private final static int NUMPERPAGE = 50;
	private final static int RETRYLIMIT = 2;

	@Override
	public boolean isMatch(URL url) {
		String urlString = url.toString();
		
		return urlString.matches(USERPHOTOS) || urlString.matches(PERSONPROFILE) || urlString.matches(PHOTOSETS_SHORT);
	}

	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		final String userId = getUserId(url);
		if(userId == null) {
			System.err.format("Unable to parse user Id: %s\n", url.toString());
			return;
		}
		
		final PeopleInterface peopleService = FlickrAPI.getAPIInstance().getPeopleInterface();
		final PhotosInterface photosService = FlickrAPI.getAPIInstance().getPhotosInterface();
		
		User userInfo = null;
		try {
			userInfo = peopleService.getInfo(userId);
		} catch (Exception e1) {
			e1.printStackTrace(System.err);
			System.err.format("Unable to retrieve info for user: %s\n", userId);
		}
		
		int photosCount = userInfo.getPhotosCount();
		List<Photo> photos = null;
		try {
			photos = getPhotos(peopleService, userId, photosCount, NUMPERPAGE);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.format("Unable to retrieve list of photos for user: %s\n", userId);
			return;
		}
		
		File outputSubdir = DirectoryUtils.createOutputDirectory(outputPath, String.format("%s (%s)", userInfo.getUsername(), userId));
		
		for(int i=0; i < photos.size(); i++) {
			Photo photo = (Photo)photos.get(i);
			threadService.execute(new FlickrLargestImageDownloader(photosService, photo, outputSubdir, i, RETRYLIMIT));
		}
		
		writeUserInfo(userInfo, photos, outputSubdir);
	}

	protected String getUserId(URL url) {
		String ret = null;

		Matcher m = userPhotosPattern.matcher(url.toString());
		if(m.matches()) {
			ret = m.group(USERPHOTOS_GROUP);
		} else {
			m = personProfilePattern.matcher(url.toString());
			if(m.matches()) {
				ret = m.group(PERSONPROFILE_GROUP);
			} else {
				m = photoSetsShortPattern.matcher(url.toString());
				if(m.matches()) {
					ret = ((new Long(FlickrBase58.decode(m.group(PHOTOSETS_SHORT_GROUP))))).toString();
					
					ret = ret.replaceAll("(\\d+)(\\d)", "$1@N0$2");
				}
			}
		}
		return ret;
	}
	
	protected List<Photo> getPhotos(final PeopleInterface peopleInterface, final String userId, final int count, final int numPerPage) throws IOException, SAXException, FlickrException {
		ArrayList<Photo> photos = new ArrayList<Photo>();
		
		int pages = (int)Math.ceil((double)count/(double)numPerPage);
		
		for(int p = 0; p < pages; p++) {
			PhotoList page = peopleInterface.getPublicPhotos(userId, numPerPage, p);
			for(int i=0; i < page.size(); i++)
				photos.add((Photo)page.get(i));
		}
		return photos;
	}

	protected void writeUserInfo(User info, List<Photo> photos, File outputDirectory) {
		File f = new File(outputDirectory, "user.txt");
		
		PrintStream ps = null;
		try {
			ps = new PrintStream(f);
			
			ps.format("User: %s - %s (%s)\n", info.getUsername(), info.getRealName(), info.getId());
			ps.format("Count: %d\n", info.getPhotosCount());
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
