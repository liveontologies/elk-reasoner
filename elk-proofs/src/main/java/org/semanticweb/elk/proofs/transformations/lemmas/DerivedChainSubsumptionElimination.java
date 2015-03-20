/**
 * 
 */
package org.semanticweb.elk.proofs.transformations.lemmas;
/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaObjectFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.impl.ElkLemmaObjectFactoryImpl;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialChainAxiomComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialLemmaChainComposition;
import org.semanticweb.elk.proofs.inferences.classes.NaryExistentialLemmaComposition;
import org.semanticweb.elk.proofs.inferences.classes.ReflexiveExistentialComposition;
import org.semanticweb.elk.proofs.inferences.properties.AbstractSubPropertyChainInference;
import org.semanticweb.elk.proofs.inferences.properties.SubPropertyChainAxiom;
import org.semanticweb.elk.proofs.inferences.properties.SubsumptionViaLeftReflexivity;
import org.semanticweb.elk.proofs.inferences.properties.SubsumptionViaRightReflexivity;
import org.semanticweb.elk.proofs.utils.ProofUtils;
import org.semanticweb.elk.proofs.utils.TautologyChecker;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Pair;

/**
 * This class transforms existential and composition inferences, that is,
 * {@link ExistentialComposition}, {@link ExistentialChainAxiomComposition}, and
 * {@link ExistentialLemmaChainComposition}, by transforming class subsumptions
 * with existentials on the right, e.g. C <= RR some D, given the derivation of
 * RR <= SS.
 * 
 * It may return instances of {@link NaryExistentialLemmaComposition} which are
 * then eliminated by {@link LemmaElimination} by replacing the conclusion by
 * the premises.
 * 
 * This class guarantees that the transformed inferences do not have non-trivial
 * property subsumption inferences.
 * 
 * @author Pavel Klinov pavel.klinov@uni-ulm.de
 *
 */
public class DerivedChainSubsumptionElimination {
	
	private final ReasonerInferenceReader reader_;
	
	private final ElkObjectFactory elkFactory_ = new ElkObjectFactoryImpl();
	
	private final ElkLemmaObjectFactory lemmaFactory_ = new ElkLemmaObjectFactoryImpl();
	
	DerivedChainSubsumptionElimination(ReasonerInferenceReader reader) {
		reader_ = reader;
	}

	Iterable<? extends Inference> transform(final ExistentialComposition inference) {
		DerivedAxiomExpression<ElkSubClassOfAxiom> conclusion = inference.getConclusion();
		DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propertyPremise = inference.getSubPropertyPremise();
		
		if (isTautology(propertyPremise) || propertyPremise.isAsserted()) {
			// told hierarchies are OK here
			return Collections.singleton(inference);
		}

		DerivedExpression expandedExistential = rewriteUnderPropertyChainHierarchy(inference.getExistentialPremise(), propertyPremise);
		
		if (expandedExistential.equals(inference.getConclusion())) {
			// this is what happens if the subsumer premise is trivial so the
			// whole existential composition is just due to the property
			// subsumption
			return ProofUtils.getInferences(expandedExistential);
		}
		
		ExistentialComposition result = new ExistentialComposition(
				conclusion,
				inference.getSubsumerPremise(),
				expandedExistential, 
				createTrivialPropertyPremise(propertyPremise.getAxiom().getSuperObjectPropertyExpression()));
		
		return Collections.singleton(result);
	}
	
	ExistentialChainAxiomComposition transform(final ExistentialChainAxiomComposition original) {
		DerivedAxiomExpression<ElkSubClassOfAxiom> conclusion = original.getConclusion();
		DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propertyPremise = original.getFirstPropertyPremise();
		DerivedExpression secondPropertyPremise = original.getSecondPropertyPremise();
		DerivedAxiomExpression<ElkSubClassOfAxiom> firstExPremise = original.getFirstExistentialPremise();
		DerivedExpression secondExPremise = original.getSecondExistentialPremise();

		firstExPremise = (DerivedAxiomExpression<ElkSubClassOfAxiom>) rewriteUnderPropertyChainHierarchy(firstExPremise, propertyPremise);
		secondExPremise = rewriteUnderPropertyChainHierarchy(secondExPremise, secondPropertyPremise);
		
		ExistentialChainAxiomComposition transformed = new ExistentialChainAxiomComposition(
				conclusion,
				firstExPremise,
				secondExPremise, 
				createTrivialPropertyPremise(propertyPremise.getAxiom().getSuperObjectPropertyExpression()),
				createTrivialChainPremise(secondPropertyPremise),
				original.getChainPremise());
		
		return transformed;
	}
	
