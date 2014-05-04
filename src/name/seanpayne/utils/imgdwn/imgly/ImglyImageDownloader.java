/**
 * 
 */
package name.seanpayne.utils.imgdwn.imgly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import name.seanpayne.utils.imgdwn.util.HTTPUtils;

/**
 * @author Sean
 *
 */
public class ImglyImageDownloader implements Runnable {
	private static final String IMGLYIMAGEURL = "http://img.ly/show/full/%s";
	
	private String hash;
	private File outputDirectory;
	private Integer prefix;
	private int retryLimit;
	
	public ImglyImageDownloader(String hash, File outputDir, Integer prefix, int retry) {
		this.hash = hash;
		this.retryLimit = retry;
		this.outputDirectory = outputDir;
		this.prefix = prefix;
	}
	
	public String getHash() {
		return hash;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		URL originalURL = null;
		try {
			originalURL = new URL(String.format(IMGLYIMAGEURL, hash));
		} catch (MalformedURLException e1) {
			e1.printStackTrace(System.err);
			return;
		}
		
		System.out.format("Downloading \"%s\"...\n", originalURL);
		
		final String fileOutputName = createOutputFilename(hash, prefix);
		
		final File outputFile = new File(outputDirectory, fileOutputName);
		if(outputFile.exists()) {
			System.out.format("File \"%s\" already exists. Skipping.\n", fileOutputName);
			return;
		}
		
		for(int i=0; i < retryLimit; i++) {
			FileOutputStream fos = null;
			InputStream imageStream = null;
			try {
				fos = new FileOutputStream(outputFile);
				
				if(!HTTPUtils.downloadFile(originalURL, fos)) {
					System.err.format("Error while downloading \"%s\". Retrying...", originalURL);
				} else
					break;
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.err.format("Error while downloading \"%s\". Retrying...\n", originalURL);
			} finally {
				try {
					if(fos != null)
						fos.close();
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			}
		}
		
		System.out.format("Completed %s\n", fileOutputName);
	}

	protected String createOutputFilename(String hash, Integer prefix) {
		if(prefix == null)
			return String.format("%s.jpg", hash);
		else
			return String.format("%04d.%s.jpg", prefix, hash);
	}

}
