package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedFillerVisitor;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;

/**
 * @return Represents the filler of {@link ElkObjectSomeValuesFrom}
 * 
 */
public interface IndexedFiller extends IndexedContextRoot {

	public IndexedObjectProperty getProperty();

	public IndexedClassExpression getFillerConcept();

	public <O> O accept(IndexedFillerVisitor<O> visitor);

}
