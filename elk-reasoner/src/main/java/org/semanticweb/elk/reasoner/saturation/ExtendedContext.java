package org.semanticweb.elk.reasoner.saturation;
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;

/**
 * A {@link Context} with additional methods for managing {@link SubContext}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ExtendedContext extends Context {

	/**
	 * Mark the given sub-root {@link IndexedPropertyChain} as initialized. This
	 * does not automatically create the {@link SubContext} for this sub-root.
	 * 
	 * @param subRoot
	 *            the {@link IndexedPropertyChain} which should be marked as
	 *            initialized
	 * @return {@code true} if this {@link ExtendedContext} was changed as a
	 *         result of this operation, i.e., the given
	 *         {@link IndexedPropertyChain} was not marked as initialized.
	 */
	public boolean setInitSubRoot(IndexedPropertyChain subRoot);

}
