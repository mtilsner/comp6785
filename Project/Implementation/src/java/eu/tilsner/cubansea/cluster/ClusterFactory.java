package eu.tilsner.cubansea.cluster;

import java.util.List;
import java.util.Set;

import eu.tilsner.cubansea.search.Search;
import eu.tilsner.cubansea.search.SearchEngine;
import eu.tilsner.cubansea.search.SearchEngineException;
import eu.tilsner.cubansea.search.SearchResult;
import eu.tilsner.cubansea.utilities.TechnicalError;

/**
 * Factory for building clusters. Different factories might exist using
 * different algorithms.
 * 
 * @author Matthias Tilsner
 *
 */
public abstract class ClusterFactory {
	/**
	 * General logic on how clusters should be created.
	 * 
	 * @param terms The terms that shall be used when querying the search engine.
	 * @param searchEngine The search engine that shall be used.
	 * @param numberOfClusters The number of clusters that shall be created.
	 * @param maximumRank The rank of the last result to consider.
	 * @return The clusters containing the first <i>count</i> items.
	 */
	public Set<Cluster> buildClusters(List<String> terms, SearchEngine searchEngine, int numberOfClusters, int maximumRank) {
		try {
			Search _search = searchEngine.createSearch(terms, maximumRank);
			List<SearchResult> searchResults = _search.getResults(0, maximumRank);
			List<PreparedResult> _preparedResults = getPreparationAlgorithm().prepareResults(searchResults);
			return getClusteringAlgorithm().createClusters(_preparedResults, numberOfClusters);
		} catch (SearchEngineException e) {
			throw new TechnicalError(e);
		}
	}
	
	/**
	 * Returns the algorithm to be used for preparing the results. This method must be implemented in
	 * the concrete implementation class.
	 * 
	 * @return The algorithm.
	 */
	protected abstract PreparationAlgorithm getPreparationAlgorithm();
	
	/**
	 * Returns the algorithm to be used for creating the clusters. This method must be implemented in
	 * the concrete implementation class.
	 * 
	 * @return The algorithm.
	 */
	protected abstract ClusteringAlgorithm getClusteringAlgorithm();
}
