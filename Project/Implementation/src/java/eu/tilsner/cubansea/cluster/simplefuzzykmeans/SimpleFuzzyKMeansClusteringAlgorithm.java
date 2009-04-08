package eu.tilsner.cubansea.cluster.simplefuzzykmeans;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.tilsner.cubansea.cluster.Cluster;
import eu.tilsner.cubansea.cluster.ClusteredResult;
import eu.tilsner.cubansea.cluster.ClusteringAlgorithm;
import eu.tilsner.cubansea.prepare.PreparedResult;

import org.apache.log4j.Logger;

/**
 * Simple implementation of a fuzzy k-means clustering algorithm as presented
 * by (Bezdek, 1981). This implementation is not performance optimized, but
 * rather aims to provide easy comprehensability and fast implementation.
 * 
 * @author Matthias Tilsner
 */
public class SimpleFuzzyKMeansClusteringAlgorithm implements ClusteringAlgorithm {

	static Logger logger = Logger.getLogger(SimpleFuzzyKMeansClusteringAlgorithm.class.getName());
	
	private static final int MAXIMUM_ITERATIONS 		   = 250;
	private static final double MINUMUM_ITERATION_PROGRESS = 0.001;
	private static final double FUZZYFIER				   = 1.5;
	
	private Map<PreparedResult,Map<Integer,Double>> oldMemberships;
	private Map<PreparedResult,Map<Integer,Double>> memberships;
	private Map<Integer,PreparedResult>				centroids;
	private Collection<String>						attributes;
	private int										iterations;
	private double 									sensitivity;
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.ClusteringAlgorithm#createClusters(java.util.List, int)
	 */
	@Override
	public Collection<Cluster> createClusters(List<PreparedResult> items, int numberOfClusters) {
		initialize(items, Math.min(numberOfClusters,items.size()));
		while(!satisfied()) iterate();
		Collection<Cluster> _clusters = new HashSet<Cluster>();
		for(Map.Entry<Integer,PreparedResult> _centroid: centroids.entrySet()) {
			Cluster _cluster = new SimpleFuzzyKMeansCluster(null, _centroid.getValue());
			_clusters.add(_cluster);
		}
		_clusters = processClusterBase(_clusters);
		for(PreparedResult _item: items) {
			_clusters = addItemToClusters(_clusters, _item);
		}
		for(Cluster _cluster: _clusters) {
			((SimpleFuzzyKMeansCluster) _cluster).sort();
		}
		return _clusters;
	}

	/**
	 * Small helper function that allows the preprocessing of clusters before adding the
	 * results to them.
	 * 
	 * @param _clusters The original cluster base.
	 * @return The resulting cluster base.
	 */
	protected Collection<Cluster> processClusterBase(Collection<Cluster> _clusters) {
		return _clusters;
	}
	
	/**
	 * Initializes the search algorithm. The first centroids are defined
	 * and the starting memberships calculated.
	 * 
	 * @param items The items that shall be clustered.
	 * @param numberOfClusters The number of clusters that shall be created.
	 */
	private void initialize(List<PreparedResult> items, int numberOfClusters) {
		iterations = 0;
		oldMemberships = null;
		memberships = new HashMap<PreparedResult,Map<Integer,Double>>();
		for(PreparedResult _item: items) {
			memberships.put(_item, new HashMap<Integer,Double>());
		}
		centroids = new HashMap<Integer,PreparedResult>();
		for(int i=0;i<numberOfClusters;i++) {
			centroids.put(i,items.get(i));
		}
		attributes = getAllWords(items);
		updateMemberships();
	}
	
	/**
	 * Determines if the last iteration step was successful in reaching
	 * the required termination criteria. This is either reached when
	 * the change in membership evaluated by this iteration reaches a
	 * minimum or after a certain set of iterations.
	 * 
	 * @return
	 */
	private boolean satisfied() {
		return iterations > MAXIMUM_ITERATIONS || (oldMemberships != null && getEucledianDistance(memberships,oldMemberships) < MINUMUM_ITERATION_PROGRESS);
	}
	
