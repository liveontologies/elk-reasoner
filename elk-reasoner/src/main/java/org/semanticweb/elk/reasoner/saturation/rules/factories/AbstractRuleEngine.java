package org.semanticweb.elk.reasoner.saturation.rules.factories;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.Interrupter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An engine to concurrently processing pending {@link Conclusion}s within the
 * corresponding {@link Context}s using a supplied {@link ConclusionVisitor}.
 * The {@link Conclusion}s are retrieved by {@link Context#takeToDo()}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractRuleEngine implements
		InputProcessor<IndexedContextRoot> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractRuleEngine.class);

	/**
	 * Specifies what to do with {@link Conclusion}s within the processed
	 * {@link Context}
	 */
	private final ConclusionVisitor<? super Context, ?> conclusionProcessor_;

	/**
	 * Accumulates the produced {@link Conclusion}s that should be processed
	 * within the same {@link Context} in which they were produced; this should
	 * always be emptied before continuing to the next {@link Context}
	 */
	private final WorkerLocalTodo workerLocalTodo_;

	/**
	 * The {@link Interrupter} that can interrupt processing
	 */
	private final Interrupter interrupter_;

	public AbstractRuleEngine(
			ConclusionVisitor<? super Context, ?> conclusionProcessor,
			WorkerLocalTodo localizedProducer, Interrupter interrupter) {
		this.conclusionProcessor_ = conclusionProcessor;
		this.workerLocalTodo_ = localizedProducer;
		this.interrupter_ = interrupter;
	}

	@Override
	public void process() throws InterruptedException {
		try {
			for (;;) {
				if (interrupter_.isInterrupted())
					break;
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
	 * Process all pending {@link Conclusions} of the given {@link Context}
	 * 
	 * @param context
	 *            the active {@link Context} with unprocessed
	 *            {@link Conclusions}
	 */
	void process(Context context) {
		// at this point workerLocalTodo_ must be empty
		workerLocalTodo_.setActiveRoot(context.getRoot());
		for (;;) {
			Conclusion conclusion = workerLocalTodo_.poll();
			if (conclusion == null) {
				conclusion = context.takeToDo();
				if (conclusion == null)
					return;
			}
			LOGGER_.trace("{}: processing conclusion {}", context, conclusion);
			conclusion.accept(conclusionProcessor_, context);
		}
	}

	/**
	 * Removes and returns the next active {@link Context} that has unprocessed
	 * {@link Conclusion}s. The letter can be retrieved using
	 * {@link #getNextConclusion(Context)}
	 * 
	 * @return the next active {@link Context} to be processed by this
	 *         {@link BasicRuleEngine}
	 */
	abstract Context getNextActiveContext();

}
