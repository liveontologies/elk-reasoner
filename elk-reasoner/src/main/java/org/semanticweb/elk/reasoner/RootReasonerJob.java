package org.semanticweb.elk.reasoner;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;

/**
 * A {@link ReasonerOutput} that is computed from given root and sub-root
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <R>
 *            the type of the root input
 * @param <SR>
 *            the type of the sub-root input
 * @param <O>
 *            the type of the output
 */
public class RootReasonerJob<R extends IndexedContextRoot, SR extends IndexedObjectProperty, O>
		extends ReasonerOutput<O> {

	private final R root_;

	private final SR subRoot_;

	public RootReasonerJob(R root, SR subRoot) {
		this.root_ = root;
		this.subRoot_ = subRoot;
	}

	public final R getRoot() {
		return this.root_;
	}

	public final SR getSubRoot() {
		return this.subRoot_;
	}

	@Override
	public String toString() {
		return root_.toString() + (subRoot_ == null ? "" : subRoot_.toString());
	}
}
