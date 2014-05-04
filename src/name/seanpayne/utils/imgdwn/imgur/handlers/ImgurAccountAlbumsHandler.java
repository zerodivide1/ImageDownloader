package name.seanpayne.utils.imgdwn.imgur.handlers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.seanpayne.utils.imgdwn.api.IMatchingHandler;
import name.seanpayne.utils.imgdwn.imgur.ImgurAccountAlbums;
import name.seanpayne.utils.imgdwn.imgur.ImgurAlbum;
import name.seanpayne.utils.imgdwn.util.DirectoryUtils;

public class ImgurAccountAlbumsHandler implements IMatchingHandler {
	private static final String ACCOUNTURL = "(https?:\\/\\/)?([a-zA-Z0-9]+)\\.imgur\\.com(\\/.*)?";
	private static final int ACCOUNTNAMEGROUP = 2;
	private static final int RETRYLIMIT = 2;
	private final Pattern accountAlbumsPattern = Pattern.compile(ACCOUNTURL);
	
	@Override
	public boolean isMatch(URL url) {
		Matcher m = accountAlbumsPattern.matcher(url.toString());
		return m.matches();
	}

	@Override
	public void download(URL url, File outputPath, ExecutorService threadService) {
		Matcher m = accountAlbumsPattern.matcher(url.toString());
		if(m.matches()) {
			final String accountName = m.group(ACCOUNTNAMEGROUP);
			
			final ImgurAccountAlbums accountAlbums = new ImgurAccountAlbums(accountName);
			List<ImgurAlbum> albums = accountAlbums.getAlbums();
			if(albums != null) {
				File outputSubdir = DirectoryUtils.createOutputDirectory(outputPath, accountName);
				if(outputSubdir != null) {
					for(int i=0; i < albums.size(); i++) {
						try {
							ImgurAlbum album = albums.get(i);
							
							ImgurAlbumHandler albumHandler = new ImgurAlbumHandler();
							albumHandler.download(new URL(String.format("https://imgur.com/a/%s", album.getId())), outputSubdir, threadService);
						} catch (MalformedURLException e) {
							e.printStackTrace(System.err);
						}
					}
				}
			}
		}
	}

	

}
