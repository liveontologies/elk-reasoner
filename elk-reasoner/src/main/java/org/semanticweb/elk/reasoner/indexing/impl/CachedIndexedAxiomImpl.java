package org.semanticweb.elk.reasoner.indexing.impl;

import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectVisitor;

/**
 * Implements {@link CachedIndexedAxiom}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of objects this object can be structurally equal to
 */
abstract class CachedIndexedAxiomImpl<T extends CachedIndexedAxiom<T>> extends
		CachedIndexedObjectImpl<T, CachedIndexedAxiom<?>> implements
		CachedIndexedAxiom<T> {

	CachedIndexedAxiomImpl(int structuralHash) {
		super(structuralHash);
	}

	@Override
	public final <O> O accept(IndexedObjectVisitor<O> visitor) {
		return accept((IndexedAxiomVisitor<O>) visitor);
	}

}
