/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.reduction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory for engines that concurrently perform the transitive reduction of
 * the derived subsumption hierarchy between classes. The engines accept
 * instances of {@link TransitiveReductionJob} with the specified root
 * {@link IndexedClassExpression}. Upon successful completion of the job, one of
 * the two types of the {@link TransitiveReductionOutput} can be assigned:
 * either {@link TransitiveReductionOutputUnsatisfiable}, which means that the
 * given root {@link IndexedClassExpression} is unsatisfiable, or
 * {@link TransitiveReductionOutputEquivalentDirect}, which contains information
 * about equivalent classes of the given root {@link IndexedClassExpression} and
 * its direct super-classes.
 * 
 * As usual, to this engine factory it is possible to attach a
 * {@link TransitiveReductionListener} using which one can monitor the
 * processing of jobs and perform actions accordingly.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            the type of the input class expressions for which to compute the
 *            result
 * @param <J>
 *            the type of the jobs that can be processed by this transitive
 *            reduction engine
 * 
 * @see TransitiveReductionOutput
 * @see TransitiveReductionOutputUnsatisfiable
 * @see TransitiveReductionOutputEquivalentDirect
 * @see TransitiveReductionListener
 */
public class TransitiveReductionFactory<R extends IndexedClassExpression, J extends TransitiveReductionJob<R>>
		implements
			InputProcessorFactory<J, TransitiveReductionFactory<R, J>.Engine> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(TransitiveReductionFactory.class);

	/**
	 * The listener object implementing callback functions for this engine
	 */
	private final TransitiveReductionListener<J> listener_;
	/**
	 * The object used to process the finished saturation jobs
	 */
	private final SaturationOutputProcessor saturationOutputProcessor_ = new SaturationOutputProcessor();
	/**
	 * The processed jobs can create new saturation jobs for super classes to be
	 * submitted to this engine. In order to avoid stack overflow due to the
	 * potentially unbounded recursion, we do not submit the jobs immediately,
	 * but use a queue to buffer such created jobs. This queue will be emptied
	 * every time the {@link Engine#process()} method is called.
	 */
	private final Queue<SaturationJobSuperClass<R, J>> auxJobQueue_;

	/**
	 * The jobs which root {@link IndexedClassExpression}s are already saturated
	 */
	private final Queue<J> jobsWithSaturatedRoot_;

	/**
	 * The saturation factory used for computing saturations for relevant
	 * indexed class expressions
	 */
	private final ClassExpressionSaturationFactory<SaturationJobForTransitiveReduction<R, ?, J>> saturationFactory_;

	/**
	 * The {@link SaturationState} keeping the information about saturation
	 */
	private final SaturationState<?> saturationState_;

	private final IndexedClass owlThing_;

	/**
	 * The default equivalence classes for owl:Thing to be used when there are
	 * no (direct) subsumers
	 */
	private final List<ElkClass> defaultTopOutput_;

	/**
	 * A factory for creating contradiction conclusions; used for checking
	 * unsatisfiable contexts
	 */
	private final ClassInconsistency.Factory factory_ = new SaturationConclusionBaseFactory();

	/**
	 * Creating a new transitive reduction engine for the input ontology index
	 * and a listener for executing callback functions.
	 * 
	 * @param saturationState
	 *            the saturation state of the reasoner
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 * @param listener
	 *            the listener object implementing callback functions for this
	 *            engine
	 */
	public TransitiveReductionFactory(final InterruptMonitor interrupter,
			SaturationState<?> saturationState,
			int maxWorkers, TransitiveReductionListener<J> listener) {
		this.listener_ = listener;
		this.auxJobQueue_ = new ConcurrentLinkedQueue<SaturationJobSuperClass<R, J>>();
		this.jobsWithSaturatedRoot_ = new ConcurrentLinkedQueue<J>();
		this.saturationState_ = saturationState;
		this.saturationFactory_ = new ClassExpressionSaturationFactory<SaturationJobForTransitiveReduction<R, ?, J>>(
				new RuleApplicationAdditionFactory<RuleApplicationInput>(
						interrupter, saturationState),
				maxWorkers, new ThisClassExpressionSaturationListener());
		this.owlThing_ = saturationState.getOntologyIndex().getOwlThing();
		this.defaultTopOutput_ = new ArrayList<ElkClass>(1);
		defaultTopOutput_.add(owlThing_.getElkEntity());
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		saturationFactory_.finish();
	}

	@Override
	public boolean isInterrupted() {
		return saturationFactory_.isInterrupted();
	}

	/**
	 * Print statistics about the transitive reduction stage
	 */
	public void printStatistics() {
		saturationFactory_.printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return saturationFactory_.getRuleAndConclusionStatistics();
	}

	/**
	 * The listener class used for the class expression saturation engine, which
	 * is used within this transitive reduction engine
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class ThisClassExpressionSaturationListener
			implements
				ClassExpressionSaturationListener<SaturationJobForTransitiveReduction<R, ?, J>> {

		@Override
		public void notifyFinished(
				SaturationJobForTransitiveReduction<R, ?, J> output)
						throws InterruptedException {
			output.accept(saturationOutputProcessor_);
		}
	}

	/**
	 * The class for processing the finished saturation jobs. It implements the
	 * visitor pattern for {@link SaturationJobForTransitiveReduction}.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class SaturationOutputProcessor
			implements
				SaturationJobVisitor<R, J> {

		@Override
		public void visit(SaturationJobRoot<R, J> saturationJob)
				throws InterruptedException {
			processRootSaturation(saturationJob.initiatorJob);

		}

		@Override
		public void visit(SaturationJobSuperClass<R, J> saturationJob)
				throws InterruptedException {
			/*
			 * In this case the saturation for the super-class candidate is
			 * computed; we need to update the output of the transitive
			 * reduction using this candidate and resume processing of the
			 * transitive reduction state.
			 */
			IndexedClass candidate = saturationJob.getInput();
			TransitiveReductionState<R, J> state = saturationJob.state;
			updateTransitiveReductionState(state, candidate);
			processTransitiveReductionState(state);
		}

		/**
		 * Processing jobs for which saturation of the root
		 * {@link IndexedClassExpression} has been computed
		 * 
		 * @param initiatorJob
		 *            the jobs for which saturation of the root
		 *            {@link IndexedClassExpression} has been computed
		 * @throws InterruptedException
		 *             if interrupted while processing
		 */
		private void processRootSaturation(J initiatorJob)
				throws InterruptedException {
			/*
			 * It is required that the saturation for the root indexed class
			 * expression of the initiator job should already be computed
			 */
			R root = initiatorJob.getInput();
			Context saturation = saturationState_.getContext(root);

			/*
			 * If saturation is unsatisfiable, return the unsatisfiable output.
			 */
			if (saturation
					.containsConclusion(factory_.getContradiction(root))) {
				LOGGER_.trace("{}: transitive reduction finished: inconsistent",
						root);

				TransitiveReductionOutput<R> output = new TransitiveReductionOutputUnsatisfiable<R>(
						root);
				initiatorJob.setOutput(output);
				listener_.notifyFinished(initiatorJob);
				return;
			}
			/*
			 * Otherwise, to perform the transitive reduction, we need to
			 * compute the saturation for every derived indexed super-class of
			 * the saturation. We initialize the transitive reduction state for
			 * this purpose.
			 */
			TransitiveReductionState<R, J> state = new TransitiveReductionState<R, J>(
					initiatorJob, saturationState_);
			/*
			 * here we processing this state where we compute the output of the
			 * transitive reduction; when the state will be processed, its
			 * output will be submitted as the output of transitive reduction.
			 */
			processTransitiveReductionState(state);
		}

		/**
		 * Processing of transitive reduction state by iterating over the
		 * derived subsumers and updating the equivalent and direct subsumers
		 * using their saturations. If the saturation is not yet computed, a new
		 * saturation job is created for this purpose and the processing of the
		 * state is suspended until the job is finished.
		 * 
		 * @param state
		 *            the transitive reduction state to be processed
		 * @throws InterruptedException
		 *             if was interrupted during the processing
		 */
		private void processTransitiveReductionState(
				TransitiveReductionState<R, J> state)
						throws InterruptedException {

			Iterator<IndexedClassExpression> subsumerIterator = state.subsumerIterator;

			while (subsumerIterator.hasNext()) {
				IndexedClassExpression next = subsumerIterator.next();

				if (!(next instanceof IndexedClass))
					continue;

				IndexedClass candidate = (IndexedClass) next;
				Context candidateSaturation = saturationState_
						.getContext(candidate);
				/*
				 * If the saturation for the candidate is not yet computed,
				 * create a corresponding saturation job and suspend processing
				 * of the state until the job will be finished.
				 */
				if (candidateSaturation == null
						|| !candidateSaturation.isInitialized()
						|| !candidateSaturation.isSaturated()) {
					auxJobQueue_.add(new SaturationJobSuperClass<R, J>(
							candidate, state));
					return;
				}
				/*
				 * Otherwise update the transitive state using the saturation
				 */
				updateTransitiveReductionState(state, candidate);
			}

			/* When all candidates are processed, the output is computed */
			TransitiveReductionOutputEquivalentDirect<R> output = computeOutput(
					state);

			// if (output.equivalent.isEmpty()) {
			// LOGGER_.error("{}: empty equivalent class!", output.getRoot());
			// }

			state.initiatorJob.setOutput(output);
			listener_.notifyFinished(state.initiatorJob);

			if (LOGGER_.isTraceEnabled()) {
				R root = output.getRoot();
				LOGGER_.trace(root + ": transitive reduction finished");
				for (ElkClass equivalent : output.getEquivalent()) {
					LOGGER_.trace(root + ": equivalent " + equivalent.getIri());
				}
				for (IndexedClass direct : output.directSubsumers.keySet()) {
					String message = root + ": direct super class " + direct
							+ ": " + output.directSubsumers.get(direct);
					LOGGER_.trace(message);
				}
			}
		}

		/**
		 * Updates the transitive reduction state using a new candidate indexed
		 * super class and its saturation. During this update, all pruned
		 * subsumers that are strictly subsumed by the candidate will be
		 * removed.
		 * 
		 * @param state
		 *            the transitive reduction state in progress for the root
		 * @param candidate
		 *            the super class of the root
		 */
		private void updateTransitiveReductionState(
				TransitiveReductionState<R, J> state, IndexedClass candidate) {

			Set<IndexedClassExpression> candidateSubsumers = saturationState_
					.getContext(candidate).getComposedSubsumers();

			/*
			 * Check if subsumer is equivalent to the root
			 */
			int candidateSubsumersSize = candidateSubsumers.size();

			if (candidateSubsumersSize == state.subsumerCount
					&& candidateSubsumers
							.contains(state.initiatorJob.getInput())) {
				state.rootEquivalent.add(candidate.getElkEntity());
				return;
			}

			if (candidate.getElkEntity() == owlThing_.getElkEntity()
					&& candidateSubsumersSize == 1) {
				/*
				 * if subsumer is top and has no other subsumers (which means,
				 * in particular, it does not occur negatively), then it can be
				 * safely ignored; top will be automatically introduced if no
				 * direct subsumers are found
				 */
				return;
			}

			/*
			 * removing all pruned subsumers strictly subsumed by the candidate
			 */
			if (candidateSubsumersSize > state.prunedSubsumers.size()) {
				/*
				 * iterating over pruned subsumers checking candidate subsumers
				 */
				Iterator<IndexedClass> iteratorPrunedSubsumers = state.prunedSubsumers
						.iterator();
				while (iteratorPrunedSubsumers.hasNext()) {
					IndexedClass prunedSubsumer = iteratorPrunedSubsumers
							.next();
					Set<IndexedClassExpression> prunedSubsumerSubsumers = saturationState_
							.getContext(prunedSubsumer).getComposedSubsumers();
					if (candidateSubsumersSize > prunedSubsumerSubsumers
							.size()) {
						if (candidateSubsumers.contains(prunedSubsumer)) {
							/*
							 * candidate strictly subsumes the pruned subsumer
							 */
							iteratorPrunedSubsumers.remove();
						}
					} else if (prunedSubsumerSubsumers.contains(candidate)
							&& candidateSubsumersSize != prunedSubsumerSubsumers
									.size()) {
						/* pruned subsumer strictly subsumes the candidate */
						return;
					}
				}
			} else {
				/*
				 * iterating over candidate subsumers checking pruned subsumers
				 */
				for (IndexedClassExpression candidateSubsumer : candidateSubsumers) {
					if (!(candidateSubsumer instanceof IndexedClass)
							|| !state.prunedSubsumers
									.contains(candidateSubsumer))
						continue;
					IndexedClass prunedSubsumer = (IndexedClass) candidateSubsumer;
					Set<IndexedClassExpression> prunedSubsumerSubsumers = saturationState_
							.getContext(prunedSubsumer).getComposedSubsumers();
					if (candidateSubsumersSize > prunedSubsumerSubsumers
							.size()) {
						/* candidate strictly subsumes the pruned subsumer */
						state.prunedSubsumers.remove(prunedSubsumer);
					}
				}
			}
			/*
			 * if the candidate has survived all the tests, then it is pruned
			 */
			state.prunedSubsumers.add(candidate);
		}

		/**
		 * Computes the result of the transitive reduction from the transitive
		 * reduction state. During this process, direct subsumers and its
		 * equivalent classes will be computed from the pruned subsumers.
		 * 
		 * @param state
		 * @return
		 */
		private TransitiveReductionOutputEquivalentDirect<R> computeOutput(
				TransitiveReductionState<R, J> state) {

			R root = state.initiatorJob.getInput();

			TransitiveReductionOutputEquivalentDirect<R> output = new TransitiveReductionOutputEquivalentDirect<R>(
					root, state.rootEquivalent);

			/*
			 * if there are no direct subsumers found, then use the default
			 * direct subsumer for owl:Thing unless it is the output for
			 * owl:Thing itself
			 */
			if (state.prunedSubsumers.isEmpty() && !output.getEquivalent()
					.contains(owlThing_.getElkEntity())) {
				output.directSubsumers.put(owlThing_, defaultTopOutput_);
				return output;
			}

			/*
			 * Second pruning stage: it is important that we process subsumers
			 * again in the same order. In the first stage we removed subsumers
			 * that are strictly subsumed by some following subsumers in this
			 * order. Now we removed subsumers that are strictly subsumed by
			 * previous subsumers in this order.
			 */
			for (IndexedClassExpression subsumer : saturationState_
					.getContext(root).getComposedSubsumers()) {
				if (!(subsumer instanceof IndexedClass)
						|| !state.prunedSubsumers.contains(subsumer))
					continue;
				IndexedClass candidate = (IndexedClass) subsumer;
				/*
				 * Otherwise the candidate subsumer must be direct as it
				 * strictly subsumed by neither previous (due to second stage)
				 * nor following (due to first stage) subsumers. We further
				 * remove subsumers that it subsumes (including itself) from the
				 * pruned subsumers and find those that are equivalent to it.
				 */
				Set<IndexedClassExpression> candidateSubsumers = saturationState_
						.getContext(candidate).getComposedSubsumers();
				int candidateSubsumersSize = candidateSubsumers.size();
				List<ElkClass> candidateEquivalent = new ArrayList<ElkClass>(1);
				/* pruning other pruned subsumers */
				if (candidateSubsumersSize > state.prunedSubsumers.size()) {
					/*
					 * iterating over pruned subsumers checking candidate
					 * subsumers
					 */
					Iterator<IndexedClass> iteratorPrunedSubsumers = state.prunedSubsumers
							.iterator();
					while (iteratorPrunedSubsumers.hasNext()) {
						IndexedClass prunedSubsumer = iteratorPrunedSubsumers
								.next();
						Set<IndexedClassExpression> prunedSubsumerSubsumers = saturationState_
								.getContext(prunedSubsumer)
								.getComposedSubsumers();
						if (candidateSubsumersSize >= prunedSubsumerSubsumers
								.size()
								&& candidateSubsumers
										.contains(prunedSubsumer)) {
							/* candidate is subsumed by the pruned subsumer */
							iteratorPrunedSubsumers.remove();
							if (candidateSubsumersSize == prunedSubsumerSubsumers
									.size()
									&& prunedSubsumerSubsumers
											.contains(candidate)) {
								/* candidate equivalent to pruned subsumer */
								candidateEquivalent
										.add(prunedSubsumer.getElkEntity());
							}
						}
					}
				} else {
					/*
					 * iterating over candidate subsumers checking pruned
					 * subsumers
					 */
					for (IndexedClassExpression candidateSubsumer : candidateSubsumers) {
						if (!(candidateSubsumer instanceof IndexedClass)
								|| !state.prunedSubsumers
										.contains(candidateSubsumer))
							continue;
						IndexedClass prunedSubsumer = (IndexedClass) candidateSubsumer;
						/*
						 * otherwise saturation for the candidate subsumer
						 * computed
						 */
						Set<IndexedClassExpression> prunedSubsumerSubsumers = saturationState_
								.getContext(prunedSubsumer)
								.getComposedSubsumers();
						if (candidateSubsumersSize >= prunedSubsumerSubsumers
								.size()
								&& candidateSubsumers
										.contains(prunedSubsumer)) {
							/* candidate is subsumed by the pruned subsumer */
							state.prunedSubsumers.remove(candidateSubsumer);
							if (candidateSubsumersSize == prunedSubsumerSubsumers
									.size()
									&& prunedSubsumerSubsumers
											.contains(candidate)) {
								/* candidate equivalent to pruned subsumer */
								candidateEquivalent
										.add(prunedSubsumer.getElkEntity());
							}
						}
					}
				}
				output.directSubsumers.put(candidate, candidateEquivalent);
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace("{}: new direct subsumer {} [{}]", root,
							candidate, candidateEquivalent);
				}
			}
			if (!state.prunedSubsumers.isEmpty())
				LOGGER_.error("{}: pruned subsumers not removed: {}", root,
						state.prunedSubsumers);
			return output;
		}
	}

	public class Engine implements InputProcessor<J> {

		/**
		 * The saturation engine used for transitive reduction computation
		 */
		private final ClassExpressionSaturationFactory<SaturationJobForTransitiveReduction<R, ?, J>>.Engine saturationEngine = saturationFactory_
				.getEngine();

		// don't allow creating of engines directly; only through the factory
		private Engine() {
		}

		@Override
		public final void submit(J job) {
			R root = job.getInput();

			LOGGER_.trace("{}: transitive reduction started", root);

			Context context = saturationState_.getContext(root);
			if (context != null && context.isInitialized()
					&& context.isSaturated()) {
				jobsWithSaturatedRoot_.add(job);
			} else {
				saturationEngine.submit(new SaturationJobRoot<R, J>(job));
			}
		}

		@Override
		public final void process() throws InterruptedException {
			for (;;) {
				if (isInterrupted()) {
					return;
				}
				J processedJob = jobsWithSaturatedRoot_.poll();
				if (processedJob != null) {
					saturationOutputProcessor_
							.processRootSaturation(processedJob);
					continue;
				}
				saturationEngine.process();
				SaturationJobForTransitiveReduction<R, ?, J> nextJob = auxJobQueue_
						.poll();
				if (nextJob == null)
					break;
				saturationEngine.submit(nextJob);
			}
		}

		@Override
		public void finish() {
			saturationEngine.finish();
		}

	}

}
