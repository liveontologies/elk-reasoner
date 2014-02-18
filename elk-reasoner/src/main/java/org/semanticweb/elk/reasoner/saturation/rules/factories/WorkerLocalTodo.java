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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A queue to keep {@link Conclusion}s that should be processed in the
 * {@link Context} currently processed by the worker.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
interface WorkerLocalTodo {

	/**
	 * @return the next {@link Conclusion} in the queue or {@link null} if there
	 *         is no such {@link Conclusion}
	 */
	Conclusion poll();

	/**
	 * Inserts the given {@link Conclusion} to be processed in the current
	 * {@link Context}
	 * 
	 * @param concusion
	 */
	void add(Conclusion concusion);

	/**
	 * @return the root {@link IndexedClassExpression} of the {@link Context}
	 *         that is currently processed
	 * @see Context#getRoot()
	 */
	IndexedClassExpression getActiveRoot();

	/**
	 * set the new value of the root for the currently processed {@link Context}
	 * 
	 * @param currentActiveRoot
	 *            the new value of the root for the currently processed
	 *            {@link Context}
	 */
	void setActiveRoot(IndexedClassExpression currentActiveRoot);
}
