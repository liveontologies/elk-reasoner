/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * Creates indexed axioms with binding to the original {@link ElkAxiom}s.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IndexedAxiomFactoryWithBinding implements IndexedAxiomFactory {

	@Override
	public IndexedSubClassOfAxiom createSubClassOfAxiom(
			IndexedClassExpression subClass, IndexedClassExpression superClass,
			ElkAxiom axiom) {
		return new IndexedSubClassOfAxiomWithBinding(subClass, superClass, axiom);
	}

	@Override
	public IndexedDisjointnessAxiom createDisjointnessAxiom(
			List<IndexedClassExpression> disjointClasses, ElkAxiom axiom) {
		return new IndexedDisjointnessAxiomWithBinding(disjointClasses, axiom);
	}

}
