package eu.tilsner.cubansea.api;

import java.awt.Color;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.tilsner.cubansea.cluster.ClusteredResult;
import eu.tilsner.cubansea.utilities.StemmerHelper;
import eu.tilsner.cubansea.utilities.StringHelper;

/**
 * Facade cluster class that handles all result access. Facade clusters are created
 * by the facade search and wrap a specific cluster.Cluster plus additional results
 * discovered later on. It provides a major function getResults(start, end). This
 * function returns results from the cache. If the cache is not sufficient, it asks
 * the search for additional results.
 * 
 * @author Matthias Tilsner
 */
public class Cluster {
	
	private eu.tilsner.cubansea.cluster.Cluster cluster;
	
	private Color 			color;
	private List<Result>	results;
	private Search			search;
	private int				topicSize;
	private String			topic;
	
	/**
	 * Returns the base color for this cluster. Each cluster has one
	 * distinct base color that allows easy identification of items
	 * belonging to it.
	 * 
	 * @return The base color.
	 */
	public Color getBaseColor() {
		return color;
	}
	
	/**
	 * Primary method for this cluster. It allows the retrieval of search results by passing
	 * the first and last result. In case the results are already fetched, it provides them
	 * from the cache. If not, the search will be asked to provide additional results.
	 * 
	 * @param first The index (=rank-1) of the first item to be returned.
	 * @param count The number of items that shall be returned.
	 * @return An ordered list of the items returned from the search engine.
	 */
	public List<Result> getResults(int first, int count) {
		if(count < 0) throw new InvalidParameterException("You cannot request 0 or less results");
		while(results.size() < first+count) {
			try {
				search.fetchNextBlock();
			} catch (NoMoreResultsException e) {
				break;
			}
		}
		int _last  = Math.min(first+count,results.size());
		int _first = Math.min(first, _last);
		return results.subList(_first,_last);
	}
	
	/**
	 * Returns the stems of the centroid attributes ordered by
	 * frequency
	 * 
	 * @return An ordered list of the centroid stems.
	 */
	private List<String> getFrequencyOrderedStems() {
		List<Map.Entry<String, Double>> _frequencies = new ArrayList<Map.Entry<String, Double>>();
		_frequencies.addAll(cluster.getCentroid().getAllFrequencies().entrySet());
		Collections.sort(_frequencies, new Comparator<Map.Entry<String,Double>>(){
			@Override
			public int compare(Entry<String, Double> _item1,Entry<String, Double> _item2){
				return (int) ((_item2.getValue() - _item1.getValue())*5);
			}
		});
		List<String> _stems = new ArrayList<String>();
		for(Map.Entry<String,Double> _frequency: _frequencies) {
			_stems.add(StemmerHelper.stem(_frequency.getKey()));
		}
		return _stems;
	}
	
	/**
	 * Determines the most frequently used word in the result
	 * items that leads to the specified stem.
	 * 
	 * @param stem The stem for which the words shall be examined.
	 * @return The word most commonly used leading to the stem.
	 */
	private String getMostCommonStemUtilization(String stem) {
		Map<String,Integer> _frequencies = new HashMap<String,Integer>();
		String _stem;
		for(Result _result: results) {
			for(String _word: StringHelper.split(_result.getSummary()," ")) {
				_stem = StemmerHelper.stem(_word);
				if(stem.equals(_stem)) {
					if(!_frequencies.containsKey(_word)) 
						_frequencies.put(_word, 1);
					else
						_frequencies.put(_word, _frequencies.get(_word)+1);
				}
			}
		}
		List<Map.Entry<String, Integer>> _freqs = new ArrayList<Map.Entry<String, Integer>>();
		_freqs.addAll(_frequencies.entrySet());
		Collections.sort(_freqs, new Comparator<Map.Entry<String,Integer>>(){
			@Override
			public int compare(Entry<String, Integer> _item1,Entry<String, Integer> _item2){
				return _item2.getValue() - _item1.getValue();
			}
		});
		return _freqs.get(0).getKey();
	}
	
	/**
	 * Returns the topic of the cluster. If the topic has not yet
	 * been determined, it is created. 
	 * 
	 * @return The topic.
	 */
	public String getTopic() {
		if(topic == null) {
			List<String> _searchStems = new ArrayList<String>();
			for(String _term: search.getTerms()) {
				_searchStems.add(StemmerHelper.stem(_term));
			}
			List<String> _stems = new ArrayList<String>();
			for(String _stem: getFrequencyOrderedStems()) {
				if(!_searchStems.contains(_stem)) _stems.add(_stem);
				if(_stems.size() >= topicSize) break;
			}
			List<String> _words = new ArrayList<String>();
			for(String _stem: _stems) {
				_words.add(getMostCommonStemUtilization(_stem));
			}
			topic = StringHelper.join(_words, " ");
		}
		return topic;
	}
	
	/**
	 * Wrapper method called by the search to add additional results to the
	 * result list. The results are provided as ClusteredResults and must be
	 * converted into regular results.
	 * 
	 * @param newResults The new results that have to be added.
	 */
	protected void addResults(List<ClusteredResult> newResults) {
		for(ClusteredResult _result: newResults) {
			results.add(new Result(_result));
		}
	}
	
	/**
	 * Getter for the wrapped cluster.Cluster. Required by the general results for
	 * retrieving the relevance data.
	 * 
	 * @return The wrapped cluster.Cluster.
	 */
	protected eu.tilsner.cubansea.cluster.Cluster getCluster() {
		return cluster;
	}
	
	/**
	 * Returns the rank of a specific result in the result list. This method
	 * is protected, since outside code should use the {@link Result#getRank(Cluster)}
	 * method instead. 
	 * 
	 * @param result The result whose rank is to be determined.
	 * @return The rank of the result in the cluster (starting with 1).
	 */
	protected int getRank(Result result) {
		return results.indexOf(result)+1;
	}
	
	/**
	 * Constructor for the facade cluster. It requires the wrapped cluster.Cluster, the color
	 * it shall use, and a reference to the original search as parameters. The cluster is
	 * dependent on those parameters. 
	 * 
	 * @param _cluster The cluster.Cluster to be wrapped. 
	 * @param _color The color of this cluster.
	 * @param _search The search that can be used for retrieving additional results.
	 * @param _topicSize The number of words the cluster topic shall consist of.
	 */
	public Cluster(eu.tilsner.cubansea.cluster.Cluster _cluster, Color _color, Search _search, int _topicSize) {
		color   	= _color;
		cluster 	= _cluster;
		search  	= _search;
		topicSize	= _topicSize;
		results = new ArrayList<Result>();
		for(ClusteredResult _result: _cluster.getResults()) {
			results.add(new Result(_result));
		}
	}	
}
