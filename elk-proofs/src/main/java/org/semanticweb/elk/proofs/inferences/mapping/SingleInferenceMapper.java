/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.mapping;
/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Collections;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.proofs.expressions.Explanation;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.MultiAxiomExpression;
import org.semanticweb.elk.proofs.expressions.SingleAxiomExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.classes.ClassInitialization;
import org.semanticweb.elk.proofs.inferences.classes.ClassSubsumption;
import org.semanticweb.elk.proofs.inferences.classes.ConjunctionComposition;
import org.semanticweb.elk.proofs.inferences.classes.ConjunctionDecomposition;
import org.semanticweb.elk.proofs.inferences.classes.DisjointnessContradiction;
import org.semanticweb.elk.proofs.inferences.classes.DisjunctionComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialCompositionViaChain;
import org.semanticweb.elk.proofs.inferences.classes.InconsistentDisjointness;
import org.semanticweb.elk.proofs.inferences.classes.NegationContradiction;
import org.semanticweb.elk.proofs.inferences.classes.ReflexiveExistentialComposition;
import org.semanticweb.elk.proofs.inferences.properties.ChainSubsumption;
import org.semanticweb.elk.proofs.inferences.properties.ReflexiveComposition;
import org.semanticweb.elk.proofs.inferences.properties.ReflexivityViaSubsumption;
import org.semanticweb.elk.proofs.inferences.properties.SubsumptionViaReflexivity;
import org.semanticweb.elk.proofs.inferences.properties.ToldReflexivity;
import org.semanticweb.elk.proofs.utils.ProofUtils;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.SideConditionLookup;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromInconsistentDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromNegation;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromOwlNothing;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistentialForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedContradiction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReflexiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.TracedPropagation;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.BottomUpPropertySubsumptionInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.LeftReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.PropertyChainInitialization;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexiveToldSubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.RightReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldReflexiveProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.TopDownPropertySubsumptionInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.util.TracingUtils;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps one or more lower-level inferences to a single {@link Inference}. 
 * It returns {@code null} if the low-level inference does not correspond to any {@link Inference}. 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SingleInferenceMapper {
	
	private static final Logger LOGGER_ = LoggerFactory.getLogger(SingleInferenceMapper.class);
	
	private final TraceStore.Reader traceReader_;
	
	private final ElkObjectFactory factory_;

	private final SideConditionLookup sideConditionLookup_;

	public SingleInferenceMapper(TraceStore.Reader reader) {
		traceReader_ = reader;
		factory_ = new ElkObjectFactoryImpl();
		sideConditionLookup_ = new SideConditionLookup();
	}
	
	public Inference map(ClassInference inference,
			IndexedClassExpression whereStored) {
		LOGGER_.trace("Mapping {} in {}", inference, whereStored);
		
		Inference userInf = inference.acceptTraced(new MappingVisitor(), whereStored);
		
		LOGGER_.trace("Mapped {} in {} => {}", inference, whereStored, userInf);
		
		return userInf;
	}

	public Inference map(ObjectPropertyInference inference) {
		LOGGER_.trace("Mapping {}", inference);
		
		Inference userInf = inference.acceptTraced(new MappingVisitor(), null);
		
		LOGGER_.trace("Mapped {} => {}", inference, userInf);
		
		return userInf;
	}

	/**
	 * Creates an expression which can't be represented as an OWL axiom. It requests all inferences for the given conclusion, maps each of them to the higher-level inference,
	 * and merges their conclusions. In the end, the expression is represented as a collection of explanations (each being a set of axioms) 
	 */
	private Expression createExpression(final IndexedClassExpression root, final Conclusion conclusion) {
		// first, get the inferences for the conclusion
		Iterable<ClassInference> inferences = TracingUtils.getClassInferences(traceReader_, root, conclusion);
		// map each of them recursively and get explanations for conclusions
		Iterable<Iterable<Explanation>> explanations = Operations.map(inferences, new Transformation<ClassInference, Iterable<Explanation>>() {

			@Override
			public Iterable<Explanation> transform(ClassInference inf) {
				// recursion here
				Inference userInf = map(inf, root);
				
				if (userInf == null) {
					throw new IllegalStateException(String.format("Mapping failed for %s in %s", inf, root));
				}
				
				return userInf.getConclusion().getExplanations();
			}
			
		});
		// finally, union explanations for each conclusion
		return ProofUtils.fromInferenceConclusions(explanations);
	}
	
	/**
	 * As above but for object property expressions
	 */
	private Expression createExpression(ObjectPropertyConclusion conclusion) {
		// first, get the inferences for the conclusion
		Iterable<ObjectPropertyInference> inferences = TracingUtils.getObjectPropertyInferences(traceReader_, conclusion);
		// map each of them recursively and get explanations for conclusions
		Iterable<Iterable<Explanation>> explanations = Operations.map(inferences, new Transformation<ObjectPropertyInference, Iterable<Explanation>>() {

			@Override
			public Iterable<Explanation> transform(ObjectPropertyInference inf) {
				// recursion here
				Inference userInf = map(inf); 
				
				return userInf == null ? noExplanations() : userInf.getConclusion().getExplanations();
			}
			
		});
		// finally, union explanations for each conclusion
		return ProofUtils.fromInferenceConclusions(explanations);
	}
	
	// for initialization inferences
	private Iterable<Explanation> noExplanations() {
		return Collections.singletonList(new Explanation(Collections.<ElkAxiom>emptyList()));
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class MappingVisitor
			implements
			org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor<IndexedClassExpression, Inference>,
			org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor<Void, Inference> {

		@Override
		public Inference visit(InitializationSubsumer<?> inference,
				IndexedClassExpression parameter) {
			return new ClassInitialization(
					Deindexer.deindex(inference.getExpression()), factory_);
		}

		@Override
		public Inference visit(SubClassOfSubsumer<?> inference,
				IndexedClassExpression parameter) {
			ElkAxiom sideCondition = sideConditionLookup_.lookup(inference);
			
			return new ClassSubsumption(sideCondition, Deindexer.deindex(parameter),
					Deindexer.deindex(inference.getExpression()),
					Deindexer.deindex(inference.getPremise().getExpression()),
					factory_);
		}

		@Override
		public Inference visit(ComposedConjunction inference,
				IndexedClassExpression input) {
			return new ConjunctionComposition(Deindexer.deindex(input),
					Deindexer.deindex(inference.getFirstConjunct()
							.getExpression()), Deindexer.deindex(inference
							.getSecondConjunct().getExpression()), factory_);
		}

		@Override
		public Inference visit(DecomposedConjunction inference,
				IndexedClassExpression input) {
			IndexedClassExpression conclusion = inference.getExpression();
			IndexedObjectIntersectionOf conjunction = inference
					.getConjunction().getExpression();
			ElkClassExpression sub = Deindexer.deindex(input);
			ElkClassExpression other = Deindexer
					.deindex(conclusion == conjunction.getFirstConjunct() ? conjunction
							.getSecondConjunct() : conjunction
							.getFirstConjunct());

			return new ConjunctionDecomposition(sub, Deindexer.deindex(conclusion),
					other, factory_);
		}

		@Override
		public Inference visit(PropagatedSubsumer inference,
				IndexedClassExpression whereStored) {
			// the left premise is an axiom with a simple existential on the right
			ElkClassExpression c = Deindexer.deindex(whereStored);
			ElkObjectProperty r = Deindexer.deindex(inference.getBackwardLink().getRelation());
			ElkClassExpression d = Deindexer.deindex(inference.getInferenceContextRoot(whereStored));
			ElkObjectProperty s = Deindexer.deindex(inference.getExpression().getRelation());
			ElkClassExpression e = Deindexer.deindex(inference.getExpression().getFiller());
			ElkObjectSomeValuesFrom rSomeD = factory_.getObjectSomeValuesFrom(r, d);
			ElkObjectSomeValuesFrom sSomeE = factory_.getObjectSomeValuesFrom(s, e);
			
			return new ExistentialComposition(c, sSomeE,
					factory_.getSubClassOfAxiom(c, rSomeD),
					factory_.getSubClassOfAxiom(d, e),
					factory_.getSubObjectPropertyOfAxiom(r, s), factory_);
		}

		@Override
		public Inference visit(ReflexiveSubsumer<?> inference,
				IndexedClassExpression input) {
			ElkClassExpression sub = Deindexer.deindex(input);
			ElkClassExpression sup = Deindexer.deindex(inference
					.getExpression());
			ElkSubClassOfAxiom subsumerPremise = factory_.getSubClassOfAxiom(
					sub, sup);
			ElkObjectProperty property = inference.getReflexivityPremise()
					.getPropertyChain().getElkObjectProperty();
			ElkSubObjectPropertyOfAxiom propertySubsumption = factory_
					.getSubObjectPropertyOfAxiom(property,
							Deindexer.deindex(inference.getRelation()));

			return new ReflexiveExistentialComposition(Deindexer.deindex(input),
					subsumerPremise,
					factory_.getReflexiveObjectPropertyAxiom(property),
					propertySubsumption, factory_);
		}

		@Override
		public Inference visit(ComposedBackwardLink inference,
				IndexedClassExpression whereStored) {
			SubObjectProperty leftPropertyPremise = inference.getLeftSubObjectProperty();
			SubPropertyChain<?,?> rightChainPremise = inference.getRightSubObjectPropertyChain();			
			ElkObjectProperty r = Deindexer.deindex(leftPropertyPremise.getSubPropertyChain());
			ElkClassExpression c = Deindexer.deindex(inference.getBackwardLink().getSource());
			ElkClassExpression d = Deindexer.deindex(inference.getInferenceContextRoot(whereStored));
			ElkClassExpression rSomeD = factory_.getObjectSomeValuesFrom(r, d);
			ElkClassExpression e = Deindexer.deindex(inference.getForwardLink().getTarget());
			ElkObjectProperty h = Deindexer.deindex(inference.getRelation());
			ElkClassExpression hSomeE = factory_.getObjectSomeValuesFrom(h, e);
			// the first premise is a simple axiom with an existential on the right
			ElkSubClassOfAxiom firstExPremise = factory_.getSubClassOfAxiom(c, rSomeD);
			ElkObjectProperty rPrime = Deindexer.deindex(leftPropertyPremise.getSuperPropertyChain());
			ElkSubObjectPropertyOfAxiom propSubsumption = factory_.getSubObjectPropertyOfAxiom(r, rPrime);
			// TODO will get rid of this cast later
			ElkSubObjectPropertyOfAxiom chainAxiom = (ElkSubObjectPropertyOfAxiom) sideConditionLookup_.lookup(inference);			
			
			if (inference.getForwardLink().getRelation() instanceof IndexedBinaryPropertyChain) {
				// complex right property premise
				// the second premise is not representable as an OWL axiom
				Expression secondExPremise = createExpression(inference.getInferenceContextRoot(whereStored), inference.getForwardLink());
				Expression chainSubsumption = createExpression(rightChainPremise);
				
				return new ExistentialCompositionViaChain(hSomeE, firstExPremise, secondExPremise, propSubsumption, chainSubsumption, chainAxiom, factory_);
			}
			else {
				// the right property premise is a simple property inclusion axiom
				ElkObjectProperty ss = Deindexer.deindex((IndexedObjectProperty) rightChainPremise.getSubPropertyChain());
				ElkObjectProperty ssPrime = Deindexer.deindex((IndexedObjectProperty) rightChainPremise.getSuperPropertyChain());
				ElkClassExpression ssSomeE = factory_.getObjectSomeValuesFrom(ss, e);
				Expression secondExPremise = new SingleAxiomExpression(factory_.getSubClassOfAxiom(d, ssSomeE));
				ElkSubObjectPropertyOfAxiom chainSubsumption = factory_.getSubObjectPropertyOfAxiom(ss, ssPrime);
				
				return new ExistentialCompositionViaChain(hSomeE, firstExPremise, secondExPremise, propSubsumption, chainSubsumption, chainAxiom, factory_);
			}
		}

		@Override
		public Inference visit(ComposedForwardLink inference, IndexedClassExpression whereStored) {
			SubObjectProperty leftPropertyPremise = inference.getLeftSubObjectProperty();
			SubPropertyChain<?,?> rightChainPremise = inference.getRightSubObjectPropertyChain();			
			ElkObjectProperty r = Deindexer.deindex(leftPropertyPremise.getSubPropertyChain());
			ElkClassExpression c = Deindexer.deindex(whereStored);
			ElkClassExpression d = Deindexer.deindex(inference.getInferenceContextRoot(whereStored));
			ElkClassExpression rSomeD = factory_.getObjectSomeValuesFrom(r, d);
			ElkClassExpression e = Deindexer.deindex(inference.getTarget());
			// the first premise is a simple axiom with an existential on the right
			ElkSubClassOfAxiom firstExPremise = factory_.getSubClassOfAxiom(c, rSomeD);
			ElkObjectProperty rPrime = Deindexer.deindex(leftPropertyPremise.getSuperPropertyChain());
			ElkSubObjectPropertyOfAxiom propSubsumption = factory_.getSubObjectPropertyOfAxiom(r, rPrime);			
			
			if (inference.getForwardLink().getRelation() instanceof IndexedBinaryPropertyChain) {
				// complex right property premise
				// the second premise is not representable as an OWL axiom
				Expression secondExPremise = createExpression(inference.getInferenceContextRoot(whereStored), inference.getForwardLink());
				Expression chainSubsumption = createExpression(rightChainPremise);
				
				return new ExistentialCompositionViaChain(firstExPremise, secondExPremise, propSubsumption, chainSubsumption);
			}
			else {
				// the right property premise is a simple property inclusion axiom
				ElkObjectProperty ss = Deindexer.deindex((IndexedObjectProperty) rightChainPremise.getSubPropertyChain());
				ElkObjectProperty ssPrime = Deindexer.deindex((IndexedObjectProperty) rightChainPremise.getSuperPropertyChain());
				ElkClassExpression ssSomeE = factory_.getObjectSomeValuesFrom(ss, e);
				ElkSubClassOfAxiom secondExPremise = factory_.getSubClassOfAxiom(d, ssSomeE);
				ElkSubObjectPropertyOfAxiom chainSubsumption = factory_.getSubObjectPropertyOfAxiom(ss, ssPrime);
				
				return new ExistentialCompositionViaChain(firstExPremise, secondExPremise, propSubsumption, chainSubsumption);				
			}
		}

		@Override
		public Inference visit(ReversedForwardLink inference,
				IndexedClassExpression input) {
			// not a self-contained user inference
			return null;
		}

		@Override
		public Inference visit(DecomposedExistentialBackwardLink inference,
				IndexedClassExpression input) {
			// not a self-contained user inference
			return null;
		}

		@Override
		public Inference visit(DecomposedExistentialForwardLink inference,
				IndexedClassExpression input) {
			// not a self-contained user inference
			return null;
		}

		@Override
		public Inference visit(TracedPropagation inference,
				IndexedClassExpression input) {
			// not a self-contained user inference
			return null;
		}

		@Override
		public Inference visit(
				ContradictionFromInconsistentDisjointnessAxiom inference,
				IndexedClassExpression input) {
			ElkDisjointClassesAxiom sideCondition = (ElkDisjointClassesAxiom) sideConditionLookup_.lookup(inference);
			ElkClassExpression c = Deindexer.deindex(input);
			ElkClassExpression d = Deindexer.deindex(inference.getPremise().getExpression());
			
			return new InconsistentDisjointness(c, d, sideCondition, factory_);
		}

		@Override
		public Inference visit(ContradictionFromDisjointSubsumers inference,
				IndexedClassExpression input) {
			ElkDisjointClassesAxiom sideCondition = (ElkDisjointClassesAxiom) sideConditionLookup_.lookup(inference);
			ElkClassExpression c = Deindexer.deindex(input);
			ElkClassExpression d = Deindexer.deindex(inference.getPremises()[0].getMember());
			ElkClassExpression e = Deindexer.deindex(inference.getPremises()[1].getMember());
			// TODO check this
			return new DisjointnessContradiction(c, d, e, sideCondition, factory_);
		}

		@Override
		public Inference visit(ContradictionFromNegation inference,
				IndexedClassExpression input) {
			ElkClassExpression c = Deindexer.deindex(input);
			ElkClassExpression d = Deindexer.deindex(inference.getPositivePremise().getExpression());
			
			return new NegationContradiction(c, d, factory_);
		}

		@Override
		public Inference visit(ContradictionFromOwlNothing inference,
				IndexedClassExpression input) {
			// not a self-contained user inference			
			return null;
		}

		@Override
		public Inference visit(PropagatedContradiction inference,
				IndexedClassExpression whereStored) {
			// the left premise is an axiom with a simple existential on the right
			ElkClassExpression c = Deindexer.deindex(whereStored);
			ElkObjectProperty r = Deindexer.deindex(inference.getLinkPremise().getRelation());
			ElkClassExpression d = Deindexer.deindex(inference.getInferenceContextRoot(whereStored));
			ElkObjectSomeValuesFrom rSomeD = factory_.getObjectSomeValuesFrom(r, d);
			// TODO check that we can explain D <= owl:Nothing
			return new ExistentialComposition(c, PredefinedElkClass.OWL_NOTHING,
					factory_.getSubClassOfAxiom(c, rSomeD),
					factory_.getSubClassOfAxiom(d, PredefinedElkClass.OWL_NOTHING),
					factory_.getSubObjectPropertyOfAxiom(r, r), factory_);
		}

		@Override
		public Inference visit(DisjointSubsumerFromSubsumer inference,
				IndexedClassExpression input) {
			// not a self-contained user inference
			return null;
		}

		@Override
		public Inference visit(
				org.semanticweb.elk.reasoner.saturation.tracing.inferences.DisjunctionComposition inference,
				IndexedClassExpression input) {
			ElkClassExpression c = Deindexer.deindex(input);
			ElkClassExpression d = Deindexer.deindex(inference.getPremise().getExpression());
			ElkObjectUnionOf dOrE = (ElkObjectUnionOf) Deindexer.deindex(inference.getExpression());
			
			return new DisjunctionComposition(c, dOrE, d, factory_);
		}

		@Override
		public Inference visit(TopDownPropertySubsumptionInference inference,
				Void input) {
			final ElkObjectProperty sup = inference.getSuperPropertyChain().getElkObjectProperty();
			ElkSubObjectPropertyOfAxiom sideCondition = (ElkSubObjectPropertyOfAxiom) sideConditionLookup_.lookup(inference);
			ElkObjectProperty h = inference.getPremise().getSuperPropertyChain().getElkObjectProperty();
			ElkSubObjectPropertyOfAxiom conclusion = inference.getSubPropertyChain().accept(new IndexedPropertyChainVisitor<ElkSubObjectPropertyOfAxiom>() {

				@Override
				public ElkSubObjectPropertyOfAxiom visit(IndexedObjectProperty property) {
					ElkObjectProperty sub = Deindexer.deindex(property);
					
					return factory_.getSubObjectPropertyOfAxiom(sub, sup);
				}

				@Override
				public ElkSubObjectPropertyOfAxiom visit(IndexedBinaryPropertyChain chain) {
					ElkObjectPropertyChain sub = Deindexer.deindex(chain);
					
					return factory_.getSubObjectPropertyOfAxiom(sub, sup);
				}
			});
			
			return new ChainSubsumption(conclusion, factory_.getSubObjectPropertyOfAxiom(h, sup), sideCondition);
		}

		@Override
		public Inference visit(final BottomUpPropertySubsumptionInference inference,
				Void input) {
			final ElkObjectProperty s = inference.getSuperPropertyChain().getElkObjectProperty();
			final ElkSubObjectPropertyExpression sub = inference.getSubPropertyChain().accept(new IndexedPropertyChainVisitor<ElkSubObjectPropertyExpression>() {

				@Override
				public ElkSubObjectPropertyExpression visit(IndexedObjectProperty property) {
					return Deindexer.deindex(property);
				}

				@Override
				public ElkSubObjectPropertyExpression visit(IndexedBinaryPropertyChain chain) {
					return Deindexer.deindex(chain);
				}
			});
			
			return inference.getFirstPremise().getSuperPropertyChain().accept(new IndexedPropertyChainVisitor<ChainSubsumption>() {

				@Override
				public ChainSubsumption visit(IndexedObjectProperty hh) {
					// first premise is an axiom
					Expression firstPremise = new SingleAxiomExpression(factory_.getSubObjectPropertyOfAxiom(sub, hh.getElkObjectProperty()));
					ElkSubObjectPropertyOfAxiom secondPremise = factory_.getSubObjectPropertyOfAxiom(hh.getElkObjectProperty(), s);
					
					return new ChainSubsumption(factory_.getSubObjectPropertyOfAxiom(sub, s), firstPremise, secondPremise);
				}

				@Override
				public ChainSubsumption visit(IndexedBinaryPropertyChain hh) {
					// first premise is not an axiom
					Expression firstPremise = createExpression(inference.getFirstPremise());
					ElkSubObjectPropertyOfAxiom secondPremise = factory_.getSubObjectPropertyOfAxiom(Deindexer.deindex(hh), s);
					
					return new ChainSubsumption(factory_.getSubObjectPropertyOfAxiom(sub, s), firstPremise, secondPremise);
				}
			});
		}

		@Override
		public Inference visit(PropertyChainInitialization inference, Void input) {
			return null;
		}

		@Override
		public Inference visit(ToldReflexiveProperty inference, Void input) {
			return new ToldReflexivity((ElkReflexiveObjectPropertyAxiom) sideConditionLookup_.lookup(inference), factory_);
		}

		@Override
		public Inference visit(final ReflexiveToldSubObjectProperty inference,
				Void input) {
			ElkSubObjectPropertyOfAxiom sideCondition = (ElkSubObjectPropertyOfAxiom) sideConditionLookup_.lookup(inference);
			Expression premise = inference.getSubProperty().getPropertyChain().accept(new IndexedPropertyChainVisitor<Expression>() {

				@Override
				public Expression visit(IndexedObjectProperty h) {
					return new SingleAxiomExpression(factory_.getReflexiveObjectPropertyAxiom(h.getElkObjectProperty()));
				}

				@Override
				public Expression visit(IndexedBinaryPropertyChain hh) {
					// can't represent reflexive chains as axioms
					return createExpression(inference.getSubProperty());
				}
				
			});
			
			return new ReflexivityViaSubsumption(sideCondition, premise, factory_);
		}

		@Override
		public Inference visit(final ReflexivePropertyChainInference inference,
				Void input) {
			ElkObjectProperty r = inference.getLeftReflexiveProperty().getPropertyChain().getElkObjectProperty();
			Expression second = inference.getRightReflexivePropertyChain().getPropertyChain().accept(reflexiveChainExpressionCreator(inference.getRightReflexivePropertyChain()));
			
			return new ReflexiveComposition(factory_.getReflexiveObjectPropertyAxiom(r), second);
		}

		@Override
		public Inference visit(
				LeftReflexiveSubPropertyChainInference inference, Void input) {
			ElkObjectProperty r = inference.getReflexivePremise().getPropertyChain().getElkObjectProperty();
			ElkReflexiveObjectPropertyAxiom first = factory_.getReflexiveObjectPropertyAxiom(r);
			Expression conclusion = new MultiAxiomExpression(Collections.singletonList(new Explanation(first)));
			
			return new SubsumptionViaReflexivity(new SingleAxiomExpression(first), conclusion);
		}

		@Override
		public Inference visit(
				RightReflexiveSubPropertyChainInference inference, Void input) {
			Expression first = inference.getReflexivePremise().getPropertyChain().accept(reflexiveChainExpressionCreator(inference.getReflexivePremise()));
			Expression conclusion = new MultiAxiomExpression(first.getExplanations());
			
			return new SubsumptionViaReflexivity(first, conclusion);
		}
		
		// creates a visitor which creates simple or complex expression out of reflexive chains
		private IndexedPropertyChainVisitor<Expression> reflexiveChainExpressionCreator(final ObjectPropertyConclusion conclusion) {
			return new IndexedPropertyChainVisitor<Expression>() {

				@Override
				public Expression visit(IndexedObjectProperty property) {
					return new SingleAxiomExpression(factory_.getReflexiveObjectPropertyAxiom(property.getElkObjectProperty()));
				}

				@Override
				public Expression visit(IndexedBinaryPropertyChain chain) {
					// can't represent reflexive chains as axioms
					return createExpression(conclusion);
				}
				
			};
		}
	}
}
