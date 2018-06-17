package Test.Test.subtitle;

class WordDuration {
	private Time start;
	private Time end;

	public WordDuration(Time start, Time end) {
		this.start = start;
		this.end = end;
	}

	public WordDuration shift(Time t) {
		start.shift(t);
		end.shift(t);
		return this;
	}

	public WordDuration shift(long ms) {
		start.shift(ms);
		end.shift(ms);
		return this;
	}
	
	public Time start() {
		return start;
	}
	public Time end() {
		return end;
	}

	public String toString() {
		return "[" +start.toString() + "-" + end.toString() + "]";
	}
}
