/**
 * 
 */
package org.semanticweb.elk.proofs.transformations.lemmas;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaObjectFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.impl.ElkLemmaObjectFactoryImpl;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.NaryExistentialAxiomComposition;
import org.semanticweb.elk.proofs.inferences.mapping.Deindexer;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;

/**
 * Axiom expression of the form A <= S some B where R1 o ... o Rn <= S, this
 * expression produces inferences which derive it from A <= R1 o ... o Rn some
 * B.
 * 
 * @author Pavel Klinov pavel.klinov@uni-ulm.de
 *
 */
class SuperPropertyExistential implements DerivedAxiomExpression<ElkSubClassOfAxiom> {

	private final DerivedAxiomExpression<ElkSubClassOfAxiom> expr_;
	
	private final IndexedPropertyChain subChain_;
	
	private final IndexedObjectProperty superProperty_;
	
	private final ReasonerInferenceReader reader_;
	
	SuperPropertyExistential(DerivedAxiomExpression<ElkSubClassOfAxiom> e, IndexedPropertyChain sub, IndexedObjectProperty sup, ReasonerInferenceReader r) {
		expr_ = e;
		subChain_ = sub;
		superProperty_ = sup;
		reader_ = r;
	}
	
	@Override
	public ElkSubClassOfAxiom getAxiom() {
		return expr_.getAxiom();
	}

	@Override
	public boolean isAsserted() {
		return expr_.isAsserted();
	}

	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}
	
	@Override
	public Iterable<? extends Inference> getInferences() throws ElkException {
		// create inferences depending on how the subchain entails the superchain
		final List<Inference> inferences = new ArrayList<Inference>();
		final ElkObjectSomeValuesFrom existential = (ElkObjectSomeValuesFrom) expr_.getAxiom().getSuperClassExpression();
		final ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();
		final DerivedExpressionFactory exprFactory = reader_.getExpressionFactory();
		final ElkClassExpression subClass = expr_.getAxiom().getSubClassExpression();
		
		for (IndexedPropertyChain toldSubChain : superProperty_.getToldSubProperties()) {
			if (toldSubChain.equals(subChain_)) {
				// finishing
				ElkSubObjectPropertyOfAxiom toldPropAxiom = lookup(toldSubChain, superProperty_, elkFactory);
				DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propPremise = exprFactory.create(toldPropAxiom);
				DerivedAxiomExpression<ElkSubClassOfAxiom> subsumerPremise = exprFactory.create(elkFactory.getSubClassOfAxiom(existential.getFiller(), existential.getFiller()));
				DerivedExpression exPremise = toldSubChain.accept(new IndexedPropertyChainVisitor<DerivedExpression>() {

					@Override
					public DerivedExpression visit(IndexedObjectProperty prop) {
						return exprFactory.create(
								elkFactory.getSubClassOfAxiom(
										getAxiom().getSubClassExpression(), 
										elkFactory.getObjectSomeValuesFrom(prop.getElkObjectProperty(), existential.getFiller())));
					}

					@Override
					public DerivedExpression visit(IndexedBinaryPropertyChain chain) {
						ElkLemmaObjectFactory lemmaFactory = new ElkLemmaObjectFactoryImpl();
						
						return exprFactory.create(
								lemmaFactory.getSubClassOfLemma(
										getAxiom().getSubClassExpression(), 
										lemmaFactory.getComplexObjectSomeValuesFrom(Deindexer.deindex(chain), existential.getFiller())));
					}
					
				});
				
				inferences.add(new ExistentialComposition(expr_, subsumerPremise, exPremise, propPremise));
			}
			
			if (toldSubChain.getSaturated().getSubProperties().contains(subChain_)) {
				toldSubChain.accept(new IndexedPropertyChainVisitor<Void>() {

					@Override
					public Void visit(IndexedObjectProperty subProp) {
						// this is a trivial premise
						DerivedAxiomExpression<ElkSubClassOfAxiom> subsumerPremise = exprFactory.create(elkFactory.getSubClassOfAxiom(existential.getFiller(), existential.getFiller()));
						ElkSubObjectPropertyOfAxiom toldPropAxiom = lookup(subProp, superProperty_, elkFactory);
						DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propPremise = exprFactory.create(toldPropAxiom);
						ElkSubClassOfAxiom exPremiseAxiom = elkFactory.getSubClassOfAxiom(subClass, elkFactory.getObjectSomeValuesFrom(Deindexer.deindex(subProp), existential.getFiller()));
						DerivedAxiomExpression<ElkSubClassOfAxiom> exPremise = exprFactory.create(exPremiseAxiom);
						
						if (!subProp.equals(subChain_)) {
							exPremise = new SuperPropertyExistential(exPremise, subChain_, subProp, reader_);
						}
						
						inferences.add(new ExistentialComposition(expr_, subsumerPremise, exPremise, propPremise));
						
						return null;
					}

					@Override
					public Void visit(IndexedBinaryPropertyChain subChain) {
						// eliminating reflexivity in a loop
						IndexedPropertyChain top = subChain;
						List<DerivedAxiomExpression<ElkSubClassOfAxiom>> existentialPremises = new ArrayList<DerivedAxiomExpression<ElkSubClassOfAxiom>>();
						
						for (;;) {
							if (top instanceof IndexedObjectProperty) {
								break;
							}
							
							IndexedBinaryPropertyChain ibc = (IndexedBinaryPropertyChain) top;
							IndexedObjectProperty left = ibc.getLeftProperty();
							IndexedPropertyChain right = ibc.getRightProperty();
							
							if (left.getSaturated().isDerivedReflexive()) {
								existentialPremises.add(createExistentialPremise(subClass, left.getElkObjectProperty(), subClass, elkFactory));
							}
							else if (right.getSaturated().isDerivedReflexive()) {
								// ok, the left is a sub-property
								DerivedAxiomExpression<ElkSubClassOfAxiom> premise = createExistentialPremise(subClass, left.getElkObjectProperty(), existential.getFiller(), elkFactory);
								
								if (!left.equals(subChain_)) {
									premise = new SuperPropertyExistential(premise, subChain_, left, reader_);
								}
								
								existentialPremises.add(premise);
							}
							else {
								// shouldn't get here, chains can only be super-properties if there's some reflexivity involved
								throw new RuntimeException("Reflexivity fuckup " + subChain);
							}
							
							top = right;
						}
						
						ElkSubObjectPropertyOfAxiom toldChain = lookup(subChain, superProperty_, elkFactory);
						DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propPremise = exprFactory.createAsserted(toldChain);
						
						inferences.add(new NaryExistentialAxiomComposition(expr_, existentialPremises, propPremise));
						
						return null;
					}
					
				});
			}
		}

		return inferences;
	}

	protected DerivedAxiomExpression<ElkSubClassOfAxiom> createExistentialPremise(ElkClassExpression subClass, ElkObjectProperty property, ElkClassExpression filler, ElkObjectFactory elkFactory) {
		return reader_.getExpressionFactory().create(elkFactory.getSubClassOfAxiom(subClass, elkFactory.getObjectSomeValuesFrom(property, filler)));
	}

	protected ElkSubObjectPropertyOfAxiom lookup(IndexedPropertyChain sub, IndexedObjectProperty sup, ElkObjectFactory elkFactory) {
		return elkFactory.getSubObjectPropertyOfAxiom(Deindexer.deindex(sub), sup.getElkObjectProperty());
	}

	@Override
	public boolean equals(Object obj) {
		return expr_.equals(obj);
	}

	@Override
	public int hashCode() {
		return expr_.hashCode();
	}

	@Override
	public String toString() {
		return expr_.toString();
	}
}
