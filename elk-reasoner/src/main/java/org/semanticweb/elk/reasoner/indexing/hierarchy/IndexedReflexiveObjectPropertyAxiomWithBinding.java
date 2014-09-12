/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IndexedReflexiveObjectPropertyAxiomWithBinding extends
		IndexedReflexiveObjectPropertyAxiom<IndexedObjectPropertyWithBinding> {

	private final ElkReflexiveObjectPropertyAxiom axiom_;
	
	IndexedReflexiveObjectPropertyAxiomWithBinding(IndexedObjectPropertyWithBinding p, ElkReflexiveObjectPropertyAxiom axiom) {
		super(p);
		axiom_ = axiom;
	}

	public ElkReflexiveObjectPropertyAxiom getAxiom() {
		return axiom_;
	}

	@Override
	void updateOccurrenceNumbers(ModifiableOntologyIndex index, int increment) {
		if (property.reflexiveAxiomOccurrenceNo == 0
				&& increment > 0) {
			// first occurrence of reflexivity axiom
			index.addReflexiveProperty(property);
			property.setAxiom(axiom_);
		}

		property.reflexiveAxiomOccurrenceNo += increment;

		if (property.reflexiveAxiomOccurrenceNo == 0
				&& increment < 0) {
			// no occurrence of reflexivity axiom
			index.removeReflexiveProperty(property);
			property.setAxiom(null);
		}
	}
	
	
}
