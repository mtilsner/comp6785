package eu.tilsner.cubansea.topic.wordfrequency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
				return (_item2.getValue() > _item1.getValue()) ? 1 : ((_item2.getValue() < _item1.getValue()) ? -1 : 0);
			}
		});
		List<String> _stems = new ArrayList<String>();
		for(Map.Entry<String,Double> _frequency: _frequencies) {
			_stems.add(StemmerHelper.stem(_frequency.getKey()));
		}
		return _stems;
	}

	/**
	 * Returns all non-trivial words found inside a string.
	 * 
	 * @param content The string of the content with words separated by spaces.
	 * @return A filtered list of the words to be considered.
	 */
	private List<String> getWords(String content) {
		if(content == null) return new ArrayList<String>();
		content = content.replaceAll(PreparedResult.INVALID_CHARACTER_PATTERN, " ");
		content = content.replaceAll("\\s+", " ");
		List<String> _words = StringHelper.split(content, " ");
		return filterWords(_words);
	}

	/**
	 * Filters a list of words according to the filtering information provided
	 * in <i>PreparedResult</i>. Returns a new list without all words specified
	 * to be ignored.
	 * 
	 * @param words The list of words to be filtered.
	 * @return The filtered word list.
	 */
	private List<String> filterWords(List<String> words) {
		List<String> _filtered = new ArrayList<String>();
		for(String _word: words) {
			_word = _word.toLowerCase();
			if(_word.length() > PreparedResult.IGNORED_WORD_LENGTH && !PreparedResult.IGNORED_WORDS.contains(_word))
				_filtered.add(_word);
		}
		return _filtered;
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
		Collection<String> _debug = new HashSet<String>(); 
		for(PreparedResult _result: results) {
			for(String _word: getWords(_result.getSearchResult().getSummary())) {
				_stem = StemmerHelper.stem(_word).toLowerCase();
				if(stem.toLowerCase().equals(_stem)) {
					if(!_frequencies.containsKey(_word)) 
						_frequencies.put(_word, 1);
					else
						_frequencies.put(_word, _frequencies.get(_word)+1);
				} else _debug.add(_stem);
			}
		}
		if(_frequencies.size() == 0) return null;
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
		}
		List<PreparedResult> _results = new ArrayList<PreparedResult>();
		for(ClusteredResult _result: cluster.getResults()) {
			_results.add(_result.getPreparedResult());
		}
		List<String> _words = new ArrayList<String>();
		String _word;
		for(String _stem: _stems) {
			_word = getMostCommonStemUtilization(_stem, _results);
			if(_word != null) _words.add(_word);
			if(_words.size() >= numWords) break;
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
