/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusionEquality;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusionHash;
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
			return ClassConclusionHash.hashCode(conclusion_);
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof Key) {
				return ClassConclusionEquality.equals(conclusion_,
						((Key) other).conclusion_);
			}
			// else
			return false;
		}

	}

}
