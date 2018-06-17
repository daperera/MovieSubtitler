package Test.Test;

import Test.Test.subscene.SubtitleFinder;


public class SubtitleDownloader {

	/**
	 * @param movieName name of the downloaded movie file
	 * @param subtitleFilename 
	 * @return location where subtitles srt file have been downloaded
	 */
	public static void downloadSubtitle(String movieName, String subtitleFilename) {
		movieName = removeExtension(movieName);
		SubtitleFinder.downloadSubtitle(movieName, subtitleFilename);
	}
	
	private static String removeExtension(String filename) {
		try {
	        return filename.substring(0, filename.lastIndexOf("."));
	    } catch (Exception e) {
	        return filename;
	    }
	}
}
