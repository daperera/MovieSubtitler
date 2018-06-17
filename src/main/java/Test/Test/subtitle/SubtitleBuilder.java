package Test.Test.subtitle;

class SubtitleBuilder {
	
	private final Subtitle sub;
	
	private Time startTime;
	private Time endTime;
	
	private SubtitleBuilder() {
		sub = new Subtitle();
		startTime = null;
		endTime = null;
	}
	
	public static SubtitleBuilder getBuilder() {
		return new SubtitleBuilder();
	}
	
	public Subtitle build() {
		return sub;
	}

	public SubtitleBuilder setStartTime(Time t) {
		this.startTime = t;
		return this;
	}
	
	public SubtitleBuilder setEndTime(Time t) {
		this.endTime = t;
		return this;
	}
	
	public SubtitleBuilder addWord(String word) {
		sub.put(word, new WordDuration(startTime, endTime));
		return this;
	}
	
	
}
