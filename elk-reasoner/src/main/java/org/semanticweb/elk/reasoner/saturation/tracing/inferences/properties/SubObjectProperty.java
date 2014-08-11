/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyConclusionVisitor;

/**
 * Represents a {@link Conclusion} that one {@link IndexedObjectProperty} is a sub-property of another.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubObjectProperty implements ObjectPropertyConclusion {

	private final IndexedObjectProperty subProperty_;
	
	private final IndexedObjectProperty superProperty_;
	
	public SubObjectProperty(IndexedObjectProperty sub, IndexedObjectProperty sup) {
		subProperty_ = sub;
		superProperty_ = sup;
	}
	
	public IndexedObjectProperty getSubProperty() {
		return subProperty_;
	}

	public IndexedObjectProperty getSuperProperty() {
		return superProperty_;
	}

	@Override
	public String toString() {
		return "SubObjectProperty(" + subProperty_ + " " + superProperty_ + ")";
	}

	@Override
	public <I, O> O accept(ObjectPropertyConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
}
