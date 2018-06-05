package Test.Test;

public class Duration {
	private Time start;
	private Time end;
	
	public Duration(Time start, Time end) {
		this.start = start;
		this.end = end;
	}
	
	public Duration shift(Time t) {
		start.shift(t);
		end.shift(t);
		return this;
	}
	
	public Time start() {
		return start;
	}
	public Time end() {
		return end;
	}
}
