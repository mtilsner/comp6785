import java.awt.Color
import java.util.Arrays;

import eu.tilsner.cubansea.api.Configuration as APIConfiguration
import eu.tilsner.cubansea.cluster.ClusteringAlgorithm
import eu.tilsner.cubansea.prepare.PreparationAlgorithm
import eu.tilsner.cubansea.topic.TopicGeneratorAlgorithm
import eu.tilsner.cubansea.search.SearchEngine

class Configuration implements APIConfiguration {
	SortedSet topicColors
    static hasMany = [ topicColors : TopicColor ]
    static transients = ['clusteringAlgorithm',
                         'preparationAlgorithm',
                         'topicGeneratorAlgorithm',
                         'searchEngine',
                         'clusterColors'] 

    String name
    
	int numberOfClusters
	int clusteringBase
	int blockSize
	
	Class clusteringAlgorithmClass
	Class preparationAlgorithmClass
	Class topicGeneratorAlgorithmClass
	Class searchEngineClass
	
	public ClusteringAlgorithm getClusteringAlgorithm() {
		return clusteringAlgorithmClass.newInstance()
	}
	
	public PreparationAlgorithm getPreparationAlgorithm() {
		return preparationAlgorithmClass.newInstance()
	}

	public TopicGeneratorAlgorithm getTopicGeneratorAlgorithm() {
		return topicGeneratorAlgorithmClass.newInstance()
	}
	
	public SearchEngine getSearchEngine() {
		return searchEngineClass.newInstance()
	}
	
	public List<Color> getClusterColors() {
		return topicColors.collect {topicColor -> Color.decode("0x${topicColor.color}")}
	}
	
    static constraints = {
		clusteringAlgorithmClass(validator: {val,obj ->
			return Arrays.asList(val.interfaces).contains(eu.tilsner.cubansea.cluster.ClusteringAlgorithm.class)
		})
		preparationAlgorithmClass(validator: {val,obj ->
			return Arrays.asList(val.interfaces).contains(eu.tilsner.cubansea.prepare.PreparationAlgorithm.class)
		})
		topicGeneratorAlgorithmClass(validator: {val,obj ->
			return Arrays.asList(val.interfaces).contains(eu.tilsner.cubansea.topic.TopicGeneratorAlgorithm.class)
		})
		searchEngineClass(validator: {val,obj ->
			return Arrays.asList(val.interfaces).contains(eu.tilsner.cubansea.search.SearchEngine.class)
		})
    }
}
