package eu.tilsner.cubansea.topic;

import java.util.List;

import eu.tilsner.cubansea.cluster.Cluster;

/**
 * General interface for algorithms generating the topic for a given cluster.
 * 
 * @author Matthias Tilsner
 */
public interface TopicGeneratorAlgorithm {
	
	/**
	 * Generates the topic for a cluster
	 * 
	 * @param cluster The cluster for which the topic shall be generated.
	 * @return The topic.
	 */
	public String generateTopic(Cluster cluster);
	
	/**
	 * Generates the topic for a cluster based on the search terms used.
	 * 
	 * @param cluster The cluster for which the topic shall be generated.
	 * @param searchTerms The search terms used for generating this cluster.
	 * @return The topic.
	 */
	public String generateTopic(Cluster cluster, List<String> searchTerms);
}
