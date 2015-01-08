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

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
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
	private final Set<ElkClass> defaultTopOutput_;

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
	public TransitiveReductionFactory(SaturationState<?> saturationState,
			int maxWorkers, TransitiveReductionListener<J> listener) {
		this.listener_ = listener;
		this.auxJobQueue_ = new ConcurrentLinkedQueue<SaturationJobSuperClass<R, J>>();
		this.jobsWithSaturatedRoot_ = new ConcurrentLinkedQueue<J>();
		this.saturationState_ = saturationState;
		this.saturationFactory_ = new ClassExpressionSaturationFactory<SaturationJobForTransitiveReduction<R, ?, J>>(
				new RuleApplicationAdditionFactory(saturationState),
				maxWorkers, new ThisClassExpressionSaturationListener());
		this.owlThing_ = saturationState.getOntologyIndex()
				.getIndexedOwlThing();
		this.defaultTopOutput_ = new ArrayHashSet<ElkClass>(1);
		defaultTopOutput_.add(PredefinedElkClass.OWL_THING);
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		saturationFactory_.finish();
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
	private class SaturationOutputProcessor implements
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
			updateTransitiveReductionOutput(state.output, candidate);
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
			if (saturation.containsConclusion(ContradictionImpl.getInstance())) {
				LOGGER_.trace(
						"{}: transitive reduction finished: inconsistent", root);

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
				 * Otherwise update the output of the transitive reduction using
				 * the saturation
				 */
				updateTransitiveReductionOutput(state.output, candidate);
			}

			/* When all candidates are processed, the output is computed */
			TransitiveReductionOutputEquivalentDirect<R> output = state.output;
			/*
			 * if there are no direct subsumers found, then use the default
			 * direct subsumer for owl:Thing unless it is the output for
			 * owl:Thing itself
			 */
			if (output.directSubsumers.isEmpty()
					&& !output.getEquivalent().contains(
							PredefinedElkClass.OWL_THING)) {
				output.directSubsumers.put(owlThing_, defaultTopOutput_);
			}
			// if (output.equivalent.isEmpty()) {
			// LOGGER_.error("{}: empty equivalent class!", output.getRoot());
			// }

			state.initiatorJob.setOutput(output);
			listener_.notifyFinished(state.initiatorJob);

			if (LOGGER_.isTraceEnabled()) {
				R root = output.getRoot();
				LOGGER_.trace(root + ": transitive reduction finished");
				for (ElkClass equivalent : output.equivalent) {
					LOGGER_.trace(root + ": equivalent " + equivalent.getIri());
				}
				for (IndexedClass direct : output.directSubsumers.keySet()) {
					String message = root + ": direct super class [" + direct;
					for (ElkClass equivalent : output.directSubsumers
							.get(direct))
						message = message + ", " + equivalent.getIri();
					message = message + "]";
					LOGGER_.trace(message);
				}
			}
		}

		/**
		 * Updates the output of the transitive reduction using a new candidate
		 * indexed super class and its saturation.
		 * 
		 * @param output
		 *            the partially computed transitive reduction output
		 * @param candidate
		 *            the super class of the root
		 */
		private void updateTransitiveReductionOutput(
				TransitiveReductionOutputEquivalentDirect<R> output,
				IndexedClass candidate) {

			Set<IndexedClassExpression> candidateSubsumers = saturationState_
					.getContext(candidate).getSubsumers();

			/*
			 * Check if subsumer is equivalent to the root, and if so, add it to
			 * the equivalent classes of the output
			 */
			Set<IndexedClassExpression> rootSubsumers = saturationState_
					.getContext(output.getRoot()).getSubsumers();
			int candidateSubsumersSize = candidateSubsumers.size();

			if (candidateSubsumersSize == rootSubsumers.size()) {
				output.equivalent.add(candidate.getElkClass());
				return;
			}

			if (candidate.getElkClass().getIri() == PredefinedElkIri.OWL_THING
					.get() && candidateSubsumersSize == 1) {
				/*
				 * if subsumer is top and has no other subsumers (which means,
				 * in particular, it does not occur negatively), then it can be
				 * safely ignored; top will be automatically introduced if no
				 * direct subsumers are found
				 */
				return;
			}

			/*
			 * Update the list of current direct subsumers using the new
			 * candidate
			 */
			Iterator<IndexedClass> iteratorDirectSubsumers = output.directSubsumers
					.keySet().iterator();

			while (iteratorDirectSubsumers.hasNext()) {
				IndexedClass directSubsumer = iteratorDirectSubsumers.next();

				Set<IndexedClassExpression> directSubsumerSubsumers = saturationState_
						.getContext(directSubsumer).getSubsumers();

				if (candidateSubsumersSize > directSubsumerSubsumers.size()) {
					if (candidateSubsumers.contains(directSubsumer)) {
						/*
						 * directSubsumer strictly subsumes candidate, so it is
						 * not a direct subsumer anymore
						 */
						iteratorDirectSubsumers.remove();
					}
				} else if (directSubsumerSubsumers.contains(candidate)) {
					/* candidate is subsumed by the direct subsumer */
					if (candidateSubsumersSize == directSubsumerSubsumers
							.size())
						/* candidate is equivalent to the directSubsumer */
						output.directSubsumers.get(directSubsumer).add(
								candidate.getElkClass());
					return;
				}
			}
			/*
			 * if the candidate has survived all the tests, then it is a direct
			 * subsumer
			 */
			Set<ElkClass> candidateEquivalent = new ArrayHashSet<ElkClass>(1);
			candidateEquivalent.add(candidate.getElkClass());
			output.directSubsumers.put(candidate, candidateEquivalent);
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
				if (Thread.currentThread().isInterrupted())
					return;
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
