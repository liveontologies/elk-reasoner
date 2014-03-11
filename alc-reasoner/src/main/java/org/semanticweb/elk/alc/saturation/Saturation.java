package org.semanticweb.elk.alc.saturation;

/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BacktrackedConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossibleConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PropagatedComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Saturation {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Saturation.class);

	private final static Conclusion CONTEXT_INIT_ = new ContextInitializationImpl();

	private final SaturationState saturationState_;

	private final ConclusionVisitor<Context, Void> ruleApplicationVisitor_;

	private final ConclusionVisitor<Context, Void> backtrackingVisitor_;

	public Saturation(SaturationState saturationState) {
		this.saturationState_ = saturationState;
		this.ruleApplicationVisitor_ = new RuleApplicationVisitor(
				saturationState);
		this.backtrackingVisitor_ = new BacktrackingVisitor(saturationState);
	}

	public void submit(IndexedClassExpression expression) {
		Root root = new Root(expression);
		saturationState_.produce(root, CONTEXT_INIT_);
	}

	public void process() {
		for (;;) {
			Context context = saturationState_.pollActiveContext();
			if (context == null) {
				context = saturationState_.pollPossibleContext();
				if (context == null)
					return;
			}
			for (;;) {
				// TODO: take from the local queue
				Conclusion conclusion = context.takeToDo();
				if (conclusion == null) {
					conclusion = context.takeToGuess();
					if (conclusion == null)
						break;
				}
				if (conclusion instanceof BacktrackedConclusion) {
					context.removeConclusion(conclusion);
					continue;
				}
				if (!context.addConclusion(conclusion))
					continue;
				// else conclusion was added
				conclusion.accept(ruleApplicationVisitor_, context);
				if ((!context.isDeterministic() || conclusion instanceof PossibleConclusion)
						&& !(conclusion instanceof BackwardLink)
						&& !(conclusion instanceof PropagatedComposedSubsumer)) {
					context.pushToHistory(conclusion);
				}
				if (context.isInconsistent()) {
					context.clearToDo();
					for (;;) {
						Conclusion toBacktrack = context.popHistory();
						if (toBacktrack == null)
							break;
						LOGGER_.trace("{}: backtrack {}", context.getRoot(),
								toBacktrack);
						context.removeConclusion(toBacktrack);
						toBacktrack.accept(backtrackingVisitor_, context);
						if (toBacktrack instanceof PossibleConclusion)
							return;
					}
				}

			}
		}
	}
}
