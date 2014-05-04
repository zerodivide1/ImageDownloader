/**
 * 
 */
package name.seanpayne.utils.imgdwn;

import java.io.File;
import java.net.URL;

/**
 * @author Sean
 *
 */
public class Settings {
	private File outputPath;
	private URL inputURL;
	
	public File getOutputPath() {
		return outputPath;
	}
	public void setOutputPath(File outputPath) {
		this.outputPath = outputPath;
	}
	public URL getInputURL() {
		return inputURL;
	}
	public void setInputURL(URL inputURL) {
		this.inputURL = inputURL;
	}
	
}
