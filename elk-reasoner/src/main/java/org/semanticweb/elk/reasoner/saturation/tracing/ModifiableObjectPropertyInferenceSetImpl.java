/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusionEquality;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusionHash;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * An implementation for {@link ModifiableObjectPropertyInferenceSet}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ModifiableObjectPropertyInferenceSetImpl implements
		ModifiableObjectPropertyInferenceSet {

	private final Multimap<Key, ObjectPropertyInference> inferenceMap_ = new HashListMultimap<Key, ObjectPropertyInference>();

	@Override
	public void add(ObjectPropertyInference inference) {
		inferenceMap_.add(new Key(inference), inference);
	}

	@Override
	public void clear() {
		inferenceMap_.clear();
	}

	@Override
	public Iterable<? extends ObjectPropertyInference> getObjectPropertyInferences(
			ObjectPropertyConclusion conclusion) {
		return inferenceMap_.get(new Key(conclusion));
	}

	static class Key {

		private final ObjectPropertyConclusion conclusion_;

		Key(ObjectPropertyConclusion conclusion) {
			this.conclusion_ = conclusion;
		}

		@Override
		public int hashCode() {
			return ObjectPropertyConclusionHash.hashCode(conclusion_);
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof Key) {
				return ObjectPropertyConclusionEquality.equals(conclusion_,
						((Key) other).conclusion_);
			}
			// else
			return false;
		}

	}

}
