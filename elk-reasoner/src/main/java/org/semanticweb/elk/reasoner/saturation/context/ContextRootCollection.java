package org.semanticweb.elk.reasoner.saturation.context;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * A collection of {@link IndexedClassExpression}s that are roots of the given
 * collection of {@link Context}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ContextRootCollection extends
		AbstractCollection<IndexedClassExpression> {

	private final Collection<? extends Context> contexts_;

	public ContextRootCollection(Collection<? extends Context> contexts) {
		this.contexts_ = contexts;
	}

	@Override
	public Iterator<IndexedClassExpression> iterator() {
		return new Iterator<IndexedClassExpression>() {

			private final Iterator<? extends Context> iter_ = contexts_
					.iterator();

			@Override
			public boolean hasNext() {
				return iter_.hasNext();
			}

			@Override
			public IndexedClassExpression next() {
				return iter_.next().getRoot();
			}

			@Override
			public void remove() {
				iter_.remove();
			}

		};
	}

	@Override
	public int size() {
		return contexts_.size();
	}

}
