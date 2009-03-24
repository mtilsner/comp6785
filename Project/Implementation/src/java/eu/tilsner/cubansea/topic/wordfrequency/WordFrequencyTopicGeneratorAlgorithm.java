package eu.tilsner.cubansea.topic.wordfrequency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.tilsner.cubansea.cluster.Cluster;
import eu.tilsner.cubansea.cluster.ClusteredResult;
import eu.tilsner.cubansea.prepare.PreparedResult;
import eu.tilsner.cubansea.topic.TopicGeneratorAlgorithm;
import eu.tilsner.cubansea.utilities.StemmerHelper;
import eu.tilsner.cubansea.utilities.StringHelper;

/**
 * Simple implementation of the TopicGeneratorAlgorithm. It generates the topic
 * by determining the most commonly used words in the cluster.
 * 
 * @author Matthias Tilsner
 */
public class WordFrequencyTopicGeneratorAlgorithm implements TopicGeneratorAlgorithm {

	private int numWords;
	
	/**
	 * Returns the stems of the centroid attributes ordered by
	 * frequency
	 * 
	 * @return An ordered list of the centroid stems.
	 */
	private List<String> getFrequencyOrderedStems(Cluster cluster) {
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
	private String getMostCommonStemUtilization(String stem, List<PreparedResult> results) {
		Map<String,Integer> _frequencies = new HashMap<String,Integer>();
		String _stem;
		for(PreparedResult _result: results) {
			for(String _word: StringHelper.split(_result.getSearchResult().getSummary()," ")) {
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
		
	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.topic.TopicGeneratorAlgorithm#generateTopic(eu.tilsner.cubansea.cluster.Cluster)
	 */
	@Override
	public String generateTopic(Cluster cluster) {
		return generateTopic(cluster, new ArrayList<String>());
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.topic.TopicGeneratorAlgorithm#generateTopic(eu.tilsner.cubansea.cluster.Cluster, java.util.List)
	 */
	@Override
	public String generateTopic(Cluster cluster, List<String> searchTerms) {
		List<String> _searchStems = new ArrayList<String>();
		for(String _term: searchTerms) {
			_searchStems.add(StemmerHelper.stem(_term));
		}
		List<String> _stems = new ArrayList<String>();
		for(String _stem: getFrequencyOrderedStems(cluster)) {
			if(!_searchStems.contains(_stem)) _stems.add(_stem);
			if(_stems.size() >= numWords) break;
		}
		List<PreparedResult> _results = new ArrayList<PreparedResult>();
		for(ClusteredResult _result: cluster.getResults()) {
			_results.add(_result.getPreparedResult());
		}
		List<String> _words = new ArrayList<String>();
		for(String _stem: _stems) {
			_words.add(getMostCommonStemUtilization(_stem, _results));
		}
		return StringHelper.join(_words, " ");
	}
	
	/**
	 * Constructor for this WordFrequencyTopicGenerator. It requires the configuration of how
	 * many words shall be used for constructing the topics. 
	 * 
	 * @param _numWords
	 */
	public WordFrequencyTopicGeneratorAlgorithm(int _numWords) {
		numWords = _numWords;
	}
	
	/**
	 * Constructor for this WordFrequencyTopicGenerator. It defines a standard of 3 words to be
	 * used when constructing the topics. 
	 */
	public WordFrequencyTopicGeneratorAlgorithm() {
		this(3);
	}
}
