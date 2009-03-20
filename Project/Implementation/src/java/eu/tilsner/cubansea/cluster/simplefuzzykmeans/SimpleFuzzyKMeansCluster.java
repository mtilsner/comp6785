package eu.tilsner.cubansea.cluster.simplefuzzykmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
	private Map<String,Double>		centroidAttributes;
	private Comparator<ClusteredResult> comparator;
	
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
		return new CentroidPreparedResult(centroidAttributes);
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.Cluster#getResults()
	 */
	@Override
	public List<ClusteredResult> getResults() {
		sort();
		return results;
	}
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.Cluster#getMaximumRelevance()
	 */
	@Override
	public double getMaximumRelevance() {
		sort();
		return results.get(0).getAbsoluteRelevance(this);
	}

	/**
	 * Sorts the current result set using the provided sorting algorithm.
	 */
	private void sort() {
		Collections.sort(results, comparator);
	}

	/**
	 * Constructor for SimpleFuzzyKMeansCluster requiring all attributes as parameters.
	 * A new constructor is created that can be used later on by the sorting algorithm.
	 * 
	 * @param _results The initial results for this cluster.
	 * @param _attributes
	 */
	public SimpleFuzzyKMeansCluster(List<ClusteredResult> _results, Map<String,Double> _attributes) {
		centroidAttributes = _attributes;
		results = (_results == null) ? new ArrayList<ClusteredResult>() : _results;
		if(results == null) return;
		final Cluster _cluster = this;
		comparator = new Comparator<ClusteredResult>(){
			@Override
			public int compare(ClusteredResult item1, ClusteredResult item2) {
				return (int) ((item2.getAbsoluteRelevance(_cluster) - item2.getAbsoluteRelevance(_cluster))*5.0);
			}
		};
	}
}
