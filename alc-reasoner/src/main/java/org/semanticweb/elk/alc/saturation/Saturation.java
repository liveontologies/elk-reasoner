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

import java.util.ArrayDeque;
import java.util.Queue;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ClashImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.NegatedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalDeterministicConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalPossibleConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.LocalConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.LocalDeterministicConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.LocalPossibleConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PropagatedConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.RetractedConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.LocalConclusionVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Saturation {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Saturation.class);

	private final static ExternalDeterministicConclusion CONTEXT_INIT_ = new ContextInitializationImpl();

	private final SaturationState saturationState_;

	private final Queue<LocalDeterministicConclusion> localDeterministicConclusions_;

	private final Queue<LocalPossibleConclusion> localPossibleConclusions_;

	private final ConclusionProducer conclusionProducer_;

	private final ConclusionVisitor<Context, Void> ruleApplicationVisitor_;

	private final LocalConclusionVisitor<Context, Boolean> backtrackingVisitor_;

	private final LocalConclusionVisitor<Context, Boolean> revertingVisitor_;

	public Saturation(SaturationState saturationState) {
		this.saturationState_ = saturationState;
		this.localDeterministicConclusions_ = new ArrayDeque<LocalDeterministicConclusion>(
				1024);
		this.localPossibleConclusions_ = new ArrayDeque<LocalPossibleConclusion>(
				1024);
		this.conclusionProducer_ = new ConclusionProducer() {
			@Override
			public void produce(Root root, ExternalPossibleConclusion conclusion) {
				LOGGER_.trace("{}: produced {}", root, conclusion);
				saturationState_.produce(root, conclusion);
			}

			@Override
			public void produce(Root root,
					ExternalDeterministicConclusion conclusion) {
				LOGGER_.trace("{}: produced {}", root, conclusion);
				saturationState_.produce(root, conclusion);
			}

			@Override
			public void produce(LocalDeterministicConclusion conclusion) {
				LOGGER_.trace("produced {}", conclusion);
				localDeterministicConclusions_.add(conclusion);
			}

			@Override
			public void produce(LocalPossibleConclusion conclusion) {
				LOGGER_.trace("produced {}", conclusion);
				localPossibleConclusions_.add(conclusion);
			}
		};
		this.ruleApplicationVisitor_ = new RuleApplicationVisitor(
				conclusionProducer_);
		this.backtrackingVisitor_ = new BacktrackingVisitor(conclusionProducer_);
		this.revertingVisitor_ = new RevertingVisitor(conclusionProducer_);
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

	boolean checkSubsumerOptimized(Context context,
			IndexedClassExpression possibleSubsumer) {
		LOGGER_.trace("{}: checking possible subsumer {}", context.getRoot(),
				possibleSubsumer);
		// make sure everything is processed
		process();
		if (context.isInconsistent())
			return true;
		LocalDeterministicConclusion conjecture = new NegatedSubsumerImpl(
				possibleSubsumer);
		// backtrack everything
		for (;;) {
			LocalConclusion toBacktrack = context.popHistory();
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
			processContext(context);
		}
		process();
		return (context.getSubsumers().contains(possibleSubsumer));
	}

	boolean checkSubsumerSimple(Context context,
			IndexedClassExpression possibleSubsumer) {
		LOGGER_.trace("{}: checking possible subsumer {}", context.getRoot(),
				possibleSubsumer);
		// make sure everything is processed
		process();
		Root conjectureRoot = Root.addNegativeMember(context.getRoot(),
				possibleSubsumer);
		saturationState_.produce(conjectureRoot, CONTEXT_INIT_);
		process();
		// saturationState_.checkSaturation();
		return (saturationState_.getContext(conjectureRoot).isInconsistent());
	}

	public boolean checkSubsumer(Context context,
			IndexedClassExpression possibleSubsumer) {
		return checkSubsumerSimple(context, possibleSubsumer);
		// return checkSubsumerOptimized(context, possibleSubsumer);
	}

	public void process() {
		for (;;) {
			Context context = saturationState_.pollActiveContext();
			if (context == null) {
				context = saturationState_.pollPossibleContext();
				if (context == null) {
					return;
				}
			}
			processContext(context);
		}
	}

	public void processContext(Context context) {
		for (;;) {
			Conclusion conclusion = localDeterministicConclusions_.poll();
			if (conclusion == null) {
				conclusion = context.takeToDo();
				if (conclusion == null) {
					conclusion = localPossibleConclusions_.poll();
					if (conclusion == null) {
						conclusion = context.takeToGuess();
						if (conclusion == null)
							break;
					}
				}
			}
			LOGGER_.trace("{}: processing {}", context.getRoot(), conclusion);
			if (conclusion instanceof RetractedConclusion) {
				if (!context.removeConclusion(conclusion))
					LOGGER_.error("{}: backtracked conclusion not found: {}!",
							context, conclusion);
				continue;
			}
			if (conclusion instanceof PropagatedConclusion) {
				// check if the conclusion is still relevant
				PropagatedConclusion propagatedConclusion = ((PropagatedConclusion) conclusion);
				IndexedObjectProperty relation = propagatedConclusion
						.getRelation();
				Root sourceRoot = propagatedConclusion.getSourceRoot();
				if (!context.getForwardLinks().get(relation)
						.contains(sourceRoot.getPositiveMember())
						|| !context.getNegativePropagations().get(relation)
								.equals(sourceRoot.getNegatitveMembers())) {
					LOGGER_.trace("{}: obsolete {}", context, conclusion);
					continue;
				}
			}
			if (!context.addConclusion(conclusion))
				continue;

			if (conclusion instanceof LocalConclusion
					&& !context.isInconsistent()
					&& (!context.isDeterministic() || conclusion instanceof LocalPossibleConclusion)) {
				context.pushToHistory((LocalConclusion) conclusion);
			}

			conclusion.accept(ruleApplicationVisitor_, context);

			if (context.hasClash()) {
				localDeterministicConclusions_.clear();
				boolean proceedNext;
				do {
					LocalConclusion toBacktrack = context.popHistory();
					if (toBacktrack == null) {
						LOGGER_.trace("{}: nothing to backtrack",
								context.getRoot());
						break;
					}
					proceedNext = toBacktrack.accept(backtrackingVisitor_,
							context);
					context.removeConclusion(toBacktrack);
				} while (proceedNext);
				if (!context.getInconsistentSuccessors().isEmpty()) {
					conclusionProducer_.produce(ClashImpl.getInstance());
				}
			}

		}
	}
}
