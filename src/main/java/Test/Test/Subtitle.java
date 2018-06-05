package Test.Test;

import java.util.HashMap;
import java.util.Map;

public class Subtitle {
	private long maxId;
	private final Map<Long, String> word;
	private final Map<Long, Duration> timestamp;

	public Subtitle() {
		maxId = 0;
		word = new HashMap<Long, String>();
		timestamp = new HashMap<Long, Duration>();
	}

	public void put(String word, Duration timestamp) {
		this.word.put(maxId, word);
		this.timestamp.put(maxId, timestamp);
		maxId++;
	}

	public long getSize() {
		return maxId;
	}

	public void remove(long id) {
		if(id>= maxId)
			return;
		word.remove(id);
		timestamp.remove(id);
		maxId--;
	}
	
	public void removeBetween(long start, long end){
		if(start>=maxId)
			return;
		for(long id=start; id<=Long.max(end, maxId-1); id++) {
			word.remove(id);
			timestamp.remove(id);
			maxId--;
		}
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
}
