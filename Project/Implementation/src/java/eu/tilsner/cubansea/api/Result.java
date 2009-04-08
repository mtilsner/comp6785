package eu.tilsner.cubansea.api;

import java.awt.Color;
import java.net.URL;
import java.util.Set;
import java.util.UUID;

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
	private String			id;

	/**
	 * Returns a unique string id for this result.
	 * 
	 * @return The string id.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Determines how relevant the result is for a specific cluster.
	 * 
	 * @param cluster The cluster for which the relevance of the result has to be determined.
	 * @return The relevance.
	 */
	public double getRelevance(Cluster cluster) {
		return result.getRelevance(cluster.getCluster());
	}
	
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
		baseHSB[1] *= Math.pow(getRelevance(cluster),2);
		baseHSB[2] += (1-baseHSB[2])*Math.pow(1.0-getRelevance(cluster),2);
		return Color.getHSBColor(baseHSB[0], baseHSB[1], baseHSB[2]);
	}

	/**
	 * Returns the CSS string of the color that shall be used for this result
	 * for a specific cluster.
	 * 
	 * @param cluster The cluster for which the color shall be determined.
	 * @return The CSS color string.
	 */
	public String getCssColor(Cluster cluster) {
		Color _color = getColor(cluster);
		String _red 	= Integer.toHexString(_color.getRed());
		String _green	= Integer.toHexString(_color.getGreen());
		String _blue	= Integer.toHexString(_color.getBlue());
		if(_red.length() < 2) 	_red   = "0"+_red;
		if(_green.length() < 2) _green = "0"+_green;
		if(_blue.length() < 2) 	_blue  = "0"+_blue; 
		return _red+_green+_blue;		
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
	public Set<String> getLinks() {
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
	 * Returns the original result rank from the web search query.
	 * 
	 * @return The original rank.
	 */
	public int getOriginalRank() {
		return result.getPreparedResult().getSearchResult().getRank();
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
	public URL getUrl() {
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
		id	   = UUID.randomUUID().toString();
		result = _result;
	}

}
