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
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialLemmaChainComposition;
import org.semanticweb.elk.proofs.inferences.classes.NaryExistentialLemmaComposition;
import org.semanticweb.elk.proofs.inferences.classes.ReflexiveExistentialComposition;
import org.semanticweb.elk.proofs.inferences.mapping.Deindexer;
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
		final ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();
		final List<Inference> inferences = new ArrayList<Inference>();
		
		for (Inference inf : lemma_.getInferences()) {
			Inference transformed = inf.accept(new AbstractInferenceVisitor<Void, Inference>() {

				@Override
				protected Inference defaultVisit(Inference inference, Void input) {
					// shouldn't get here
					return null;
				}

				@Override
				public Inference visit(ExistentialLemmaChainComposition lemmaInf, Void input) {
					ElkSubObjectPropertyOfAxiom firstPropAxiom = lemmaInf.getFirstPropertyPremise().getAxiom();
					ElkSubClassOfAxiom firstPremiseAxiom = lemmaInf.getFirstExistentialPremise().getAxiom();
					DerivedAxiomExpression<ElkSubClassOfAxiom> firstExPremise = createExistentialPremise(
							getSubClass(), 
							(ElkObjectProperty) firstPropAxiom.getSubObjectPropertyExpression(), 
							((ElkObjectSomeValuesFrom) firstPremiseAxiom.getSuperClassExpression()).getFiller(), 
							elkFactory);
					List<DerivedExpression> premises = new ArrayList<DerivedExpression>();
					List<DerivedExpression> otherExPremises = createExistentialPremise(lemmaInf.getSecondExistentialPremise(), lemmaInf.getSecondPropertyPremise(), reader_);
					
					premises.add(firstExPremise);
					premises.addAll(otherExPremises);
					// continue producing inferences with lemmas
					return new NaryExistentialLemmaComposition(lemma_, premises);
				}
			}, null);
			
			inferences.add(transformed);
		}
		
		return inferences;
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
	
	//TODO
	List<DerivedExpression> eliminateReflexivity() {
		if (superChain_ instanceof IndexedObjectProperty) {
			return Collections.<DerivedExpression>singletonList(this);
		}
		
		IndexedBinaryPropertyChain superChain = (IndexedBinaryPropertyChain) superChain_;
		IndexedObjectProperty left = superChain.getLeftProperty();
		IndexedPropertyChain right = superChain.getRightProperty();
		
		if (left.getSaturated().isDerivedReflexive()) {
			DerivedAxiomExpression<?> reflExistential = createReflexiveExistential(getSubClass(), left.getElkObjectProperty(), reader_);
						
			return Arrays.asList(reflExistential, updateSuperChain(getLemma(), subChain_, right, reader_));
		}
		else if (right.getSaturated().isDerivedReflexive()) {
			// all properties comprising the right chain are derived reflexive so we add all reflexive existentials in one go
			List<DerivedExpression> existentials = new ArrayList<DerivedExpression>();
			IndexedPropertyChain top = right;
			
			existentials.add(updateSuperChain(getLemma(), subChain_, left, reader_));
			
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
			
			return existentials;
		}
		
		return Collections.<DerivedExpression>singletonList(this);
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
				
				return new SuperPropertyExistential(
						reader.getExpressionFactory().create(conclusionAxiom), subChain, iop, reader);
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

	static DerivedAxiomExpression<ElkSubClassOfAxiom> createReflexiveExistential(ElkClassExpression ce, ElkObjectProperty prop, ReasonerInferenceReader reader) {
		ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();
		ElkSubClassOfAxiom reflExistential = elkFactory.getSubClassOfAxiom(ce, elkFactory.getObjectSomeValuesFrom(prop, ce));
		DerivedAxiomExpression<ElkSubClassOfAxiom> expr = reader.getExpressionFactory().create(reflExistential);
		DerivedAxiomExpressionWrap<ElkSubClassOfAxiom> wrap = new DerivedAxiomExpressionWrap<ElkSubClassOfAxiom>(expr);
		
		wrap.addInference(new ReflexiveExistentialComposition(reflExistential, elkFactory.getReflexiveObjectPropertyAxiom(prop), reader.getExpressionFactory()));
		
		return wrap;
	}

	static List<DerivedExpression> createExistentialPremise(DerivedExpression classPremise, DerivedExpression chainPremise, ReasonerInferenceReader reader) {
		if (classPremise instanceof DerivedAxiomExpression) {
			DerivedAxiomExpression<ElkSubClassOfAxiom> exPremise = (DerivedAxiomExpression<ElkSubClassOfAxiom>) classPremise;
			
			return Collections.<DerivedExpression>singletonList(
					createExistentialPremise(
							(DerivedAxiomExpression<ElkSubClassOfAxiom>) classPremise, 
							(DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>) chainPremise,
							reader));
		}
		// continue to unwind lemmas
		ElkSubClassOfLemma lemmaPremise = ((LemmaExpression<ElkSubClassOfLemma>) classPremise).getLemma();
		IndexObjectConverter indexer = reader.getIndexer();
		LemmaExpression<ElkSubPropertyChainOfLemma> chainLemma = (LemmaExpression<ElkSubPropertyChainOfLemma>) chainPremise;
		IndexedPropertyChain sub = chainLemma.getLemma().getSubPropertyChain().accept(indexer);
		IndexedPropertyChain sup = chainLemma.getLemma().getSuperPropertyChain().accept(indexer);
		
		DerivedExpression premise = updateSuperChain(lemmaPremise, sub, sup, reader);
		
		return premise instanceof SuperChainExistential ? ((SuperChainExistential) premise).eliminateReflexivity() : Collections.singletonList(premise);
		
	}
	
	static DerivedAxiomExpression<ElkSubClassOfAxiom> createExistentialPremise(
			DerivedAxiomExpression<ElkSubClassOfAxiom> exPremise, DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propPremise, ReasonerInferenceReader reader) {
		ElkSubObjectPropertyOfAxiom propPremiseAxiom = propPremise.getAxiom();
		IndexObjectConverter indexer = reader.getIndexer();
		IndexedPropertyChain sub = propPremiseAxiom.getSubObjectPropertyExpression().accept(indexer);
		IndexedPropertyChain sup = propPremiseAxiom.getSuperObjectPropertyExpression().accept(indexer);
		
		if (sub.equals(sup)) {
			return exPremise;
		}
		
		return new SuperPropertyExistential(exPremise, (IndexedObjectProperty) sub, (IndexedObjectProperty) sup, reader);
	}
	
	protected DerivedAxiomExpression<ElkSubClassOfAxiom> createExistentialPremise(ElkClassExpression subClass, ElkObjectProperty property, ElkClassExpression filler, ElkObjectFactory elkFactory) {
		return reader_.getExpressionFactory().create(elkFactory.getSubClassOfAxiom(subClass, elkFactory.getObjectSomeValuesFrom(property, filler)));
	}
	
	protected ElkSubObjectPropertyOfAxiom lookup(IndexedPropertyChain sub, IndexedObjectProperty sup, ElkObjectFactory elkFactory) {
		return elkFactory.getSubObjectPropertyOfAxiom(Deindexer.deindex(sub), sup.getElkObjectProperty());
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
