package eu.tilsner.cubansea.cluster;

import java.util.List;
import java.util.Set;

/**
 * Abstract interface for clustering algorithms.
 * 
 * @author Matthias Tilsner
 */
public interface ClusteringAlgorithm {
	
	/**
	 * Creates a set of clusters based on an ordered list of results. The number of clusters
	 * to be created has to be specified.
	 * 
	 * @param items The result items to be used for building the clusters.
	 * @param numberOfClusters The number of clusters that shall be created.
	 * @return The set of clusters.
	 */
	public Set<Cluster> createClusters(List<PreparedResult> items, int numberOfClusters);

}