	ExistentialLemmaChainComposition transform(final ExistentialLemmaChainComposition original) {
		LemmaExpression<ElkSubClassOfLemma> conclusion = original.getConclusion();
		DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propertyPremise = original.getFirstPropertyPremise();
		DerivedExpression secondPropertyPremise = original.getSecondPropertyPremise();
		DerivedAxiomExpression<ElkSubClassOfAxiom> firstExPremise = original.getFirstExistentialPremise();
		DerivedExpression secondExPremise = original.getSecondExistentialPremise();

		firstExPremise = (DerivedAxiomExpression<ElkSubClassOfAxiom>) rewriteUnderPropertyChainHierarchy(firstExPremise, propertyPremise);
		secondExPremise = rewriteUnderPropertyChainHierarchy(secondExPremise, secondPropertyPremise);
		
		ExistentialLemmaChainComposition transformed = new ExistentialLemmaChainComposition(
				conclusion,
				firstExPremise,
				secondExPremise, 
				createTrivialPropertyPremise(propertyPremise.getAxiom().getSuperObjectPropertyExpression()),
				createTrivialChainPremise(secondPropertyPremise));
		
		return transformed;
	}
	
	/**
	 * Recursively transforms a pair of expressions C <= RR some D, RR <= SS
	 * into C <= SS some D and creates inferences for it based on the derivation
	 * of RR <= SS.
	 * 
	 * @param existentialPremise
	 *            C <= RR some D
	 * @param propertyPremise
	 *            RR <= SS
	 * @return C <= SS some D with the corresponding inferences
	 */
	private DerivedExpression rewriteUnderPropertyChainHierarchy(final DerivedExpression existentialPremise, final DerivedExpression propertyPremise) {
		if (isTautology(propertyPremise)) {
			// return as is
			return existentialPremise;
		}
		
		final Set<DerivedExpression> seen = new ArrayHashSet<DerivedExpression>();
		final Queue<Pair<DerivedExpression, ? extends DerivedExpression>> todo = new ArrayDeque<Pair<DerivedExpression, ? extends DerivedExpression>>();
		final DerivedExpressionWrap<?> result = createConclusion(getSubClass(existentialPremise), getSuperPropertyExpression(propertyPremise), getFiller(existentialPremise));

		todo.add(Pair.create(propertyPremise, existentialPremise));

		for (;;) {
			Pair<DerivedExpression, ? extends DerivedExpression> next = todo.poll();

			if (next == null) {
				break;
			}

			final DerivedExpression propertyExpr = next.getFirst();
			final DerivedExpression exPremise = next.getSecond();
			// going through all inferences which derive the current sub-property chain premise
			for (Inference propertyExprInf : ProofUtils.getInferences(propertyExpr)) {
				propertyExprInf.accept(new AbstractInferenceVisitor<Void, Void>() {

					@Override
					public Void visitSubChainInference(AbstractSubPropertyChainInference<?> inf, Void input) {
						if (isTautology(inf.getSecondPremise())) {
							// the base case
							result.addInference(getInferenceForToldSubsumption(
									(DerivedAxiomExpressionWrap<ElkSubClassOfAxiom>) result, 
									exPremise, 
									(DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>) inf.getFirstPremise()));
							
							return null;
						}

						// continue recursively
						DerivedExpression derivedPremise = inf.getSecondPremise();
						DerivedExpressionWrap<?> nextConclusion = createConclusion(getSubClass(exPremise), getSubPropertyExpression(derivedPremise), getFiller(exPremise));
						// this inference will be saved for the conclusion so we can explain the latter even though we may not have recorded inferences for it 
						addInferenceForDirectSubsumption(nextConclusion, exPremise, inf.getFirstPremise());

						if (seen.add(derivedPremise)) {
							todo.add(Pair.create(derivedPremise, nextConclusion));
						}

						return null;
					}

					@Override
					public Void visit(SubsumptionViaRightReflexivity inf, Void input) {
						if (isTautology(inf.getSubsumptionPremise())) {
							// base case
							result.addInference(getInferenceForRightReflexivity((LemmaExpression<ElkSubClassOfLemma>) result, exPremise, inf));
							
							return null;
						}
						
						DerivedExpressionWrap<?> nextConclusion = createConclusion(getSubClass(existentialPremise), getSubPropertyExpression(inf.getSubsumptionPremise()), getFiller(exPremise));
						
						nextConclusion.addInference(getInferenceForRightReflexivity((LemmaExpression<ElkSubClassOfLemma>) nextConclusion, exPremise, inf));
						// continue recursively
						if (seen.add(inf.getSubsumptionPremise())) {
							todo.add(Pair.create(inf.getSubsumptionPremise(), nextConclusion));
						}
						
						return null;
					}

					@Override
					public Void visit(SubsumptionViaLeftReflexivity inf, Void input) {
						if (isTautology(inf.getSubsumptionPremise())) {
							// base case
							result.addInference(getInferenceForLeftReflexivity((LemmaExpression<ElkSubClassOfLemma>) result, exPremise, inf));
							
							return null;
						}
						
						DerivedExpressionWrap<?> nextConclusion = createConclusion(getSubClass(existentialPremise), getSubPropertyExpression(inf.getSubsumptionPremise()), getFiller(exPremise));
						
						nextConclusion.addInference(getInferenceForLeftReflexivity((LemmaExpression<ElkSubClassOfLemma>) nextConclusion, exPremise, inf));
						// continue recursively
						if (seen.add(inf.getSubsumptionPremise())) {
							todo.add(Pair.create(inf.getSubsumptionPremise(), nextConclusion));
						}
						
						return null;
					}

				}, null);

			}
		}

		return result;
	}
	
