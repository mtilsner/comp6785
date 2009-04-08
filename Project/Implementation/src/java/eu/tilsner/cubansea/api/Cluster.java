package eu.tilsner.cubansea.api;

import java.awt.Color;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import eu.tilsner.cubansea.cluster.ClusteredResult;

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
	private String			id;
	private Color 			color;
	private List<Result>	results;
	private Search			search;
	private String			topic;
	private int				resultCountGuess;
	
	/**
	 * Returns a unique string id for this cluster.
	 * 
	 * @return The string id.
	 */
	public String getId() {
		return id;
	}
	
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
	
	protected void setBaseColor(Color _color) {
		color = _color;
	}
	
	/**
	 * Returns the CSS string of the base color.
	 * 
	 * @return The CSS color string.
	 */
	public String getCssColor() {
		String _red 	= Integer.toHexString(color.getRed());
		String _green	= Integer.toHexString(color.getGreen());
		String _blue	= Integer.toHexString(color.getBlue());
		if(_red.length() < 2) 	_red   = "0"+_red;
		if(_green.length() < 2) _green = "0"+_green;
		if(_blue.length() < 2) 	_blue  = "0"+_blue; 
		return _red+_green+_blue;
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
		return getCurrentResults().subList(_first,_last);
	}
	
	/**
	 * Returns the currently fetched result list.
	 * 
	 * @return The currently available results.
	 */
	public List<Result> getCurrentResults() {
		Collections.sort(results, new Comparator<Result>(){
			@Override
			public int compare(Result o1, Result o2) {
				// TODO Auto-generated method stub
				return o1.getOriginalRank()-o2.getOriginalRank();
			}
		});
		return results;
	}
	
	/**
	 * Returns the guessed number of results for this cluster.
	 * 
	 * @return The guessed number of results.
	 */
	public int getResultCountGuess() {
		return resultCountGuess;
	}
	
	/**
	 * Sets the guessed result count.
	 * 
	 * @param _resultCountGuess The guess for the result count.
	 */
	protected void setResultCountGuess(int _resultCountGuess) {
		resultCountGuess = _resultCountGuess;
	}
	
	/**
	 * Returns the current count of results.
	 * 
	 * @return The count of results fetched so far.
	 */
	public int getCurrentResultCount() {
		return results.size();
	}
	
	/**
	 * Returns the topic of the cluster. If the topic has not yet
	 * been determined, it is created. 
	 * 
	 * @return The topic.
	 */
	public String getTopic() {
		return topic;
	}
	
	/**
	 * Wrapper method called by the search to add additional results to the
	 * result list. The results are provided as ClusteredResults and must be
	 * converted into regular results.
	 * 
	 * @param newResults The new results that have to be added.
	 */
	protected void addResults(List<Result> newResults) {
		results.addAll(newResults);
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
	public Cluster(eu.tilsner.cubansea.cluster.Cluster _cluster, Color _color, Search _search, String _topic) {
		id			= UUID.randomUUID().toString();
		color   	= _color;
		cluster 	= _cluster;
		search  	= _search;
		topic		= _topic;
		results 	= new ArrayList<Result>();
		for(ClusteredResult _result: _cluster.getResults()) {
			results.add(new Result(_result));
		}
	}	
}
