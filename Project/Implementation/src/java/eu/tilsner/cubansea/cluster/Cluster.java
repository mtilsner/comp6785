package eu.tilsner.cubansea.cluster;

import java.util.List;

import eu.tilsner.cubansea.prepare.PreparedResult;

/**
 * The actual cluster. It contains an ordered list of results. Also, it must expose
 * a method that allows the user to identify the semantic meaning of this cluster.
 * For this, a topic must be somehow generated.
 * 
 * @author Matthias Tilsner
 *
 */
public interface Cluster {

	/**
	 * Returns a dummy prepared result representing the centroid of
	 * the cluster.
	 * 
	 * @return The calculated centroid. 
	 */
	public PreparedResult getCentroid();
	
	/**
	 * Returns the entire result list.
	 * 
	 * @return The subset of the result list.
	 */
	public List<ClusteredResult> getResults();
	
	/**
	 * Adds a result to a cluster after its creation.
	 * 
	 * @param result The result to be added to the cluster.
	 */
	public void addResult(ClusteredResult result);
	
	/**
	 * Returns the relevance of the result best fitting this cluster.
	 *
	 * @return The relevance value
	 */
	public double getMaximumRelevance();

}
