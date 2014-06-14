/**
 * 
 */
package name.seanpayne.utils.imgdwn.flickr.downloaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.seanpayne.utils.imgdwn.util.HTTPUtils;

import org.xml.sax.SAXException;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.Size;

/**
 * @author Sean
 *
 */
public class FlickrLargestImageDownloader implements Runnable {
	private static final String EXTENSIONREGEX = "(\\.[a-zA-Z0-9]+)$";
	private Flickr api;
	private Photo photo;
	private File outputDir;
	private Integer prefix;
	private int retryLimit;
	
	public FlickrLargestImageDownloader(Flickr api, Photo photo, File outputDir, Integer prefix, int retry) {
		this.api = api;
		this.photo = photo;
		this.outputDir = outputDir;
		this.prefix = prefix;
		this.retryLimit = retry;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Size largest = null;
		try {
			largest = getLargest(this.photo);
		} catch (Exception e1) {
			e1.printStackTrace(System.err);
			System.err.format("Unable to retrieve sizes for photo: %s\n", this.photo.getId());
			return;
		}
		final String filename = createOutputFilename(this.photo, largest, prefix != null ? prefix.toString() : null);
		
		final File outputFile = new File(outputDir, filename);
		
		if(outputFile.exists()) {
			System.out.format("File %s already exists... skipping.\n", filename);
		}
		
		for(int i=0; i < retryLimit; i++) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(outputFile);
				
				if(HTTPUtils.downloadFile(new URL(largest.getSource()), fos))
					break;
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.err.format("Error while downloading \"%s\". Retrying...\n", largest.getSource());
			} finally {
				try {
					if(fos != null)
						fos.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
			
		}
		
		System.out.format("Completed %s\n", filename);
	}

	private Size getLargest(Photo photo) throws IOException, SAXException, FlickrException {
		Collection<Size> sizes = api.getPhotosInterface().getSizes(photo.getId());
		ArrayList<Size> sortList = new ArrayList<Size>(sizes);
		Collections.sort(sortList, new Comparator<Size>() {
			@Override
			public int compare(Size o1, Size o2) {
				Integer area1 = o1.getWidth()*o1.getHeight();
				Integer area2 = o2.getWidth()*o2.getHeight();
				
				return area2.compareTo(area1);
			}
		});
		
		if(!sortList.isEmpty())
			return sortList.get(0);
		else
			return null;
	}
	
	private String createOutputFilename(Photo photo, Size size, String prefix) {
		String url = size.getSource();
		Pattern p = Pattern.compile(EXTENSIONREGEX);
		Matcher m = p.matcher(url);
		String extension = ".jpg";
		if(m.find())
			extension = m.group(1);
		if(prefix == null)
			return String.format("%s%s", photo.getId(), extension);
		else
			return String.format("%s.%s%s", prefix, photo.getId(), extension);
	}
}
