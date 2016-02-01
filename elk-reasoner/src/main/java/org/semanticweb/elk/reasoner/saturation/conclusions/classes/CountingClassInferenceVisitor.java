package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

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

import org.semanticweb.elk.reasoner.saturation.inferences.AbstractClassInferenceByConclusionTypeVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ContextInitializationInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionInference;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkInference;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubContextInitializationInference;

public class CountingClassInferenceVisitor
		extends
			AbstractClassInferenceByConclusionTypeVisitor<Boolean> {

	private final ClassConclusionCounter counter_;

	public CountingClassInferenceVisitor(ClassConclusionCounter counter) {
		this.counter_ = counter;
	}

	@Override
	public Boolean visit(ContextInitializationInference inference) {
		counter_.countContextInitialization++;
		return true;
	}

	@Override
	public Boolean visit(ContradictionInference inference) {
		counter_.countContradiction++;
		return true;
	}

	@Override
	public Boolean visit(DisjointSubsumerInference inference) {
		counter_.countDisjointSubsumer++;
		return true;
	}

	@Override
	public Boolean visit(SubContextInitializationInference inference) {
		counter_.countSubContextInitialization++;
		return true;
	}

	@Override
	public Boolean visit(ForwardLinkInference inference) {
		counter_.countForwardLink++;
		return true;
	}

	@Override
	public Boolean visit(BackwardLinkInference inference) {
		counter_.countBackwardLink++;
		return true;
	}

	@Override
	public Boolean visit(PropagationInference inference) {
		counter_.countPropagation++;
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionComposedInference inference) {
		counter_.countSubClassInclusionComposed++;
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposedInference inference) {
		counter_.countSubClassInclusionDecomposed++;
		return true;
	}

}
