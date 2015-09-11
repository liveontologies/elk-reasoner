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
import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainExpanded;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainInit;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * An implementation of a {@link ModifiableObjectPropertyInferenceSet}.
 * Insertion of inferences is synchronized. Inferences should not be read while
 * insertion is in place.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class ModifiableObjectPropertyInferenceSetImpl implements
		ModifiableObjectPropertyInferenceSet {

	private final ObjectPropertyInferenceVisitor<ModifiableObjectPropertyInferenceSetImpl, Void> INFERENCE_INSERTER_ = new InferenceInserter();

	private final ObjectPropertyConclusionVisitor<ModifiableObjectPropertyInferenceSetImpl, Iterable<? extends ObjectPropertyInference>> INFERENCE_READER_ = new InferenceReader();

	private Map<IndexedPropertyChain, Multimap<IndexedPropertyChain, SubPropertyChainInference>> subPropertyChainInferences_;

	@Override
	public Iterable<? extends ObjectPropertyInference> getObjectPropertyInferences(
			ObjectPropertyConclusion conclusion) {
		return conclusion.accept(INFERENCE_READER_, this);
	}

	@Override
	public synchronized void add(ObjectPropertyInference inference) {
		inference.accept(INFERENCE_INSERTER_, this);
	}

	@Override
	public void clear() {
		subPropertyChainInferences_.clear();
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

	private synchronized void addInference(SubPropertyChainInference inference) {
		if (subPropertyChainInferences_ == null)
			subPropertyChainInferences_ = new ArrayHashMap<IndexedPropertyChain, Multimap<IndexedPropertyChain, SubPropertyChainInference>>();
		addValue(subPropertyChainInferences_, inference.getSubChain(),
				inference.getSuperChain(), inference);
	}

	private Iterable<? extends SubPropertyChainInference> getInferences(
			SubPropertyChain conclusion) {
		if (subPropertyChainInferences_ == null)
			return Collections.emptyList();
		return getValues(subPropertyChainInferences_, conclusion.getSubChain(),
				conclusion.getSuperChain());
	}

	/**
	 * Writes inferences.
	 * 
	 * @author Pavel Klinov pavel.klinov@uni-ulm.de
	 *
	 */
	private static class InferenceInserter
			implements
			ObjectPropertyInferenceVisitor<ModifiableObjectPropertyInferenceSetImpl, Void> {

		@Override
		public Void visit(SubPropertyChainInit inference,
				ModifiableObjectPropertyInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

		@Override
		public Void visit(SubPropertyChainExpanded inference,
				ModifiableObjectPropertyInferenceSetImpl input) {
			input.addInference(inference);
			return null;
		}

	}

	/**
	 * Reads inferences
	 * 
	 * 
	 * @author Pavel Klinov pavel.klinov@uni-ulm.de
	 *
	 */
	private static class InferenceReader
			implements
			ObjectPropertyConclusionVisitor<ModifiableObjectPropertyInferenceSetImpl, Iterable<? extends ObjectPropertyInference>> {

		@Override
		public Iterable<? extends ObjectPropertyInference> visit(
				SubPropertyChain conclusion,
				ModifiableObjectPropertyInferenceSetImpl input) {
			return input.getInferences(conclusion);
		}

	}

}
