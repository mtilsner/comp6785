package eu.tilsner.cubansea.search;

import java.net.URL;
import java.util.Set;

/**
 * Abstract interface for the results provided by a web search. Each result exposes a set of read-only properties.
 * While title and summary are retrieved upon result creation, both content and links are fetched from the
 * original document when accessing either one for the first time. The URL might be malformed. 
 *
 * @author Matthias Tilsner
 */
public interface SearchResult {

	/**
	 * Gets the rank of the result fetched.
	 * 
	 * @return The rank of the result (starting with 1).
	 */
	public int getRank(); 

    /**
     * Gets the title of the result fetched.
     * 
     * @return The title of result.
     */
    public String getTitle();
    
    /**
     * Gets the URL that can be used for accessing the document. The URL is specifically required
     * when fetching the content.
     * 
     * @return The URL of the result.
     * @throws SearchResultException The URL might be malformed.
     */
    public URL getURL() throws SearchResultException;  
    
    /**
     * Get a short summary of the document. This summary is usually provided by the SearchEngine
     * and should therefore not resolve in an exception.
     * 
     * @return The summary of the result.
     */
    public String getSummary();
    
    /**
     * Retrieves the HTML content of a result. For this, a HTTP connection has to be created that
     * can result in connection exceptions. These exceptions are packed in abstract 
     * "SearchResultException". The content will be plain text.
     * 
     * @return The content document related to a result
     * @throws SearchResultException The retrieval of the document might fail.
     */
    public String getContent() throws SearchResultException;
    
    /**
     * Retrieves all links of a result document. For this the content document is searched for all
     * links and forms. Their URLs are extracted, cleaned, and returned as a list. 
     * 
     * @return List of URLs of documents linked to by this document
     * @throws SearchResultException The document might not be available and retrieval might fail.
     */
    public Set<String> getLinks() throws SearchResultException;
}
