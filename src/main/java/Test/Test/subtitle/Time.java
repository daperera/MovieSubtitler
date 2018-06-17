package Test.Test.subtitle;

import com.google.protobuf.Duration;

public class Time {

	private long ms;


	public Time(int hour, int minute, int second, int ms) {
		this.ms = (ms%1000) + 
				(second%60)*1000 +
				(minute%60)*1000*60 +
				(hour%100)*1000*60*60;
	}

	public Time() {
		this(0);
	}
	
	public Time(long ms) {
		this.ms = ms;
	}
	
	/**
	 * return time elapsed between t1 and t2, that is Time(t2-t1)
	 */
	public static Time elapsedTime(Time t1, Time t2) {
		return new Time(t2.ms-t1.ms);
	}
	/**
	 * 	shift time from t (time object), that is : this <- Time(this+t)
	 * @ return shifted Time object
	 */
	public Time shift(Time t) {
		ms += t.ms;
		return this;
	}

	/**
	 * 	shift time from t (in ms), that is : this <- Time(this+t)
	 * @ return shifted Time object
	 */
	public Time shift(long ms) {
		this.ms += ms;
		return this;
	}
	
	public long divide(Time t) {
		return ms/t.ms;
	}
	
	public long divide(long ms) {
		return this.ms/ms;
	}
	
	/**
	 * @return time in ms
	 */
	public long toMs() {
		return ms;
	}

	public boolean before(Time t) {
		return ms <= t.ms;
	}

	public boolean after(Time t) {
		return ms > t.ms;
	}

	@Override
	public boolean equals(Object t) {
		return ms == ((Time) t).ms;
	}

	/**
	 * write current time in srt time format
	 * Warning hour = hour%100 in the output
	 */
	public String toString() {
		long tmp = ms/1000;
		int second = (int) tmp%60;
		tmp/=60;
		int minute = (int) tmp%60;
		tmp/=60;
		int hour= (int) tmp%100;
		
		String hourStr = String.format("%02d", hour);
		String minuteStr = String.format("%02d", minute);
		String secondStr = String.format("%02d", second);
		String msStr = String.format("%03d", ms%1000);
		return hourStr+":"+minuteStr+":"+secondStr+","+msStr;
	}
	public int getHour() {
		return (int) (ms/3600000) %100;
	}
	public int getMinute() {
		return (int) (ms/60000) %60;
	}
	public int getSecond() {
		return (int) (ms/1000) %60;
	}
	/**
	 * @return the ms of the current time. Ex : getMs(08:42:36,568) = 568
	 */
	public int getMs() {
		return (int) ms%1000;
	}

	/**
	 * Create Time object from google Duration object
	 */
	public static Time timeFromGoogleDuration(Duration duration) {
		try {
			long sec = duration.getSeconds();
			int hour = (int) (sec/3600)%100;
			int minute = (int) ((sec/60)%60);
			int second = (int) (sec%60);
			int ms = (duration.getNanos()/1000000)%1000;
			
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
		String[] splits = time.split("[:,.]");
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
