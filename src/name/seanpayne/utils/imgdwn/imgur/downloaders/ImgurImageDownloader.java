package name.seanpayne.utils.imgdwn.imgur.downloaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.google.common.base.Objects;
import name.seanpayne.utils.imgdwn.imgur.ImgurImage;

import com.google.common.io.ByteStreams;

/**
 * 
 * @author Sean
 *
 */
public class ImgurImageDownloader implements Runnable {
	private static final int DIFF_FUDGE = 18;
	ImgurImage image;
	File outputDirectory;
	Integer index;
	int retryLimit;
	
	public ImgurImageDownloader(ImgurImage image, File outputDirectory, Integer indexPrefix, int retries) {
		this.image = image;
		this.outputDirectory = outputDirectory;
		this.index = indexPrefix;
		this.retryLimit = retries;
	}
	
	@Override
	public void run() {
		downloadOriginalImage();

        if(!Objects.equal(image.getLinks().getOriginal(), image.getLinks().getAnimatedVideo()))
            downloadAnimatedImage();
	}

    protected void downloadOriginalImage() {
        final URL originalURL = image.getLinks().getOriginal();

        System.out.format("Downloading \"%s\"...\n", originalURL);

        final String fileOutputName = createOuputFilename(originalURL, this.index);

        final File outputFile = new File(outputDirectory, fileOutputName);
        if(outputFile.exists()) {
            System.out.format("File \"%s\" already exists. Skipping.\n", fileOutputName);
            return;
        }

        for(int i=0; i < retryLimit; i++) {
            FileOutputStream fos = null;
            InputStream imageStream = null;
            try {
                image.clearImageData();
                imageStream = image.getImageData();

                fos = new FileOutputStream(outputFile);

                long written = ByteStreams.copy(imageStream, fos);
                if(written < image.getMetadata().getSize()) {
                    long diff = image.getMetadata().getSize()-written;
                    if(diff > 0 && diff > DIFF_FUDGE) {
                        System.err.format("WARN: Retrying download for \"%s\" - exceeded acceptable download difference: %d.\n", originalURL, diff);
                        continue;
                    }else
                        System.err.format("WARN: Downloaded size different from expected for \"%s\" by %d bytes.\n", originalURL, diff);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.err.format("Error while downloading \"%s\". Retrying...\n", originalURL);
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        }

        System.out.format("Completed %s\n", fileOutputName);
    }

    protected void downloadAnimatedImage() {
        if(image.getLinks().getAnimatedVideo() != null) {
            final URL animatedUrl = image.getLinks().getAnimatedVideo();

            System.out.format("Downloading \"%s\"...\n", animatedUrl);

            final String fileOutputName = createOuputFilename(animatedUrl, this.index);

            final File outputFile = new File(outputDirectory, fileOutputName);
            if(outputFile.exists()) {
                System.out.format("File \"%s\" already exists. Skipping.\n", fileOutputName);
                return;
            }

            for(int i=0; i < retryLimit; i++) {
                FileOutputStream fos = null;
                InputStream imageStream = null;
                try {
                    image.clearAnimatedImageData();
                    imageStream = image.getAnimatedImageData();

                    fos = new FileOutputStream(outputFile);

                    ByteStreams.copy(imageStream, fos);
                    break;
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    System.err.format("Error while downloading \"%s\". Retrying...\n", animatedUrl);
                } finally {
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }

            System.out.format("Completed %s\n", fileOutputName);
        }
    }

	protected String createOuputFilename(URL url, Integer prefix) {
		String imgPath = url.getPath();
		int lastSlash = imgPath.lastIndexOf('/');
		
		String retPath = imgPath;
		if(lastSlash >= 0) {
			if(imgPath.length() > 1) {
				retPath = imgPath.substring(lastSlash+1);
			} else
				retPath = url.getHost();
		}
		
		if(prefix != null)
			return String.format("%04d.%s", prefix, retPath);
		else
			return retPath;
	}
}