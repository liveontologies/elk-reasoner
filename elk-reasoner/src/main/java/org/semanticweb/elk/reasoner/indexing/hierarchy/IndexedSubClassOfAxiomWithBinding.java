/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRuleWithAxiomBinding;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IndexedSubClassOfAxiomWithBinding extends IndexedSubClassOfAxiom {

	private final ElkAxiom assertedAxiom_;
	
	protected IndexedSubClassOfAxiomWithBinding(
			IndexedClassExpression subClass, IndexedClassExpression superClass,
			ElkAxiom assertedAxiom) {
		super(subClass, superClass);
		
		assertedAxiom_ = assertedAxiom;
	}

	@Override
	protected void updateOccurrenceNumbers(ModifiableOntologyIndex index, int increment) {
		if (increment > 0) {
			SuperClassFromSubClassRuleWithAxiomBinding.addRuleFor(this, index, assertedAxiom_);
		} else {
			SuperClassFromSubClassRuleWithAxiomBinding.removeRuleFor(this, index);
		}
	}
	
}
