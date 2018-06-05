package Test.Test;

import subscene.api.model.Subtitle;
import java.io.File;


public class SubtitleDownloader {

	private static String SUBTITLE_TEMP_FOLDER = "data/subtitles";
	
	/**
	 * 
	 * @param movieName name of the downloaded movie file
	 * @return location where subtitles srt file have been downloaded
	 */
	public static String downloadSubtitle(String movieName) {
		movieName = removeExtension(movieName);
		String videoFilename = SUBTITLE_TEMP_FOLDER + "/" + movieName + ".mkv";
		File video = new File(videoFilename);
        Subtitle sub = new Subtitle(video, Subtitle.Type.ForeignLangOnly);
        sub.download();
        String subFilename = removeExtension(videoFilename) + ".en.srt";
        return subFilename;
	}
	
	private static String removeExtension(String filename) {
		try {
	        return filename.substring(0, filename.lastIndexOf("."));
	    } catch (Exception e) {
	        return filename;
	    }
	}

}
