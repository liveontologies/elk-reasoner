package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

/**
 * A {@link ClassInference.Visitor} that is composed of several given
 * {@link ClassInference.Visitor}s. The visit method of the composed visitor
 * calls the original {@link ClassInference.Visitor}s in the specified order and
 * returns {@code true} for the {@link ClassInference} if and only all of the
 * {@link ClassInference.Visitor}s return {@code true}. The result is evaluated
 * lazily, i.e., if some {@link ClassInference.Visitor} returns {@code false},
 * the subsequent {@link ClassInference.Visitor}s are not called.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ComposedClassInferenceVisitor
		implements
			ClassInference.Visitor<Boolean> {

	/**
	 * The original {@link ClassInference.Visitor}s to be called in the
	 * specified order
	 */
	final private ClassInference.Visitor<Boolean>[] visitors_;

	/**
	 * Creates a new {@link ClassInference.Visitor} that combines several given
	 * {@link ClassInference.Visitor}s. The visit method of the combined visitor
	 * calls the original {@link ClassInference.Visitor}s in the specified order
	 * and returns {@code true} for the {@link ClassInference} if and only all
	 * of the {@link ClassInference.Visitor}s return {@code true}. The result is
	 * evaluated lazily, i.e., if some {@link ClassInference.Visitor} returns
	 * {@code false}, the subsequent {@link ClassInference.Visitor}s are not
	 * called.
	 * 
	 * @param visitors
	 *            the {@link ClassInference.Visitor} to be composed
	 */
	public ComposedClassInferenceVisitor(
			ClassInference.Visitor<Boolean>... visitors) {
		this.visitors_ = visitors;
	}

	@Override
	public Boolean visit(BackwardLinkComposition inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(BackwardLinkOfObjectHasSelf inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(BackwardLinkReversedExpanded inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ClassInconsistencyOfDisjointSubsumers inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ClassInconsistencyOfObjectComplementOf inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ClassInconsistencyOfOwlNothing inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ClassInconsistencyPropagated inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ContextInitializationNoPremises inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(DisjointSubsumerFromSubsumer inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ForwardLinkComposition inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ForwardLinkOfObjectHasSelf inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(PropagationGenerated inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionComposedDefinedClass inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionComposedEntity inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(
			SubClassInclusionComposedObjectIntersectionOf inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(
			SubClassInclusionComposedObjectSomeValuesFrom inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionComposedObjectUnionOf inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposedFirstConjunct inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposedSecondConjunct inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionExpandedDefinition inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(
			SubClassInclusionExpandedFirstEquivalentClass inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(
			SubClassInclusionExpandedSecondEquivalentClass inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionExpandedSubClassOf inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(
			SubClassInclusionObjectHasSelfPropertyRange inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionOwlThing inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionRange inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionTautology inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubContextInitializationNoPremises inference) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(inference))
				return false;
		}
		return true;
	}

}
