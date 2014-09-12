/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;

/**
 * Creates indexed objects with binding to the original {@link ElkAxiom}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IndexedAxiomFactoryWithBinding implements IndexedObjectFactory {

	@Override
	public IndexedSubClassOfAxiom createSubClassOfAxiom(
			IndexedClassExpression subClass, IndexedClassExpression superClass,
			ElkAxiom axiom) {
		return new IndexedSubClassOfAxiomWithBinding(subClass, superClass,
				axiom);
	}

	@Override
	public IndexedDisjointnessAxiom createDisjointnessAxiom(
			List<IndexedClassExpression> disjointClasses, ElkAxiom axiom) {
		return new IndexedDisjointnessAxiomWithBinding(disjointClasses, axiom);
	}

	@Override
	public IndexedObjectProperty createdIndexedObjectProperty(
			ElkObjectProperty property) {
		return new IndexedObjectPropertyWithBinding(property);
	}

	@Override
	public IndexedSubObjectPropertyOfAxiom<?> createdSubObjectPropertyOfAxiom(
			IndexedPropertyChain subChain, IndexedObjectProperty superProperty,
			ElkObjectPropertyAxiom axiom) {
		return new IndexedSubObjectPropertyOfAxiomWithBinding(subChain,
				(IndexedObjectPropertyWithBinding) superProperty, axiom);
	}

	@Override
	public IndexedReflexiveObjectPropertyAxiom<?> createReflexiveObjectPropertyAxiom(
			IndexedObjectProperty property,
			ElkReflexiveObjectPropertyAxiom axiom) {
		return new IndexedReflexiveObjectPropertyAxiomWithBinding(
				(IndexedObjectPropertyWithBinding) property, axiom);
	}

}