	DerivedExpressionWrap<?> createConclusion(final ElkClassExpression subClass, final ElkSubObjectPropertyExpression subObjectPropertyExpression, final ElkClassExpression filler) {
		final DerivedExpressionFactory exprFactory = reader_.getExpressionFactory();
		
		return subObjectPropertyExpression.accept(new BaseElkSubObjectPropertyExpressionVisitor<DerivedExpressionWrap<?>>() {

			@Override
			public DerivedExpressionWrap<?> visit(ElkObjectProperty property) {
				return new DerivedAxiomExpressionWrap<ElkSubClassOfAxiom>(
						exprFactory.create(elkFactory_.getSubClassOfAxiom(subClass, elkFactory_.getObjectSomeValuesFrom(property, filler))));
			}

			@Override
			public DerivedExpressionWrap<?> visit(ElkObjectPropertyChain chain) {
				return new DerivedLemmaExpressionWrap<ElkSubClassOfLemma>(
						exprFactory.create(lemmaFactory_.getSubClassOfLemma(subClass, lemmaFactory_.getComplexObjectSomeValuesFrom(chain, filler))));
			}
		});
	}
	
	void addInferenceForDirectSubsumption(final DerivedExpressionWrap<?> conclusion, final DerivedExpression exPremise, final DerivedExpression directPropertyPremise) {
		for (Inference directPropInf : ProofUtils.getInferences(directPropertyPremise)) {
			Inference inf = directPropInf.accept(new AbstractInferenceVisitor<Void, Inference>() {

				@Override
				public Inference visit(SubPropertyChainAxiom inf, Void input) {
					return getInferenceForToldSubsumption((DerivedAxiomExpression<ElkSubClassOfAxiom>) conclusion, exPremise, inf.getConclusion());
				}

				@Override
				public Inference visit(SubsumptionViaRightReflexivity inf, Void input) {
					return getInferenceForRightReflexivity((LemmaExpression<ElkSubClassOfLemma>) conclusion, exPremise, inf);
				}

				@Override
				public Inference visit(SubsumptionViaLeftReflexivity inf, Void input) {
					return getInferenceForLeftReflexivity((LemmaExpression<ElkSubClassOfLemma>) conclusion, exPremise, inf);
				}
				
			}, null);
			
			conclusion.addInference(inf);
		}
	}

