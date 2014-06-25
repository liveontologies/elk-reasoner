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

import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * This class synchronizes access to the backward link map because backward links may belong to other contexts. Thus the same map
 * may store links which belong to contexts being traced and contexts already traced. A concurrent modification may occur if
 * a trace for a traced context is read when a trace for context being traced in written.
 * 
 * TODO: better to create a synchronized multimap (or even store inference for backward links by their source contexts?)
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SimpleContextTraceStore implements ContextTraceStore {
	
	private final Multimap<IndexedClassExpression, Inference> subsumerInferenceMap_;
	
	private final Map<IndexedPropertyChain, Multimap<Context, Inference>> backwardLinkInferenceMap_;
	
	private final Map<IndexedPropertyChain, Multimap<Context, Inference>> forwardLinkInferenceMap_;
	
	private final Map<IndexedPropertyChain, Multimap<IndexedObjectSomeValuesFrom, Inference>> propagationMap_;
	
	private final ConclusionVisitor<Void, InferenceVisitor<?,?>> inferenceReader_ = new BaseConclusionVisitor<Void, InferenceVisitor<?,?>>() {

		private void visitAll(Iterable<Inference> conclusions, InferenceVisitor<?,?> visitor) {
			if (conclusions != null) {
				for (Inference inf : conclusions) {
					inf.acceptTraced(visitor, null);
				}
			}
		}
		
		private void visitBackwardLinkInferences(IndexedPropertyChain relation, Context source, InferenceVisitor<?, ?> visitor) {
			synchronized (relation) {
				Multimap<Context, Inference> sourceToInferenceMap = backwardLinkInferenceMap_.get(relation);

				if (sourceToInferenceMap != null) {
					synchronized (source) {
						for (Inference inference : sourceToInferenceMap.get(source)) {
							inference.acceptTraced(visitor, null);
						}
					}
				}
			}
		}
		
		@Override
		public Void visit(ComposedSubsumer negSCE, InferenceVisitor<?,?> visitor) {
			visitAll(getSubsumerInferences(negSCE.getExpression()), visitor);
			
			return null;
		}

		@Override
		public Void visit(DecomposedSubsumer posSCE, InferenceVisitor<?,?> visitor) {
			visitAll(getSubsumerInferences(posSCE.getExpression()), visitor);
			
			return null;
		}

		@Override
		public Void visit(BackwardLink link, InferenceVisitor<?,?> visitor) {
			//visitAll(getLinkInferences(backwardLinkInferenceMap_, link.getRelation(), link.getSource()), visitor);
			visitBackwardLinkInferences(link.getRelation(), link.getSource(), visitor);
			
			return null;
		}

		@Override
		public Void visit(ForwardLink link, InferenceVisitor<?,?> visitor) {
			visitAll(getLinkInferences(forwardLinkInferenceMap_, link.getRelation(), link.getTarget()), visitor);
			
			return null;
		}
		
		@Override
		public Void visit(Propagation propagation,
				InferenceVisitor<?, ?> visitor) {
			visitAll(getLinkInferences(propagationMap_, propagation.getRelation(), propagation.getCarry()), visitor);

			return null;
		}
		
	}; 
	
	private final InferenceVisitor<Boolean, ?> inferenceWriter_ = new BaseInferenceVisitor<Boolean, Void>() {

		@Override
		public Boolean visit(InitializationSubsumer conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(SubClassOfSubsumer conclusion, Void param) {
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
		public Boolean visit(ReflexiveSubsumer conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(ComposedBackwardLink conclusion, Void param) {
			return addTracedBackwardLink(conclusion.getRelation(), conclusion.getSource(), conclusion);
		}

		@Override
		public Boolean visit(ReversedBackwardLink conclusion, Void param) {
			return addTracedValue(forwardLinkInferenceMap_, conclusion.getRelation(), conclusion.getTarget(), conclusion);
		}

		@Override
		public Boolean visit(DecomposedExistential conclusion, Void param) {
			return addTracedBackwardLink(conclusion.getRelation(), conclusion.getSource(), conclusion);
		}

		@Override
		public Boolean visit(TracedPropagation conclusion, Void param) {
			return addTracedValue(propagationMap_, conclusion.getRelation(), conclusion.getCarry(), conclusion);
		}
		
	};
	
	/**
	 * 
	 */
	public SimpleContextTraceStore() {
		subsumerInferenceMap_ = new HashListMultimap<IndexedClassExpression, Inference>();
		backwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<Context,Inference>>();
		forwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<Context,Inference>>();
		propagationMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<IndexedObjectSomeValuesFrom,Inference>>();
	}
	

	protected Boolean addTracedBackwardLink(IndexedPropertyChain relation, Context source, Inference link) {
		//TODO need a synchronized multimap here
		synchronized (relation) {
			Multimap<Context, Inference> sourceToInferenceMap = backwardLinkInferenceMap_.get(relation);
			
			synchronized (source) {
				if (sourceToInferenceMap == null) {
					sourceToInferenceMap = new HashListMultimap<Context, Inference>();
					sourceToInferenceMap.add(source, link);
					backwardLinkInferenceMap_.put(relation, sourceToInferenceMap);

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
	protected static <K, V, T> Boolean addTracedValue(
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

	protected Boolean addSubsumerInference(IndexedClassExpression ice, Inference inf) {
		return subsumerInferenceMap_.add(ice, inf);
	}

	@Override
	public void accept(Conclusion conclusion, InferenceVisitor<?,?> visitor) {
		conclusion.accept(inferenceReader_, visitor);
	}

	public Iterable<Inference> getSubsumerInferences(IndexedClassExpression conclusion) {
		return subsumerInferenceMap_.get(conclusion);
	}

	public static <K, V, T> Iterable<T> getLinkInferences(
			Map<K, Multimap<V, T>> traceMultiMap, K key, V value) {
		Multimap<V, T> traces = traceMultiMap.get(key);

		return traces == null ? null : traces.get(value);
	}

	@Override
	public boolean addInference(Inference conclusion) {
		return conclusion.acceptTraced(inferenceWriter_, null);
	}
	
	/*
	 * Visits all conclusions for which at least one inference is stored.
	 */
	@Override
	public void visitConclusions(ConclusionVisitor<?, ?> visitor) {
		// subsumers
		for (IndexedClassExpression ice : subsumerInferenceMap_.keySet()) {
			TracingUtils.getSubsumerWrapper(ice).accept(visitor, null);
		}
		// backward links
		for (IndexedPropertyChain linkRelation : backwardLinkInferenceMap_
				.keySet()) {
			Multimap<Context, Inference> contextMap = backwardLinkInferenceMap_
					.get(linkRelation);

			for (Context source : contextMap.keySet()) {
				TracingUtils.getBackwardLinkWrapper(linkRelation, source)
						.accept(visitor, null);
			}
		}
		// forward links
		for (IndexedPropertyChain linkRelation : forwardLinkInferenceMap_
				.keySet()) {

			Multimap<Context, Inference> contextMap = forwardLinkInferenceMap_
					.get(linkRelation);

			for (Context target : contextMap.keySet()) {
				TracingUtils.getForwardLinkWrapper(linkRelation, target)
						.accept(visitor, null);
			}
		}
		// propagations
		for (IndexedPropertyChain propRelation : propagationMap_.keySet()) {

			Multimap<IndexedObjectSomeValuesFrom, Inference> carryMap = propagationMap_
					.get(propRelation);

			for (IndexedObjectSomeValuesFrom carry : carryMap.keySet()) {
				TracingUtils.getPropagationWrapper(propRelation, carry).accept(
						visitor, null);
			}
		}
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
			Multimap<Context, Inference> contextMap = backwardLinkInferenceMap_.get(linkRelation); 
			
			for (Context source : contextMap.keySet()) {
				for (Inference inference : contextMap.get(source)) {
					inference.acceptTraced(visitor, null);
				}
			}
		}
		// forward links
		for (IndexedPropertyChain linkRelation : forwardLinkInferenceMap_.keySet()) {
			
			Multimap<Context, Inference> contextMap = forwardLinkInferenceMap_.get(linkRelation);
			
			for (Context target : contextMap.keySet()) {
				for (Inference inference : contextMap.get(target)) {
					inference.acceptTraced(visitor, null);
				}
			}
		}
		// propagations
		for (IndexedPropertyChain propRelation : propagationMap_.keySet()) {
			
			Multimap<IndexedObjectSomeValuesFrom, Inference> carryMap = propagationMap_.get(propRelation);
			
			for (IndexedObjectSomeValuesFrom carry : carryMap.keySet()) {
				for (Inference inference : carryMap.get(carry)) {
					inference.acceptTraced(visitor, null);
				}
			}
		}
	}

}
