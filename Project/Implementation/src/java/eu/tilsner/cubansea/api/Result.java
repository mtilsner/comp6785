package eu.tilsner.cubansea.api;

import java.awt.Color;
import java.net.URL;
import java.util.Set;

import eu.tilsner.cubansea.cluster.ClusteredResult;
import eu.tilsner.cubansea.search.SearchResultException;
import eu.tilsner.cubansea.utilities.TechnicalError;

/**
 * Facade result class that can be used when dealing with the cubansea. It
 * wraps a clustered result.
 * 
 * @author Matthias Tilsner
 */
public class Result {
	
	private ClusteredResult result;
	
	/**
	 * Determines the Color that shall be used for this result for a specific
	 * cluster. The relevance of the result for this cluster is encoded in the
	 * color's saturation. 
	 * 
	 * @param cluster The cluster for which the color shall be determined.
	 * @return The color of this result.
	 */
	public Color getColor(Cluster cluster) {
		Color baseRGB = cluster.getBaseColor();
		float[] baseHSB = Color.RGBtoHSB(baseRGB.getRed(), baseRGB.getGreen(), baseRGB.getBlue(), null);
		baseHSB[1] *= result.getRelevance(cluster.getCluster());
		return Color.getHSBColor(baseHSB[0], baseHSB[1], baseHSB[2]);
	}
	
    /**
     * Retrieves the HTML content of a result. For this, a HTTP connection has to be created that
     * can result in connection exceptions. These exceptions are packed in abstract 
     * "SearchResultException". The content will be plain text.
     * 
     * @return The content document related to a result
     */
	public String getContent() {
		try {
			return result.getPreparedResult().getSearchResult().getContent();
		} catch (SearchResultException e) {
			throw new TechnicalError(e);
		}
	}

    /**
     * Retrieves all links of a result document. For this the content document is searched for all
     * links and forms. Their URLs are extracted, cleaned, and returned as a list. 
     * 
     * @return List of URLs of documents linked to by this document
     */
	public Set<URL> getLinks() {
		try {
			return result.getPreparedResult().getSearchResult().getLinks();
		} catch (SearchResultException e) {
			throw new TechnicalError(e);
		}
	}

	/**
	 * Gets the rank of the result in a specific cluster.
	 * 
	 * @param cluster The cluster for which the rank shall be determined.
	 * @return The rank of the result.
	 */
	public int getRank(Cluster cluster) {
		return cluster.getRank(this);
	}

    /**
     * Get a short summary of the document. This summary is usually provided by the SearchEngine
     * and should therefore not resolve in an exception.
     * 
     * @return The summary of the result.
     */
	public String getSummary() {
		return result.getPreparedResult().getSearchResult().getSummary();
	}

    /**
     * Gets the title of the result fetched.
     * 
     * @return The title of result.
     */
	public String getTitle() {
		return result.getPreparedResult().getSearchResult().getTitle();
	}

    /**
     * Gets the URL that can be used for accessing the document. The URL is specifically required
     * when fetching the content.
     * 
     * @return The URL of the result.
     */
	public URL getURL() {
		try {
			return result.getPreparedResult().getSearchResult().getURL();
		} catch (SearchResultException e) {
			throw new TechnicalError(e);
		}
	}

	/**
	 * Constructor for cubansea results. Require the passing of a wrapped
	 * clustered result. 
	 * 
	 * @param _result
	 */
	public Result(ClusteredResult _result) {
		result = _result;
	}

}
