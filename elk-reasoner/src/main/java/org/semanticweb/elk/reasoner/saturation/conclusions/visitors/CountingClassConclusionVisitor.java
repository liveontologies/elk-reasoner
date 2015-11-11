package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubContextInitialization;

/**
 * A {@link ClassConclusionVisitor} that increments the corresponding counters of the
 * given {@link ClassConclusionCounter} when visiting {@link ClassConclusion}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 */
public class CountingClassConclusionVisitor<I> implements
		ClassConclusionVisitor<I, Boolean> {

	private final ClassConclusionCounter counter_;

	public CountingClassConclusionVisitor(ClassConclusionCounter counter) {
		this.counter_ = counter;
	}

	@Override
	public Boolean visit(BackwardLink subConclusion, I input) {
		counter_.countBackwardLinks++;
		return true;
	}

	@Override
	public Boolean visit(ComposedSubsumer conclusion, I input) {
		counter_.countComposedSubsumers++;
		return true;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion, I input) {
		counter_.countContextInitializations++;
		return true;
	}

	@Override
	public Boolean visit(Contradiction conclusion, I input) {
		counter_.countContradictions++;
		return true;
	}

	@Override
	public Boolean visit(DecomposedSubsumer conclusion, I input) {
		counter_.countDecomposedSubsumers++;
		return true;
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion, I input) {
		counter_.countDisjointSubsumers++;
		return true;
	}

	@Override
	public Boolean visit(ForwardLink conclusion, I input) {
		counter_.countForwardLinks++;
		return true;
	}

	@Override
	public Boolean visit(Propagation subConclusion, I input) {
		counter_.countPropagations++;
		return true;
	}

	@Override
	public Boolean visit(SubContextInitialization subConclusion, I input) {
		counter_.countSubContextInitializations++;
		return true;
	}

}