	Inference getInferenceForToldSubsumption(
			DerivedAxiomExpression<ElkSubClassOfAxiom> conclusion, 
			DerivedExpression exPremise, 
			DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> toldPropertyPremise) {
		return new ExistentialComposition(conclusion, createTrivialSubsumerPremise(exPremise), exPremise, toldPropertyPremise);
	}
	
	Inference getInferenceForLeftReflexivity(
			LemmaExpression<ElkSubClassOfLemma> conclusion, 
			DerivedExpression exPremise, 
			SubsumptionViaLeftReflexivity inf) {
		ElkObjectPropertyExpression property = inf.getReflexivityPremise().getAxiom().getProperty();
		
		return new NaryExistentialLemmaComposition(conclusion, Arrays.<DerivedExpression>asList(createReflexivePremise(getSubClass(exPremise), property), exPremise));
	}
	
	Inference getInferenceForRightReflexivity(
			LemmaExpression<ElkSubClassOfLemma> conclusion, 
			DerivedExpression exPremise, 
			SubsumptionViaRightReflexivity inf) {
		List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> reflPremises = getReflexivityPremises(inf.getReflexivityPremise());
		List<DerivedExpression> premises = new ArrayList<DerivedExpression>(reflPremises.size() + 1);
		
		premises.add(exPremise);
		
		for (DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom> reflPremise : reflPremises) {
			premises.add(createReflexivePremise(getFiller(exPremise), reflPremise.getAxiom().getProperty()));
		}
		
		return new NaryExistentialLemmaComposition((LemmaExpression<ElkSubClassOfLemma>) conclusion, premises);
	}
	
	DerivedAxiomExpression<ElkSubClassOfAxiom> createReflexivePremise(ElkClassExpression ce, ElkObjectPropertyExpression prop) {
		DerivedExpressionFactory exprFactory = reader_.getExpressionFactory();
		ElkSubClassOfAxiom reflExistential = elkFactory_.getSubClassOfAxiom(ce, elkFactory_.getObjectSomeValuesFrom(prop, ce));
		DerivedAxiomExpression<ElkSubClassOfAxiom> expr = exprFactory.create(reflExistential);
		DerivedAxiomExpressionWrap<ElkSubClassOfAxiom> wrap = new DerivedAxiomExpressionWrap<ElkSubClassOfAxiom>(expr);
		
		wrap.addInference(new ReflexiveExistentialComposition(wrap, exprFactory.create(elkFactory_.getReflexiveObjectPropertyAxiom(prop))));
		
		return wrap;
	}

	DerivedAxiomExpression<ElkSubClassOfAxiom> createTrivialSubsumerPremise(DerivedExpression existentialPremise) {
		ElkClassExpression filler = getFiller(existentialPremise);
		
		return reader_.getExpressionFactory().create(elkFactory_.getSubClassOfAxiom(filler, filler));
	}
	
	DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> createTrivialPropertyPremise(ElkObjectPropertyExpression property) {
		return reader_.getExpressionFactory().create(elkFactory_.getSubObjectPropertyOfAxiom(property, property));
	}
	
	DerivedExpression createTrivialChainPremise(DerivedExpression propertyExpression) {
		return propertyExpression.accept(new BaseExpressionVisitor<Void, DerivedExpression>() {

			@Override
			public DerivedExpression visit(ElkSubPropertyChainOfLemma lemma, Void input) {
				ElkObjectPropertyChain chain = lemma.getSuperPropertyChain();
				
				return reader_.getExpressionFactory().create(lemmaFactory_.getSubPropertyChainOfLemma(chain, chain));
			}

			@Override
			public DerivedExpression visit(ElkSubObjectPropertyOfAxiom axiom) {
				return createTrivialPropertyPremise(axiom.getSuperObjectPropertyExpression());
			}
			
		}, null);
	}
	
