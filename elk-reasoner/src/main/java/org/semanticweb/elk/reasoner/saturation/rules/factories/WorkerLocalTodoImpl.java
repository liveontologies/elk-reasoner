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

import java.util.ArrayDeque;
import java.util.Deque;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of a {@link WorkerLocalTodo} backed by an
 * {@link ArrayDeque}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class WorkerLocalTodoImpl implements WorkerLocalTodo {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(WorkerLocalTodoImpl.class);

	/**
	 * The thread in which this {@link WorkerLocalTodo} should be used.
	 */
	private final Thread thisThread_;

	private final Deque<Conclusion> localConclusions_;

	/**
	 * {@code true} if this {@link WorkerLocalTodo} is assigned to some
	 * {@link Context}
	 */
	private boolean isActivated_ = false;

	/**
	 * the root of the {@link Context} to which this {@link WorkerLocalTodo} is
	 * assigned or {@link null} if it is not activated.
	 */
	private IndexedContextRoot activeRoot_ = null;

	public WorkerLocalTodoImpl() {
		this.thisThread_ = Thread.currentThread();
		this.localConclusions_ = new ArrayDeque<Conclusion>(1024);
	}

	@Override
	public Conclusion poll() {
		checkThread();
		return localConclusions_.pollLast();
	}

	@Override
	public void add(Conclusion concusion) {
		checkThread();
		LOGGER_.trace("{}: produced local conclusion", concusion);
		localConclusions_.add(concusion);
	}

	@Override
	public boolean isActivated() {
		checkThread();
		return isActivated_;
	}

	@Override
	public IndexedContextRoot getActiveRoot() {
		checkThread();
		if (isActivated_)
			return activeRoot_;
		// else
		return null;
	}

	@Override
	public void setActiveRoot(IndexedContextRoot currentActiveRoot) {
		checkThread();
		LOGGER_.trace("{}: new active root", currentActiveRoot);
		this.activeRoot_ = currentActiveRoot;
	}

	@Override
	public boolean deactivate() {
		checkThread();
		if (!isActivated_)
			return false;
		// else
		LOGGER_.trace("local todo deactivated");
		return true;
	}

	void checkThread() {
		if (Thread.currentThread() != thisThread_)
			LOGGER_.error("Worker Local Todo used from a different thread than created!");
	}
}
