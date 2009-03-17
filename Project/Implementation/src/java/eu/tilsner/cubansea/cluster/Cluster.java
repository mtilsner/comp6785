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
	 * Returns a subset of the result list.
	 * 
	 * @param first The index of the first item in the result list.
	 * @param count The number of results that are required.
	 * @return The subset of the result list.
	 */
	public List<ClusteredResult> getResults(int first, int count);

	/**
	 * Determines the number of available results.
	 * 
	 * @return The number of results available.
	 */
	public int getResultCount();
	
	/**
	 * Identifies a common topic for all results in this cluster.
	 * 
	 * @return The topic of this cluster.
	 */
	public String getTopic();
	
	/**
	 * Identifies a common topic for all results in this cluster
	 * based on the search terms used to retrieve the results.
	 * 
	 * @param searchTerms A list of the search terms used.
	 * @return The topic of this cluster.
	 */
	public String getTopic(List<String> searchTerms);
	
	/**
	 * Adds a result to a cluster after its creation.
	 * 
	 * @param result The result to be added to the cluster.
	 */
	public void addResult(ClusteredResult result);
	
	/**
	 * Sorts the results inside a cluster.
	 */
	public void sort();
}
