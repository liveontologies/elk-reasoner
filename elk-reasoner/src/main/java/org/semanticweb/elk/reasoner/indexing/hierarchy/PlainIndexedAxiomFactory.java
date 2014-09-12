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
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class PlainIndexedAxiomFactory implements IndexedObjectFactory {

	@Override
	public IndexedSubClassOfAxiom createSubClassOfAxiom(
			IndexedClassExpression subClass, IndexedClassExpression superClass,
			ElkAxiom axiom) {
		return new IndexedSubClassOfAxiom(subClass, superClass);
	}

	@Override
	public IndexedDisjointnessAxiom createDisjointnessAxiom(
			List<IndexedClassExpression> disjointClasses, ElkAxiom axiom) {
		return new IndexedDisjointnessAxiom(disjointClasses);
	}

	@Override
	public IndexedObjectProperty createdIndexedObjectProperty(
			ElkObjectProperty property) {
		return new IndexedObjectProperty(property);
	}

	@Override
	public IndexedSubObjectPropertyOfAxiom<IndexedObjectProperty> createdSubObjectPropertyOfAxiom(
			IndexedPropertyChain subChain, IndexedObjectProperty superProperty,
			ElkObjectPropertyAxiom axiom) {
		return new IndexedSubObjectPropertyOfAxiom<IndexedObjectProperty>(
				subChain, superProperty);
	}

	@Override
	public IndexedReflexiveObjectPropertyAxiom<IndexedObjectProperty> createReflexiveObjectPropertyAxiom(
			IndexedObjectProperty property,
			ElkReflexiveObjectPropertyAxiom axiom) {
		return new IndexedReflexiveObjectPropertyAxiom<IndexedObjectProperty>(
				property);
	}

}
