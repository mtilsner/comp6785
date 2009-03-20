package eu.tilsner.cubansea.cluster.simplefuzzykmeans;

import java.util.Map;

import eu.tilsner.cubansea.cluster.Cluster;
import eu.tilsner.cubansea.cluster.ClusteredResult;
import eu.tilsner.cubansea.prepare.PreparedResult;

/**
 * A small implementation for the clusterd results inside the simple-fuzzy-k-means
 * algorithm implementation. It requires all parameters upon creation. 
 * 
 * @author Matthias Tilsner
 */
public class SimpleFuzzyKMeansClusteredResult implements ClusteredResult {

	private PreparedResult preparedResult;
	private Map<Cluster,Double> relevances;
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.ClusteredResult#getPreparedResult()
	 */
	@Override
	public PreparedResult getPreparedResult() {
		return preparedResult;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.ClusteredResult#getAbsoluteRelevance(eu.tilsner.cubansea.cluster.Cluster)
	 */
	@Override
	public double getAbsoluteRelevance(Cluster cluster) {
		return relevances.get(cluster);
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.ClusteredResult#getRelevance(eu.tilsner.cubansea.cluster.Cluster)
	 */
	@Override
	public double getRelevance(Cluster cluster) {
		return getAbsoluteRelevance(cluster) / cluster.getMaximumRelevance();
	}

	/**
	 * Constructor for SimpleFuzzyKMeansClusteredResult beans.
	 * Requires all attributes as parameters.
	 * 
	 * @param _preparedResult The prepared result this clusered result wraps.
	 * @param _relevances A map of the relevances to each cluster.
	 */
	public SimpleFuzzyKMeansClusteredResult(PreparedResult _preparedResult, Map<Cluster,Double> _relevances) {
		preparedResult	= _preparedResult;
		relevances		= _relevances;
	}
}
