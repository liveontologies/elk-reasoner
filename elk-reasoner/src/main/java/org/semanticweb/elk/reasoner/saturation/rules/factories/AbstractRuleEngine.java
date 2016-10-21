/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.rules.factories;

import org.semanticweb.elk.ModifiableReference;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.util.concurrent.computation.DelegateInterruptMonitor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An engine to concurrently processing pending {@link ClassInference}s within
 * the corresponding {@link Context}s using a supplied
 * {@link ClassInference.Visitor}. The {@link ClassInference}s are retrieved
 * by {@link Context#takeToDo()}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractRuleEngine<I extends RuleApplicationInput>
		extends DelegateInterruptMonitor
		implements
			InputProcessor<I> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractRuleEngine.class);

	/**
	 * A reference to the current {@link Context} processed by the worker
	 */
	private final ModifiableReference<Context> activeContext_;

	/**
	 * Specifies how {@link ClassInference}s in the {@link Context} should be
	 * processed
	 */
	private final ClassInference.Visitor<?> inferenceProcessor_;

	/**
	 * Accumulates the produced {@link ClassInference}s that should be
	 * processed within the same {@link Context} in which they were produced;
	 * this should always be emptied before continuing to the next
	 * {@link Context}
	 */
	private final WorkerLocalTodo workerLocalTodo_;

	public AbstractRuleEngine(
			ModifiableReference<Context> activeContext,
			ClassInference.Visitor<?> inferenceProcessor,
			WorkerLocalTodo localizedProducer, InterruptMonitor interrupter) {
		super(interrupter);
		this.activeContext_ = activeContext;
		this.inferenceProcessor_ = inferenceProcessor;
		this.workerLocalTodo_ = localizedProducer;
	}

	@Override
	public void process() throws InterruptedException {
		try {
			for (;;) {
				if (isInterrupted()) {
					LOGGER_.trace("Rule application interrupted");

					break;
				}
				Context nextContext = getNextActiveContext();
				if (nextContext == null) {
					break;
				}
				process(nextContext);
			}
		} finally {
			workerLocalTodo_.deactivate();
		}
	}

	/**
	 * Process all pending {@link ClassInference}s of the given {@link Context}
	 * 
	 * @param context
	 *            the active {@link Context} with unprocessed
	 *            {@link ClassInference}s
	 */
	void process(Context context) {
		activeContext_.set(context);
		// at this point workerLocalTodo_ must be empty
		workerLocalTodo_.setActiveRoot(context.getRoot());
		for (;;) {
			ClassInference inference = workerLocalTodo_.poll();
			if (inference == null) {
				inference = context.takeToDo();
				if (inference == null)
					return;
			}
			LOGGER_.trace("{}: processing inference {}", context, inference);
			inference.accept(inferenceProcessor_);
		}
	}

	/**
	 * Removes and returns the next active {@link Context} that has unprocessed
	 * {@link ClassInference}s.
	 * 
	 * @return the next active {@link Context} to be processed by this
	 *         {@link BasicRuleEngine}
	 */
	abstract protected Context getNextActiveContext();

}
