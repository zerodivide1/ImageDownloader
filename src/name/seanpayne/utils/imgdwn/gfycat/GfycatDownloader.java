package name.seanpayne.utils.imgdwn.gfycat;

import java.io.File;
import java.io.FileOutputStream;

import name.seanpayne.utils.imgdwn.util.HTTPUtils;

public class GfycatDownloader implements Runnable {
	
	private File outputDirectory;
	private int retryLimit;
	private GfyItem item;

	public GfycatDownloader(GfyItem item, File outputDir, int retry) {
		this.item = item;
		this.outputDirectory = outputDir;
		this.retryLimit = retry;
	}

	@Override
	public void run() {
		final String fileOutputName = createOutputFilename(item);
		
		final File outputFile = new File(outputDirectory, fileOutputName);
		if(outputFile.exists()) {
			System.out.format("File \"%s\" already exists. Skipping.\n", fileOutputName);
			return;
		}
		
		for(int i=0; i < retryLimit; i++) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(outputFile);
				
				if(HTTPUtils.downloadFile(item.getUrl(), fos))
					break;
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.err.format("Error while downloading \"%s\". Retrying...\n", item.getUrl().toString());
			} finally {
				try {
					if(fos != null)
						fos.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		
		System.out.format("Completed %s\n", fileOutputName);
	}
	
	protected String createOutputFilename(GfyItem item) {
		return String.format("%s.%s", item.getName().trim(), item.getType().ext);
	}

}
