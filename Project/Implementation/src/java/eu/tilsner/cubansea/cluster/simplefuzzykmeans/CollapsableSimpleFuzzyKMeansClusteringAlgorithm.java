package eu.tilsner.cubansea.cluster.simplefuzzykmeans;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.tilsner.cubansea.cluster.Cluster;
import eu.tilsner.cubansea.prepare.PreparedResult;

public class CollapsableSimpleFuzzyKMeansClusteringAlgorithm extends SimpleFuzzyKMeansClusteringAlgorithm {

	static Logger logger = Logger.getLogger(CollapsableSimpleFuzzyKMeansClusteringAlgorithm.class.getName());

	public static final double MIN_DISTANCE = 0.0001;
	
	@Override
	protected Collection<Cluster> processClusterBase(Collection<Cluster> _clusters) {
		while(collapseNextCluster(_clusters)) {
			logger.debug("ClusterBase: "+_clusters.size());
		}
		return _clusters;
	}
	
	private boolean collapseNextCluster(Collection<Cluster> _clusters) {
		for(Cluster _base : _clusters) {
			for(Cluster _compare : _clusters) {
				if(_base != _compare && getEucledianDistance(_base.getCentroid(),_compare.getCentroid()) < MIN_DISTANCE) {
					logger.debug("Collapsed clusters with distance "+getEucledianDistance(_base.getCentroid(),_compare.getCentroid()));
					_base.getCentroid().setAllFrequencies(getMiddle(_base.getCentroid(),_compare.getCentroid()));
					_clusters.remove(_compare);
					return true;
				} else {
					logger.debug("Did not collapse clusters with distance "+getEucledianDistance(_base.getCentroid(),_compare.getCentroid()));
				}
			}
		}
		return false;
	}
	
	private PreparedResult getMiddle(PreparedResult _item1, PreparedResult _item2) {
		Map<String,Double> _centroid = new HashMap<String,Double>();
		Collection<PreparedResult> _clusters = new HashSet<PreparedResult>();
		_clusters.add(_item1);
		_clusters.add(_item2);
		for(String _attribute: SimpleFuzzyKMeansClusteringAlgorithm.getAllWords(_clusters)) {
			_centroid.put(_attribute, (_item1.getFrequency(_attribute)+_item2.getFrequency(_attribute))/2.0);
		}
		return new CentroidPreparedResult(_centroid);
	}
}
