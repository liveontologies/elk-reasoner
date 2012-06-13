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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.classes.RuleStatistics;
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
public class RuleApplicationEngine implements
		InputProcessor<IndexedClassExpression> {

	private final RuleApplicationShared shared;
	private final RuleStatistics statistics;

	public RuleApplicationEngine(RuleApplicationShared shared,
			RuleStatistics statistics) {
		this.shared = shared;
		this.statistics = statistics;
	}

	@Override
	public void submit(IndexedClassExpression job) {
		shared.getCreateContext(job);
	}

	@Override
	public void process() {
		for (;;) {
			if (Thread.currentThread().isInterrupted())
				break;
			Context nextContext = shared.activeContexts.poll();
			if (nextContext == null) {
				if (!shared.activeContextsEmpty.compareAndSet(false, true))
					break;
				nextContext = shared.activeContexts.poll();
				if (nextContext == null)
					break;
				shared.tryNotifyCanProcess();
			}
			shared.process(nextContext, statistics);
		}
	}

	@Override
	public boolean canProcess() {
		return !shared.activeContextsEmpty.get();
	}

	@Override
	public void finish() {
		shared.sharedStatistics.merge(statistics);
	}
}
