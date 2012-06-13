/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.rulesystem;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.classes.ContextElClassSaturation;
import org.semanticweb.elk.reasoner.saturation.classes.InferenceSystemElClassSaturation;
import org.semanticweb.elk.reasoner.saturation.classes.InferenceSystemInvocationManagerSCE;
import org.semanticweb.elk.reasoner.saturation.classes.RuleStatistics;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationFactory.Engine;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * The engine for computing the saturation of class expressions. This is the
 * class that implements the application of inference rules.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * 
 */
public class RuleApplicationFactory implements
		InputProcessorFactory<IndexedClassExpression, Engine> {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(RuleApplicationFactory.class);

	protected InferenceSystem<? extends Context> inferenceSystem = new InferenceSystemElClassSaturation();

	protected InferenceSystemInvocationManager inferenceSystemInvocationManager;

	// TODO Try to get rid of the ontology index, if possible.
	/**
	 * The index used for executing the rules
	 */
	public final OntologyIndex ontologyIndex;

	/**
	 * Cached constants
	 */
	public final IndexedClassExpression owlThing, owlNothing;

	/**
	 * The queue containing all activated contexts. Every activated context
	 * occurs exactly once.
	 */
	protected final Queue<Context> activeContexts;

	/**
	 * The number of contexts ever created by this engine. This number is used
	 * not only for statistical purposes, but also by some callers to control
	 * the number of parallel workers (if the number of new contexts is too
	 * small, it does not make sense to run independent workers).
	 */
	protected final AtomicInteger contextNumber = new AtomicInteger(0);

	/**
	 * <tt>true</tt> if the {@link #activeContexts} queue is empty
	 */
	protected final AtomicBoolean activeContextsEmpty;
	/**
	 * The listener for rule application callbacks
	 */
	protected final RuleApplicationListener listener;

	/**
	 * The aggregated statistics of all workers
	 */
	protected final RuleStatistics sharedStatistics;

	public RuleApplicationFactory(OntologyIndex ontologyIndex,
			RuleApplicationListener listener) {
		this.ontologyIndex = ontologyIndex;
		this.listener = listener;
		this.activeContexts = new ConcurrentLinkedQueue<Context>();
		this.activeContextsEmpty = new AtomicBoolean(true);
		this.sharedStatistics = new RuleStatistics();

		owlThing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_THING);
		owlNothing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_NOTHING);

		// TODO: provide an option for specifying the invocation manager
		inferenceSystemInvocationManager = new InferenceSystemInvocationManagerSCE<ContextElClassSaturation>();
		try {
			inferenceSystemInvocationManager
					.addInferenceSystem(inferenceSystem);
		} catch (IllegalInferenceMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns the approximate number of contexts created. The real number of
	 * contexts is larger, but to ensure good concurrency performance it is not
	 * updated often. It is exact when all engines finish.
	 * 
	 * @return the approximate number of created contexts
	 */
	public int getApproximateContextNumber() {
		return contextNumber.get();
	}

	/**
	 * Prints statistic of rule applications
	 */
	public void printStatistics() {
		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Contexts created:" + contextNumber);
			LOGGER_.debug("Derived Produced/Unique:"
					+ sharedStatistics.getSuperClassExpressionInfNo() + "/"
					+ sharedStatistics.getSuperClassExpressionNo());
			LOGGER_.debug("Backward Links Produced/Unique:"
					+ sharedStatistics.getBackLinkInfNo() + "/"
					+ sharedStatistics.getBackLinkNo());
			// LOGGER_.debug("Forward Links Produced/Unique:" +
			// QueueableStore.forwLinkInfNo + "/" +
			// QueueableStore.forwLinkNo.get());
			// LOGGER_.debug("Processed queueables:" +
			// InferenceRuleManager.debugProcessedQueueables);
			// LOGGER_.debug("Rule applications:" +
			// InferenceRuleManager.debugRuleApplications);
		}
	}

	public class Engine implements InputProcessor<IndexedClassExpression> {

		// non-thread safe objects created for every worker
		private final RuleStatistics statistics = new RuleStatistics();
		private int localContextNumber = 0;

		@Override
		public void submit(IndexedClassExpression job) {
			getCreateContext(job);
		}

		@Override
		public void process() {
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					break;
				Context nextContext = activeContexts.poll();
				if (nextContext == null) {
					if (!activeContextsEmpty.compareAndSet(false, true))
						break;
					nextContext = activeContexts.poll();
					if (nextContext == null)
						break;
					tryNotifyCanProcess();
				}
				process(nextContext);
			}
		}

		@Override
		public boolean canProcess() {
			return !activeContextsEmpty.get();
		}

		@Override
		public void finish() {
			contextNumber.addAndGet(localContextNumber);
			localContextNumber = 0;
			sharedStatistics.merge(statistics);
		}

		public IndexedClassExpression getOwlNothing() {
			return owlNothing;
		}

		public IndexedClassExpression getOwlThing() {
			return owlThing;
		}

		public Iterable<IndexedObjectProperty> getReflexiveObjectProperties() {
			return ontologyIndex.getReflexiveObjectProperties();
		}

		public RuleStatistics getStatistics() {
			return this.statistics;
		}

		/**
		 * Return the context which has the input indexed class expression as a
		 * root. In case no such context exists, a new one is created with the
		 * given root and is returned. It is ensured that no two different
		 * contexts are created with the same root. In case a new context is
		 * created, it is scheduled to be processed.
		 * 
		 * @param root
		 *            the input indexed class expression for which to return the
		 *            context having it as a root
		 * @return context which root is the input indexed class expression.
		 * 
		 */
		public Context getCreateContext(IndexedClassExpression root) {
			if (root.getContext() == null) {
				Context context = inferenceSystem.createContext(root);
				if (root.setContext(context)) {
					if (++localContextNumber == 16) {
						contextNumber.addAndGet(localContextNumber);
						localContextNumber = 0;
					}
					if (LOGGER_.isTraceEnabled()) {
						LOGGER_.trace(root + ": context created");
					}
					inferenceSystemInvocationManager.initContext(context, this);
				}
			}
			return root.getContext();
		}

		public void enqueue(Context context, Queueable<?> item) {
			context.getQueue().add(item);
			activateContext(context);
		}

		protected void process(Context context) {
			for (;;) {
				Queueable<?> item = context.getQueue().poll();
				if (item == null)
					break;

				inferenceSystemInvocationManager.processItemInContext(item,
						context, this);
			}

			deactivateContext(context);
		}

		protected void tryNotifyCanProcess() {
			if (activeContextsEmpty.compareAndSet(true, false))
				listener.notifyCanProcess();
		}

		protected void activateContext(Context context) {
			if (context.tryActivate()) {
				activeContexts.add(context);
				tryNotifyCanProcess();
			}
		}

		protected void deactivateContext(Context context) {
			if (context.tryDeactivate())
				if (!context.getQueue().isEmpty())
					activateContext(context);
		}

	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}
}
