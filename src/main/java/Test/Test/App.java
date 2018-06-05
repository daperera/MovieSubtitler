package Test.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.WordInfo;
import com.google.protobuf.ByteString;

/**
 * Hello world!
 *
 */
public class App 
{

	public static void main( String[] args ) throws Exception
	{
		test();
		//		testSubtitleExtraction();
		//		testSubtitleDownload();
	}

	private static void test() {
		Time t = SubtitleFactory.timeFromSrt("00:36:46,280");
		System.out.println(t);
	}

	public static void testSubtitleExtraction() {

		try {
			// The path to the audio file to transcribe
			String fileName = "data/audio/sample.wav";

			// Reads the audio file into memory
			Path path = Paths.get(fileName);
			byte[] data = Files.readAllBytes(path);
			ByteString audioBytes = ByteString.copyFrom(data);

			// Extract subtitles
			System.out.println("Extracting subtitles");
			RecognizeResponse response = SubtitleExtractor.extractSubtitles(audioBytes);

			// display extracted subtitles
			List<SpeechRecognitionResult> results = response.getResultsList();
			for (SpeechRecognitionResult result : results) {
				// There can be several alternative transcripts for a given chunk of speech. Just use the
				// first (most likely) one here.
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				System.out.printf("Transcription: %s\n", alternative.getTranscript());
				for (WordInfo wordInfo : alternative.getWordsList()) {
					System.out.println(wordInfo.getWord());
					System.out.printf("\t%s.%s sec - %s.%s sec\n",
							wordInfo.getStartTime().getSeconds(),
							wordInfo.getStartTime().getNanos() / 100000000,
							wordInfo.getEndTime().getSeconds(),
							wordInfo.getEndTime().getNanos() / 100000000);
				}

			}

		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public static void testSubtitleDownload() {
		// download subtitle of a movie
		System.out.println("Downloading subtitles");
		String videoFilename = "Whiskey.Tango.Foxtrot.2016.720p.BluRay.x264-DRONES";
		String subtitleFilename = SubtitleDownloader.downloadSubtitle(videoFilename);

		// print downloaded file
		System.out.println("Printing subtitles");
		try (BufferedReader br = new BufferedReader(new FileReader(subtitleFilename))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
