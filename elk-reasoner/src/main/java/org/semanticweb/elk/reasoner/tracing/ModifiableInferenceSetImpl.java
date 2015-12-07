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

/**
 * An implementation of {@link ModifiableInferenceSet} backed by a
 * {@link Multimap}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ModifiableInferenceSetImpl<I extends Inference>
		implements
			ModifiableInferenceSet<I> {

	private final Multimap<ConclusionKey, I> inferenceMap_ = new HashListMultimap<ConclusionKey, I>();

	@Override
	public void produce(I inference) {
		inferenceMap_.add(new ConclusionKey(inference), inference);
	}

	@Override
	public void clear() {
		inferenceMap_.clear();
	}

	@Override
	public Iterable<? extends I> getInferences(Conclusion conclusion) {
		return inferenceMap_.get(new ConclusionKey(conclusion));
	}

}
