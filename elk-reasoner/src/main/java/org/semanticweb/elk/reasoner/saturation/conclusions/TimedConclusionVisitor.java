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

public class TimedConclusionVisitor implements ConclusionVisitor<Long> {

	private final ConclusionTimer timer_;
	private final ConclusionVisitor<?> processor_;

	public TimedConclusionVisitor(ConclusionTimer timer,
			ConclusionVisitor<?> processor) {
		this.timer_ = timer;
		this.processor_ = processor;
	}

	@Override
	public Long visit(NegativeSubsumer negSCE, Context context) {
		timer_.timeNegativeSubsumers -= CachedTimeThread.currentTimeMillis;
		processor_.visit(negSCE, context);
		return timer_.timeNegativeSubsumers += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public Long visit(PositiveSubsumer posSCE, Context context) {
		timer_.timePositiveSubsumers -= CachedTimeThread.currentTimeMillis;
		processor_.visit(posSCE, context);
		return timer_.timePositiveSubsumers += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public Long visit(BackwardLink link, Context context) {
		timer_.timeBackwardLinks -= CachedTimeThread.currentTimeMillis;
		processor_.visit(link, context);
		return timer_.timeBackwardLinks += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public Long visit(ForwardLink link, Context context) {
		timer_.timeForwardLinks -= CachedTimeThread.currentTimeMillis;
		processor_.visit(link, context);
		return timer_.timeForwardLinks += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public Long visit(Bottom bot, Context context) {
		timer_.timeBottoms -= CachedTimeThread.currentTimeMillis;
		processor_.visit(bot, context);
		return timer_.timeBottoms += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public Long visit(Propagation propagation, Context context) {
		timer_.timePropagations -= CachedTimeThread.currentTimeMillis;
		processor_.visit(propagation, context);
		return timer_.timePropagations += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public Long visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		timer_.timeDisjointnessAxioms -= CachedTimeThread.currentTimeMillis;
		processor_.visit(disjointnessAxiom, context);
		return timer_.timeDisjointnessAxioms += CachedTimeThread.currentTimeMillis;
	}

}
