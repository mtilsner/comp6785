package eu.tilsner.cubansea.prepare;

import java.util.List;

import eu.tilsner.cubansea.search.SearchResult;

/**
 * An abstract interface for algorithms that prepare search result items for the
 * clustering process.
 * 
 * @author Matthias Tilsner
 *
 */
public interface PreparationAlgorithm {
	
	/**
	 * Prepares the result items passed to the method.
	 * 
	 * @param items The result items that must be prepared.
	 * @return The prepared result items.
	 */
	public List<PreparedResult> prepareResults(List<SearchResult> items);
}
