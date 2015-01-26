/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
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
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DisjunctionComposition;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedContradiction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReflexiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.TracedPropagation;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.InferenceVisitor;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * This class synchronizes access to the backward link map because backward
 * links may belong to other contexts. Thus the same map may store links which
 * belong to contexts being traced and contexts already traced. A concurrent
 * modification may occur if a trace for a traced context is read when a trace
 * for context being traced in written.
 * 
 * TODO: better to create a synchronized multimap (or even store inference for
 * backward links by their source contexts?)
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SimpleContextTraceStore implements ContextTraceStore {

	private final List<Inference> contradictionInferences_;
	
	private final Map<IndexedClassExpression, Multimap<IndexedDisjointClassesAxiom, Inference>> disjointSubsumerInferenceMap_;
	
	private final Multimap<IndexedClassExpression, Inference> subsumerInferenceMap_;

	private final Map<IndexedPropertyChain, Multimap<IndexedClassExpression, Inference>> backwardLinkInferenceMap_;

	private final Map<IndexedPropertyChain, Multimap<IndexedClassExpression, Inference>> forwardLinkInferenceMap_;

	private final Map<IndexedPropertyChain, Multimap<IndexedObjectSomeValuesFrom, Inference>> propagationMap_;

	private final ConclusionVisitor<InferenceVisitor<?, ?>, Void> inferenceReader_ = new ConclusionVisitor<InferenceVisitor<?, ?>, Void>() {

		private void visitAll(Iterable<Inference> conclusions,
				InferenceVisitor<?, ?> visitor) {
			if (conclusions != null) {
				for (Inference inf : conclusions) {
					inf.acceptTraced(visitor, null);
				}
			}
		}

		private void visitBackwardLinkInferences(IndexedPropertyChain relation,
				IndexedClassExpression source, InferenceVisitor<?, ?> visitor) {
			synchronized (relation) {
				Multimap<IndexedClassExpression, Inference> sourceToInferenceMap = backwardLinkInferenceMap_
						.get(relation);

				if (sourceToInferenceMap != null) {
					synchronized (source) {
						for (Inference inference : sourceToInferenceMap
								.get(source)) {
							inference.acceptTraced(visitor, null);
						}
					}
				}
			}
		}

		@Override
		public Void visit(ComposedSubsumer<?> negSCE,
				InferenceVisitor<?, ?> visitor) {
			visitAll(getSubsumerInferences(negSCE.getExpression()), visitor);

			return null;
		}

		@Override
		public Void visit(DecomposedSubsumer<?> posSCE,
				InferenceVisitor<?, ?> visitor) {
			visitAll(getSubsumerInferences(posSCE.getExpression()), visitor);

			return null;
		}

		@Override
		public Void visit(BackwardLink link, InferenceVisitor<?, ?> visitor) {
			visitBackwardLinkInferences(link.getRelation(), link.getSource(),
					visitor);

			return null;
		}

		@Override
		public Void visit(ForwardLink link, InferenceVisitor<?, ?> visitor) {
			visitAll(
					getInferences(forwardLinkInferenceMap_,
							link.getRelation(), link.getTarget()), visitor);

			return null;
		}

		@Override
		public Void visit(Propagation propagation,
				InferenceVisitor<?, ?> visitor) {
			visitAll(
					getInferences(propagationMap_,
							propagation.getRelation(), propagation.getCarry()),
					visitor);

			return null;
		}

		@Override
		public Void visit(SubContextInitialization subConclusion,
				InferenceVisitor<?, ?> input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(ContextInitialization conclusion,
				InferenceVisitor<?, ?> input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(Contradiction conclusion, InferenceVisitor<?, ?> input) {
			visitAll(contradictionInferences_, input);
			return null;
		}

		@Override
		public Void visit(DisjointSubsumer conclusion, InferenceVisitor<?, ?> input) {
			visitAll(getInferences(disjointSubsumerInferenceMap_, conclusion.getMember(), conclusion.getAxiom()), input);
			return null;
		}

	};

	private final InferenceVisitor<?, Boolean> inferenceWriter_ = new InferenceVisitor<Void, Boolean>() {

		@Override
		public Boolean visit(InitializationSubsumer<?> conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(SubClassOfSubsumer<?> conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(ComposedConjunction conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(DecomposedConjunction conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(PropagatedSubsumer conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(ReflexiveSubsumer<?> conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(ComposedBackwardLink conclusion, Void param) {
			return addTracedBackwardLink(conclusion.getRelation(),
					conclusion.getSource(), conclusion);
		}

		@Override
		public Boolean visit(ComposedForwardLink conclusion, Void input) {
			return addInference(forwardLinkInferenceMap_,
					conclusion.getRelation(), conclusion.getTarget(),
					conclusion);
		}

		@Override
		public Boolean visit(ReversedForwardLink conclusion, Void param) {
			return addTracedBackwardLink(conclusion.getRelation(),
					conclusion.getSource(), conclusion);
		}

		@Override
		public Boolean visit(DecomposedExistentialBackwardLink conclusion,
				Void param) {
			return addTracedBackwardLink(conclusion.getRelation(),
					conclusion.getSource(), conclusion);
		}

		@Override
		public Boolean visit(DecomposedExistentialForwardLink conclusion,
				Void input) {
			return addInference(forwardLinkInferenceMap_,
					conclusion.getRelation(), conclusion.getTarget(),
					conclusion);
		}

		@Override
		public Boolean visit(TracedPropagation conclusion, Void param) {
			return addInference(propagationMap_, conclusion.getRelation(),
					conclusion.getCarry(), conclusion);
		}

		@Override
		public Boolean visit(
				ContradictionFromInconsistentDisjointnessAxiom conclusion,
				Void input) {
			return addContradictionInference(conclusion);
		}

		@Override
		public Boolean visit(ContradictionFromDisjointSubsumers conclusion,
				Void input) {
			return addContradictionInference(conclusion);
		}

		@Override
		public Boolean visit(ContradictionFromNegation conclusion, Void input) {
			return addContradictionInference(conclusion);
		}

		@Override
		public Boolean visit(ContradictionFromOwlNothing conclusion, Void input) {
			return addContradictionInference(conclusion);
		}

		@Override
		public Boolean visit(PropagatedContradiction conclusion, Void input) {
			return addContradictionInference(conclusion);
		}

		@Override
		public Boolean visit(DisjointSubsumerFromSubsumer conclusion, Void input) {
			return addInference(disjointSubsumerInferenceMap_, conclusion.getMember(), conclusion.getAxiom(), conclusion);
		}

		@Override
		public Boolean visit(DisjunctionComposition conclusion, Void input) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

	};

	/**
	 * 
	 */
	public SimpleContextTraceStore() {
		contradictionInferences_ = new ArrayList<Inference>(2);
		disjointSubsumerInferenceMap_ = new ArrayHashMap<IndexedClassExpression, Multimap<IndexedDisjointClassesAxiom, Inference>>();
		subsumerInferenceMap_ = new HashListMultimap<IndexedClassExpression, Inference>();
		backwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<IndexedClassExpression, Inference>>();
		forwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<IndexedClassExpression, Inference>>();
		propagationMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<IndexedObjectSomeValuesFrom, Inference>>();
	}

	protected Boolean addTracedBackwardLink(IndexedPropertyChain relation,
			IndexedClassExpression source, Inference link) {
		// TODO need a synchronized multimap here
		synchronized (relation) {
			Multimap<IndexedClassExpression, Inference> sourceToInferenceMap = backwardLinkInferenceMap_
					.get(relation);

			synchronized (source) {
				if (sourceToInferenceMap == null) {
					sourceToInferenceMap = new HashListMultimap<IndexedClassExpression, Inference>();
					sourceToInferenceMap.add(source, link);
					backwardLinkInferenceMap_.put(relation,
							sourceToInferenceMap);

					return true;
				}

				return sourceToInferenceMap.add(source, link);
			}
		}
	}

	/**
	 * Adds a trace associated with the given key-value pair into the trace
	 * multimap
	 * 
	 * @param traceMultiMap
	 *            the multimap into which to insert new trace
	 * @param key
	 *            the key for which to insert
	 * @param value
	 *            the value for which to associate the trace
	 * @param trace
	 *            the trace to be associated
	 * @return {@code true} if the trace multimap has changed as a result of
	 *         this operation
	 */
	protected static <K, V, T> Boolean addInference(
			Map<K, Multimap<V, T>> traceMultiMap, K key, V value, T trace) {
		Multimap<V, T> traces = traceMultiMap.get(key);

		if (traces == null) {
			traces = new HashListMultimap<V, T>();

			traces.add(value, trace);
			traceMultiMap.put(key, traces);

			return true;
		}

		return traces.add(value, trace);
	}

	protected Boolean addContradictionInference(Inference inf) {
		return contradictionInferences_.add(inf);
	}
	
	protected Boolean addSubsumerInference(IndexedClassExpression ice,
			Inference inf) {
		return subsumerInferenceMap_.add(ice, inf);
	}

	@Override
	public void accept(Conclusion conclusion, InferenceVisitor<?, ?> visitor) {
		conclusion.accept(inferenceReader_, visitor);
	}

	public Iterable<Inference> getSubsumerInferences(
			IndexedClassExpression conclusion) {
		return subsumerInferenceMap_.get(conclusion);
	}

	public static <K, V, T> Iterable<T> getInferences(
			Map<K, Multimap<V, T>> traceMultiMap, K key, V value) {
		Multimap<V, T> traces = traceMultiMap.get(key);

		return traces == null ? null : traces.get(value);
	}

	@Override
	public boolean addInference(Inference conclusion) {
		return conclusion.acceptTraced(inferenceWriter_, null);
	}

	@Override
	public void visitInferences(InferenceVisitor<?, ?> visitor) {
		// subsumers
		for (IndexedClassExpression ice : subsumerInferenceMap_.keySet()) {
			for (Inference inference : subsumerInferenceMap_.get(ice)) {
				inference.acceptTraced(visitor, null);
			}
		}
		// backward links
		for (IndexedPropertyChain linkRelation : backwardLinkInferenceMap_
				.keySet()) {
			Multimap<IndexedClassExpression, Inference> contextMap = backwardLinkInferenceMap_
					.get(linkRelation);

			for (IndexedClassExpression source : contextMap.keySet()) {
				for (Inference inference : contextMap.get(source)) {
					inference.acceptTraced(visitor, null);
				}
			}
		}
		// forward links
		for (IndexedPropertyChain linkRelation : forwardLinkInferenceMap_
				.keySet()) {

			Multimap<IndexedClassExpression, Inference> contextMap = forwardLinkInferenceMap_
					.get(linkRelation);

			for (IndexedClassExpression target : contextMap.keySet()) {
				for (Inference inference : contextMap.get(target)) {
					inference.acceptTraced(visitor, null);
				}
			}
		}
		// propagations
		for (IndexedPropertyChain propRelation : propagationMap_.keySet()) {

			Multimap<IndexedObjectSomeValuesFrom, Inference> carryMap = propagationMap_
					.get(propRelation);

			for (IndexedObjectSomeValuesFrom carry : carryMap.keySet()) {
				for (Inference inference : carryMap.get(carry)) {
					inference.acceptTraced(visitor, null);
				}
			}
		}
	}

}
