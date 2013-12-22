package org.semanticweb.elk.reasoner.saturation.conclusions;

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

import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.logging.CachedTimeThread;

public class TimedConclusionVisitor implements ConclusionVisitor<Long, Context> {

	private final ConclusionTimer timer_;
	private final ConclusionVisitor<?, Context> processor_;

	public TimedConclusionVisitor(ConclusionTimer timer,
			ConclusionVisitor<?, Context> processor) {
		this.timer_ = timer;
		this.processor_ = processor;
	}

	@Override
	public Long visit(ComposedSubsumer negSCE, Context context) {
		timer_.timeNegativeSubsumers -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(negSCE, context);
		return timer_.timeNegativeSubsumers += CachedTimeThread.getCurrentTimeMillis();
	}

	@Override
	public Long visit(DecomposedSubsumer posSCE, Context context) {
		timer_.timePositiveSubsumers -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(posSCE, context);
		return timer_.timePositiveSubsumers += CachedTimeThread.getCurrentTimeMillis();
	}

	@Override
	public Long visit(BackwardLink link, Context context) {
		timer_.timeBackwardLinks -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(link, context);
		return timer_.timeBackwardLinks += CachedTimeThread.getCurrentTimeMillis();
	}

	@Override
	public Long visit(ForwardLink link, Context context) {
		timer_.timeForwardLinks -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(link, context);
		return timer_.timeForwardLinks += CachedTimeThread.getCurrentTimeMillis();
	}

	@Override
	public Long visit(Contradiction bot, Context context) {
		timer_.timeContradictions -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(bot, context);
		return timer_.timeContradictions += CachedTimeThread.getCurrentTimeMillis();
	}

	@Override
	public Long visit(Propagation propagation, Context context) {
		timer_.timePropagations -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(propagation, context);
		return timer_.timePropagations += CachedTimeThread.getCurrentTimeMillis();
	}

	@Override
	public Long visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		timer_.timeDisjointnessAxioms -= CachedTimeThread.getCurrentTimeMillis();
		processor_.visit(disjointnessAxiom, context);
		return timer_.timeDisjointnessAxioms += CachedTimeThread.getCurrentTimeMillis();
	}

}
