package eu.tilsner.cubansea.search.yahoo;

import eu.tilsner.cubansea.search.SearchResult;
import eu.tilsner.cubansea.search.Search;
import eu.tilsner.cubansea.search.SearchEngineException;
import eu.tilsner.cubansea.utilities.StringHelper;
import eu.tilsner.cubansea.utilities.TechnicalError;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.yahoo.search.SearchClient;
import com.yahoo.search.SearchException;
import com.yahoo.search.WebSearchRequest;
import com.yahoo.search.WebSearchResult;
import com.yahoo.search.WebSearchResults;

/**
 * Searches performed by the Yahoo Search Engine. Yahoo  searches are restricted to a maximum fetch of 50 items
 * per request. Thus, if more items are needed, they have to be fetched in multiple requests. This class uses a
 * private array of Result objects. Whenever a subset of the total result set is fetched, it is inserted into 
 * this array. Before fetching results, the search will check whether the items are already in the array, thus
 * providing them from cache. 
 * 
 * @author Matthias Tilsner
 */
public class YahooSearch implements Search {
	
	private String query;
	private List<SearchResult> results = new ArrayList<SearchResult>();
	private int resultCount = -1;
	private int cacheStatus = 0;
	
	/**
     * YahooSearches can only be created by providing a search string composed of the search terms. All terms must
     * be concatenated by spaces.
     * 
     * @param searchQuery The query string consisting of the search terms concatenated by spaces
     * @throws SearchEngineException The search engine cannot be initialized.
     */
    public YahooSearch(List<String> terms) {
    	query = StringHelper.join(terms, " ");
    }

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.search.Search#getResults(int, int)
	 */
	@Override
    public List<SearchResult> getResults(int first, int count) {
		int _last = Math.min(getResultCount(), first+count);
		ensureResultsAreFetched(_last);
    	return results.subList(first, first+count);
    }

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.search.Search#getResultCount()
	 */
	@Override
	public int getResultCount() {
		if(resultCount == -1) fetchNextBlock();
		return resultCount;
	}

	/**
	 * Ensures that all results up to a specific point have been fetched yet. The method keeps continuing
	 * fetching block after block until the required result is in the cache and can be served. 
	 * 
	 * @param last The index of the last result that has to be fetched
	 * @throws SearchEngineException
	 */
	protected void ensureResultsAreFetched(int last) {
		while(last > cacheStatus) {
			fetchNextBlock();
		}
	}
	
	
	/**
	 * Fetches the next block of the results. Uses the current size of the cache as first element
	 * to be fetched and the configured FETCH_BLOCK_SIZE as the size of the fetch.
	 */
	private void fetchNextBlock() {
        try {
        	SearchClient _client = new SearchClient("cubansea-instance");
        	WebSearchResults _results = _client.webSearch(createNextRequest());
    		for(WebSearchResult _result: _results.listResults()) {
    			results.add(new YahooSearchResult(_result, cacheStatus+1));
    			cacheStatus++;
    		}
    		if(resultCount == -1) resultCount = _results.getTotalResultsAvailable().intValue();
        } catch (IOException e) {
			throw new TechnicalError(e);
		} catch (SearchException e) {
			throw new TechnicalError(e);
		}
	}
		
	/**
	 * Creates a request based on the query of the search and the limits
	 * for the next block. This request can later be used to fetch Yahoo results.
	 * A subset of the total result list is returned.
	 * 
	 * @return The request object that can be used for performing the Yahoo search
	 */
	private WebSearchRequest createNextRequest() {
		WebSearchRequest _request = new WebSearchRequest(query);
		_request.setStart(BigInteger.valueOf(cacheStatus+1));					//Yahoo API starts index with 1, not with 0
		_request.setResults(getFetchBlockSize());
		return _request;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.search.Search#getFetchBlockSize()
	 */
	@Override
	public int getFetchBlockSize() {
		return 50;
	}
}
