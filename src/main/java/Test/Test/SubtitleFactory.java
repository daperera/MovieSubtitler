package Test.Test;

import com.google.protobuf.Duration;

public class SubtitleFactory {
	
	
	/**
	 * Create Time object from google Duration object
	 */
	public static Time timeFromGoogleDuration(Duration duration) {
		try {
			long sec = duration.getSeconds();
			int hour = (int) (sec/3600);
			int minute = (int) ((sec%3600)/60);
			int second = (int) (sec%60);
			int ms = (duration.getNanos()/1000)%1000;
			return new Time(hour, minute, second, ms);
			} catch(Exception e) {
				return new Time();
			}
	}
	
	
	/**
	 * create Time object from string (.srt file encoding of time)
	 * @time the time string (ex : 00:05:52,960)
	 */
	public static Time timeFromSrt(String time) {
		String[] splits = time.split("[:,]");
		try {
			int hour = Integer.parseInt(splits[0]);
			int minute = Integer.parseInt(splits[1])%60;
			int second = Integer.parseInt(splits[2])%60;
			int ms = Integer.parseInt(splits[3])%1000;
			return new Time(hour, minute, second, ms);
		} catch(Exception e) {
			return new Time();
		}
	}
}
