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
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ClashImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.NegatedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BacktrackedConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossibleConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PropagatedClash;
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

	private final ConclusionVisitor<Context, Void> revertingVisitor_;

	public Saturation(SaturationState saturationState) {
		this.saturationState_ = saturationState;
		this.ruleApplicationVisitor_ = new RuleApplicationVisitor(
				saturationState);
		this.backtrackingVisitor_ = new BacktrackingVisitor(saturationState);
		this.revertingVisitor_ = new RevertingVisitor(saturationState);
	}

	public void submit(IndexedClassExpression expression) {
		Root root = new Root(expression);
		saturationState_.produce(root, CONTEXT_INIT_);
	}

	public void submit(IndexedClassExpression expression,
			IndexedClassExpression possibleSubsumer) {
		Root root = new Root(expression, possibleSubsumer);
		saturationState_.produce(root, CONTEXT_INIT_);
	}

	public void discard(IndexedClassExpression expression,
			IndexedClassExpression possibleSubsumer) {
		Root root = new Root(expression, possibleSubsumer);
		saturationState_.discard(root);
	}

	public boolean checkSubsumerOptimized(Context context,
			IndexedClassExpression possibleSubsumer) {
		LOGGER_.trace("{}: checking possible subsumer {}", context.getRoot(),
				possibleSubsumer);
		// make sure everything is processed
		process();
		Conclusion conjecture = new NegatedSubsumerImpl(possibleSubsumer);
		// backtrack everything
		for (;;) {
			Conclusion toBacktrack = context.popHistory();
			if (toBacktrack == null) {
				LOGGER_.trace("{}: nothing to backtrack", context.getRoot());
				break;
			}
			toBacktrack.accept(revertingVisitor_, context);
			context.removeConclusion(toBacktrack);
		}
		if (context.addConclusion(conjecture)) {
			context.pushToHistory(conjecture);
			// start applying the rules
			conjecture.accept(ruleApplicationVisitor_, context);
		}
		process();
		return (context.getSubsumers().contains(possibleSubsumer));
	}

	public boolean checkSubsumer(Context context,
			IndexedClassExpression possibleSubsumer) {
		LOGGER_.trace("{}: checking possible subsumer {}", context.getRoot(),
				possibleSubsumer);
		// make sure everything is processed
		process();
		Root conjectureRoot = Root.addNegativeMember(context.getRoot(),
				possibleSubsumer);
		saturationState_.produce(conjectureRoot, CONTEXT_INIT_);
		process();
		return (saturationState_.getContext(conjectureRoot).isInconsistent());
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
				LOGGER_.trace("{}: processing {}", context.getRoot(),
						conclusion);
				if (conclusion instanceof BacktrackedConclusion) {
					context.removeConclusion(conclusion);
					continue;
				}
				// else conclusion was added
				if (!context.addConclusion(conclusion))
					continue;
				if ((!context.isDeterministic() || conclusion instanceof PossibleConclusion)
						&& !(conclusion instanceof BackwardLink)
						&& !(conclusion instanceof PropagatedClash)) {
					context.pushToHistory(conclusion);
				}
				conclusion.accept(ruleApplicationVisitor_, context);
				if (context.hasClash()) {
					context.clearToDo();
					for (;;) {
						Conclusion toBacktrack = context.popHistory();
						if (toBacktrack == null) {
							LOGGER_.trace("{}: nothing to backtrack",
									context.getRoot());
							break;
						}
						toBacktrack.accept(backtrackingVisitor_, context);
						context.removeConclusion(toBacktrack);
						if (toBacktrack instanceof PossibleConclusion)
							break;
					}
					if (!context.getPropagatedClashes().isEmpty())
						saturationState_.produce(context,
								ClashImpl.getInstance());
				}

			}
		}
	}
}
