package eu.tilsner.cubansea.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.tilsner.cubansea.cluster.ClusteredResult;
import eu.tilsner.cubansea.cluster.ClusteringAlgorithm;
import eu.tilsner.cubansea.prepare.PreparationAlgorithm;
import eu.tilsner.cubansea.prepare.PreparedResult;
import eu.tilsner.cubansea.search.SearchEngineException;
import eu.tilsner.cubansea.search.SearchResult;
import eu.tilsner.cubansea.utilities.TechnicalError;

/**
 * Primary facade object that can be used when dealing with the cubansea API.
 * It creates the different clusters and will be used by them to retrieve
 * additional results later on. It depends on a given configuration.
 * 
 * @author Matthias Tilsner
 */
public class Search {
	private	Configuration 						configuration;
	private	eu.tilsner.cubansea.search.Search	search;
	private	int									cacheStatus;
	private	PreparationAlgorithm				prepAlgorithm;
	private	ClusteringAlgorithm					clusterAlgorithm;
	private	Map<eu.tilsner.cubansea.cluster.Cluster,Cluster> clusters;
	
	/**
	 * Fetches the next chunk of results from the search engine and transforms the result
	 * into clustered results.
	 * 
	 * @return An ordered list of clustered results. Ordered in the same way as retrieved from
	 * 		   the search engine.
	 * @throws NoMoreResultsException The end of the result list has been reached. No additional
	 * 		   						  results can be fetched.
	 */
	private List<ClusteredResult> performFetch() throws NoMoreResultsException {
		if(cacheStatus >= search.getResultCount()) throw new NoMoreResultsException("all results fetched");
		List<SearchResult> _searchResults 			= search.getResults(cacheStatus, 
																		Math.min(configuration.getBlockSize(),search.getResultCount()));
		List<PreparedResult> _preparedResults		= prepAlgorithm.prepareResults(_searchResults);
		List<ClusteredResult> _clusteredResults 	= new ArrayList<ClusteredResult>();
		for(PreparedResult _result: _preparedResults) {
			_clusteredResults.add(clusterAlgorithm.createClusteredResult(clusters.keySet(), _result));
		}
		cacheStatus += _clusteredResults.size();
		return _clusteredResults;
	}
	
	/**
	 * Wrapper for the performFetch method. It can be invoked by a cluster in order to request
	 * additional results.
	 * 
	 * @throws NoMoreResultsException The end of the result list has been reached.
	 */
	protected void fetchNextBlock() throws NoMoreResultsException {
		List<ClusteredResult> _results = performFetch();
		Map<Cluster,List<ClusteredResult>> _assignments = new HashMap<Cluster,List<ClusteredResult>>();
		for(ClusteredResult _result: _results) {
			for(eu.tilsner.cubansea.cluster.Cluster _cluster: clusterAlgorithm.determineRelevantClusters(clusters.keySet(), _result)) {
				if(_assignments.get(clusters.get(_cluster)) == null) 
					_assignments.put(clusters.get(_cluster), new ArrayList<ClusteredResult>());
				_assignments.get(clusters.get(_cluster)).add(_result);
			}
		}
		for(Map.Entry<Cluster,List<ClusteredResult>> _entry: _assignments.entrySet()) {
			_entry.getKey().addResults(_entry.getValue());
		}
	}
	
	/**
	 * Returns the clusters generated by this search.
	 * 
	 * @return The clusters.
	 */
	public Collection<Cluster> getClusters() {
		return clusters.values();
	}
	
	/**
	 * Generates a new search.
	 * 
	 * @param terms The search terms that shall be used when searching the web.
	 * @param config The configuration for this search.
	 */
	public Search(List<String> terms, Configuration config) {
		try {
			clusters			= new HashMap<eu.tilsner.cubansea.cluster.Cluster,Cluster>();
			configuration		= config;
			prepAlgorithm		= config.getPrepareAlgorithm();
			clusterAlgorithm	= config.getClusteringAlgorithm();
			cacheStatus			= config.getClusteringBase();
			search				= config.getSearchEngine().createSearch(terms, cacheStatus);
			List<SearchResult> _sres	= search.getResults(0, cacheStatus);
			List<PreparedResult> _pres	= prepAlgorithm.prepareResults(_sres);
			Collection<eu.tilsner.cubansea.cluster.Cluster> _clusters = 
				clusterAlgorithm.createClusters(_pres, config.getNumberOfClusters());
			int _index = 0;
			for(eu.tilsner.cubansea.cluster.Cluster _cluster: _clusters) {
				clusters.put(_cluster,new Cluster(_cluster, config.getClusterColors().get(_index++), this));
			}
		} catch (SearchEngineException e) {
			throw new TechnicalError(e);
		}
	}
}
