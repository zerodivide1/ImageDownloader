package name.seanpayne.utils.imgdwn.gfycat;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.seanpayne.utils.imgdwn.api.IMatchingHandler;
import name.seanpayne.utils.imgdwn.json.JSONReader;
import name.seanpayne.utils.imgdwn.json.JSONUtils;

import org.json.JSONObject;

import com.google.common.collect.ImmutableList;

public class GfycatHandler implements IMatchingHandler {
	private static final String IMAGEURL = "https?://(.*\\.)?gfycat\\.com/([a-zA-Z0-9]+)(\\.gif)?";
	private static final String GFYCATINFOURL = "http://gfycat.com/cajax/get/%s";
	private static final int IMAGEURLGROUP = 2;
	private static final int RETRYLIMIT = 2;
	private static Pattern imageUrlPattern = Pattern.compile(IMAGEURL);

	@Override
	public boolean isMatch(URL url) {
		return imageUrlPattern.matcher(url.toString()).matches();
	}
	
	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		Matcher matcher = imageUrlPattern.matcher(url.toString());
		if(matcher.matches()) {
			final String imageName = matcher.group(IMAGEURLGROUP);
			
			List<GfyItem> urls;
			try {
				URL originalUrl = new URL(String.format(GFYCATINFOURL, imageName));
				
				urls = getImageUrls(originalUrl);
			} catch (Exception e) {
				e.printStackTrace(System.err);
				return;
			} 
			
			for (GfyItem item : urls) {
				threadService.execute(new GfycatDownloader(item, outputPath, RETRYLIMIT));
			}
		}
	}

	private List<GfyItem> getImageUrls(URL infoURL) throws Exception {
		JSONObject jsonResult = JSONReader.readJsonFromUrl(infoURL);
		
		JSONObject gfyItem = jsonResult.getJSONObject("gfyItem");
		
		String imageName = JSONUtils.getNonNullString(gfyItem, "gfyName");
		
		URL mp4Url = JSONUtils.getURL(gfyItem, "mp4Url");
		long mp4Size = gfyItem.getLong("mp4Size");
		
		URL gifUrl = JSONUtils.getURL(gfyItem, "gifUrl");
		long gifSize = gfyItem.getLong("gifSize");
		
		return ImmutableList.<GfyItem>builder()
				.add(new GfyItem(imageName, mp4Url, mp4Size, GfyItem.Type.MP4))
				.add(new GfyItem(imageName, gifUrl, gifSize, GfyItem.Type.GIF))
				.build();
	}
	

}
