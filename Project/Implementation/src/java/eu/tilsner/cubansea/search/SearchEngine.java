package eu.tilsner.cubansea.search;

import java.util.List;

/**
 * This is an abstract interface for search engines. Every search
 * engine needs to provide the ability of creating new searches
 * based on a list of search terms  
 * @author Matthias Tilsner
 */
public interface SearchEngine {

	/**
	 * Creates a new search based on a specific list of search terms.
	 * @param searchTerms List of search terms.
	 * @param initialLoad Number of elements that are to be fetched
	 * 					  upon initialization.
	 * @return Search
	 * @throws SearchEngineException
	 * @see Search
	 */
    public abstract Search createSearch(List<String> searchTerms, int initialLoad) throws SearchEngineException;
}
