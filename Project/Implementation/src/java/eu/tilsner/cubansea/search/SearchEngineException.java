package eu.tilsner.cubansea.search;

/**
 * Exception wrapper for search engine exceptions. These exceptions can
 * occur when Search Engines are currently not available or encounter 
 * internal errors or exceptions.
 * 
 * @author Matthias Tilsner
 */
public class SearchEngineException extends Exception {

	private static final long serialVersionUID = 4038444291525196316L;

	public SearchEngineException(String message) {
		super(message);
	}
	
	public SearchEngineException(Throwable e) {
		super(e);
	}
	
}
