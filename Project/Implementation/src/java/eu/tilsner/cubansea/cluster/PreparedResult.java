package eu.tilsner.cubansea.cluster;

import java.util.Map;

import eu.tilsner.cubansea.search.SearchResult;

/**
 * This is a wrapper class for search results prepared for the clustering.
 * 
 * @author Matthias Tilsner
 *
 */
public interface PreparedResult extends SearchResult {
	
	/**
	 * Returns information on how frequently words are used inside
	 * the result summary.
	 * 
	 * @return The words from the summary and their frequencies.
	 */
	public Map<String,Integer> getWordFrequencies();
}
