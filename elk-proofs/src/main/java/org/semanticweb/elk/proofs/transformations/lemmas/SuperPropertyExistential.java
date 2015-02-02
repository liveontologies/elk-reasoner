/**
 * 
 */
package org.semanticweb.elk.proofs.transformations.lemmas;

import java.util.ArrayList;
import java.util.Collections;
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
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaObjectFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
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
				final ElkSubObjectPropertyOfAxiom toldPropAxiom = createSubPropertyChainAxiom(toldSubChain, superProperty_, elkFactory);
				final DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> chainAxiom = exprFactory.create(toldPropAxiom);
				final DerivedAxiomExpression<ElkSubClassOfAxiom> subsumerPremise = exprFactory.create(elkFactory.getSubClassOfAxiom(existential.getFiller(), existential.getFiller()));
				Inference inf = toldSubChain.accept(new IndexedPropertyChainVisitor<Inference>() {

					@Override
					public Inference visit(IndexedObjectProperty prop) {
						DerivedAxiomExpression<ElkSubClassOfAxiom> exPremise = exprFactory.create(
																					elkFactory.getSubClassOfAxiom(
																							getAxiom().getSubClassExpression(), 
																							elkFactory.getObjectSomeValuesFrom(prop.getElkObjectProperty(), existential.getFiller())));
						
						return new ExistentialComposition(expr_, subsumerPremise, exPremise, chainAxiom);
					}

					@Override
					public Inference visit(IndexedBinaryPropertyChain chain) {
						ElkLemmaObjectFactory lemmaFactory = new ElkLemmaObjectFactoryImpl();
						LemmaExpression<ElkSubClassOfLemma> exPremise = exprFactory.create(
																			lemmaFactory.getSubClassOfLemma(
																					getAxiom().getSubClassExpression(), 
																					lemmaFactory.getComplexObjectSomeValuesFrom(Deindexer.deindex(chain), existential.getFiller())));
						
						return new NaryExistentialAxiomComposition(expr_, Collections.singletonList(exPremise), chainAxiom);
					}
					
				});
				
				inferences.add(inf);
				
				continue;
			}
			
			if (toldSubChain.getSaturated().getSubPropertyChains().contains(subChain_)) {
				Inference inf = toldSubChain.accept(new IndexedPropertyChainVisitor<Inference>() {

					@Override
					public Inference visit(final IndexedObjectProperty toldSubProperty) {
						// this is a trivial premise
						DerivedAxiomExpression<ElkSubClassOfAxiom> subsumerPremise = exprFactory.create(elkFactory.getSubClassOfAxiom(existential.getFiller(), existential.getFiller()));
						ElkSubObjectPropertyOfAxiom toldPropAxiom = createSubPropertyChainAxiom(toldSubProperty, superProperty_, elkFactory);
						DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propPremise = exprFactory.create(toldPropAxiom);
						final DerivedAxiomExpression<ElkSubClassOfAxiom> premise = createExistentialPremise(subClass, toldSubProperty.getElkObjectProperty(), existential.getFiller(), elkFactory);
						
						DerivedExpression exPremise = subChain_.accept(new IndexedPropertyChainVisitor<DerivedAxiomExpression<ElkSubClassOfAxiom>>() {

							@Override
							public DerivedAxiomExpression<ElkSubClassOfAxiom> visit(IndexedObjectProperty subProp) {
								return SuperChainExistential.createExistentialPremise(premise, subProp, reader_);
							}

							@Override
							public DerivedAxiomExpression<ElkSubClassOfAxiom> visit(IndexedBinaryPropertyChain subChain) {
								return new SuperPropertyExistential(premise, subChain, toldSubProperty, reader_);
							}
							
						});
						
						return new ExistentialComposition(expr_, subsumerPremise, exPremise, propPremise);
					}

					@Override
					public Inference visit(IndexedBinaryPropertyChain subChain) {
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
								existentialPremises.add(SuperChainExistential.createReflexiveExistential(subClass, left.getElkObjectProperty(), reader_));
							}
							else if (right.getSaturated().isDerivedReflexive()) {
								// ok, the left is a sub-property
								DerivedAxiomExpression<ElkSubClassOfAxiom> premise = createExistentialPremise(subClass, left.getElkObjectProperty(), existential.getFiller(), elkFactory);
								DerivedAxiomExpression<ElkSubClassOfAxiom> exPremise = SuperChainExistential.createExistentialPremise(premise, left, reader_);
								
								existentialPremises.add(exPremise);
							}
							else {
								// shouldn't get here, chains can only be super-properties if there's some reflexivity involved
								throw new RuntimeException("Reflexivity fuckup " + subChain);
							}
							
							top = right;
						}
						
						ElkSubObjectPropertyOfAxiom toldChain = createSubPropertyChainAxiom(subChain, superProperty_, elkFactory);
						DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propPremise = exprFactory.createAsserted(toldChain);
						
						return new NaryExistentialAxiomComposition(expr_, existentialPremises, propPremise);
					}
					
				});
				
				inferences.add(inf);
			}
		}

		return inferences;
	}

	private DerivedAxiomExpression<ElkSubClassOfAxiom> createExistentialPremise(ElkClassExpression subClass, ElkObjectProperty property, ElkClassExpression filler, ElkObjectFactory elkFactory) {
		return reader_.getExpressionFactory().create(elkFactory.getSubClassOfAxiom(subClass, elkFactory.getObjectSomeValuesFrom(property, filler)));
	}

	private ElkSubObjectPropertyOfAxiom createSubPropertyChainAxiom(IndexedPropertyChain sub, IndexedObjectProperty sup, ElkObjectFactory elkFactory) {
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
