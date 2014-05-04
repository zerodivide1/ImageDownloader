package name.seanpayne.utils.imgdwn.defaults;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import name.seanpayne.utils.imgdwn.util.HTTPUtils;

import com.google.common.io.ByteStreams;

/**
 * 
 * @author Sean
 *
 */
public class DefaultDownloader implements Runnable {
	private static final int BUFFERSIZE = 1024*1024;
	private static final long FUDGE = 0;
	URL url;
	File outputDirectory;
	int retryLimit;

	public DefaultDownloader(URL url, File outputDirectory, int retries) {
		this.url = url;
		this.outputDirectory = outputDirectory;
		this.retryLimit = retries;
	}

	@Override
	public void run() {
		System.out.format("Downloading \"%s\"...\n", url);
		
		final String fileOutputName = createOuputFilename(url);
		
		for(int i=0; i < retryLimit; i++) {
			FileOutputStream fos = null;
			InputStream dataStream = null;
			try {
				dataStream = getDownloadStream(url);
				
				fos = new FileOutputStream(new File(outputDirectory, fileOutputName));
				
				long written = ByteStreams.copy(dataStream, fos);
				if(!verify(written))
					continue;
				break;
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.err.format("Error while downloading \"%s\". Retrying...\n", url.toString());
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
	
	protected InputStream getDownloadStream(URL url) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(BUFFERSIZE);
		
		if(HTTPUtils.downloadFile(url, bos)) {
			byte[] data = bos.toByteArray();
			return new ByteArrayInputStream(data);
		} else
			throw new IOException("Download failed.");
	}
	
	protected boolean verify(long written) throws IOException {
		URLConnection conn = url.openConnection();
		if(conn != null) {
			long expected = conn.getContentLength();

			if(expected >= 0) {
				if(written < expected) {
					long diff = expected - written;
					if(diff > 0 && diff > getSizeDifferenceThreshold()) {
						System.err.format("WARN: Retrying download for \"%s\" - exceeded acceptable download difference: %d.\n", url.toString(), diff);
						return false;
					} else {
						System.err.format("WARN: Download size different from expected for \"%s\" by %d bytes.\n", url.toString(), diff);
					}
				} else if (written > expected)
					System.err.format("WARN: Written size greater than expected for download: %s - %d > %d", url.toString(), written, expected);
			} else
				System.err.format("WARN: Size unavailable for download: %s\n", url.toString());
		} else {
			System.err.format("WARN: Unable to verify size of download: %s\n", url.toString());
		}
		return true;
	}
	
	protected long getSizeDifferenceThreshold() {
		return FUDGE;
	}

	protected String createOuputFilename(URL url) {
		String imgPath = url.getPath();
		int lastSlash = imgPath.lastIndexOf('/');
		
		String retPath = imgPath;
		if(lastSlash >= 0) {
			if(imgPath.length() > 1) {
				retPath = imgPath.substring(lastSlash+1);
			} else
				retPath = url.getHost();
		}
		
		return retPath;
	}

}