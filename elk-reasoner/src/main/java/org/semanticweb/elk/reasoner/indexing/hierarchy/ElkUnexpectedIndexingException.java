package org.semanticweb.elk.reasoner.indexing.hierarchy;

/**
 * An exception to signal incorrect indexing behavior.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkUnexpectedIndexingException extends ElkIndexingException {

	private static final long serialVersionUID = -6297215279078361253L;

	protected ElkUnexpectedIndexingException() {
	}

	public ElkUnexpectedIndexingException(String message) {
		super(message);
	}

	public ElkUnexpectedIndexingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElkUnexpectedIndexingException(Throwable cause) {
		super(cause);
	}

}
