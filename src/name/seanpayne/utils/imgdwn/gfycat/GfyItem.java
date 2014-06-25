package name.seanpayne.utils.imgdwn.gfycat;

import java.net.URL;

public class GfyItem {
	public enum Type {
		GIF("gif"),
		MP4("mp4");
		
		public String ext;
		private Type(String ext) {
			this.ext = ext;
		}
	};
	
	private String name;
	private URL url;
	private long size;
	private Type type;

	public GfyItem(String name, URL url, long size, Type type) {
		this.name = name;
		this.url = url;
		this.size = size;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public long getSize() {
		return size;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public Type getType() {
		return type;
	}
}
