/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IndexedDisjointnessAxiomWithBinding extends
		IndexedDisjointnessAxiom {

	private final ElkAxiom assertedAxiom_;
	
	IndexedDisjointnessAxiomWithBinding(List<IndexedClassExpression> members, ElkAxiom axiom) {
		super(members);
		
		assertedAxiom_ = axiom;
	}

	public ElkAxiom getAssertedAxiom() {
		return assertedAxiom_;
	}
	
}
