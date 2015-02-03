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
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
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
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
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
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class DerivedChainSubsumptionElimination {
	
	private final ReasonerInferenceReader reader_;
	
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

		DerivedExpression expandedExistential = expandUnderChainHierarchy(inference.getExistentialPremise(), propertyPremise);
		
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

		firstExPremise = (DerivedAxiomExpression<ElkSubClassOfAxiom>) expandUnderChainHierarchy(firstExPremise, propertyPremise);
		secondExPremise = expandUnderChainHierarchy(secondExPremise, secondPropertyPremise);
		
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

		firstExPremise = (DerivedAxiomExpression<ElkSubClassOfAxiom>) expandUnderChainHierarchy(firstExPremise, propertyPremise);
		secondExPremise = expandUnderChainHierarchy(secondExPremise, secondPropertyPremise);
		
		ExistentialLemmaChainComposition transformed = new ExistentialLemmaChainComposition(
				conclusion,
				firstExPremise,
				secondExPremise, 
				createTrivialPropertyPremise(propertyPremise.getAxiom().getSuperObjectPropertyExpression()),
				createTrivialChainPremise(secondPropertyPremise));
		
		return transformed;
	}
	
	// the key method
	private DerivedExpression expandUnderChainHierarchy(final DerivedExpression existentialPremise, final DerivedExpression propertyPremise) {
		if (isTautology(propertyPremise)) {
			// return as is
			return existentialPremise;
		}
		
		final Set<DerivedExpression> seen = new ArrayHashSet<DerivedExpression>();
		final Queue<Pair<DerivedExpression, DerivedExpression>> todo = new ArrayDeque<Pair<DerivedExpression, DerivedExpression>>();
		final DerivedExpressionWrap<?> result = createConclusion(getSubClass(existentialPremise), getSuperPropertyExpression(propertyPremise), getFiller(existentialPremise));

		todo.add(new Pair<DerivedExpression, DerivedExpression>(propertyPremise, existentialPremise));

		for (;;) {
			Pair<DerivedExpression, DerivedExpression> next = todo.poll();

			if (next == null) {
				break;
			}

			final DerivedExpression propertyExpr = next.getFirst();
			final DerivedExpression exPremise = next.getSecond();
			// going through all inferences which derive the current sub-property chain premise
			for (Inference propertyExprInf : ProofUtils.getInferences(propertyExpr)) {
				propertyExprInf.accept(new AbstractInferenceVisitor<Void, Void>() {

					@Override
					protected Void defaultVisit(Inference inference, Void input) {
						return null;
					}

					@Override
					public Void visitSubChainInference(AbstractSubPropertyChainInference<?> inf, Void input) {
						if (isTautology(inf.getSecondPremise())) {
							// the base case
							addInferenceForDirectSubsumption(result, exPremise, inf.getFirstPremise());
							
							return null;
						}

						// continue recursively
						DerivedExpression derivedPremise = inf.getSecondPremise();
						DerivedExpressionWrap<?> nextConclusion = createConclusion(getSubClass(exPremise), getSubPropertyExpression(derivedPremise), getFiller(exPremise));
						// this inference will be saved for the conclusion so we can explain the latter even though we may not have recorded inferences for it 
						addInferenceForDirectSubsumption(nextConclusion, exPremise, inf.getFirstPremise());

						if (seen.add(derivedPremise)) {
							todo.add(new Pair<DerivedExpression, DerivedExpression>(derivedPremise, nextConclusion));
						}

						return null;
					}

					@Override
					public Void visit(SubsumptionViaRightReflexivity inf, Void input) {
						// the base case
						addInferenceForDirectSubsumption(result, exPremise, inf.getConclusion());
						return null;
					}

					@Override
					public Void visit(SubsumptionViaLeftReflexivity inf, Void input) {
						// the base case
						addInferenceForDirectSubsumption(result, exPremise, inf.getConclusion());
						return null;
					}

				}, null);

			}
		}

		return result;
	}
	
	DerivedExpressionWrap<?> createConclusion(final ElkClassExpression subClass, final ElkSubObjectPropertyExpression subObjectPropertyExpression, final ElkClassExpression filler) {
		final ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();
		final ElkLemmaObjectFactory lemmaFactory = new ElkLemmaObjectFactoryImpl();
		final DerivedExpressionFactory exprFactory = reader_.getExpressionFactory();
		
		return subObjectPropertyExpression.accept(new BaseElkSubObjectPropertyExpressionVisitor<DerivedExpressionWrap<?>>() {

			@Override
			public DerivedExpressionWrap<?> visit(ElkObjectProperty property) {
				return new DerivedAxiomExpressionWrap<ElkSubClassOfAxiom>(
						exprFactory.create(elkFactory.getSubClassOfAxiom(subClass, elkFactory.getObjectSomeValuesFrom(property, filler))));
			}

			@Override
			public DerivedExpressionWrap<?> visit(ElkObjectPropertyChain chain) {
				return new DerivedLemmaExpressionWrap<ElkSubClassOfLemma>(
						exprFactory.create(lemmaFactory.getSubClassOfLemma(subClass, lemmaFactory.getComplexObjectSomeValuesFrom(chain, filler))));
			}
		});
	}
	
	void addInferenceForDirectSubsumption(final DerivedExpressionWrap<?> conclusion, final DerivedExpression exPremise, final DerivedExpression directPropertyPremise) {
		for (Inference directPropInf : ProofUtils.getInferences(directPropertyPremise)) {
			Inference inf = directPropInf.accept(new AbstractInferenceVisitor<Void, Inference>() {

				@Override
				protected Inference defaultVisit(Inference inference, Void input) {
					return null;
				}

				@Override
				public Inference visit(SubPropertyChainAxiom inf, Void input) {
					return new ExistentialComposition(
							(DerivedAxiomExpression<ElkSubClassOfAxiom>) conclusion, 
							createTrivialSubsumerPremise(exPremise), 
							exPremise, 
							inf.getConclusion());
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
			
			if (inf != null) {
				conclusion.addInference(inf);
			}
		}
	}

	void addInferenceForToldSubsumption(
			DerivedAxiomExpressionWrap<ElkSubClassOfAxiom> conclusion, 
			DerivedExpression exPremise, 
			DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> toldPropertyPremise) {
		conclusion.addInference(new ExistentialComposition(conclusion, createTrivialSubsumerPremise(exPremise), exPremise, toldPropertyPremise));
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
		ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();
		DerivedExpressionFactory exprFactory = reader_.getExpressionFactory();
		ElkSubClassOfAxiom reflExistential = elkFactory.getSubClassOfAxiom(ce, elkFactory.getObjectSomeValuesFrom(prop, ce));
		DerivedAxiomExpression<ElkSubClassOfAxiom> expr = exprFactory.create(reflExistential);
		DerivedAxiomExpressionWrap<ElkSubClassOfAxiom> wrap = new DerivedAxiomExpressionWrap<ElkSubClassOfAxiom>(expr);
		
		wrap.addInference(new ReflexiveExistentialComposition(wrap, exprFactory.create(elkFactory.getReflexiveObjectPropertyAxiom(prop))));
		
		return wrap;
	}

	DerivedAxiomExpression<ElkSubClassOfAxiom> createTrivialSubsumerPremise(DerivedExpression existentialPremise) {
		ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();
		ElkClassExpression filler = getFiller(existentialPremise);
		
		return reader_.getExpressionFactory().create(elkFactory.getSubClassOfAxiom(filler, filler));
	}
	
	DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> createTrivialPropertyPremise(ElkObjectPropertyExpression property) {
		ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();
		
		return reader_.getExpressionFactory().create(elkFactory.getSubObjectPropertyOfAxiom(property, property));
	}
	
	DerivedExpression createTrivialChainPremise(DerivedExpression propertyExpression) {
		return propertyExpression.accept(new ExpressionVisitor<Void, DerivedExpression>() {

			@Override
			public DerivedExpression visit(DerivedAxiomExpression<? extends ElkAxiom> expr, Void input) {
				return createTrivialPropertyPremise(((DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>) expr).getAxiom().getSuperObjectPropertyExpression());
			}

			@Override
			public DerivedExpression visit(LemmaExpression<?> expr, Void input) {
				ElkLemmaObjectFactory lemmaFactory = new ElkLemmaObjectFactoryImpl();
				ElkObjectPropertyChain chain = ((LemmaExpression<ElkSubPropertyChainOfLemma>) expr).getLemma().getSuperPropertyChain();
				
				return reader_.getExpressionFactory().create(lemmaFactory.getSubPropertyChainOfLemma(chain, chain));
			}
		}, null);
	}
	
	// transforms the reflexive property chain into the corresponding list of reflexive properties
	List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> getReflexivityPremises(DerivedExpression reflexivityPremise) {
		final ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();
		final DerivedExpressionFactory exprFactory = reader_.getExpressionFactory();
		
		return reflexivityPremise.accept(new ExpressionVisitor<Void, List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>>>() {

			@Override
			public List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> visit(DerivedAxiomExpression<? extends ElkAxiom> expr, Void input) {
				ElkReflexiveObjectPropertyAxiom axiom = ((DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>) expr).getAxiom();
				
				return Collections.singletonList(exprFactory.create(axiom));
			}

			@Override
			public List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> visit(LemmaExpression<?> expr, Void input) {
				ElkReflexivePropertyChainLemma lemma = ((LemmaExpression<ElkReflexivePropertyChainLemma>) expr).getLemma();
				ElkSubObjectPropertyExpression chain = lemma.getPropertyChain();
				
				return chain.accept(new ElkSubObjectPropertyExpressionVisitor<List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>>>() {

					@Override
					public List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> visit(ElkObjectInverseOf elkObjectInverseOf) {
						return null;
					}

					@Override
					public List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> visit(ElkObjectProperty property) {
						return Collections.singletonList(exprFactory.create(elkFactory.getReflexiveObjectPropertyAxiom(property)));
					}

					@Override
					public List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> visit(ElkObjectPropertyChain chain) {
						List<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>> premises = new ArrayList<DerivedAxiomExpression<ElkReflexiveObjectPropertyAxiom>>(chain.getObjectPropertyExpressions().size());
						
						for (ElkObjectPropertyExpression property : chain.getObjectPropertyExpressions()) {
							premises.add(exprFactory.create(elkFactory.getReflexiveObjectPropertyAxiom(property)));
						}
						
						return premises;
					}
				});
			}
		}, null);
	}
	
	private ElkClassExpression getSubClass(DerivedExpression expr) {
		return expr.accept(new ExpressionVisitor<Void, ElkClassExpression>() {

			@Override
			public ElkClassExpression visit(DerivedAxiomExpression<? extends ElkAxiom> expr, Void input) {
				return ((DerivedAxiomExpression<ElkSubClassOfAxiom>) expr).getAxiom().getSubClassExpression();
			}

			@Override
			public ElkClassExpression visit(LemmaExpression<?> expr, Void input) {
				return ((LemmaExpression<ElkSubClassOfLemma>) expr).getLemma().getSubClass();
			}
		}, null);
	}
	
	private ElkClassExpression getFiller(DerivedExpression expr) {
		return expr.accept(new ExpressionVisitor<Void, ElkClassExpression>() {

			@Override
			public ElkClassExpression visit(DerivedAxiomExpression<? extends ElkAxiom> expr, Void input) {
				return ((ElkObjectSomeValuesFrom)((DerivedAxiomExpression<ElkSubClassOfAxiom>) expr).getAxiom().getSuperClassExpression()).getFiller();
			}

			@Override
			public ElkClassExpression visit(LemmaExpression<?> expr, Void input) {
				return ((ElkComplexObjectSomeValuesFrom)((LemmaExpression<ElkSubClassOfLemma>) expr).getLemma().getSuperClass()).getFiller();
			}
		}, null);
	}
	
	private ElkSubObjectPropertyExpression getSuperPropertyExpression(DerivedExpression expr) {
		return expr.accept(new ExpressionVisitor<Void, ElkSubObjectPropertyExpression>() {

			@Override
			public ElkObjectPropertyExpression visit(DerivedAxiomExpression<? extends ElkAxiom> expr, Void input) {
				return ((DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>) expr).getAxiom().getSuperObjectPropertyExpression();
			}

			@Override
			public ElkObjectPropertyChain visit(LemmaExpression<?> expr, Void input) {
				return ((LemmaExpression<ElkSubPropertyChainOfLemma>) expr).getLemma().getSuperPropertyChain();
			}
		}, null);
	}
	
	private ElkSubObjectPropertyExpression getSubPropertyExpression(DerivedExpression expr) {
		return expr.accept(new ExpressionVisitor<Void, ElkSubObjectPropertyExpression>() {

			@Override
			public ElkSubObjectPropertyExpression visit(DerivedAxiomExpression<? extends ElkAxiom> expr, Void input) {
				return ((DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>) expr).getAxiom().getSubObjectPropertyExpression();
			}

			@Override
			public ElkSubObjectPropertyExpression visit(LemmaExpression<?> expr, Void input) {
				return ((LemmaExpression<ElkSubPropertyChainOfLemma>) expr).getLemma().getSubPropertyChain();
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
