/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link ModifiableTracingInferenceSet} backed by a
 * {@link Multimap}.
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <I>
 *            the type of inferences stored in this
 *            {@link ModifiableTracingInferenceSet}
 */
public class ModifiableTracingInferenceSetImpl<I extends TracingInference>
		implements ModifiableTracingInferenceSet<I> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ModifiableTracingInferenceSetImpl.class);
	
	private final Multimap<Conclusion, I> inferenceMap_ = new HashListMultimap<Conclusion, I>();

	@Override
	public void produce(I inference) {
		LOGGER_.trace("{}: inference produced", inference);
		inferenceMap_.add(new TracingInferenceConclusion(inference), inference);
	}

	@Override
	public void clear() {
		inferenceMap_.clear();
	}

	@Override
	public Iterable<? extends I> getInferences(Conclusion conclusion) {
		// assumes structural equality and hash of conclusions
		return inferenceMap_.get(conclusion);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Conclusion key : inferenceMap_.keySet()) {
			for (I inf : inferenceMap_.get(key)) {
				sb.append(inf.toString());
				sb.append('\n');
			}
		}
		return sb.toString();
	}

}
