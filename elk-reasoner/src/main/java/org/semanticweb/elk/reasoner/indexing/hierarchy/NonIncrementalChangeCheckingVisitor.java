/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.incremental.NonIncrementalChangeListener;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class NonIncrementalChangeCheckingVisitor extends DelegatingElkAxiomVisitor implements ElkAxiomIndexingVisitor {

	private final NonIncrementalChangeListener<ElkAxiom> listener_;
	
	public NonIncrementalChangeCheckingVisitor(ElkAxiomIndexingVisitor visitor,
			NonIncrementalChangeListener<ElkAxiom> listener) {
		super(visitor);

		listener_ = listener;
	}
	
	public ElkAxiomIndexingVisitor getIndexingVisitor() {
		return (ElkAxiomIndexingVisitor) getVisitor();
	}

	@Override
	public Void visit(
			ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {
		listener_.notify(elkEquivalentObjectProperties);
		
		return super.visit(elkEquivalentObjectProperties);
	}

	@Override
	public Void visit(
			ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
		listener_.notify(elkReflexiveObjectPropertyAxiom);
		
		return super.visit(elkReflexiveObjectPropertyAxiom);
	}

	@Override
	public Void visit(ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
		listener_.notify(elkSubObjectPropertyOfAxiom);
		
		return super.visit(elkSubObjectPropertyOfAxiom);
	}	

	@Override
	public Void visit(
			ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom) {
		listener_.notify(elkTransitiveObjectPropertyAxiom);
		
		return super.visit(elkTransitiveObjectPropertyAxiom);
	}

	@Override
	public void indexSubClassOfAxiom(ElkClassExpression subClass,
			ElkClassExpression superClass) {
		getIndexingVisitor().indexSubClassOfAxiom(subClass, superClass);
	}

	@Override
	public void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty) {
		getIndexingVisitor().indexSubObjectPropertyOfAxiom(subProperty, superProperty);
	}

	@Override
	public void indexClassAssertion(ElkIndividual individual,
			ElkClassExpression type) {
		getIndexingVisitor().indexClassAssertion(individual, type);
	}

	@Override
	public void indexDisjointClassExpressions(
			List<? extends ElkClassExpression> list) {
		getIndexingVisitor().indexDisjointClassExpressions(list);
	}

	@Override
	public void indexReflexiveObjectProperty(
			ElkObjectPropertyExpression reflexiveProperty) {
		getIndexingVisitor().indexReflexiveObjectProperty(reflexiveProperty);
	}

	@Override
	public IndexedClass indexClassDeclaration(ElkClass ec) {
		return getIndexingVisitor().indexClassDeclaration(ec);
	}

	@Override
	public IndexedObjectProperty indexObjectPropertyDeclaration(
			ElkObjectProperty eop) {
		return getIndexingVisitor().indexObjectPropertyDeclaration(eop);
	}

	@Override
	public IndexedIndividual indexNamedIndividualDeclaration(
			ElkNamedIndividual eni) {
		return getIndexingVisitor().indexNamedIndividualDeclaration(eni);
	}

	@Override
	public int getMultiplicity() {
		return getIndexingVisitor().getMultiplicity();
	}


	
}
