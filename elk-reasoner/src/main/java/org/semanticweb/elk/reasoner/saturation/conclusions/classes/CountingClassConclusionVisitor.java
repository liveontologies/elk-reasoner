package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;

/**
 * A {@link ClassConclusion.Visitor} that increments the corresponding counters of the
 * given {@link ClassConclusionCounter} when visiting {@link ClassConclusion}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class CountingClassConclusionVisitor implements
		ClassConclusion.Visitor<Boolean> {

	private final ClassConclusionCounter counter_;

	public CountingClassConclusionVisitor(ClassConclusionCounter counter) {
		this.counter_ = counter;
	}

	@Override
	public Boolean visit(BackwardLink subConclusion) {
		counter_.countBackwardLinks++;
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionComposed conclusion) {
		counter_.countComposedSubsumers++;
		return true;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion) {
		counter_.countContextInitializations++;
		return true;
	}

	@Override
	public Boolean visit(Contradiction conclusion) {
		counter_.countContradictions++;
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposed conclusion) {
		counter_.countDecomposedSubsumers++;
		return true;
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion) {
		counter_.countDisjointSubsumers++;
		return true;
	}

	@Override
	public Boolean visit(ForwardLink conclusion) {
		counter_.countForwardLinks++;
		return true;
	}

	@Override
	public Boolean visit(Propagation subConclusion) {
		counter_.countPropagations++;
		return true;
	}

	@Override
	public Boolean visit(SubContextInitialization subConclusion) {
		counter_.countSubContextInitializations++;
		return true;
	}

}
