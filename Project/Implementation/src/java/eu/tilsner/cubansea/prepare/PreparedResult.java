package eu.tilsner.cubansea.prepare;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.tilsner.cubansea.search.SearchResult;

/**
 * This is a wrapper class for search results prepared for the clustering.
 * 
 * @author Matthias Tilsner
 *
 */
public interface PreparedResult {

	/**
	 * All words with this or less letters are ignored when counting
	 * the result frequencies.
	 */
	public static final int IGNORED_WORD_LENGTH = 3;
	
	/**
	 * All strings that are to be ignored when counting the result
	 * frequencies.
	 */
	public static final List<String> IGNORED_WORDS = Arrays.<String>asList(
		"therefor", "thus", "hence", "because", "since", "consequently", "however", "though", "while", "until", 
		"from", "need", "such", "what", "with", "that", "your", "about"
	);
	
	/**
	 * Pattern matching all characters that must be deleted from words.
	 */
	public static final String INVALID_CHARACTER_PATTERN = "[^a-zA-Zäöüß]+";

	/**
	 * The search result this prepared result was created for.
	 * 
	 * @return The search result wrapped by this class.
	 */
	public SearchResult getSearchResult();
		
	/**
	 * Returns the set of all words used in the result document. 
	 * 
	 * @return The set of all words.
	 */
	public Set<String> getWords();
	
	/**
	 * Returns the frequency on how often a specific word is used inside
	 * a document.
	 * 
	 * @param word The word for which the frequency has to be determined.
	 * @return The frequency on how often the word is used.
	 */
	public double getFrequency(String word);
	
	/**
	 * Returns a map of all frequencies of all words occurring in the 
	 * result.
	 * 
	 * @return A map of the frequencies.
	 */
	public Map<String,Double> getAllFrequencies();
	
	/**
	 * Sets the frequencies to those of a provided prepared result. 
	 * 
	 * @param _base
	 */
	public void setAllFrequencies(PreparedResult _base);
}
