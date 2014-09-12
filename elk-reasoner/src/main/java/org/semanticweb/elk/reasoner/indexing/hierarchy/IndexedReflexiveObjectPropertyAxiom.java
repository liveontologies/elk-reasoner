/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IndexedReflexiveObjectPropertyAxiom<I extends IndexedObjectProperty> extends IndexedAxiom {

	protected final I property;
	
	IndexedReflexiveObjectPropertyAxiom(I p) {
		property = p;
	}
	
	public IndexedObjectProperty getProperty() {
		return property;
	}
	
	@Override
	void updateOccurrenceNumbers(ModifiableOntologyIndex index, int increment) {
		if (property.reflexiveAxiomOccurrenceNo == 0
				&& increment > 0) {
			// first occurrence of reflexivity axiom
			index.addReflexiveProperty(property);
		}

		property.reflexiveAxiomOccurrenceNo += increment;

		if (property.reflexiveAxiomOccurrenceNo == 0
				&& increment < 0) {
			// no occurrence of reflexivity axiom
			index.removeReflexiveProperty(property);
		}
	}

	@Override
	public <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean occurs() {
		// don't cache these axioms
		return false;
	}

	@Override
	String toStringStructural() {
		return "ReflexiveObjectProperty(" + property + ")";
	}

}
