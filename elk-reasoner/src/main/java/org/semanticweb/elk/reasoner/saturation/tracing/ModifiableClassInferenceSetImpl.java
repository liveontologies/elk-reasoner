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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.semanticweb.elk.owl.exceptions.ElkRuntimeException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
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
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedDecomposition;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedDefinition;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedDisjunction;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedExistential;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedSubsumerInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromInconsistentDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromNegation;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionInference;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedDefinition;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedSubsumerInference;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkInference;
import org.semanticweb.elk.reasoner.saturation.inferences.GeneratedPropagation;
import org.semanticweb.elk.reasoner.saturation.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ObjectHasSelfPropertyRangeSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagatedContradiction;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.SuperReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * A {@link ModifiableClassInferenceSet} containing {@link ClassInference}s for
 * {@link Conclusion}s with the same origin {@link IndexedContextRoot}.
 * 
 * @see Conclusion#getOriginRoot()
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class ModifiableClassInferenceSetImpl implements
		ModifiableClassInferenceSet {

	private static final ClassInferenceVisitor<ModifiableClassInferenceSetImpl, ?> INFERENCE_INSERTER_ = new InferenceInserter();

	private static final ConclusionVisitor<ModifiableClassInferenceSetImpl, Iterable<? extends ClassInference>> INFERENCE_READER_ = new InferenceReader();

	private final IndexedContextRoot originRoot_;

	private List<ContradictionInference> contradictionInferences_;

	private Map<IndexedClassExpression, Multimap<IndexedDisjointClassesAxiom, DisjointSubsumerInference>> disjointSubsumerInferenceMap_;

	private Multimap<IndexedClassExpression, ComposedSubsumerInference<?>> composedSubsumerInferenceMap_;

	private Multimap<IndexedClassExpression, DecomposedSubsumerInference> decomposedSubsumerInferenceMap_;

	private Map<IndexedPropertyChain, Multimap<IndexedContextRoot, BackwardLinkInference>> backwardLinkInferenceMap_;

	private Map<IndexedPropertyChain, Multimap<IndexedContextRoot, ForwardLinkInference>> forwardLinkInferenceMap_;

	private Map<IndexedPropertyChain, Multimap<IndexedObjectSomeValuesFrom, PropagationInference>> propagationMap_;

	/**
	 * 
	 */
	public ModifiableClassInferenceSetImpl(IndexedContextRoot originRoot) {
		this.originRoot_ = originRoot;
	}

	@Override
	public Iterable<? extends ClassInference> getClassInferences(
			Conclusion conclusion) {
		return conclusion.accept(INFERENCE_READER_, this);
	}

	@Override
	public void add(ClassInference inference) {
		if (inference.getOriginRoot() != originRoot_)
			throw new ElkRuntimeException(inference
					+ " is expected to have origin " + originRoot_
					+ " but its origin is " + inference.getOriginRoot() + " !");
		// else
		inference.accept(INFERENCE_INSERTER_, this);
	}

	// TODO: move the static methods to a better location for sharing
	static <K1, K2, V> void addValue(Map<K1, Multimap<K2, V>> nestedMap,
			K1 key1, K2 key2, V value) {
		Multimap<K2, V> traces = nestedMap.get(key1);
		if (traces == null) {
			traces = new HashListMultimap<K2, V>();
			nestedMap.put(key1, traces);
		}
		traces.add(key2, value);
	}

	static <V> Iterable<V> emptyIfNull(Iterable<V> items) {
		return (items == null) ? Collections.<V> emptyList() : items;
	}

	static <K1, K2, V> Iterable<V> getValues(Map<K1, Multimap<K2, V>> map,
			K1 key, K2 value) {
		Multimap<K2, V> traces = map.get(key);
		return (traces == null) ? Collections.<V> emptyList()
				: emptyIfNull(traces.get(value));
	}

	private void addInference(ContradictionInference inference) {
		if (contradictionInferences_ == null)
			contradictionInferences_ = new ArrayList<ContradictionInference>(2);
		contradictionInferences_.add(inference);
	}

	private Iterable<? extends ContradictionInference> getInferences(
			@SuppressWarnings("unused") Contradiction conclusion) {
		return emptyIfNull(contradictionInferences_);
	}

	private void addInference(ComposedSubsumerInference<?> inf) {
		if (composedSubsumerInferenceMap_ == null)
			composedSubsumerInferenceMap_ = new HashListMultimap<IndexedClassExpression, ComposedSubsumerInference<?>>();
		composedSubsumerInferenceMap_.add(inf.getExpression(), inf);
	}

	private Iterable<? extends ComposedSubsumerInference<?>> getInferences(
			ComposedSubsumer conclusion) {
		if (composedSubsumerInferenceMap_ == null)
			return Collections.emptyList();
		return emptyIfNull(composedSubsumerInferenceMap_.get(conclusion
				.getExpression()));
	}

	private void addInference(DecomposedSubsumerInference inf) {
		if (decomposedSubsumerInferenceMap_ == null)
			decomposedSubsumerInferenceMap_ = new HashListMultimap<IndexedClassExpression, DecomposedSubsumerInference>();
		decomposedSubsumerInferenceMap_.add(inf.getExpression(), inf);
	}

	private Iterable<? extends DecomposedSubsumerInference> getInferences(
			DecomposedSubsumer conclusion) {
		if (decomposedSubsumerInferenceMap_ == null)
			return Collections.emptyList();
		return emptyIfNull(decomposedSubsumerInferenceMap_.get(conclusion
				.getExpression()));
	}

	private void addInference(BackwardLinkInference inference) {
		if (backwardLinkInferenceMap_ == null)
			backwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<IndexedContextRoot, BackwardLinkInference>>();
		addValue(backwardLinkInferenceMap_, inference.getBackwardRelation(),
				inference.getConclusionRoot(), inference);
	}

	private Iterable<? extends BackwardLinkInference> getInferences(
			BackwardLink conclusion) {
		return getValues(backwardLinkInferenceMap_,
				conclusion.getBackwardRelation(),
				conclusion.getConclusionRoot());
	}

	private void addInference(ForwardLinkInference inference) {
		if (forwardLinkInferenceMap_ == null)
			forwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<IndexedContextRoot, ForwardLinkInference>>();
		addValue(forwardLinkInferenceMap_, inference.getForwardChain(),
				inference.getTarget(), inference);
	}

	private Iterable<? extends ForwardLinkInference> getInferences(
			ForwardLink conclusion) {
		return getValues(forwardLinkInferenceMap_,
				conclusion.getForwardChain(), conclusion.getTarget());
	}

	private void addInference(PropagationInference inference) {
		if (propagationMap_ == null)
			propagationMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<IndexedObjectSomeValuesFrom, PropagationInference>>();
		addValue(propagationMap_, inference.getRelation(),
				inference.getCarry(), inference);
	}

	private Iterable<? extends PropagationInference> getInferences(
			Propagation conclusion) {
		return getValues(propagationMap_, conclusion.getRelation(),
				conclusion.getCarry());
	}

	private void addInference(DisjointSubsumerInference inference) {
		if (disjointSubsumerInferenceMap_ == null)
			disjointSubsumerInferenceMap_ = new ArrayHashMap<IndexedClassExpression, Multimap<IndexedDisjointClassesAxiom, DisjointSubsumerInference>>(
					2);
		addValue(disjointSubsumerInferenceMap_, inference.getMember(),
				inference.getAxiom(), inference);
	}

	private Iterable<? extends DisjointSubsumerInference> getInferences(
			DisjointSubsumer conclusion) {
		return getValues(disjointSubsumerInferenceMap_, conclusion.getMember(),
				conclusion.getAxiom());
	}

	private static class InferenceInserter implements
			ClassInferenceVisitor<ModifiableClassInferenceSetImpl, Void> {

		@Override
		public Void visit(InitializationSubsumer inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(SubClassOfSubsumer inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ComposedConjunction inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(DecomposedFirstConjunct inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(DecomposedSecondConjunct inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ComposedExistential inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ComposedBackwardLink inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ComposedForwardLink inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ReversedForwardLink inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(SuperReversedForwardLink inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(DecomposedExistentialBackwardLink inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(DecomposedExistentialForwardLink inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(DecomposedReflexiveBackwardLink inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(DecomposedReflexiveForwardLink inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(GeneratedPropagation inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(
				ContradictionFromInconsistentDisjointnessAxiom inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ContradictionFromDisjointSubsumers inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ContradictionFromNegation inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ContradictionFromOwlNothing inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(PropagatedContradiction inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(DisjointSubsumerFromSubsumer inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ComposedDisjunction inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ObjectHasSelfPropertyRangeSubsumer inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ComposedDecomposition inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(ComposedDefinition inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(DecomposedDefinition inference,
				ModifiableClassInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

	}

	private static class InferenceReader
			implements
			ConclusionVisitor<ModifiableClassInferenceSetImpl, Iterable<? extends ClassInference>> {

		@Override
		public Iterable<? extends ClassInference> visit(
				BackwardLink subConclusion,
				ModifiableClassInferenceSetImpl input) {
			return input.getInferences(subConclusion);
		}

		@Override
		public Iterable<? extends ClassInference> visit(
				Propagation subConclusion, ModifiableClassInferenceSetImpl input) {
			return input.getInferences(subConclusion);
		}

		@Override
		public Iterable<? extends ClassInference> visit(
				SubContextInitialization subConclusion,
				ModifiableClassInferenceSetImpl input) {
			// no inference can produce initialization
			return Collections.emptyList();
		}

		@Override
		public Iterable<? extends ClassInference> visit(
				ComposedSubsumer conclusion,
				ModifiableClassInferenceSetImpl input) {
			return input.getInferences(conclusion);
		}

		@Override
		public Iterable<? extends ClassInference> visit(
				ContextInitialization conclusion,
				ModifiableClassInferenceSetImpl input) {
			// no inference can produce initialization
			return Collections.emptyList();
		}

		@Override
		public Iterable<? extends ClassInference> visit(
				Contradiction conclusion, ModifiableClassInferenceSetImpl input) {
			return input.getInferences(conclusion);
		}

		@Override
		public Iterable<? extends ClassInference> visit(
				DecomposedSubsumer conclusion,
				ModifiableClassInferenceSetImpl input) {
			return input.getInferences(conclusion);
		}

		@Override
		public Iterable<? extends ClassInference> visit(
				DisjointSubsumer conclusion,
				ModifiableClassInferenceSetImpl input) {
			return input.getInferences(conclusion);
		}

		@Override
		public Iterable<? extends ClassInference> visit(ForwardLink conclusion,
				ModifiableClassInferenceSetImpl input) {
			return input.getInferences(conclusion);
		}
	}

}
