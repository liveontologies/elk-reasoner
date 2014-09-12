/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface IndexedAxiomFactory {

	public IndexedSubClassOfAxiom createSubClassOfAxiom(IndexedClassExpression subClass, IndexedClassExpression superClass, ElkAxiom axiom);
	
	public IndexedDisjointnessAxiom createDisjointnessAxiom(List<IndexedClassExpression> disjointClasses, ElkAxiom axiom);
}
