/**
 * 
 */
package name.seanpayne.utils.imgdwn;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import name.seanpayne.utils.imgdwn.api.IDownloadHandler;
import name.seanpayne.utils.imgdwn.api.IImageService;
import name.seanpayne.utils.imgdwn.defaults.DefaultDownloadHandler;
import name.seanpayne.utils.imgdwn.flickr.FlickrService;
import name.seanpayne.utils.imgdwn.gfycat.GfycatService;
import name.seanpayne.utils.imgdwn.imgly.ImglyService;
import name.seanpayne.utils.imgdwn.imgur.ImgurService;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

/**
 * @author Sean
 * 
 */
public class Main implements Runnable{
	private static final int THREADPOOLSIZE = 4;
	PrintStream errorOutput;
	PrintStream standardOutput;
	Settings appSettings;
	
	private List<IImageService> services; 
	
	public static void main(String[] args) {
		Main mainApp = new Main();
		try {
			mainApp.configure(args, System.err, System.out);
			mainApp.run();
		} catch (IllegalArgumentException e) {
			e.printStackTrace(System.err);
		}
	}
	
	public Main() {
		services = new ArrayList<IImageService>();
		services.add(new ImgurService());
		services.add(new FlickrService());
		services.add(new ImglyService());
		services.add(new GfycatService());
	}
	
	public void configure(String[] args, PrintStream errorOutput, PrintStream standardOutput) throws IllegalArgumentException {
		this.errorOutput = errorOutput;
		this.standardOutput = standardOutput;
		appSettings = parseOptions(args, errorOutput, standardOutput);
	}
	
	private Settings parseOptions(String[] args, PrintStream errorOutput, PrintStream standardOutput) throws IllegalArgumentException {
		if(args == null || args.length == 0)
			throw new IllegalArgumentException("Missing arguments");
		
		JSAP parser = configureOptions();
		if(parser != null) {
			JSAPResult configuration = parser.parse(args);
			if(!configuration.success()) {
				for (Iterator err = configuration.getErrorMessageIterator(); err.hasNext();) {
					errorOutput.println("Error: " + err.next());
				}
				errorOutput.println();
				
				printUsage(parser, standardOutput);
				
				throw new IllegalArgumentException("Invalid arguments");
			} else {
				//get arguments
				Settings settings = new Settings();
				
				settings.setInputURL(configuration.getURL("url"));
				settings.setOutputPath(configuration.getFile("output"));
				
				return settings;
			}
		} else {
			throw new IllegalArgumentException("Unable to parse arguments");
		}
		
		
	}
	
	private void printUsage(JSAP jsap, PrintStream output) {
		output.println("Usage: java -jar imgurdownload.jar <album URL> <output dir>");
		output.println(jsap.getUsage());
		output.println();
		output.println(jsap.getHelp());
	}
	
	private JSAP configureOptions() {
		JSAP jsap = new JSAP();
		
		try {
			UnflaggedOption inputURL = new UnflaggedOption("url")
										.setStringParser(JSAP.URL_PARSER)
										.setRequired(true);
			jsap.registerParameter(inputURL);
		} catch (JSAPException e) {
			System.err.println("Unable to configure options parser for input URL");
			return null;
		}
		
		try {
			UnflaggedOption outputDir = new UnflaggedOption("output")
										 .setStringParser(FileStringParser.getParser().setMustBeFile(false).setMustBeDirectory(true).setMustExist(true))
										 .setRequired(true);
			jsap.registerParameter(outputDir);
		} catch (JSAPException e) {
			System.err.println("Unable to configure options parser for output directory");
			return null;
		}
		
		return jsap;
	}
	@Override
	public void run() {
		ExecutorService threadPool = Executors.newFixedThreadPool(THREADPOOLSIZE);
		
		IDownloadHandler selectedHandler = new DefaultDownloadHandler();
		for (IImageService service : services) {
			IDownloadHandler searchHandler = service.getHandler(appSettings.getInputURL());
			if(searchHandler != null) {
				selectedHandler = searchHandler;
				break;
			}
		}
		
		if(selectedHandler != null)
			selectedHandler.download(appSettings.getInputURL(), appSettings.getOutputPath(), threadPool);

		
		try {
			threadPool.shutdown();
			threadPool.awaitTermination(1, TimeUnit.HOURS);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
