package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedEntityVisitor;

public interface IndexedEntity extends IndexedObject {

	/**
	 * @return The represented {@link ElkEntity}
	 */
	public ElkEntity getElkEntity();

	// TODO: add enum for entity types

	String getEntityType();

	public <O> O accept(IndexedEntityVisitor<O> visitor);

}
