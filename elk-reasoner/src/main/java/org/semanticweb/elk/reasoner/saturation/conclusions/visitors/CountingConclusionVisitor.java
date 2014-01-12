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

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;

public class CountingConclusionVisitor<I> implements
		ConclusionVisitor<I, Integer> {

	private final ConclusionCounter counter_;

	public CountingConclusionVisitor(ConclusionCounter counter) {
		this.counter_ = counter;
	}

	@Override
	public Integer visit(BackwardLink conclusion, I input) {
		return counter_.countBackwardLinks++;
	}

	@Override
	public Integer visit(ComposedSubsumer conclusion, I input) {
		return counter_.countNegativeSubsumers++;
	}

	@Override
	public Integer visit(ContextInitialization conclusion, I input) {
		return counter_.countContextInitializations++;
	}

	@Override
	public Integer visit(Contradiction conclusion, I input) {
		return counter_.countContradictions++;
	}

	@Override
	public Integer visit(DecomposedSubsumer conclusion, I input) {
		return counter_.countPositiveSubsumers++;
	}

	@Override
	public Integer visit(DisjointSubsumer conclusion, I input) {
		return counter_.countDisjointSubsumers++;
	}

	@Override
	public Integer visit(ForwardLink conclusion, I input) {
		return counter_.countForwardLinks++;
	}

	@Override
	public Integer visit(Propagation conclusion, I input) {
		return counter_.countPropagations++;
	}

}
