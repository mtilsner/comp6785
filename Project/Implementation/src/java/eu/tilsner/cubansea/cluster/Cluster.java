package eu.tilsner.cubansea.cluster;

import java.util.List;

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
}
