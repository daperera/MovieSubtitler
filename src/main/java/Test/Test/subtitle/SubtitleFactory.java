package Test.Test.subtitle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.WordInfo;

class SubtitleFactory {

	public static Subtitle subtitleFromSrtFile(String filename) {
		SubtitleBuilder builder = SubtitleBuilder.getBuilder();
		try {
			// init variable
			FileInputStream fis = new FileInputStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			Scanner sc = null;
			boolean allGood = true;
			int captionNumber = 1;
			String warnings = "";

			String line = br.readLine();
			line = line.replace("\uFEFF", ""); //remove BOM character
			int lineCounter = 0;
			try {
				while(line!=null){
					line = line.trim();
					lineCounter++;
					// if its a blank line, ignore it, otherwise...
					if (!line.isEmpty()){
						allGood = false;
						// the first thing should be an increasing number
						try {
							int num = Integer.parseInt(line);
							if (num != captionNumber)
								throw new Exception();
							else {
								captionNumber++;
								allGood = true;
							}
						} catch (Exception e) {
							warnings+= captionNumber + " expected at line " + lineCounter;
						}
						if (allGood){
							// go to next line, here the begin and end time should be found
							try {
								lineCounter++;
								line = br.readLine().trim();
								String start = line.substring(0, 12);
								String end = line.substring(line.length()-12, line.length());
								Time startTime = Time.timeFromSrt(start);
								Time endTime = Time.timeFromSrt(end);
								builder.setStartTime(startTime);
								builder.setEndTime(endTime);
							} catch (Exception e){
								warnings += "incorrect time format at line " + lineCounter;
								allGood = false;
							}
						}
						if (allGood){

							// go to next line, where the caption text starts
							line = br.readLine().trim();
							lineCounter++;

							while(!line.isEmpty()) {

								// clean caption from XML tags
								line.replaceAll("\\<.*?\\>", "");

								// read this line by word and add it to subtitle 
								sc = new Scanner(line);
								while (sc.hasNext()) {
									String s = sc.next();
									builder.addWord(s);
								}
								sc.close();
								
								// advance to next line, while it is not empty
								line = br.readLine().trim();
								lineCounter++;
							}

						}
					}
					line = br.readLine();
				}

			}  catch (NullPointerException e){
				warnings+= "unexpected end of file, last caption may not be complete.\n\n";
			} finally{
				// close streams
				fis.close();
				if(sc != null)
					sc.close();
			}	

			if(!allGood)
				throw new IOException("Error while parsing srt file " + filename + "\n" + warnings);


		} catch(IOException e) {
			System.err.println("Error : unable to extract subtitle from file " + filename);
			e.printStackTrace();
		}
		return builder.build();
	}




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




	public static void saveToFile(Subtitle subtitle, String outputFilename) {
		if(subtitle.getSize()==0)
			return; // void subtitle
		try {
			Time prevStartTime = null, prevEndTime = null, nextStartTime = null;
			List<String> text = new ArrayList<String>();
			CaptionWriter writer = new SubtitleFactory().new CaptionWriter(outputFilename);

			prevStartTime = subtitle.getTimestamp(0).start();
			prevEndTime = subtitle.getTimestamp(0).end();
			for(int k=0; k<subtitle.getSize(); k++) {
				nextStartTime = subtitle.getTimestamp(k).start();
				if(nextStartTime.equals(prevStartTime)) {
					text.add(subtitle.getWord(k));
				} 
				else {
					writer.write(prevStartTime, prevEndTime, text);
					prevStartTime = subtitle.getTimestamp(k).start();
					prevEndTime = subtitle.getTimestamp(k).end();
					text.clear();
					text.add(subtitle.getWord(k));
				}
			}
			if(!text.isEmpty()) {
				writer.write(prevStartTime, prevEndTime, text);
			}
			writer.close();
		} catch (IOException e) {
			System.err.println("Error : unable to save subtitle to file : " + outputFilename);
			e.printStackTrace();
		} finally {
		}
	}

	private class CaptionWriter {

		private final PrintWriter writer;
		private int count;

		public CaptionWriter(String filename) throws FileNotFoundException {
			writer = new PrintWriter(filename);
			count = 1;
		}

		public void write(Time startTime, Time endTime, List<String> text) {
			writer.println(count++);
			writer.println(startTime.toString() + " --> " + endTime.toString());
			for(String word : text) {
				writer.print(word + " ");
			}
			writer.println("");
			writer.println("");
		}
		
		public void close() {
			writer.close();
		}
	}

}
