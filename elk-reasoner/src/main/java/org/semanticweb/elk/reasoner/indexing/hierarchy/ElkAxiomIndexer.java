/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/**
 * A collection of method for indexing ELK objects
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ElkAxiomIndexer {

	public void indexSubClassOfAxiom(ElkClassExpression subClass,
			ElkClassExpression superClass);

	public void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty);

	public void indexClassAssertion(ElkIndividual individual,
			ElkClassExpression type);

	public void indexDisjointClassExpressions(
			List<? extends ElkClassExpression> list);

	public void indexReflexiveObjectProperty(
			ElkObjectPropertyExpression reflexiveProperty);

	public IndexedClass indexClassDeclaration(ElkClass ec);

	public IndexedObjectProperty indexObjectPropertyDeclaration(
			ElkObjectProperty eop);

	public IndexedIndividual indexNamedIndividualDeclaration(
			ElkNamedIndividual eni);
}
