package Test.Test.old;

import java.util.Locale;

import org.apache.commons.text.similarity.FuzzyScore;
import org.apache.commons.text.similarity.JaroWinklerDistance;

public class StringDistance {

	public static void main(String[] args) {
		distance("hello", "hallo");
		distance("PENNSYLVANIA", "PENNCISYLVNIA");
	}

	public static void distance(String a, String b) {
		double distance;
		
		System.out.println("comparing string " + a + " and " + b);
		
		// Fuzzy Score
		FuzzyScore fz = new FuzzyScore(Locale.ENGLISH);
		distance = fz.fuzzyScore(a, b);
		System.out.println("fuzzy score : " + distance);
		
		// Jaro-Winkler Distance
		JaroWinklerDistance   jwd = new JaroWinklerDistance();
		distance = jwd.apply(a, b);
		System.out.println("Jaro-Winkler distance score : " + distance);
		
	}
}
