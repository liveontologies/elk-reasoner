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
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationEngine;
import org.semanticweb.elk.reasoner.saturation.elkrulesystem.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.elkrulesystem.InferenceSystemElClassSaturation;
import org.semanticweb.elk.reasoner.saturation.elkrulesystem.SuperClassExpression;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine for computing the saturation of class expressions. This is the
 * class that implements the application of inference rules.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * 
 */
public class RuleApplicationEngine implements InputProcessor<IndexedClassExpression> {

	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturationEngine.class);

	protected InferenceSystem<? extends Context> inferenceSystem = new InferenceSystemElClassSaturation();

	protected final InferenceSystemInvocationManager inferenceSystemInvocationManager;

	// TODO Try to get rid of the ontology index, if possible.
	/**
	 * The index used for executing the rules
	 */
	protected final OntologyIndex ontologyIndex;

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

	public RuleApplicationEngine(OntologyIndex ontologyIndex,
			RuleApplicationListener listener) {
		this.ontologyIndex = ontologyIndex;
		this.listener = listener;
		this.activeContexts = new ConcurrentLinkedQueue<Context>();
		this.activeContextsEmpty = new AtomicBoolean(true);

		// reset saturation in case of re-saturation after changes
		// TODO: introduce a separate method for this
		for (IndexedClassExpression ice : ontologyIndex
				.getIndexedClassExpressions())
			ice.resetContext();

		owlThing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_THING);
		owlNothing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_NOTHING);

		inferenceSystemInvocationManager = new InferenceSystemInvocationManager(
				this);
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

	public void submit(IndexedClassExpression job) {
		getCreateContext(job);
	}

	public void process() throws InterruptedException {
		processActiveContexts();
	}

	public boolean canProcess() {
		return !activeContextsEmpty.get();
	}

	/**
	 * Returns the total number of contexts created
	 * 
	 * @return number of created contexts
	 */
	public int getContextNumber() {
		return contextNumber.get();
	}

	/**
	 * Prints statistic of rule applications
	 */
	public void printStatistics() {
		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Contexts created:" + contextNumber);
			LOGGER_.debug("Derived Produced/Unique:"
					+ SuperClassExpression.superClassExpressionInfNo + "/"
					+ SuperClassExpression.superClassExpressionNo);
			LOGGER_.debug("Backward Links Produced/Unique:"
					+ BackwardLink.backLinkInfNo + "/"
					+ BackwardLink.backLinkNo);
			// LOGGER_.debug("Forward Links Produced/Unique:" +
			// QueueableStore.forwLinkInfNo + "/" +
			// QueueableStore.forwLinkNo.get());
			// LOGGER_.debug("Processed queueables:" +
			// InferenceRuleManager.debugProcessedQueueables);
			// LOGGER_.debug("Rule applications:" +
			// InferenceRuleManager.debugRuleApplications);
		}
	}

	protected void tryNotifyCanProcess() {
		if (activeContextsEmpty.compareAndSet(true, false))
			listener.notifyCanProcess();
	}

	/**
	 * Return the context which has the input indexed class expression as a
	 * root. In case no such context exists, a new one is created with the given
	 * root and is returned. It is ensured that no two different contexts are
	 * created with the same root. In case a new context is created, it is
	 * scheduled to be processed.
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
				contextNumber.incrementAndGet();
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root + ": context created");
				}
				inferenceSystemInvocationManager.initContext(context);
			}
		}
		return root.getContext();
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

	public void enqueue(Context context, Queueable<?> item) {
		context.getQueue().add(item);
		activateContext(context);
	}

	protected void processActiveContexts() throws InterruptedException {
		for (;;) {
			Context nextContext = activeContexts.poll();
			if (nextContext == null) {
				if (!activeContextsEmpty.compareAndSet(false, true))
					return;
				nextContext = activeContexts.poll();
				if (nextContext == null)
					return;
				tryNotifyCanProcess();
			}
			process(nextContext);
		}
	}

	protected void process(Context context) {
		for (;;) {
			Queueable<?> item = context.getQueue().poll();
			if (item == null)
				break;

			inferenceSystemInvocationManager
					.processItemInContext(item, context);
		}

		deactivateContext(context);
	}

}