	/**
	 * Performs an iteration step.
	 */
	private void iterate() {
		Map<Integer,Double> _mems;
		oldMemberships = new HashMap<PreparedResult,Map<Integer,Double>>();
		for(PreparedResult _result: memberships.keySet()) {
			_mems = new HashMap<Integer,Double>();
			_mems.putAll(memberships.get(_result));
			oldMemberships.put(_result, _mems);
		}
		updateCentroids();
		updateMemberships();
		iterations++;
		logger.debug("performed iteration "+iterations+": change so far was "+getEucledianDistance(memberships,oldMemberships));
	}
	
	/**
	 * Updates all memberships based on current centroid values.
	 */
	private void updateMemberships() {
		Map<PreparedResult,Double> _memberships;
		for(PreparedResult _item: memberships.keySet()) {
			_memberships = calculateMemberships(centroids.values(), _item);
			for(Integer _cluster: centroids.keySet()) {
				memberships.get(_item).put(_cluster, _memberships.get(centroids.get(_cluster)));
			}
		}
	}

	/**
	 * Calculates the relevances of an item to a collection of cluster centroids.
	 * This method is required when adding additional results to an already existing
	 * 
	 * @param centroids The centroids of a collection of clusters.
	 * @param item The item for which the relevances shall be determined.
	 * @return A map of the relevances assigned to the specific centroids. 
	 */
	private Map<PreparedResult,Double> calculateMemberships(Collection<PreparedResult> centroids, PreparedResult item) {
		Map<PreparedResult,Double> _distances = new HashMap<PreparedResult,Double>();
		for(PreparedResult _cluster: centroids) {
			_distances.put(_cluster, Math.max(getEucledianDistance(_cluster, item),0.0000000001));
			//_distances.put(_cluster, getEucledianDistance(_cluster, item));
		}

		Map<PreparedResult,Double> _memberships = new HashMap<PreparedResult,Double>();
		double _sum;
		for(PreparedResult _cluster: centroids) {
			_sum = 0.0;
			for(PreparedResult _clusterJ: centroids) {
				_sum += Math.pow(_distances.get(_cluster)/_distances.get(_clusterJ),
								 2/(FUZZYFIER-1)); 
			}
			_memberships.put(_cluster, 1/_sum);
		}
		return _memberships;
	}

	/**
	 * Updates the centroids of all clusters based on current membership values.
	 */
	private void updateCentroids() {
		Map<String,Double> _centroid;
		Double			   _membership;
		double			   _membershipSum;
		for(Integer _cluster: centroids.keySet()) {
			_centroid = new HashMap<String,Double>();
			for(String _attribute: attributes) {
				_centroid.put(_attribute, 0.0);
			}
			_membershipSum = 0;
			for(Map.Entry<PreparedResult,Map<Integer,Double>> _item: memberships.entrySet()) {
				_membership     = _item.getValue().get(_cluster);
				_membershipSum += _membership;
				for(String _attribute: attributes) {
					_centroid.put(_attribute, _centroid.get(_attribute) +
											  _item.getKey().getFrequency(_attribute)*_membership);
				}
			}
			for(String _attribute: attributes) {
				_centroid.put(_attribute, _centroid.get(_attribute) / _membershipSum);
			}
			centroids.put(_cluster, new CentroidPreparedResult(_centroid));
		}
	}
	
	/**
	 * Calculates the eucledian distance between two membership maps.
	 * It uses the sum of all cluster memberships for calculation.
	 * 
	 * @param items1 First item for comparison.
	 * @param items2 Second item for comparison.
	 * @return Eucledian distance between the two items.
	 */
	private double getEucledianDistance(Map<PreparedResult,Map<Integer,Double>> items1, Map<PreparedResult,Map<Integer,Double>> items2) {
		double _distance = 0.0;
		for(PreparedResult _item: memberships.keySet()) {
			Map<Integer,Double> _item1 = items1.get(_item);
			Map<Integer,Double> _item2 = items2.get(_item);
			for(Integer _cluster: centroids.keySet()) {
				double _value1 = (_item1 != null && _item1.containsKey(_cluster)) ? _item1.get(_cluster) : 0.0;
				double _value2 = (_item2 != null && _item2.containsKey(_cluster)) ? _item2.get(_cluster) : 0.0;
				_distance += Math.pow(_value1 - _value2, 2);
			}
		}
		return Math.sqrt(_distance);
	}
	
