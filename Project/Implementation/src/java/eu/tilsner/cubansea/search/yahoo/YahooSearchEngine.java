package eu.tilsner.cubansea.search.yahoo;

import java.util.List;

import eu.tilsner.cubansea.search.Search;
import eu.tilsner.cubansea.search.SearchEngineException;
import eu.tilsner.cubansea.search.SearchEngine;

/**
 * Wrapper for YahooSearchEngine. This class is only a factory
 * for creating an initializing YahooSearches. 
 *
 * @author Matthias Tilsner
 */
public class YahooSearchEngine implements SearchEngine {

    /* (non-Javadoc)
     * @see eu.tilsner.cubansea.search.SearchEngine#createSearch(java.util.List, int)
     */
	@Override
    public Search createSearch(List<String> searchTerms, int initialLoadCount) throws SearchEngineException {
        YahooSearch search = new YahooSearch(searchTerms);
        search.ensureResultsAreFetched(initialLoadCount);
        return search;
    }

}
