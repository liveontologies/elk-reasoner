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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BacktrackedBackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ClashImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ComposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.NegatedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalDeterministicConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalPossibleConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.LocalConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.LocalDeterministicConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.LocalPossibleConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.NegatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossibleComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossibleDecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PropagatedConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.RetractedConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.LocalConclusionVisitor;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.LazySetUnion;
import org.semanticweb.elk.util.collections.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Saturation {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Saturation.class);

	private static final ExternalDeterministicConclusion CONTEXT_INIT_ = new ContextInitializationImpl();

	/**
	 * if {@code true} will use an optimized subsumption test
	 */
	private final static boolean OPTIMIZED_SUBSUMPTION_TEST_ = true;

	/**
	 * if {@code true}, the integrity of saturation will be periodically tested
	 */
	private final static boolean CHECK_SATURATION_ = false;

	/**
	 * if {@code true}, some statistics will be printed
	 */
	private static final boolean PRINT_STATS_ = false;

	private final SaturationState saturationState_;

	private final Queue<LocalDeterministicConclusion> localDeterministicConclusions_;

	private final ConclusionProducer conclusionProducer_;

	private final ConclusionVisitor<Context, Void> ruleApplicationVisitor_;

	private final LocalConclusionVisitor<Context, Boolean> backtrackingVisitor_;

	private final LocalConclusionVisitor<Context, Boolean> revertingVisitor_;

	/**
	 * The {@link Context} that is currently processed by the saturation
	 */
	private Context activeContext_;

	/*
	 * temporary maps to buffer produced and retracted backward links to avoid
	 * the backward links being added and removed
	 */
	private final Multimap<IndexedObjectProperty, Root> producedBackwardLinks_;
	private final Multimap<IndexedObjectProperty, Root> rectractedBackwardLinks_;

	// some statistics counters
	private static int inconsistentRootCount_ = 0;
	private static int addedConclusions_ = 0;
	private static int removedConclusions_ = 0;

	public Saturation(SaturationState saturationState) {
		this.saturationState_ = saturationState;
		this.localDeterministicConclusions_ = new ArrayDeque<LocalDeterministicConclusion>(
				1024);
		this.producedBackwardLinks_ = new HashSetMultimap<IndexedObjectProperty, Root>(
				64);
		this.rectractedBackwardLinks_ = new HashSetMultimap<IndexedObjectProperty, Root>(
				64);
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
				if (conclusion instanceof BackwardLink) {
					BackwardLink link = (BackwardLink) conclusion;
					IndexedObjectProperty relation = link.getRelation();
					if (link instanceof RetractedConclusion) {
						if (!producedBackwardLinks_.remove(relation, root))
							rectractedBackwardLinks_.add(relation, root);
					} else {
						producedBackwardLinks_.add(relation, root);
					}
					return;
				}
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
				saturationState_.produce(activeContext_, conclusion);
			}
		};
		this.ruleApplicationVisitor_ = new RuleApplicationVisitor(
				conclusionProducer_);
		this.backtrackingVisitor_ = new BacktrackingVisitor(conclusionProducer_);
		this.revertingVisitor_ = new RevertingVisitor(conclusionProducer_);
	}

	/**
	 * Submits the given {@link IndexedClassExpression} for checking
	 * satisfiability
	 * 
	 * @param expression
	 */
	public void submit(IndexedClassExpression expression) {
		Root root = new Root(expression);
		saturationState_.produce(root, CONTEXT_INIT_);
	}

	public boolean checkSubsumer(Context context,
			IndexedClassExpression possibleSubsumer) {
		LOGGER_.trace("{}: checking possible subsumer {}", context,
				possibleSubsumer);
		if (context.isInconsistent())
			return true;
		if (!possibleSubsumer.occursNegatively()
				&& !(possibleSubsumer instanceof IndexedClass))
			LOGGER_.error("{}: checking subsumption with {} not supported",
					context, possibleSubsumer);
		process();
		if (!context.getSubsumers().contains(possibleSubsumer))
			return false;
		/*
		 * use one of the two methods: simple tests just creates a context with
		 * the root obtained by the root of the original context and adding the
		 * negated possible subsumer; optimized test, instead, reuses the
		 * existing contexts and adds a negative subsumer for the possible
		 * subsumer
		 */
		return OPTIMIZED_SUBSUMPTION_TEST_ ? checkSubsumerOptimized(context,
				possibleSubsumer) : checkSubsumerSimple(context,
				possibleSubsumer);
	}

	private boolean checkSubsumerSimple(Context context,
			IndexedClassExpression possibleSubsumer) {
		LOGGER_.trace("{}: checking possible subsumer {}", context,
				possibleSubsumer);
		Root conjectureRoot = Root.addNegativeMember(context.getRoot(),
				possibleSubsumer);
		saturationState_.produce(conjectureRoot, CONTEXT_INIT_);
		process();
		return (saturationState_.getContext(conjectureRoot).isInconsistent());
	}

	private boolean checkSubsumerOptimized(Context context,
			IndexedClassExpression possibleSubsumer) {
		// set the active context to be used for newly produced local
		// conclusions
		activeContext_ = context;
		// we are going to re-saturate this context
		saturationState_.setNotSaturated(context);
		// backtrack everything
		for (;;) {
			LocalConclusion toBacktrack = context.popHistory();
			if (toBacktrack == null) {
				LOGGER_.trace("{}: nothing to backtrack", context);
				break;
			}
			toBacktrack.accept(revertingVisitor_, context);
			if (!context.removeConclusion(toBacktrack))
				LOGGER_.error("{}: cannot backtrack {}", context, toBacktrack);
			LOGGER_.trace("{}: backtracking {}", context, toBacktrack);
			removedConclusions_++;
		}
		restoreValidConclusions(context);
		if (context.getSubsumers().contains(possibleSubsumer)) {
			// it was derived deterministically
			LOGGER_.trace("{}: deterministic subsumer {}", context,
					possibleSubsumer);
			return true;
		}
		/*
		 * else we will add negation of the possible subsumer as the first
		 * "nondeterministic" conclusion; if the possible subsumer will still be
		 * derived, this conclusion must be backtracked, and so, the possible
		 * subsumer will be derived deterministically
		 */
		// LocalConclusion conjecture = new ConjectureNonSubsumerImpl(
		// possibleSubsumer);
		// FIXME: or some reason, the above results in more inferences, although
		// definite conclusions should result in fewer non-deterministic steps
		LocalConclusion conjecture = new NegatedSubsumerImpl(possibleSubsumer);
		if (context.addConclusion(conjecture)) {
			addedConclusions_++;
			context.pushToHistory(conjecture);
			// start applying the rules
			conjecture.accept(ruleApplicationVisitor_, context);
			processDeterministic(context, BacktrackingListener.DUMMY);
			process();
		} else {
			LOGGER_.error("{}: conjecture cannot be added {}", context,
					conjecture);
		}
		if (context.getSubsumers().contains(possibleSubsumer)) {
			// if (context.getComposedSubsumers().contains(possibleSubsumer))
			// LOGGER_.error("{}: subsumer {} is still not definite!",
			// context, possibleSubsumer);
			return true;
		}
		return false;
	}

	Set<IndexedClass> getSubsumersOptimized(Context context) {
		// make sure everything is processed
		process();
		Set<IndexedClass> subsumerCandidates = new HashSet<IndexedClass>(32);
		// initializing with current possible subsumers
		for (IndexedClassExpression possibleSubsumer : context
				.getComposedSubsumers()) {
			if (possibleSubsumer instanceof IndexedClass)
				subsumerCandidates.add((IndexedClass) possibleSubsumer);
		}
		for (;;) {
			// we are going to re-saturate this context
			activeContext_ = context;
			// visitor for exploring branches
			LocalConclusionVisitor<Context, Boolean> branchExplorer = new SubsumptionExploringVisitor(
					conclusionProducer_, subsumerCandidates);
			boolean proceedNext = true;
			while (proceedNext) {
				LocalConclusion toBacktrack = context.popHistory();
				if (toBacktrack == null) {
					if (proceedNext && context.setNotSaturated())
						// this means that the last removed conclusion in
						// history was deterministic or history was empty
						LOGGER_.trace("{}: history fully explored", context);
					break;
				}
				proceedNext = toBacktrack.accept(branchExplorer, context);
				if (!context.removeConclusion(toBacktrack))
					LOGGER_.error("{}: cannot backtrack {}", context,
							toBacktrack);
				LOGGER_.trace("{}: backtracking {}", context, toBacktrack);
				removedConclusions_++;
			}
			restoreValidConclusions(context);
			// re-saturate for the new choices
			processDeterministic(context, BacktrackingListener.DUMMY);
			process();
			if (context.setSaturated()) {
				// context was set not saturated before, this means
				// that all (relevant) non-deterministic choices have
				// been explored, so we are done
				break;
			}
			// filter out subsumer candidates
			Set<IndexedClassExpression> newPossibleSubsumers = context
					.getComposedSubsumers();
			Iterator<IndexedClass> subsumerCandidatesIterator = subsumerCandidates
					.iterator();
			while (subsumerCandidatesIterator.hasNext()) {
				IndexedClass candidate = subsumerCandidatesIterator.next();
				if (!newPossibleSubsumers.contains(candidate)) {
					LOGGER_.trace(
							"{}: {} is missing in a new branch, deleting",
							context, candidate);
					subsumerCandidatesIterator.remove();
				}
			}
			if (context.getPossibleComposedSubsumers().isEmpty()
					&& context.getPossibleDecomposedSubsumers().isEmpty()) {
				// no choices left anymore, we are at the last branch
				break;
			}
		}
		// finally adding all deterministically derived subsumers
		Set<IndexedClassExpression> nonDeterministicSubsumers = context
				.getComposedSubsumers();
		for (IndexedClassExpression subsumer : context.getSubsumers()) {
			if (subsumer instanceof IndexedClass
					&& !nonDeterministicSubsumers.contains(subsumer)) {
				subsumerCandidates.add((IndexedClass) subsumer);
			}
		}
		return subsumerCandidates;
	}

	public void process() {
		process(BacktrackingListener.DUMMY);
	}

	/**
	 * Processes all previously submitted {@link IndexedClassExpression}s for
	 * checking satisfiability
	 */
	public void process(BacktrackingListener listener) {
		for (;;) {
			activeContext_ = saturationState_.pollActiveContext();
			if (activeContext_ == null) {
				activeContext_ = saturationState_.pollPossibleContext();
				if (activeContext_ == null)
					break;
				process(activeContext_, listener);
				continue;
			}
			processDeterministic(activeContext_, listener);
		}
		// setting all contexts as saturated
		for (;;) {
			Context context = saturationState_.takeAndSetSaturated();
			if (context == null)
				break;
		}
		if (CHECK_SATURATION_)
			saturationState_.checkSaturation();
	}

	public int getAddedConclusionsCount() {
		return addedConclusions_;
	}

	public int getRemovedConclusionsCount() {
		return removedConclusions_;
	}

	private void producedBufferedBackwardLinks(Context context) {
		Root sourceRoot = context.getRoot();
		for (IndexedObjectProperty relation : rectractedBackwardLinks_.keySet()) {
			for (Root root : rectractedBackwardLinks_.get(relation)) {
				saturationState_.produce(root, new BacktrackedBackwardLinkImpl(
						sourceRoot, relation));
				context.removePropagatedConclusions(root);
			}
		}
		for (IndexedObjectProperty relation : producedBackwardLinks_.keySet()) {
			for (Root root : producedBackwardLinks_.get(relation)) {
				saturationState_.produce(root, new BackwardLinkImpl(sourceRoot,
						relation));
			}
		}
		rectractedBackwardLinks_.clear();
		producedBackwardLinks_.clear();
	}

	private void processDeterministic(Context context,
			BacktrackingListener listener) {
		for (;;) {
			Conclusion conclusion = localDeterministicConclusions_.poll();
			if (conclusion == null) {
				conclusion = context.takeToDo();
				if (conclusion == null) {
					break;
				}
			}
			process(context, conclusion, listener);
		}
		producedBufferedBackwardLinks(context);
	}

	private void process(Context context, BacktrackingListener listener) {
		for (;;) {
			Conclusion conclusion = localDeterministicConclusions_.poll();
			if (conclusion == null) {
				conclusion = context.takeToDo();
				if (conclusion == null) {
					conclusion = context.takeToGuess();
					if (conclusion == null)
						break;
				}
			}
			process(context, conclusion, listener);
		}
		producedBufferedBackwardLinks(context);
	}

	private void process(Context context, Conclusion conclusion,
			BacktrackingListener listener) {
		LOGGER_.trace("{}: processing {}", context, conclusion);
		if (conclusion instanceof RetractedConclusion) {
			if (!context.removeConclusion(conclusion))
				LOGGER_.error("{}: retracted conclusion not found: {}!",
						context, conclusion);
			removedConclusions_++;
			return;
		}
		if (conclusion instanceof PropagatedConclusion) {
			// check if the conclusion is still relevant
			PropagatedConclusion propagatedConclusion = ((PropagatedConclusion) conclusion);
			IndexedObjectProperty relation = propagatedConclusion.getRelation();
			Root sourceRoot = propagatedConclusion.getSourceRoot();
			if (!context.getForwardLinks().get(relation)
					.contains(sourceRoot.getPositiveMember())
					|| !context.getNegativePropagations().get(relation)
							.equals(sourceRoot.getNegatitveMembers())) {
				LOGGER_.trace("{}: conclusion not relevant {}", context,
						conclusion);
				return;
			}
		}
		if (!context.addConclusion(conclusion))
			return;

		addedConclusions_++;

		if (PRINT_STATS_) {
			if (conclusion == ClashImpl.getInstance()
					&& context.isInconsistent()) {
				inconsistentRootCount_++;
				if ((inconsistentRootCount_ / 1000) * 1000 == inconsistentRootCount_)
					LOGGER_.info("{} inconsistent roots",
							inconsistentRootCount_);
			}
		}

		if (conclusion instanceof LocalConclusion
				&& !context.isInconsistent()
				&& (!context.isDeterministic() || context.isSaturated() || conclusion instanceof LocalPossibleConclusion)) {
			context.pushToHistory((LocalConclusion) conclusion);
		}

		conclusion.accept(ruleApplicationVisitor_, context);

		if (context.hasClash()) {
			localDeterministicConclusions_.clear();
			boolean proceedNext = true;
			while (proceedNext) {
				LocalConclusion toBacktrack = context.popHistory();
				if (toBacktrack == null) {
					listener.notifyEndOfBacktracking(context, null);
					LOGGER_.trace("{}: nothing to backtrack", context.getRoot());
					if (proceedNext && context.setNotSaturated())
						// this means that the last removed conclusion in
						// history was deterministic or history was empty
						LOGGER_.trace("{}: history fully explored", context);
					break;
				}

				proceedNext = toBacktrack.accept(backtrackingVisitor_, context);

				if (!context.removeConclusion(toBacktrack))
					LOGGER_.error("{}: cannot backtrack {}", context,
							toBacktrack);
				LOGGER_.trace("{}: backtracking {}", context, toBacktrack);

				listener.notifyBacktracking(context, toBacktrack);

				removedConclusions_++;

				if (!proceedNext) {
					// will exit the loop on this iteration
					listener.notifyEndOfBacktracking(context, toBacktrack);
				}

			}

			restoreValidConclusions(context);
		}

	}

	/**
	 * Restores (propagated) conclusions that are still valid. This is needed
	 * after every backtracking step.
	 * 
	 * @param context
	 */
	void restoreValidConclusions(Context context) {
		if (!context.getInconsistentSuccessors().isEmpty()) {
			conclusionProducer_.produce(ClashImpl.getInstance());
		}
		for (Root root : context.getPropagatedComposedSubsumers().keySet()) {
			for (IndexedClassExpression subsumer : context
					.getPropagatedComposedSubsumers().get(root))
				conclusionProducer_.produce(new ComposedSubsumerImpl(subsumer));
		}
	}

	public Collection<IndexedClass> getAtomicSubsumers(
			IndexedClassExpression rootClass) {
		Context rootContext = saturationState_.getContext(rootClass);

		if (rootContext.getSaturatedContext() != null) {
			// everything has been computed and is up-to-date
			return rootContext.getSaturatedContext().getAtomicSubsumers();
		}

		if (rootContext.isInconsistent()) {
			// TODO return {owl:Nothing}
			LOGGER_.trace("{} is unsatisfiable", rootClass);
			return null;
		}

		LOGGER_.trace("Started computing subsumers for {}", rootClass);
		rootContext.setSaturated(new SaturatedContext(
				getSubsumersOptimized(rootContext)));
		return rootContext.getSaturatedContext().getAtomicSubsumers();

	}

	/**
	 * TODO
	 * 
	 * @param rootClass
	 * @return
	 */
	public Collection<IndexedClass> getAtomicSubsumersOld(
			IndexedClassExpression rootClass) {
		Context rootContext = saturationState_.getContext(rootClass);

		if (rootContext.getSaturatedContext() != null) {
			// everything has been computed and is up-to-date
			return rootContext.getSaturatedContext().getAtomicSubsumers();
		}

		if (rootContext.isInconsistent()) {
			// TODO return {owl:Nothing}
			LOGGER_.trace("{} is unsatisfiable", rootClass);
			return null;
		}

		LOGGER_.trace("Started computing subsumers for {}", rootClass);

		rootContext.setNotSaturated();
		// the root class has a model, we're now at the first branch which
		// finished without deriving a clash -- that's the starting point.
		Set<IndexedClass> candidates = new HashSet<IndexedClass>();
		AtomicSubsumerCandidatesCollector collector = new AtomicSubsumerCandidatesCollector(
				rootContext.getRoot(), null, candidates);
		// first, get definite atomic subsumers, i.e. derived before the first
		// branching point. They need not be checked later.
		Set<IndexedClass> subsumers = new HashSet<IndexedClass>(rootContext
				.getSubsumers().size()
				- rootContext.getComposedSubsumers().size());

		for (IndexedClassExpression possibleSubsumer : rootContext
				.getSubsumers()) {
			if (possibleSubsumer instanceof IndexedClass
					&& !rootContext.getComposedSubsumers().contains(
							possibleSubsumer)) {
				subsumers.add((IndexedClass) possibleSubsumer);
			}
		}

		for (;;) {
			// Start moving up the branch tree. Iterate backwards over the
			// history and collect all atomic subsumers till the next branching
			// point (possible conclusion).
			activeContext_ = rootContext;
			backtrackToLastBranchingPoint(rootContext, collector);

			if (collector.getCurrentBranchingSubsumer() == null) {
				// the local history must be empty
				break;
			}

			LOGGER_.trace("Exploring a new branch starting at {}",
					collector.getCurrentBranchingSubsumer());
			// if processing backtracks above the current top branching point,
			// the collector object will start collecting atomic subsumer
			// candidates.
			conclusionProducer_.produce(rootContext.getRoot(),
					new ContextInitializationImpl());
			process(collector);

			if (!rootContext.hasClash()) {
				LOGGER_.trace(
						"Found an alternative model for {}, begin filtering subsumer candidates",
						rootClass);
				// Finished exploring an alternative branch, found another
				// model, can now filter the set of candidates by removing those
				// not derived deterministically.
				Set<IndexedClassExpression> possibleSubsumers = rootContext
						.isDeterministic() ? rootContext.getSubsumers()
						: rootContext.getComposedSubsumers();
				Iterator<IndexedClass> candidateIterator = candidates
						.iterator();

				while (candidateIterator.hasNext()) {
					IndexedClass candidate = candidateIterator.next();

					if (!possibleSubsumers.contains(candidate)) {
						LOGGER_.trace(
								"{} has not been derived deterministically in this branch, deleting",
								candidate);
						// this conclusion has not been derived in this branch
						// so it's not a definite subsumer (there's a model
						// which witnesses this non-subsumption).
						candidateIterator.remove();
					}
				}
			} else {
				LOGGER_.trace(
						"{} has a clash so all candidate subsumers are retained",
						rootContext);
				// nothing to do, we're interested in exploring models which
				// witness non-subsumptions but this
				// branch is inconsistent so doesn't have a model
			}
		}

		rootContext.setSaturated(new SaturatedContext(
				new LazySetUnion<IndexedClass>(candidates, subsumers)));
		rootContext.setSaturated();

		return rootContext.getSaturatedContext().getAtomicSubsumers();
	}

	private void backtrackToLastBranchingPoint(Context context,
			AtomicSubsumerCandidatesCollector atomicSubsumerCollector) {

		for (;;) {
			LocalConclusion nextFromHistory = context.popHistory();

			if (nextFromHistory == null) {
				atomicSubsumerCollector.notifyEndOfBacktracking(context, null);
				return;
			}

			LOGGER_.trace("Examining history, next: {}", nextFromHistory);

			boolean branchingPoint = !nextFromHistory.accept(
					backtrackingVisitor_, context);

			context.removeConclusion(nextFromHistory);
			/*
			 * TODO can also skip the branching point if no atomic subsumers
			 * were derived after it. Add tests for that.
			 */
			if (branchingPoint
					&& !atomicSubsumerCollector.getCandidates().isEmpty()) {
				// came across the next branching point
				atomicSubsumerCollector.notifyEndOfBacktracking(context,
						nextFromHistory);
				return;
			}
			// see if it's an atomic subsumer
			atomicSubsumerCollector
					.notifyBacktracking(context, nextFromHistory);
		}
	}

	/**
	 * Responsible for examining conclusions during backtracking and collecting
	 * some of them (atomic subsumers) as subsumer candidates.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class AtomicSubsumerCandidatesCollector implements
			BacktrackingListener {

		private final Root root_;

		private final Collection<IndexedClass> candidates_;

		private LocalPossibleConclusion currentBranchingPoint_;

		private boolean collecting_;

		AtomicSubsumerCandidatesCollector(Root root,
				LocalPossibleConclusion branchingPoint,
				Collection<IndexedClass> candidates) {
			root_ = root;
			candidates_ = candidates;
			// set the initial branching point
			if (branchingPoint != null) {
				// TODO so far we assume that all branching points are subsumers
				currentBranchingPoint_ = branchingPoint;
				collecting_ = false;
			} else {
				// there is no current branching point, i.e. we backtrack to the
				// nearest one start collecting candidates immediately
				collecting_ = true;
			}
		}

		boolean isPossibleSubsumer(LocalConclusion conclusion) {
			return conclusion instanceof PossibleComposedSubsumer
					|| conclusion instanceof PossibleDecomposedSubsumer;
		}

		@Override
		public void notifyBacktracking(Context context,
				LocalConclusion conclusionToBacktrack) {
			if (root_ != context.getRoot()) {
				return;
			}

			if (collecting_) {
				// we've backtracked far enough to start collecting atomic
				// subsumer candidates
				if (conclusionToBacktrack instanceof Subsumer) {
					IndexedClassExpression expression = ((Subsumer) conclusionToBacktrack)
							.getExpression();

					if (expression instanceof IndexedClass) {
						candidates_.add((IndexedClass) expression);

						LOGGER_.trace(
								"New subsumer candidate found during backtracking: {}",
								expression);
					}
				}
			} else {
				// check if we've backtracked far enough to start collecting
				// atomic subsumer candidates
				if (conclusionToBacktrack instanceof NegatedSubsumer) {
					IndexedClassExpression expression = ((NegatedSubsumer) conclusionToBacktrack)
							.getNegatedExpression();

					if (expression == ((Subsumer) currentBranchingPoint_)
							.getExpression()) {
						LOGGER_.trace(
								"Backtracked beyond {}, starting to collect candidates",
								expression);

						collecting_ = true;
					}
				}
			}
		}

		@Override
		public void notifyEndOfBacktracking(Context context,
				LocalConclusion lastBacktracked) {
			if (root_ != context.getRoot()) {
				return;
			}

			collecting_ = false;

			if (lastBacktracked == null) {
				// exhausted the local history
				currentBranchingPoint_ = null;
				return;
			}

			// update the top branching point
			if (isPossibleSubsumer(lastBacktracked)) {
				LOGGER_.trace("Stopped backtracking at {}", lastBacktracked);

				currentBranchingPoint_ = (LocalPossibleConclusion) lastBacktracked;
			}
		}

		LocalPossibleConclusion getCurrentBranchingSubsumer() {
			return currentBranchingPoint_;
		}

		Collection<IndexedClass> getCandidates() {
			return candidates_;
		}
	}

}
