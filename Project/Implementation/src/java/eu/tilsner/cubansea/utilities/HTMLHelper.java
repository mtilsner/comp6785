package eu.tilsner.cubansea.utilities;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Class that provides static helper methods for working with HTML files.
 * It provides functionality for fetching and parsing HTML documents. 
 * 
 * @author Matthias Tilsner
 */
public class HTMLHelper {

	/**
	 * Fetches an HTML document accessible under a specific URL.
	 * It uses the Apache HTTP Client library.
	 * 
	 * @param url The url that can be used for retrieving the document.
	 * @return The string content of the document including HTML tags.
	 */
	public static String fetchDocument(URL url) {
		return fetchDocument(url, new HashMap<String,String>());
	}
	
	/**
	 * Fetches an HTML document accessible under a specific URL.
	 * It uses the Apache HTTP Client library.
	 * 
	 * @param url The url that can be used for retrieving the document.
	 * @param headers Additional headers that have to be set for the request.
	 * @return The string content of the document including HTML tags.
	 */
	public static String fetchDocument(URL url, Map<String,String> headers) {
        try {
        	HttpClient _client = new HttpClient();
        	HttpMethod _method = new GetMethod(url.toString());
        	_method.setFollowRedirects(true);
        	for(Map.Entry<String,String> _header: headers.entrySet()) {
        		_method.setRequestHeader(_header.getKey(), _header.getValue());
        	}
			_client.executeMethod(_method);
	        return _method.getResponseBodyAsString();
		} catch (HttpException e) {
			throw new TechnicalError(e);
		} catch (IOException e) {
			throw new TechnicalError(e);
		}		
	}
	
	/**
	 * Collects all links and references from the body of a provided HTML
	 * document. Regular patterns are used for identifying the them.
	 * 
	 * @param document The document content
	 * @return A set of all URLs linked to in this document.
	 */
	public static Set<String> extractLinks(String document) {
		Set<String> _links = new HashSet<String>();
		Pattern _linkPattern = Pattern.compile("<a[^>]*href=\"([^\";?]+)",Pattern.CASE_INSENSITIVE);
			Matcher _linkMatcher = _linkPattern.matcher(document);
			while(_linkMatcher.find()) {
				_links.add(_linkMatcher.group(1));
			}
		Pattern _formPattern = Pattern.compile("<form[^>]*action=\"([^\";?]+)",Pattern.CASE_INSENSITIVE);
		 	Matcher _formMatcher = _formPattern.matcher(document);
		 	while(_formMatcher.find()) {
				_links.add(_formMatcher.group(1));
		 	}
		return _links;
	}
}
