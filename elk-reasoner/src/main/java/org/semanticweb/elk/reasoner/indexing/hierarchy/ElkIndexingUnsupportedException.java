package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.util.OwlObjectNameVisitor;

/**
 * This exception should be used to indicate that some {@link ElkObject} cannot
 * be represented within the index datastructure, that is, it is not supported
 * by the reasoner.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkIndexingUnsupportedException extends ElkIndexingException {

	private static final long serialVersionUID = -4575658482490999720L;

	protected ElkIndexingUnsupportedException() {
	}

	protected ElkIndexingUnsupportedException(String message) {
		super(message);
	}

	protected ElkIndexingUnsupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	protected ElkIndexingUnsupportedException(Throwable cause) {
		super(cause);
	}

	public ElkIndexingUnsupportedException(ElkObject elkObject) {
		this("ELK does not support " + OwlObjectNameVisitor.getName(elkObject)
				+ ".");
	}

	public ElkIndexingUnsupportedException(ElkObject elkObject, Throwable cause) {
		this("ELK does not support " + OwlObjectNameVisitor.getName(elkObject)
				+ ".", cause);
	}

}
