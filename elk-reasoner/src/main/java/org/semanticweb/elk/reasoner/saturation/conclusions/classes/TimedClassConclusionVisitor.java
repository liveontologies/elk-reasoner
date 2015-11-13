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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.util.logging.CachedTimeThread;

public class TimedClassConclusionVisitor<I, O> implements ClassConclusion.Visitor<I, O> {

	private final ClassConclusion.Visitor<? super I, O> processor_;
	private final ClassConclusionTimer timer_;

	public TimedClassConclusionVisitor(ClassConclusionTimer timer,
			ClassConclusion.Visitor<? super I, O> processor) {
		this.timer_ = timer;
		this.processor_ = processor;
	}

	@Override
	public O visit(BackwardLink subConclusion, I input) {
		timer_.timeBackwardLinks -= CachedTimeThread.getCurrentTimeMillis();
		O result = processor_.visit(subConclusion, input);
		timer_.timeBackwardLinks += CachedTimeThread.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(SubClassInclusionComposed conclusion, I input) {
		timer_.timeComposedSubsumers -= CachedTimeThread.getCurrentTimeMillis();
		O result = processor_.visit(conclusion, input);
		timer_.timeComposedSubsumers += CachedTimeThread.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ContextInitialization conclusion, I input) {
		timer_.timeContextInitializations -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = processor_.visit(conclusion, input);
		timer_.timeContextInitializations += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(Contradiction conclusion, I input) {
		timer_.timeContradictions -= CachedTimeThread.getCurrentTimeMillis();
		O result = processor_.visit(conclusion, input);
		timer_.timeContradictions += CachedTimeThread.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(SubClassInclusionDecomposed conclusion, I input) {
		timer_.timeDecomposedSubsumers -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = processor_.visit(conclusion, input);
		timer_.timeDecomposedSubsumers += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(DisjointSubsumer conclusion, I input) {
		timer_.timeDisjointSubsumers -= CachedTimeThread.getCurrentTimeMillis();
		O result = processor_.visit(conclusion, input);
		timer_.timeDisjointSubsumers += CachedTimeThread.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ForwardLink conclusion, I input) {
		timer_.timeForwardLinks -= CachedTimeThread.getCurrentTimeMillis();
		O result = processor_.visit(conclusion, input);
		timer_.timeForwardLinks += CachedTimeThread.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(Propagation subConclusion, I input) {
		timer_.timePropagations -= CachedTimeThread.getCurrentTimeMillis();
		O result = processor_.visit(subConclusion, input);
		timer_.timePropagations += CachedTimeThread.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(SubContextInitialization subConclusion, I input) {
		timer_.timeSubContextInitializations -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = processor_.visit(subConclusion, input);
		timer_.timeSubContextInitializations += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

}
