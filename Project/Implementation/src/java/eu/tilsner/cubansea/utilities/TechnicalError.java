package eu.tilsner.cubansea.utilities;

/**
 * Wrapper error for any kind of technical exceptions that might, unexpectately
 * occur. This wrapper allows an at-will catching of technical errors.
 * 
 * @author Matthias Tilsner
 */
public class TechnicalError extends Error {

	private static final long serialVersionUID = 9085593516473850775L;

	public TechnicalError(String message) {
		super(message);
	}

	public TechnicalError(Throwable e) {
		super(e);
	}
	
}
