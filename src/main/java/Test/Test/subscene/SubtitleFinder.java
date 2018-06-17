package Test.Test.subscene;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SubtitleFinder {

	public static final String BASE_SEARCH_URL = "http://subscene.com/subtitles/release?q=";
	public static final String BASE_SUBTITLE_URL = "http://subscene.com";

	/*
	public static void main(String[] args) {
		downloadSubtitle("Scorpion S01E08 HDTV x264-LOL[ettv]");
		downloadSubtitle("Paterson.2016.1080p.BRRip.x264.AAC-ETRG");
	}
	*/
	
	public static void downloadSubtitle(String movieName, String subtitleFilename) {
		try {
			SubtitleFinder subtitleFinder = new SubtitleFinder();
			String url = subtitleFinder.getZipURL(movieName);

			Zip.download(url)
			.unzip(subtitleFilename)
			.delete();
			
		} catch(IOException e) {
			System.err.println("Error : unable to download subtitle from movie :" + movieName);
			e.printStackTrace();
		}
	}


	public String getZipURL(String movieName) throws IOException {
		// get all subtitle pages
		String url = getSubtitleURLs(movieName);

		Document doc = Jsoup.connect(BASE_SUBTITLE_URL + url)
				.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
				.get();
		Elements link = doc.select(".button");
		String subtitleUrl = link.get(0).toString().split("\"")[1];
		return BASE_SUBTITLE_URL + subtitleUrl;
	}

	private String getSubtitleURLs(String movieName) throws IOException {
		Document doc = Jsoup.connect(BASE_SEARCH_URL + movieName)
				.cookie("LanguageFilter", "13")
				.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
				.get();
		Elements links = doc.select("table .a1 a");

		int selectedSubtitle = 0;

		/*
        if(showPicker){
            for (int i = 0; i < 5; i++) {
                System.out.println(i+1 + " " + links.get(i).text());
            }
            Scanner scanner = new Scanner(System.in);
            System.out.print("Select a number: ");
            selectedSubtitle = scanner.nextInt();
            scanner.nextLine();
            selectedSubtitle -= 1;
            scanner.close();
        }
		 */
		if(links.size() == 0)
			throw new IOException("Error : no subtitle found for movie : " + movieName);
		
		return links.get(selectedSubtitle).attr("href");

	}

}
