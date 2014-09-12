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
 * Certain kinds of indexed objects are created via this factory, usually those
 * which require different implementations. One example of different
 * implementations arises when we may or may need to store axiom bindings.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface IndexedObjectFactory {

	public IndexedSubClassOfAxiom createSubClassOfAxiom(
			IndexedClassExpression subClass, IndexedClassExpression superClass,
			ElkAxiom axiom);

	public IndexedDisjointnessAxiom createDisjointnessAxiom(
			List<IndexedClassExpression> disjointClasses, ElkAxiom axiom);

	public IndexedSubObjectPropertyOfAxiom<?> createdSubObjectPropertyOfAxiom(
			IndexedPropertyChain subChain, IndexedObjectProperty superProperty,
			ElkObjectPropertyAxiom axiom);

	public IndexedReflexiveObjectPropertyAxiom<?> createReflexiveObjectPropertyAxiom(
			IndexedObjectProperty property,
			ElkReflexiveObjectPropertyAxiom axiom);

	public IndexedObjectProperty createdIndexedObjectProperty(
			ElkObjectProperty property);

}
