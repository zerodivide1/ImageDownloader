/**
 * 
 */
package name.seanpayne.utils.imgdwn.util;

import java.io.File;

/**
 * @author Sean
 *
 */
public final class DirectoryUtils {
	public static File createOutputDirectory(final File outputDirectoryRoot, final String id) {
		File outputSubdir = new File(outputDirectoryRoot, id);
		if(outputSubdir.exists() || (!outputSubdir.exists() && outputSubdir.mkdirs()))
			return outputSubdir;
		else
			return null;
	}
}
