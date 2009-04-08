package eu.tilsner.cubansea.prepare.pottersummary;

import java.util.Map;
import java.util.Set;

import eu.tilsner.cubansea.prepare.PreparedResult;
import eu.tilsner.cubansea.search.SearchResult;

/**
 * Wrapper class for search results. It provides accessors for word
 * frequencies. The frequencies must be calculated prior to object
 * creation.
 * 
 * @author Matthias Tilsner
 */
public class PotterSummaryPreparedResult implements PreparedResult {

	private SearchResult searchResult;
	private Map<String, Double> wordFrequencies;
	
	/**
	 * Constructor for the prepared results requiring all attributes
	 * upon creation. 
	 * 
	 * @param _searchResult Search result wrapped in prepared result
	 * @param _wordFrequencies Frequencies of the words in the result
	 */
	public PotterSummaryPreparedResult(SearchResult _searchResult, Map<String, Double> _wordFrequencies) {
		searchResult = _searchResult;
		wordFrequencies = _wordFrequencies;
	}
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.prepare.PreparedResult#getFrequency(java.lang.String)
	 */
	@Override
	public double getFrequency(String word) {
		if(wordFrequencies.containsKey(word))
			return wordFrequencies.get(word);
		else
			return 0;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.prepare.PreparedResult#getWords()
	 */
	@Override
	public Set<String> getWords() {
		return wordFrequencies.keySet();
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.prepare.PreparedResult#getSearchResult()
	 */
	@Override
	public SearchResult getSearchResult() {
		return searchResult;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.prepare.PreparedResult#getAllFrequencies()
	 */
	@Override
	public Map<String, Double> getAllFrequencies() {
		return wordFrequencies;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.prepare.PreparedResult#setAllFrequencies(eu.tilsner.cubansea.prepare.PreparedResult)
	 */
	@Override
	public void setAllFrequencies(PreparedResult _base) {
		wordFrequencies = _base.getAllFrequencies();
	}
}
