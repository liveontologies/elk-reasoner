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
public class IndexedSubObjectPropertyOfAxiom<I extends IndexedObjectProperty> extends IndexedAxiom {

	protected final IndexedPropertyChain subProperty_;
	
	protected final I superProperty_;
	
	IndexedSubObjectPropertyOfAxiom(IndexedPropertyChain sub, I sup) {
		subProperty_ = sub;
		superProperty_ = sup;
	}
	
	public IndexedPropertyChain getSubProperty() {
		return subProperty_;
	}
	
	public IndexedObjectProperty getSuperProperty() {
		return superProperty_;
	}
	
	@Override
	void updateOccurrenceNumbers(ModifiableOntologyIndex index, int increment) {
		if (increment > 0) {
			subProperty_.addToldSuperObjectProperty(superProperty_);
			superProperty_.addToldSubPropertyChain(subProperty_);
		} else {
			subProperty_.removeToldSuperObjectProperty(superProperty_);
			superProperty_.removeToldSubObjectProperty(subProperty_);
		}
	}

	@Override
	public <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean occurs() {
		// we do not cache property axioms
		return false;
	}

	@Override
	String toStringStructural() {
		return "SubObjectPropertyChain(" + subProperty_ + " " + superProperty_ + ")";
	}

}