	// transforms the reflexive property chain into the corresponding list of reflexive properties
	List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> getReflexivityPremises(DerivedExpression reflexivityPremise) {
		final DerivedExpressionFactory exprFactory = reader_.getExpressionFactory();
		
		return reflexivityPremise.accept(new BaseExpressionVisitor<Void, List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>>>() {

			@Override
			public List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> visit(ElkReflexivePropertyChainLemma lemma, Void input) {
				ElkSubObjectPropertyExpression chain = lemma.getPropertyChain();
				
				return chain.accept(new BaseElkSubObjectPropertyExpressionVisitor<List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>>>() {

					@Override
					public List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> visit(ElkObjectProperty property) {
						return Collections.singletonList(exprFactory.create(elkFactory_.getReflexiveObjectPropertyAxiom(property)));
					}

					@Override
					public List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> visit(ElkObjectPropertyChain chain) {
						List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> premises = new ArrayList<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>>(chain.getObjectPropertyExpressions().size());
						
						for (ElkObjectPropertyExpression property : chain.getObjectPropertyExpressions()) {
							premises.add(exprFactory.create(elkFactory_.getReflexiveObjectPropertyAxiom(property)));
						}
						
						return premises;
					}
				});
			}

			@Override
			public List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> visit(ElkReflexiveObjectPropertyAxiom axiom) {
				return Collections.singletonList(exprFactory.create(axiom));
			}
			
		}, null);
	}
	
	private ElkClassExpression getSubClass(DerivedExpression expr) {
		return expr.accept(new BaseExpressionVisitor<Void, ElkClassExpression>() {

			@Override
			public ElkClassExpression visit(ElkSubClassOfLemma lemma, Void input) {
				return lemma.getSubClass();
			}

			@Override
			public ElkClassExpression visit(ElkSubClassOfAxiom axiom) {
				return axiom.getSubClassExpression();
			}
			
		}, null);
	}
	
	private ElkClassExpression getFiller(DerivedExpression expr) {
		return expr.accept(new BaseExpressionVisitor<Void, ElkClassExpression>() {

			@Override
			public ElkClassExpression visit(ElkSubClassOfLemma lemma, Void input) {
				return ((ElkComplexObjectSomeValuesFrom) lemma.getSuperClass()).getFiller();
			}

			@Override
			public ElkClassExpression visit(ElkSubClassOfAxiom axiom) {
				return ((ElkObjectSomeValuesFrom) axiom.getSuperClassExpression()).getFiller();
			}
			
		}, null);
	}
	
	private ElkSubObjectPropertyExpression getSuperPropertyExpression(DerivedExpression expr) {
		return expr.accept(new BaseExpressionVisitor<Void, ElkSubObjectPropertyExpression>() {

			@Override
			public ElkSubObjectPropertyExpression visit(ElkSubPropertyChainOfLemma lemma, Void input) {
				return lemma.getSuperPropertyChain();
			}

			@Override
			public ElkSubObjectPropertyExpression visit(ElkSubObjectPropertyOfAxiom axiom) {
				return axiom.getSuperObjectPropertyExpression();
			}
			
		}, null);
	}
	
	private ElkSubObjectPropertyExpression getSubPropertyExpression(DerivedExpression expr) {
		return expr.accept(new BaseExpressionVisitor<Void, ElkSubObjectPropertyExpression>() {

			@Override
			public ElkSubObjectPropertyExpression visit(ElkSubPropertyChainOfLemma lemma, Void input) {
				return lemma.getSubPropertyChain();
			}

			@Override
			public ElkSubObjectPropertyExpression visit(ElkSubObjectPropertyOfAxiom axiom) {
				return axiom.getSubObjectPropertyExpression();
			}
			
		}, null);
	}

	static boolean isTautology(DerivedExpression expr) {
		return expr.accept(new TautologyChecker(), null);
	}

	private static abstract class BaseElkSubObjectPropertyExpressionVisitor<O> implements ElkSubObjectPropertyExpressionVisitor<O> {

		@Override
		public O visit(ElkObjectInverseOf elkObjectInverseOf) {
			throw new IllegalArgumentException("Illegal use of inverse property in EL: " + elkObjectInverseOf);
		}
		
	}
	
}
