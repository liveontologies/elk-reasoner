/**
 * @author Yevgeny Kazakov, Jul 1, 2011
 */
package org.semanticweb.elk.owlapi;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class ConverterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8678875783274619601L;

	protected ConverterException() {
	}

	public ConverterException(String message) {
		super(message);
	}

	public ConverterException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConverterException(Throwable cause) {
		super(cause);
	}

}
