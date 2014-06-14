package name.seanpayne.utils.imgdwn.gfycat;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import name.seanpayne.utils.imgdwn.json.JSONReader;
import name.seanpayne.utils.imgdwn.json.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableList;

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
		//TODO Generate filename
		
		//TODO Download item
	}
	
	

}
