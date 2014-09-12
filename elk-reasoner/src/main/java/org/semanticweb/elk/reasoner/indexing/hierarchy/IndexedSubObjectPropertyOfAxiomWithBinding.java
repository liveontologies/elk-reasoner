/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class IndexedSubObjectPropertyOfAxiomWithBinding extends IndexedSubObjectPropertyOfAxiom<IndexedObjectPropertyWithBinding> {

	private final ElkObjectPropertyAxiom assertedAxiom_;
	
	IndexedSubObjectPropertyOfAxiomWithBinding(IndexedPropertyChain sub, IndexedObjectPropertyWithBinding sup, ElkObjectPropertyAxiom axiom) {
		super(sub, sup);
		assertedAxiom_ = axiom;
	}
	
	@Override
	void updateOccurrenceNumbers(ModifiableOntologyIndex index, int increment) {
		if (increment > 0) {
			subProperty_.addToldSuperObjectProperty(superProperty_);
			superProperty_.addToldSubPropertyChain(subProperty_, assertedAxiom_);
		} else {
			subProperty_.removeToldSuperObjectProperty(superProperty_);
			superProperty_.removeToldSubObjectProperty(subProperty_);
		}
	}

}
