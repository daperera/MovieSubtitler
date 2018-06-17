package Test.Test.subtitle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.cloud.speech.v1.SpeechRecognitionResult;

import Test.Test.SubtitleSampleExtractor;

public class Subtitle {
	private int maxId;
	private final Map<Integer, String> word;
	private final Map<Integer, WordDuration> timestamp;

	public Subtitle() {
		maxId = 0;
		word = new HashMap<Integer, String>();
		timestamp = new HashMap<Integer, WordDuration>();
	}

	public void put(String word, WordDuration timestamp) {
		this.word.put(maxId, word);
		this.timestamp.put(maxId, timestamp);
		maxId++;
	}

	public int getSize() {
		return maxId;
	}

	public void remove(int id) {
		if(id>= maxId)
			return;
		word.remove(id);
		timestamp.remove(id);
		maxId--;
	}
	
	public void removeBetween(int start, int end){
		if(start>=maxId)
			return;
		for(int id=start; id<=Integer.max(end, maxId-1); id++) {
			word.remove(id);
			timestamp.remove(id);
			maxId--;
		}
	}

	public String getWord(int index) {
		return word.get(index);
	}
	
	public WordDuration getTimestamp(int index) {
		return timestamp.get(index);
	}
	
	public Subtitle getSubSample(Time start, Time end) {
		Subtitle subSample = new Subtitle();
		for(int k=0; k<maxId; k++) {
			Time currStart =  timestamp.get(k).start();
			if(currStart.after(end))
				break;
			if(currStart.after(start))
				subSample.put(word.get(k), timestamp.get(k));
		}
		return subSample;
	}
	
	public Subtitle shift(Time t) {
		for(Entry<Integer, WordDuration> e : timestamp.entrySet()) {
//			e.getValue().shift(t);
			e.setValue(e.getValue().shift(100));
		}
		return this;
	}
	
	public Subtitle shift(long ms) {
		for(Entry<Integer, WordDuration> e : timestamp.entrySet()) {
			e.getValue().shift(ms);
		}
		return this;
	}
	
	public String toString() {
		String str = "";
		for(int k=0; k<maxId; k++) {
			str += word.get(k) + " " + timestamp.get(k).toString() + "\n";
		}
		return str;
	}

	public static void synchronize(Subtitle base, Subtitle extract) {
		SubtitleSynchronizer.synchronize(base, extract);
	}

	public static Subtitle subtitleFromSrtFile(String subtitleFilename) {
		return SubtitleFactory.subtitleFromSrtFile(subtitleFilename);
	}

	public static Subtitle subtitleFromGoogleDuration(List<SpeechRecognitionResult> results) {
		return SubtitleFactory.subtitleFromGoogleDuration(results);
	}

	public static Subtitle extract(String movieFilename) {
		return SubtitleSampleExtractor.extract(movieFilename);
	}

	public void saveToFile(String outputFilename) {
		SubtitleFactory.saveToFile(this, outputFilename);
	}
}
