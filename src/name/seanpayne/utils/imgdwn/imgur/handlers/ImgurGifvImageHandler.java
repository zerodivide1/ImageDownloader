package name.seanpayne.utils.imgdwn.imgur.handlers;

import name.seanpayne.utils.imgdwn.api.IMatchingHandler;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class ImgurGifvImageHandler implements IMatchingHandler {
    private static final String IMGURIMAGEURLABSOLUTE = "(https?:\\/\\/)?i.imgur\\.com\\/(([a-zA-Z0-9]+)\\.gifv)";
    private static final int IMAGEURLGROUP = 3;
    private static final int RETRYLIMIT = 2;
    private final Pattern imageUrlPattern = Pattern.compile(IMGURIMAGEURLABSOLUTE);

    public boolean isMatch(URL url) {
        Matcher m = imageUrlPattern.matcher(url.toString());
        return m.matches();
    }

    @Override
    public void download(URL url, File outputPath, ExecutorService threadService) {
        Matcher m = imageUrlPattern.matcher(url.toString());
        if(m.matches()) {
            final String imageHash = m.group(IMAGEURLGROUP);
            //TODO
        }
    }
}
