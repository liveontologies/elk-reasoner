package org.semanticweb.elk.reasoner.saturation.rules.factories;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A queue to keep {@link ClassConclusion}s that should be processed in the
 * {@link Context} currently processed by the worker. It is not thread-safe, so
 * all methods should be accessed from the same thread.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface WorkerLocalTodo {

	/**
	 * @return the next {@link ClassConclusion} in the queue or {@link null} if there
	 *         is no such {@link ClassConclusion}
	 */
	ClassConclusion poll();

	/**
	 * Inserts the given {@link ClassConclusion} to be processed in the current
	 * {@link Context}
	 * 
	 * @param concusion
	 */
	void add(ClassConclusion concusion);

	/**
	 * @return {@code true} if this {@link WorkerLocalTodo} is assigned for
	 *         processing some {@link Context} or {@code false} otherwise. The
	 *         root of this {@link Context} can be obtained by
	 *         {@link #getActiveRoot()}
	 */
	boolean isActivated();

	/**
	 * @return the {@link IndexedContextRoot} of the currently assigned
	 *         {@link Context} or {@code null} if this {@link WorkerLocalTodo}
	 *         is not activated.
	 * @see #isActivated()
	 * @see Context#getRoot()
	 */
	IndexedContextRoot getActiveRoot();

	/**
	 * Set the new value of the root for the currently processed {@link Context}
	 * . This will activate this {@link WorkerLocalTodo} (
	 * {@link #getActiveRoot()} will return this value).
	 * 
	 * @param currentActiveRoot
	 *            the new value of the root for the currently processed
	 *            {@link Context}
	 */
	void setActiveRoot(IndexedContextRoot currentActiveRoot);

	/**
	 * Deactivates this {@link WorkerLocalTodo}. After that,
	 * {@link #isActivated()} returns {@code false}
	 * 
	 * @return {@code true} if this {@link WorkerLocalTodo} was activated and
	 *         {@code false} otherwise
	 */
	boolean deactivate();
}
