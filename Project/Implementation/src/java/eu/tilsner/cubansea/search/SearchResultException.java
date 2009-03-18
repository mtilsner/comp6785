package eu.tilsner.cubansea.search;

/**
 * Abstract exception class for wrapping any kind of exceptions
 * that occur while accessing result data. These can be technical
 * transport errors such as IOExceptions, invalid URLs inside the
 * results, or other exceptions.
 * 
 * @author Matthias Tilsner
 */
public class SearchResultException extends Exception {
	
	private static final long serialVersionUID = -784689440699787069L;

	public SearchResultException(String message) {
		super(message);
	}
	
	public SearchResultException(Throwable e) {
		super(e);
	}
	
}
