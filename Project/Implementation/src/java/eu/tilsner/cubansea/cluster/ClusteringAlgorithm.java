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
	 * Generates a new clustered result based on a prepared result and a collection of
	 * clusters.
	 * 
	 * @param clusters The clusters.
	 * @param item The item for which the relevances shall be determined.
	 * @return A map of the relevances assigned to the provided clusters. 
	 */
	public ClusteredResult createClusteredResult(Collection<Cluster> clusters, PreparedResult item);
	
	/**
	 * Determins which clusters are relevant for a specific item. The item
	 * will then be added to those.
	 * 
	 * @param clusters The clusters to be considered.
	 * @param item The item.
	 * @return The clusters relevant for the item.
	 */
	public Collection<Cluster> determineRelevantClusters(Collection<Cluster> clusters, ClusteredResult item);
}
