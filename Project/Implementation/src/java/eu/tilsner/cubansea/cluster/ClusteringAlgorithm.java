package eu.tilsner.cubansea.cluster;

import java.util.Collection;
import java.util.List;

import eu.tilsner.cubansea.prepare.PreparedResult;

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
	public Collection<Cluster> createClusters(List<PreparedResult> items, int numberOfClusters);

	
	/**
	 * Calculates the relevances of an item to a collection of cluster centroids.
	 * This method is required when adding additional results to an already existing
	 * 
	 * @param centroids The centroids of a collection of clusters.
	 * @param item The item for which the relevances shall be determined.
	 * @return A map of the relevances assigned to the specific centroids. 
	 */
	public Collection<Cluster> addItemToClusters(Collection<Cluster> clusters, PreparedResult item);

}
