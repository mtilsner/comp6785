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

	private static final int MAXIMUM_ITERATIONS 		   = 10;
	private static final double FUZZYNESS				   = 1.0;
	private static final double MINUMUM_ITERATION_PROGRESS = 0.5;
	
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
		Map<Integer,Cluster> _clMapping = new HashMap<Integer,Cluster>();
		for(Map.Entry<Integer,PreparedResult> _centroid: centroids.entrySet()) {
			Cluster _cluster = new SimpleFuzzyKMeansCluster(null, _centroid.getValue().getAllFrequencies());
			_clusters.add(_cluster);
		        _clMapping.put(_centroid.getKey(), _cluster);
		}
		Map<Cluster,Double> _memberships;
		for(PreparedResult _item: items) {
			_memberships = new HashMap<Cluster,Double>();			
			for(Map.Entry<Integer,Double> _mem: memberships.get(_item).entrySet()) {
			      _memberships.put(_clMapping.get(_mem.getKey()),_mem.getValue());
			}
			_clusters = addItemToClusters(_clusters, _item, _memberships);
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
		return iterations > MAXIMUM_ITERATIONS || (oldMemberships != null && getEucledianDistance(memberships,oldMemberships) < MINUMUM_ITERATION_PROGRESS);
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
		Map<PreparedResult,Double> _memberships;
		for(PreparedResult _item: memberships.keySet()) {
			_memberships = calculateMemberships(centroids.values(), _item);
			for(Integer _cluster: centroids.keySet()) {
				memberships.get(_item).put(_cluster, _memberships.get(centroids.get(_cluster)));
			}
		}
	}

	/**
	 * Calculates the memberships of a given item to a specified set of centroids.
	 */ 
	private Map<PreparedResult,Double> calculateMemberships(Collection<PreparedResult> centroids, PreparedResult item) {
		Map<PreparedResult,Double> _distances = new HashMap<PreparedResult,Double>();
		Map<PreparedResult,Double> _memberships = new HashMap<PreparedResult,Double>();
		double _distanceSum = 0.0;
		double _distance;
		double _membershipSum = 0.0;
		double _membership;
		for(PreparedResult _cluster: centroids) {
			_distance = getEucledianDistance(_cluster, item);
			_distances.put(_cluster, _distance);
			_distanceSum += _distance;
		}
		for(PreparedResult _cluster: centroids) {
			_membership = 1-_distances.get(_cluster)/_distanceSum;
			_memberships.put(_cluster, _membership);
			_membershipSum += _membership;
		}
		for(PreparedResult _cluster: centroids) {
			_memberships.put(_cluster,_memberships.get(_cluster)/_membershipSum);
		}
		return _memberships;
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
				_membership     = _item.getValue().get(_cluster)/_item.getKey().getSearchResult().getRank();
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

	/**
	 * Adds a given item to a set of clusters based on the provided membership values.
	 */
	private Collection<Cluster> addItemToClusters(Collection<Cluster> clusters, PreparedResult item, Map<Cluster,Double> memberships) {
		Collection<Cluster> _assignments = new HashSet<Cluster>();
		Cluster		    _maxMember	 = null;
		for(Map.Entry<Cluster,Double> _membership: memberships.entrySet()) {
			if(_membership.getValue() > getSensability(clusters)) _assignments.add(_membership.getKey());
			if(_maxMember == null || memberships.get(_membership.getKey()) < _membership.getValue()) _maxMember = _membership.getKey();
		}
		if(_assignments.isEmpty()) _assignments.add(_maxMember);
		ClusteredResult _result = new SimpleFuzzyKMeansClusteredResult(item, memberships);
		for(Cluster _cluster:_assignments) {
			_cluster.addResult(_result);
		}
		return clusters;
	}

	private double getSensability(Collection<Cluster> clusters) {
		return (1.0 / clusters.size())*FUZZYNESS;
	}
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.ClusteringAlgorithm#addItemToClusters(java.util.Collection, eu.tilsner.cubansea.prepare.PreparedResult)
	 */
	@Override
	public Collection<Cluster> addItemToClusters(Collection<Cluster> clusters, PreparedResult item) {
		Map<PreparedResult,Cluster> _centroids = new HashMap<PreparedResult,Cluster>();
		for(Cluster _cluster: clusters) {
		  _centroids.put(_cluster.getCentroid(),_cluster);
		}
		Map<PreparedResult,Double> _tmemberships = calculateMemberships(_centroids.keySet(), item);
		Map<Cluster,Double>	   _memberships  = new HashMap<Cluster,Double>();
		for(Map.Entry<PreparedResult,Double> _tmembership: _tmemberships.entrySet()) {
		  _memberships.put(_centroids.get(_tmembership.getKey()), _tmembership.getValue());
		}
		clusters = addItemToClusters(clusters, item, _memberships);
		return clusters;
	}
}
