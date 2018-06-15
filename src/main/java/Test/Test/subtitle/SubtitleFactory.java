package Test.Test.subtitle;

import java.util.List;

import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.WordInfo;

public class SubtitleFactory {
	
	

	
	public static Subtitle subtitleFromGoogleDuration(List<SpeechRecognitionResult> results) {
		Subtitle sub = new Subtitle();
		for (SpeechRecognitionResult result : results) {
			SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
			for (WordInfo wordInfo : alternative.getWordsList()) {
				String word = wordInfo.getWord();
				WordDuration duration = new WordDuration(Time.timeFromGoogleDuration(wordInfo.getStartTime()), 
						Time.timeFromGoogleDuration(wordInfo.getEndTime()));
				
				//System.err.println("putting word " + word + " with timestamp " + duration);
				
				sub.put(word, duration);
			}
		}
		return sub;
	}
}
