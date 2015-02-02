/**
 * 
 */
package org.semanticweb.elk.proofs.transformations.lemmas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
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
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaObjectFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.impl.ElkLemmaObjectFactoryImpl;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.classes.NaryExistentialLemmaComposition;
import org.semanticweb.elk.proofs.inferences.classes.ReflexiveExistentialComposition;
import org.semanticweb.elk.proofs.inferences.mapping.Deindexer;
import org.semanticweb.elk.proofs.utils.TautologyChecker;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexObjectConverter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;

/**
 * Lemma expression of the form B0 <= S1 o ... o Sn some Bn where R1 o ... o Rn <= S1 o ... o Sn.
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
class SuperChainExistential implements LemmaExpression<ElkSubClassOfLemma> {

	private final IndexedPropertyChain subChain_;
	
	private final IndexedPropertyChain superChain_;
	
	private final LemmaExpression<ElkSubClassOfLemma> lemma_;
	
	private final ReasonerInferenceReader reader_;

	SuperChainExistential(LemmaExpression<ElkSubClassOfLemma> l, IndexedPropertyChain sub, IndexedPropertyChain sup, ReasonerInferenceReader r) {
		lemma_ = l;
		subChain_ = sub;
		superChain_ = sup;
		reader_ = r;
	}
	
	@Override
	public Iterable<? extends Inference> getInferences() throws ElkException {
		Inference refElimination = getReflexivityEliminationInference();
		
		if (refElimination != null) {
			//return Operations.concat(Collections.singletonList(refElimination), lemma_.getInferences());
			// if there's derivation subChain <= superChain which starts at this reflexivity elimination inference, we ignore other inferences
			// of the lemma. If they exist, they're handled elsewhere.
			return Collections.singletonList(refElimination);
		}
		
		return lemma_.getInferences();
	}

	// TODO use recorded inferences
	private Inference getReflexivityEliminationInference() {
		if (superChain_ instanceof IndexedObjectProperty) {
			return null;
		}
		
		IndexedBinaryPropertyChain superChain = (IndexedBinaryPropertyChain) superChain_;
		IndexedObjectProperty left = superChain.getLeftProperty();
		IndexedPropertyChain right = superChain.getRightProperty();
		
		if (left.getSaturated().isDerivedReflexive() && right.getSaturated().getSubPropertyChains().contains(subChain_)) {
			DerivedAxiomExpression<?> reflExistential = createReflexiveExistential(getSubClass(), left.getElkObjectProperty(), reader_);
			DerivedExpression exPremise = updateSuperChain(getLemma(), subChain_, right, reader_);
			
			return new NaryExistentialLemmaComposition(lemma_, Arrays.asList(reflExistential, exPremise));
		}
		else if (right.getSaturated().isDerivedReflexive() && left.getSaturated().getSubPropertyChains().contains(subChain_)) {
			// all properties comprising the right chain are derived reflexive so we add all reflexive existentials in one go
			List<DerivedExpression> existentials = new ArrayList<DerivedExpression>();
			IndexedPropertyChain top = right;
			DerivedExpression exPremise = updateSuperChain(getLemma(), subChain_, left, reader_); 
			
			existentials.add(exPremise);
			
			for (;;) {
				if (top instanceof IndexedObjectProperty) {
					IndexedObjectProperty prop = (IndexedObjectProperty) top;
					
					existentials.add(createReflexiveExistential(getSuperClassFiller(), prop.getElkObjectProperty(), reader_));
					break;
				}
				else {
					IndexedObjectProperty prop = ((IndexedBinaryPropertyChain) top).getLeftProperty();

					existentials.add(createReflexiveExistential(getSuperClassFiller(), prop.getElkObjectProperty(), reader_));
					top = ((IndexedBinaryPropertyChain) top).getRightProperty();
				}
			}
			
			return new NaryExistentialLemmaComposition(lemma_, existentials);
		}
		
		return null;
	}

	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public ElkSubClassOfLemma getLemma() {
		return lemma_.getLemma();
	}
	
	ElkClassExpression getSubClass() {
		return getLemma().getSubClass();
	}
	
	ElkClassExpression getSuperClassFiller() {
		return ((ElkComplexObjectSomeValuesFrom) getLemma().getSuperClass()).getFiller();
	}
	
	static ElkClassExpression getSuperClassFiller(ElkSubClassOfLemma lemma) {
		return ((ElkComplexObjectSomeValuesFrom) lemma.getSuperClass()).getFiller();
	}
		
	static DerivedExpression updateSuperChain(final ElkSubClassOfLemma lemma, final IndexedPropertyChain subChain, final IndexedPropertyChain superChain, final ReasonerInferenceReader reader) {
		final ElkObjectFactory elkFactory = new ElkObjectFactoryImpl(); 
		final ElkLemmaObjectFactory lemmaFactory = new ElkLemmaObjectFactoryImpl();
		
		return superChain.accept(new IndexedPropertyChainVisitor<DerivedExpression>() {

			@Override
			public DerivedExpression visit(IndexedObjectProperty iop) {
				ElkSubClassOfAxiom conclusionAxiom = elkFactory.getSubClassOfAxiom(
						lemma.getSubClass(), 
						elkFactory.getObjectSomeValuesFrom(iop.getElkObjectProperty(), getSuperClassFiller(lemma)));
				
				return new SuperPropertyExistential(reader.getExpressionFactory().create(conclusionAxiom), subChain, iop, reader);
			}

			@Override
			public DerivedExpression visit(IndexedBinaryPropertyChain chain) {
				ElkSubClassOfLemma conclusionLemma = lemmaFactory.getSubClassOfLemma(
						lemma.getSubClass(), 
						lemmaFactory.getComplexObjectSomeValuesFrom(Deindexer.deindex(chain), getSuperClassFiller(lemma)));
				
				return new SuperChainExistential(reader.getExpressionFactory().create(conclusionLemma), subChain, chain, reader);
			}
			
		});
	}
	
	static DerivedAxiomExpression<ElkSubClassOfAxiom> updateSuperProperty(
														ElkSubClassOfAxiom premise, 
														IndexedPropertyChain sub, 
														IndexedObjectProperty sup,
														ReasonerInferenceReader reader) {
		final ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();
		
		ElkSubClassOfAxiom conclusionAxiom = elkFactory.getSubClassOfAxiom(
												premise.getSubClassExpression(), 
												elkFactory.getObjectSomeValuesFrom(sup.getElkObjectProperty(), 
														((ElkObjectSomeValuesFrom) premise.getSuperClassExpression()).getFiller()));
		
		return new SuperPropertyExistential(reader.getExpressionFactory().create(conclusionAxiom), sub, sup, reader);
	}

	static DerivedAxiomExpression<ElkSubClassOfAxiom> createReflexiveExistential(ElkClassExpression ce, ElkObjectProperty prop, ReasonerInferenceReader reader) {
		ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();
		ElkSubClassOfAxiom reflExistential = elkFactory.getSubClassOfAxiom(ce, elkFactory.getObjectSomeValuesFrom(prop, ce));
		DerivedAxiomExpression<ElkSubClassOfAxiom> expr = reader.getExpressionFactory().create(reflExistential);
		DerivedAxiomExpressionWrap<ElkSubClassOfAxiom> wrap = new DerivedAxiomExpressionWrap<ElkSubClassOfAxiom>(expr);
		
		wrap.addInference(new ReflexiveExistentialComposition(reflExistential, elkFactory.getReflexiveObjectPropertyAxiom(prop), reader.getExpressionFactory()));
		
		return wrap;
	}

	static DerivedExpression createExistentialPremise(final DerivedExpression classPremise, final DerivedExpression chainPremise, final ReasonerInferenceReader reader) {
		if (isTautology(chainPremise)) {
			return classPremise;
		}
		
		if (classPremise instanceof DerivedAxiomExpression) {
			return createExistentialPremise(
							(DerivedAxiomExpression<ElkSubClassOfAxiom>) classPremise, 
							(DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>) chainPremise,
							reader);
		}
		// continue to unwind lemmas
		final ElkSubClassOfLemma lemmaPremise = ((LemmaExpression<ElkSubClassOfLemma>) classPremise).getLemma();
		final IndexObjectConverter indexer = reader.getIndexer();
		// even though the subsumer premise is a lemma, the sub-chain premise can still be a sub-property chain axiom
		return chainPremise.accept(new ExpressionVisitor<Void, DerivedExpression>() {

			@Override
			public DerivedExpression visit(DerivedAxiomExpression<? extends ElkAxiom> expr, Void input) {
				DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> chainAxiom = (DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>) expr;
				IndexedPropertyChain sub = chainAxiom.getAxiom().getSubObjectPropertyExpression().accept(indexer);
				IndexedPropertyChain sup = chainAxiom.getAxiom().getSuperObjectPropertyExpression().accept(indexer);
				
				return updateSuperChain(lemmaPremise, sub, sup, reader);
			}

			@Override
			public DerivedExpression visit(LemmaExpression<?> chainExpr, Void input) {
				LemmaExpression<ElkSubPropertyChainOfLemma> chainLemma = (LemmaExpression<ElkSubPropertyChainOfLemma>) chainExpr;
				IndexedPropertyChain sub = chainLemma.getLemma().getSubPropertyChain().accept(indexer);
				IndexedPropertyChain sup = chainLemma.getLemma().getSuperPropertyChain().accept(indexer);
				
				return updateSuperChain(lemmaPremise, sub, sup, reader);
			}
		}, null);
	}
	
	static DerivedAxiomExpression<ElkSubClassOfAxiom> createExistentialPremise(
			DerivedAxiomExpression<ElkSubClassOfAxiom> exPremise, DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propPremise, ReasonerInferenceReader reader) {
		if (isTautology(propPremise)) {
			return exPremise;
		}
		
		ElkSubObjectPropertyOfAxiom propPremiseAxiom = propPremise.getAxiom();
		IndexObjectConverter indexer = reader.getIndexer();
		IndexedPropertyChain sup = propPremiseAxiom.getSuperObjectPropertyExpression().accept(indexer);
		
		return createExistentialPremise(exPremise, (IndexedObjectProperty) sup, reader);
	}
	
	static DerivedAxiomExpression<ElkSubClassOfAxiom> createExistentialPremise(DerivedAxiomExpression<ElkSubClassOfAxiom> exPremise, IndexedObjectProperty sup, ReasonerInferenceReader reader) {
		ElkObjectSomeValuesFrom existential = (ElkObjectSomeValuesFrom) exPremise.getAxiom().getSuperClassExpression();
		IndexObjectConverter indexer = reader.getIndexer();
		IndexedPropertyChain sub = existential.getProperty().accept(indexer);

		return updateSuperProperty(exPremise.getAxiom(), (IndexedObjectProperty) sub, (IndexedObjectProperty) sup, reader);
	}
	
	private static boolean isTautology(DerivedExpression premise) {
		return premise.accept(new TautologyChecker(), null);
	}
	
	@Override
	public boolean equals(Object obj) {
		return lemma_.equals(obj);
	}

	@Override
	public int hashCode() {
		return lemma_.hashCode();
	}

	@Override
	public String toString() {
		return lemma_.toString();
	}
	
}
