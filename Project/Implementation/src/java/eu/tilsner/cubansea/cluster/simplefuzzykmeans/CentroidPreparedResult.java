package eu.tilsner.cubansea.cluster.simplefuzzykmeans;

import java.util.Map;
import java.util.Set;

import eu.tilsner.cubansea.prepare.PreparedResult;
import eu.tilsner.cubansea.search.SearchResult;

/**
 * Dummy wrapper class that can be used for storing centroid information.
 * 
 * @author Matthias Tilsner
 */
public class CentroidPreparedResult implements PreparedResult {

	private Map<String, Double> attributes;
	
	/**
	 * Constructor for the centroid data holder.
	 * 
	 * @param _attributes The values this centroid is bound to.
	 */
	public CentroidPreparedResult(Map<String, Double> _attributes) {
		attributes = _attributes;
	}
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.prepare.PreparedResult#getFrequency(java.lang.String)
	 */
	@Override
	public double getFrequency(String word) {
		if(attributes.containsKey(word))
			return attributes.get(word);
		else
			return 0.0;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.prepare.PreparedResult#getSearchResult()
	 */
	@Override
	public SearchResult getSearchResult() {
		throw new RuntimeException("Trying to access search result of dummy Centroid prepared result");
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.prepare.PreparedResult#getWords()
	 */
	@Override
	public Set<String> getWords() {
		return attributes.keySet();
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.prepare.PreparedResult#getAllFrequencies()
	 */
	@Override
	public Map<String, Double> getAllFrequencies() {
		return attributes;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.prepare.PreparedResult#setAllFrequencies(eu.tilsner.cubansea.prepare.PreparedResult)
	 */
	@Override
	public void setAllFrequencies(PreparedResult _base) {
		attributes = _base.getAllFrequencies();
	}

}
