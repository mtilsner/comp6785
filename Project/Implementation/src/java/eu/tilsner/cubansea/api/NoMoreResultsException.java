package eu.tilsner.cubansea.api;

/**
 * Wrapper error in case the limit of browsing has been reached. 
 * 
 * @author Matthias Tilsner
 */
public class NoMoreResultsException extends Exception {
	private static final long serialVersionUID = 5529809352698173218L;

	public NoMoreResultsException(String message) {
		super(message);
	}
}
