package eu.tilsner.cubansea.cluster;

import eu.tilsner.cubansea.prepare.PreparedResult;

/**
 * This is a wrapper class for clustered search results.
 * 
 * @author Matthias Tilsner
 *
 */
public interface ClusteredResult {

	/**
	 * The prepared result this clustered result was created for.
	 * 
	 * @return The prepared result wrapped by this class.
	 */
	public PreparedResult getPreparedResult();
	
	/**
	 * Determines how relevant the result is for a a specific cluster (in percent).
	 * 
	 * @param cluster The cluster for which the relevance shall be determined.
	 * @return The relevance in percent (0.0 - 1.0).
	 */
	public double getRelevance(Cluster cluster);
	
}
