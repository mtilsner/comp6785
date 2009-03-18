package eu.tilsner.cubansea.search;

import java.util.List;

/**
 * Abstract class for web searches. Searches are always created
 * by a Search Engine and allow the user to retrieve Results. How
 * this is done depends on the implementation and the backend
 * actually used.
 * This framework allows only MAXIMUM_INT_VALUE results, since it is
 * highly unlikely that a user will ever require more results .
 * 
 * @author Matthias Tilsner
 */
public interface Search {

	/**
	 * Determines how many results will be fetched with each request.
	 * 
	 * @return The number of results.
	 */
	public abstract int getFetchBlockSize();
	
	/**
	 * Evaluates how many results exist in total for a specific search.
	 * 
	 * @return The number of existing results
	 */
	public abstract int getResultCount();
	
    /**
     * Fetches a subset of the total result list.
     * 
     * @param first The index of the first result to fetch in the total result list (starting with 0)
     * @param count The number of results to fetch.
     * @return List of results fetched from the total result list
     */
    public abstract List<SearchResult> getResults(int first, int count);
}
