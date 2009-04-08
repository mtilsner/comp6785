package eu.tilsner.cubansea.cluster.simplefuzzykmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.tilsner.cubansea.cluster.Cluster;
import eu.tilsner.cubansea.cluster.ClusteredResult;
import eu.tilsner.cubansea.prepare.PreparedResult;

/**
 * Simple cluster implementation for the Fuzzy-K-Means algorithm.
 * Requires all attributes upon creation.
 * 
 * @author Matthias Tilsner
 */
public class SimpleFuzzyKMeansCluster implements Cluster {

	private List<ClusteredResult>	results;
	private PreparedResult			centroid;
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.Cluster#addResult(eu.tilsner.cubansea.cluster.ClusteredResult)
	 */
	@Override
	public void addResult(ClusteredResult result) {
		results.add(result);
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.Cluster#getCentroid()
	 */
	@Override
	public PreparedResult getCentroid() {
		return centroid;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.Cluster#getResults()
	 */
	@Override
	public List<ClusteredResult> getResults() {
		return results;
	}
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.Cluster#getMaximumRelevance()
	 */
	@Override
	public double getMaximumRelevance() {
		return (results.size() == 0) ? 1.0 : results.get(0).getAbsoluteRelevance(this);
	}

	/**
	 * Sorts the current result set using the provided sorting algorithm.
	 */
	public void sort() {
		final Cluster _cluster = this;
		Collections.sort(results, new Comparator<ClusteredResult>(){
			@Override
			public int compare(ClusteredResult item1, ClusteredResult item2) {
				double _rel2 = item2.getAbsoluteRelevance(_cluster);
				double _rel1 = item1.getAbsoluteRelevance(_cluster);
				return (_rel2 > _rel1) ? 1 : ((_rel2 < _rel1) ? -1 : 0);
			}
		});
	}

	/**
	 * Constructor for SimpleFuzzyKMeansCluster requiring all attributes as parameters.
	 * A new constructor is created that can be used later on by the sorting algorithm.
	 * 
	 * @param _results The initial results for this cluster.
	 * @param _centroid The centroid of the cluster.
	 */
	public SimpleFuzzyKMeansCluster(List<ClusteredResult> _results, PreparedResult _centroid) {
		centroid = _centroid;
		results = (_results == null) ? new ArrayList<ClusteredResult>() : _results;
	}
}
