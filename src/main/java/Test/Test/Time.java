package Test.Test;

public class Time {

	private int hour;
	private int minute;
	private int second;
	private int ms;


	public Time(int hour, int minute, int second, int ms) {
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.ms = ms;
	}

	public Time() {
		this(0,0,0,0);
	}

	/**
	 * return time elapsed between t1 and t2, that is Time(t2-t1)
	 */
	public static Time elapsedTime(Time t1, Time t2) {
		return new Time(t2.hour-t1.hour, 
				t2.minute-t1.minute, 
				t2.second-t1.second, 
				t2.ms-t1.ms);
	}
	/**
	 * 	shift time from t, that is : this <- Time(this+t)
	 * @ return shifted Time object
	 */
	public Time shift(Time t) {
		hour += t.hour;
		minute += t.minute;
		second += t.second;
		ms += t.ms;
		return this;
	}

	public long toMs() {
		long res = (ms 
				+ 1000*second
				+ 60000*minute
				+ 3600000*hour);
		return res;
	}

	public boolean before(Time t) {
		if(t.hour < hour)
			return true;
		if(t.minute < minute)
			return true;
		if(t.second < second)
			return true;
		if(t.ms <= ms)
			return true;
		return false;
	}

	public boolean after(Time t) {
		return !before(t);
	}

	public boolean equals(Time t) {
		boolean equals = (hour==t.hour &&
				minute==t.minute &&
				second==t.second &&
				ms==t.ms);
		return equals;
	}

	/**
	 * write current time in srt time format
	 * Warning hour = hour%100 in the output
	 */
	public String toString() {
		String hourStr = String.format("%02d", hour%100);
		String minuteStr = String.format("%02d", minute);
		String secondStr = String.format("%02d", second);
		String msStr = String.format("%03d", ms);
		return hourStr+":"+minuteStr+":"+secondStr+","+msStr;
	}
	public int getHour() {
		return hour;
	}
	public int getMinute() {
		return minute;
	}
	public int getSecond() {
		return second;
	}
	public int getMs() {
		return ms;
	}

}
