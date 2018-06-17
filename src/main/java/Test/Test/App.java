package Test.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import Test.Test.subtitle.Subtitle;
import Test.Test.subtitle.Time;

/**
 * Hello world!
 *
 */
public class App 
{

	public static void main( String[] args ) throws Exception
	{
		
		// start Time
		long startTime = System.currentTimeMillis();
		
		/* *********************************************** */
		
		// execute method
//		testAudioExtraction();
//		testSubtitleExtraction("data/audio/out001bis.wav", 48000);
//		test();
//		testSubtitleDownload("Paterson.2016.1080p.BRRip.x264.AAC-ETRG.mp4", "data/subtitles/paterson.srt");
		testSynchronization("data/video/Paterson.2016.1080p.BRRip.x264.AAC-ETRG.mp4", "data/subtitles/resync-paterson.srt");
		
		
		/* *********************************************** */
		
		// end time
		long endTime = System.currentTimeMillis();
		Time duration = new Time(endTime - startTime);
		System.out.println("Elapsed time : " + duration);
	}

	public static void testSynchronization(String movieFilename, String outputFilename) {
		// extracting movieName et setting subtitleFilename
		String movieName = extractMovieName(movieFilename);
		String subtitleFilename = "data/subtitles/" + movieName + ".srt";
		
		// download subtitle of a movie
		System.err.println("Downloading subtitles");
		SubtitleDownloader.downloadSubtitle(movieName, subtitleFilename);
		
		// create a subtitle object from downloaded file
		System.err.println("Parsing downloaded file");
		Subtitle officialSub = Subtitle.subtitleFromSrtFile(subtitleFilename);
		
	
		// extract sample of subtitle from the movie
		System.err.println("Extracting sample of subtitle");
		Subtitle sampleSub = Subtitle.extract(movieFilename);
		
		
		// synchronize the official sub using the sample sub
		System.err.println("Synchronizing downloaded subtitle using sample");
		Subtitle.synchronize(officialSub, sampleSub);
		
		// saving the synchronized subtitle 
		System.err.println("Saving synchronized subtitle to file");
		officialSub.saveToFile(outputFilename);
		
	}
	
	public static String extractMovieName(String movieFilename) {
		File f = new File(movieFilename);
		return f.getName();
	}
	
	public static void testSubtitleExtraction(int nSplit, int sampleRate) {
		for(int k=0; k<nSplit; k++) {
			String filename = "data/audio/out" + String.format("%03d", k) + ".mp3";
			testSubtitleExtraction(filename, sampleRate);
		}
	}
	
	public static void testSubtitleExtraction(String filename, int sampleRate) {

		try {
			// The path to the audio file to transcribe
			
			// Reads the audio file into memory
			Path path = Paths.get(filename);
			byte[] data = Files.readAllBytes(path);
			ByteString audioBytes = ByteString.copyFrom(data);

			// Extract subtitles
			System.out.println("Extracting subtitles");
			RecognizeResponse response = SpeechRecognizer.extractSubtitles(audioBytes, sampleRate);

			// display extracted subtitles
			List<SpeechRecognitionResult> results = response.getResultsList();
			if(response.getResultsList().isEmpty()) 
				System.out.println("Error : empty list found");
			for (SpeechRecognitionResult result : results) {
				// There can be several alternative transcripts for a given chunk of speech. Just use the
				// first (most likely) one here.
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				System.out.printf("Transcription: %s\n", alternative.getTranscript());
			/*	
				for (WordInfo wordInfo : alternative.getWordsList()) {
					System.out.println(wordInfo.getWord());
					System.out.printf("\t%s.%s sec - %s.%s sec\n",
							wordInfo.getStartTime().getSeconds(),
							wordInfo.getStartTime().getNanos() / 100000000,
							wordInfo.getEndTime().getSeconds(),
							wordInfo.getEndTime().getNanos() / 100000000);
				}
*/
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public static void testSubtitleDownload(String videoFilename, String subtitleFilename) {
		// download subtitle of a movie
		System.out.println("Downloading subtitles");
		SubtitleDownloader.downloadSubtitle(videoFilename, subtitleFilename);

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