	/**
	 * Calculates the distance between two prepared result items. The
	 * word frequencies are used as numeric attributes.
	 * 
	 * @param item1 First item for comparison.
	 * @param item2 Second item for comparison.
	 * @return Eucledian distance between the two items.
	 */
	protected double getEucledianDistance(PreparedResult item1, PreparedResult item2) {
		double distance = 0.0;
		Set<PreparedResult> items = new HashSet<PreparedResult>();
		items.add(item1);
		items.add(item2);
		Collection<String> attributes = getAllWords(items);
		for(String _attribute: attributes) {
			distance += Math.pow(item1.getFrequency(_attribute) - item2.getFrequency(_attribute), 2);
		}
		return Math.sqrt(distance);
	}
	
	/**
	 * Extracts the union of all words provided by the result item list.
	 * 
	 * @param items Items from which the words shall be taken.
	 * @return Union of all occurring word sets.
	 */
	protected static Collection<String> getAllWords(Collection<PreparedResult> items) {
		Collection<String> _words = new HashSet<String>();
		for(PreparedResult _item: items) {
			for(String _word: _item.getWords()) {
				if(!_words.contains(_word)) _words.add(_word);
			}
		}
		return _words;
	}

	/**
	 * Adds a given item to a set of clusters based on the provided membership values.
	 */
	private Collection<Cluster> addItemToClusters(Collection<Cluster> clusters, PreparedResult item) {
		ClusteredResult _result 		 = createClusteredResult(clusters, item);
		Collection<Cluster> _assignments = determineRelevantClusters(clusters, _result);
		for(Cluster _cluster:_assignments) {
			_cluster.addResult(_result);
		}
		return clusters;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.ClusteringAlgorithm#determineRelevantClusters(java.util.Collection, eu.tilsner.cubansea.cluster.ClusteredResult)
	 */
	@Override
	public Collection<Cluster> determineRelevantClusters(Collection<Cluster> clusters, ClusteredResult item) {
		Collection<PreparedResult> _centroids = new HashSet<PreparedResult>();
		for(Cluster _cluster: clusters) {
			_centroids.add(_cluster.getCentroid());
		}
		Map<PreparedResult,Double> _mems = calculateMemberships(_centroids, item.getPreparedResult());
		Collection<Cluster> _assignments = new HashSet<Cluster>();
		Cluster		    	  _maxMember = null;
		double				 _membership = 0.0;
		for(Cluster _cluster: clusters) {
			_membership = _mems.get(_cluster.getCentroid());
			if(_membership > getSensability(clusters)) _assignments.add(_cluster);
			if(_maxMember == null || _mems.get(_maxMember.getCentroid()) < _membership) _maxMember = _cluster;
		}
		if(_assignments.isEmpty()) _assignments.add(_maxMember);
		return _assignments;
	}
	
	/**
	 * Evaluates the sensability for a set of clusters.
	 * 
	 * @param clusters The collection of clusters.
	 * @return The sensability.
	 */
	private double getSensability(Collection<Cluster> clusters) {
		return (1.0 / clusters.size())*sensitivity;
	}
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.ClusteringAlgorithm#createClusteredResult(java.util.Collection, eu.tilsner.cubansea.prepare.PreparedResult)
	 */
	@Override
	public ClusteredResult createClusteredResult(Collection<Cluster> clusters, PreparedResult item) {
		Map<Cluster,Double> _relevances = new HashMap<Cluster,Double>();
		for(Cluster _cluster: clusters) {
			_relevances.put(_cluster, getEucledianDistance(_cluster.getCentroid(),item));
		}
		return new SimpleFuzzyKMeansClusteredResult(item, _relevances);
	}

	/**
	 * Default constructor initializing the sensitivity with 1.
	 */
	public SimpleFuzzyKMeansClusteringAlgorithm() {
		this(1.0);
	}

	/**
	 * Constructor allow an individual specification of the sensitivity.
	 * 
	 * @param _sensitivity The sensitivity to use.
	 */
	public SimpleFuzzyKMeansClusteringAlgorithm(double _sensitivity) {
		sensitivity = _sensitivity;		
	}
	
	public Map<PreparedResult,Map<Integer,Double>> getMemberships() {
		return memberships;
	}
	
	public Map<Integer,PreparedResult> getCentroids() {
		return centroids;
	}
}
