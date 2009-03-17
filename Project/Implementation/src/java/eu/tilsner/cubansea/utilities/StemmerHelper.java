package eu.tilsner.cubansea.utilities;

import eu.tilsner.cubansea.utilities.external.PorterStemmer;

/**
 * Helper to the external.PorterStemmer class. Provides a simple access-method
 * that can be used inside the program thus reducing the loc.
 * 
 * @author Matthias Tilsner
 */
public class StemmerHelper {
	public static String stem(String origin) {
		PorterStemmer stemmer = new PorterStemmer();
		char[] letters = origin.toCharArray();
		for(char letter: letters) {
			stemmer.add(letter);
		}
		stemmer.stem();
		return stemmer.toString(); 
	}
}
