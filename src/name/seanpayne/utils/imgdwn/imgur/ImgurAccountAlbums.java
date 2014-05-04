package name.seanpayne.utils.imgdwn.imgur;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableList;

public class ImgurAccountAlbums extends AbstractImgurElement {
	private static final String METHODKEY = "account";
	private static final String TARGET = "data";
	
	private String id;
	private List<ImgurAlbum> albums;
	
	public ImgurAccountAlbums(String id) {
		this.id = id;
	}
	
	public List<ImgurAlbum> getAlbums() {
		if(albums == null)
			refresh();
		
		return albums;
	}

	@Override
	protected void refresh() {
		refresh(METHODKEY, String.format("%s/albums/ids", id));
	}

	@Override
	protected void parse(JSONObject json) {
		try {
			JSONArray ids = json.getJSONArray(TARGET);
			
			ImmutableList.Builder<ImgurAlbum> result = new ImmutableList.Builder<ImgurAlbum>();
			for(int i = 0; i < ids.length(); i++) {
				result.add(new ImgurAlbum(ids.getString(i)));
			}
			
			this.albums = result.build();
		} catch (JSONException e) {
			e.printStackTrace(System.err);
		}
	}

}
