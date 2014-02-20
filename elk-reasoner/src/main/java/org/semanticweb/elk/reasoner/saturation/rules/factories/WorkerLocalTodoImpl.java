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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
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

	private final Deque<Conclusion> localConclusions_;

	private IndexedClassExpression activeRoot_;

	public WorkerLocalTodoImpl() {
		this.localConclusions_ = new ArrayDeque<Conclusion>(1024);
	}

	@Override
	public Conclusion poll() {
		return localConclusions_.pollLast();
	}

	@Override
	public void add(Conclusion concusion) {
		LOGGER_.trace("{}: produced local conclusion", concusion);
		localConclusions_.add(concusion);
	}

	@Override
	public IndexedClassExpression getActiveRoot() {
		return activeRoot_;
	}

	@Override
	public void setActiveRoot(IndexedClassExpression currentActiveRoot) {
		LOGGER_.trace("{}: new active root", currentActiveRoot);
		this.activeRoot_ = currentActiveRoot;
	}

}