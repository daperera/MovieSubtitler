package Test.Test.subtitle;

class SubtitleSynchronizer {


	public static void synchronize(Subtitle base, Subtitle extract) {
		int maxIndex = getBestMatch(base, extract);
		Time elapsedTime = getTimeShift(base, extract, maxIndex);
		
//		System.err.println("Shifting base subtitle of " + elapsedTime.toMs() + "ms");
		
		base.shift(elapsedTime);
	}

	private static Time getTimeShift(Subtitle base, Subtitle extract, int maxIndex) {
		Time baseStartTime = base.getTimestamp(maxIndex).start();
		Time extractStartTime = extract.getTimestamp(0).start();
		Time elapsedTime = Time.elapsedTime(baseStartTime, extractStartTime);
		return elapsedTime;
	}

	private static int getBestMatch(Subtitle base, Subtitle extract) {
		int nb = base.getSize(); // nb of words in base subtitle
		int ne = extract.getSize(); // nb of words in extract
		int currentScore = 0, maxScore = 0, maxIndex = 0;
		for(int i=0; i<nb-ne; i++) {
			currentScore = 0;
			for(int j=0; j<ne; j++) {
				if(base.getWord(i+j).equals(extract.getWord(j)))
					currentScore++;
			}
			if(currentScore>maxScore) {
				maxScore=currentScore;
				maxIndex = i;
			}
		}
		return maxIndex;
	}
}
