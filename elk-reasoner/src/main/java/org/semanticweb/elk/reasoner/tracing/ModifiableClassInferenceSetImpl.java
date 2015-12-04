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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * An implementation for {@link ModifiableClassInferenceSet}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ModifiableClassInferenceSetImpl implements
		ModifiableClassInferenceSet {

	private final Multimap<Key, ClassInference> inferenceMap_ = new HashListMultimap<Key, ClassInference>();

	@Override
	public void add(ClassInference inference) {
		inferenceMap_.add(new Key(inference), inference);
	}

	@Override
	public Iterable<? extends ClassInference> getClassInferences(
			ClassConclusion conclusion) {
		return inferenceMap_.get(new Key(conclusion));
	}

	@Override
	public void clear() {
		inferenceMap_.clear();
	}

	static class Key {

		private final ClassConclusion conclusion_;

		Key(ClassConclusion conclusion) {
			this.conclusion_ = conclusion;
		}

		@Override
		public int hashCode() {
			return ConclusionHash.hashCode(conclusion_);
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof Key) {
				return ConclusionEquality.equals(conclusion_,
						((Key) other).conclusion_);
			}
			// else
			return false;
		}

	}

}
