package eu.tilsner.cubansea.search.yahoo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import com.yahoo.search.WebSearchResult;

import eu.tilsner.cubansea.search.SearchResult;
import eu.tilsner.cubansea.search.SearchResultException;
import eu.tilsner.cubansea.utilities.HTMLHelper;
import eu.tilsner.cubansea.utilities.TechnicalError;

/**
 * Adapter for Yahoo search results to make them accessible via
 * the Result interface.
 *
 * @author Matthias Tilsner
 */
public class YahooSearchResult implements SearchResult {

	private WebSearchResult result;
	private String content;
	private int rank;
	
	/**
	 * The adapter requires a result of the Yahoo framework that will
	 * be wrapped. 
	 * 
	 * @param _result Yahoo result
	 */
	public YahooSearchResult(WebSearchResult _result, int _rank) {
		result = _result;
		rank   = _rank;
	}
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.search.Result#getContent()
	 */
	@Override
	public String getContent() throws SearchResultException {
		if(content == null) {
			try {
				content = HTMLHelper.fetchDocument(getURL());
			} catch (TechnicalError e) {
				throw new SearchResultException(e);
			}
		}
		return content;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.search.Result#getLinks()
	 */
	@Override
	public Set<String> getLinks() throws SearchResultException {
		return HTMLHelper.extractLinks(getContent());
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.search.Result#getTitle()
	 */
	@Override
	public String getTitle() {
		return result.getTitle();
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.search.Result#getURL()
	 */
	@Override
	public URL getURL() throws SearchResultException {
		try {
			return new URL(result.getUrl());
		} catch (MalformedURLException e) {
			throw new SearchResultException(e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.search.Result#getSummary()
	 */
	@Override
	public String getSummary() {
		return result.getSummary();
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.search.Result#getRank()
	 */
	@Override
	public int getRank() {
		return rank;
	}

}
