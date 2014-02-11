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
import org.semanticweb.elk.reasoner.saturation.conclusions.SubContextInitialization;
import org.semanticweb.elk.util.logging.CachedTimeThread;

public class TimedConclusionVisitor<I> implements ConclusionVisitor<I, Long> {

	private final ConclusionVisitor<I, ?> processor_;
	private final ConclusionTimer timer_;

	public TimedConclusionVisitor(ConclusionTimer timer,
			ConclusionVisitor<I, ?> processor) {
		this.timer_ = timer;
		this.processor_ = processor;
	}

	@Override
	public Long visit(BackwardLink subConclusion, I input) {
		timer_.timeBackwardLinks -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(subConclusion, input);
		return timer_.timeBackwardLinks += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public Long visit(ComposedSubsumer conclusion, I input) {
		timer_.timeNegativeSubsumers -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(conclusion, input);
		return timer_.timeNegativeSubsumers += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public Long visit(ContextInitialization conclusion, I input) {
		timer_.timeContextInitializations -= CachedTimeThread
				.getCurrentTimeMillis();
		processor_.visit(conclusion, input);
		return timer_.timeContextInitializations += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public Long visit(Contradiction conclusion, I input) {
		timer_.timeContradictions -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(conclusion, input);
		return timer_.timeContradictions += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public Long visit(DecomposedSubsumer conclusion, I input) {
		timer_.timePositiveSubsumers -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(conclusion, input);
		return timer_.timePositiveSubsumers += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public Long visit(DisjointSubsumer conclusion, I input) {
		timer_.timeDisjointSubsumers -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(conclusion, input);
		return timer_.timeDisjointSubsumers += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public Long visit(ForwardLink conclusion, I input) {
		timer_.timeForwardLinks -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(conclusion, input);
		return timer_.timeForwardLinks += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public Long visit(Propagation subConclusion, I input) {
		timer_.timePropagations -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(subConclusion, input);
		return timer_.timePropagations += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public Long visit(SubContextInitialization subConclusion, I input) {
		timer_.timePropagations -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(subConclusion, input);
		return timer_.timePropagations += CachedTimeThread
				.getCurrentTimeMillis();
	}

}
