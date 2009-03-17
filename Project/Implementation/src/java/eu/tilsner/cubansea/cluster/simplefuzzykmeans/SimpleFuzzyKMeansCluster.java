package eu.tilsner.cubansea.cluster.simplefuzzykmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.tilsner.cubansea.cluster.Cluster;
import eu.tilsner.cubansea.cluster.ClusteredResult;
import eu.tilsner.cubansea.prepare.PreparedResult;
import eu.tilsner.cubansea.utilities.StringHelper;

/**
 * Simple cluster implementation for the Fuzzy-K-Means algorithm.
 * Requires all attributes upon creation.
 * 
 * @author Matthias Tilsner
 */
public class SimpleFuzzyKMeansCluster implements Cluster {

	private List<ClusteredResult> results;
	private Map<String,Double>	  centroidAttributes;
	
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
	 * @see eu.tilsner.cubansea.cluster.Cluster#getResultCount()
	 */
	@Override
	public int getResultCount() {
		return results.size();
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.Cluster#getResults(int, int)
	 */
	@Override
	public List<ClusteredResult> getResults(int first, int count) {
		return results.subList(first, first+count);
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.Cluster#getTopic()
	 */
	@Override
	public String getTopic() {
		return getTopic(null);
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.Cluster#getTopic(java.util.List)
	 */
	@Override
	public String getTopic(List<String> searchTerms) {
		Map<String,Double> _centroidAttributes = new HashMap<String,Double>();
		_centroidAttributes.putAll(centroidAttributes);
		if(searchTerms != null) {
			for(String term: searchTerms) _centroidAttributes.remove(term);
		}
		List<Map.Entry<String,Double>> _attributes = Collections.<Map.Entry<String,Double>>list(Collections.enumeration(_centroidAttributes.entrySet())); 
		Collections.sort(_attributes, new Comparator<Map.Entry<String,Double>>(){
			@Override
			public int compare(Map.Entry<String, Double> item1, Map.Entry<String, Double> item2) {
				return (int) (item2.getValue() - item1.getValue())*5;
			}
			
		});
		int _last = Math.min(3, _attributes.size());
		List<String> _keyWords = new ArrayList<String>();
		for(Map.Entry<String,Double> _attribute: _attributes.subList(0, _last)) {
			_keyWords.add(_attribute.getKey());
		}
		
		return StringHelper.join(_keyWords, " ");		
	}
	
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.cluster.Cluster#sort()
	 */
	@Override
	public void sort() {
		if(results == null) return;
		final Cluster _cluster = this;
		Collections.sort(results, new Comparator<ClusteredResult>(){
			@Override
			public int compare(ClusteredResult item1, ClusteredResult item2) {
				return (int) (item2.getRelevance(_cluster) - item2.getRelevance(_cluster))*5;
			}
		});
	}

	/**
	 * Constructor for SimpleFuzzyKMeansCluster requiring all attributes as parameters.
	 * 
	 * @param _results The initial results for this cluster.
	 * @param _attributes
	 */
	public SimpleFuzzyKMeansCluster(List<ClusteredResult> _results, Map<String,Double> _attributes) {
		centroidAttributes = _attributes;
		results = _results;
		sort();
	}
}
