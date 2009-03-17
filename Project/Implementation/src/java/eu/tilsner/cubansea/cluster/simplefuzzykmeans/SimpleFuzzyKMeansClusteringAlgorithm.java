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

/**
 * Simple implementation of a fuzzy k-means clustering algorithm as presented
 * by (Bezdek, 1981). This implementation is not performance optimized, but
 * rather aims to provide easy comprehensability and fast implementation.
 * 
 * @author Matthias Tilsner
 */
public class SimpleFuzzyKMeansClusteringAlgorithm implements ClusteringAlgorithm {

	private static final int MAXIMUM_ITERATIONS = 10;
	private static final double I = 0.75;
	private static final double M = 2.25;
	private static final double E = 0.5;
	
	private Map<PreparedResult,Map<Integer,Double>> oldMemberships;
	private Map<PreparedResult,Map<Integer,Double>> memberships;
	private Map<Integer,PreparedResult>				centroids;
	private Collection<String>						attributes;
	private int										iterations;
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.ClusteringAlgorithm#createClusters(java.util.List, int)
	 */
	@Override
	public Collection<Cluster> createClusters(List<PreparedResult> items, int numberOfClusters) {
		initialize(items, Math.min(numberOfClusters,items.size()));
		while(!satisfied()) iterate();
		Collection<Cluster> _clusters = new HashSet<Cluster>();
		for(PreparedResult _centroid: centroids.values()) {
			Cluster _cluster = new SimpleFuzzyKMeansCluster(null, _centroid.getAllFrequencies());
			_clusters.add(_cluster);
		}
		for(PreparedResult _item: items) {
			_clusters = addItemToClusters(_clusters, _item);
		}
		for(Cluster _cluster: _clusters) {
			_cluster.sort();
		}
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
		return iterations > MAXIMUM_ITERATIONS || (oldMemberships != null && getEucledianDistance(memberships,oldMemberships) < E);
	}
	
	/**
	 * Performs an iteration step.
	 */
	private void iterate() {
		oldMemberships = memberships;
		updateCentroids();
		updateMemberships();
		iterations++;
	}
	
	/**
	 * Updates all memberships based on current centroid values.
	 */
	private void updateMemberships() {
		Map<Integer,Double> _distances;
		double _distanceSum;
		double _distance;
		double _membership;
		for(PreparedResult _item: memberships.keySet()) {
			_distances = new HashMap<Integer,Double>();
			_distanceSum = 0.0;
			for(Map.Entry<Integer,PreparedResult> _cluster: centroids.entrySet()) {
				_distance = getEucledianDistance(_cluster.getValue(), _item);
				_distances.put(_cluster.getKey(), _distance);
				_distanceSum += _distance;
			}
			for(Integer _cluster: centroids.keySet()) {
				_membership = Math.pow(_distances.get(_cluster)*centroids.size()/_distanceSum,(2.0/(1-M)));
				memberships.get(_item).put(_cluster, _membership);
			}
		}
	}

	/**
	 * Updates the centroids of all clusters based on current membership values.
	 */
	private void updateCentroids() {
		Map<String,Double> _centroid;
		double			   _membership;
		double			   _membershipSum;
		double			   _value;
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
					_value  = _centroid.get(_attribute);
					_value += _item.getKey().getFrequency(_attribute)*_membership;
					_centroid.put(_attribute, _value);
				}
			}
			for(String _attribute: attributes) {
				_value  = _centroid.get(_attribute);
				_value /= _membershipSum;
				_centroid.put(_attribute, _value);
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
	public static double getEucledianDistance(PreparedResult item1, PreparedResult item2) {
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
	private static Collection<String> getAllWords(Collection<PreparedResult> items) {
		Collection<String> _words = new HashSet<String>();
		for(PreparedResult _item: items) {
			for(String _word: _item.getWords()) {
				if(!_words.contains(_word)) _words.add(_word);
			}
		}
		return _words;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.ClusteringAlgorithm#addItemToClusters(java.util.Collection, eu.tilsner.cubansea.prepare.PreparedResult)
	 */
	@Override
	public Collection<Cluster> addItemToClusters(Collection<Cluster> clusters, PreparedResult item) {
		Map<Cluster,Double> _distances = new HashMap<Cluster,Double>();
		double _distanceSum = 0.0;
		double _distance;
		double _membership;
		for(Cluster _cluster: clusters) {
			_distance = getEucledianDistance(_cluster.getCentroid(), item);
			_distances.put(_cluster, _distance);
			_distanceSum += _distance;
		}
		Collection<Cluster> _assignments = new HashSet<Cluster>();
		Cluster 			_maxMember	 = null;
		Map<Cluster,Double> _memberships = new HashMap<Cluster,Double>();
		for(Cluster _cluster: clusters) {
			_membership = Math.pow(_distances.get(_cluster)*clusters.size()/_distanceSum,(2.0/(1-M)));
			_memberships.put(_cluster, _membership);
			if(_memberships.get(_cluster) > I) _assignments.add(_cluster);
			if(_maxMember == null || _memberships.get(_maxMember) < _membership) _maxMember = _cluster;
		}
		if(_assignments.isEmpty()) _assignments.add(_maxMember);

		ClusteredResult _result = new SimpleFuzzyKMeansClusteredResult(item, _memberships);
		for(Cluster _cluster: _assignments) {
			_cluster.addResult(_result);
		}
		
		return clusters;
	}
}
