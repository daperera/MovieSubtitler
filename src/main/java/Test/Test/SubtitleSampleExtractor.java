package Test.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import Test.Test.subtitle.Subtitle;
import Test.Test.subtitle.SubtitleFactory;
import Test.Test.subtitle.Time;

public class SubtitleSampleExtractor {

	private static String AUDIO_TMP_DIR = "data/audio";
	private static String AUDIO_TMP_FILENAME = AUDIO_TMP_DIR + "/out";
	private static int SEGMENT_TIME = 55;

	public static void main(String[] args) {
		// start Time
		long startTime = System.currentTimeMillis();

		String videoFilename = "data/video/paterson.mp4";
		clearTmpDir();
		extractAudio(videoFilename);
		int nFile = predictNSample(videoFilename);
		Subtitle sub = extractSubtitle(nFile); 
		System.out.println(sub);

		// end time
		long endTime = System.currentTimeMillis();
		Time duration = new Time(endTime - startTime);
		System.out.println("Elapsed time : " + duration);
	}

	private static void clearTmpDir() {
		System.err.println("Clearing directory " + AUDIO_TMP_DIR);
		String cmd = "rm " + AUDIO_TMP_DIR + "/*";
		BashAdapter.execute(cmd); // execute and wait for result
	}

	private static void extractAudio(final String videoFilename) {
		System.err.println("Extracting audio sample");
		String cmd = "ffmpeg " +
				"-i " + videoFilename + " " + 
				"-af 'pan=mono|c0=FL' -f " +
				"segment -segment_time " + SEGMENT_TIME + " " + 
				AUDIO_TMP_FILENAME + "%03d.wav";
		BashAdapter.executeThread(cmd); // execute without waiting
	}

	/**
	 * @param nFile number of sample after split is completed
	 */
	private static Subtitle extractSubtitle(int nFile) {
		System.err.println("Extracting subtitles");
		
		// while no subtitle found in sample, continue processing them
		for(int k=2; k<nFile; k++) {
			String filename = AUDIO_TMP_FILENAME + String.format("%03d", k) + ".wav";
			
			waitFileCreation(filename); // wait until file is fully written;
			
			System.err.println("Extracting subtitles from file : " + filename);
			try {
				// load audio data
				Path path = Paths.get(filename);
				byte[] data = Files.readAllBytes(path);
				ByteString audioBytes = ByteString.copyFrom(data);
				
				// find audio rate
				int sampleRate = findAudioSample(filename);
				
				// extract subtitles
				RecognizeResponse response = SpeechRecognizer.extractSubtitles(audioBytes, sampleRate);
				List<SpeechRecognitionResult> results = response.getResultsList();

				// if some subtitles were found
				if(!response.getResultsList().isEmpty()) {
					Subtitle sub = SubtitleFactory.subtitleFromGoogleDuration(results);
					return sub.shift(k*SEGMENT_TIME*1000);
				}

			} catch (Exception e) {
				System.err.println("Error : unable to extract subtitle from file " + filename);
				e.printStackTrace();
			}

		}
		return null;
	}

	private static int findAudioSample(String videoFilename) {
		// using ffmpeg to extract video duration
		String cmd = "ffmpeg -i " + videoFilename + " 2>&1 | " + // get info of video with ffmpeg
				"grep 'Stream\\(.*\\)Audio' | " + // select line concerning audio stream
				"grep -o '[0-9]\\+ Hz' | " + // extract substring indicating sampling
				"cut -d ' ' -f 1"; // extract substring indicating sampling
		String output = BashAdapter.execute(cmd); // execute and get output	
		int audioSample = Integer.parseInt(output); 
		return audioSample;
	}

	private static Time findDuration(String videoFilename) {
		// using ffmpeg to extract video duration
		String cmd = "ffmpeg -i " + videoFilename + " 2>&1 | " + // get info of video with ffmpeg
				"grep 'Duration:' | " + // select line concerning duration
				"cut -d ',' -f 1 | " + // extract substring indicating duration
				"cut -d ' ' -f 4"; // extract substring indicating duration
		String output = BashAdapter.execute(cmd); // execute and get output		
		Time time = Time.timeFromSrt(output); // time from string
		return time;
	}

	private static int predictNSample(String videoFilename) {
		return (int) findDuration(videoFilename).divide(SEGMENT_TIME*1000);
	}
	
	private static void waitFileCreation(String filename) {
		File tmp = new File(filename);
		while(!tmp.exists()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
	}
	
}
