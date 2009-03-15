package eu.tilsner.cubansea.cluster;

/**
 * This is a wrapper class for clustered search results.
 * 
 * @author Matthias Tilsner
 *
 */
public interface ClusteredResult extends PreparedResult {

	/**
	 * Determines the rank the result has inside a specific cluster.
	 * 
	 * @param cluster The cluster for which the rank shall be determined.
	 * @return The rank (starting with 1).
	 */
	public int getRank(Cluster cluster);
	
	/**
	 * Determines how relevant the result is for a a specific cluster (in percent).
	 * 
	 * @param cluster The cluster for which the relevance shall be determined.
	 * @return The relevance in percent (0.0 - 100.0).
	 */
	public double getRelevance(Cluster cluster);
	
}
