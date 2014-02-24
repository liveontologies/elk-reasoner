/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Represents the state of saturation containing information about
 * {@link Context}s and their assignment to roots {@link IndexedClassExpression}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public interface SaturationState {

	/**
	 * @return the unmodifiable {@link Collection} of {@link Context} stored in
	 *         this {@link SaturationState}
	 */
	public Collection<? extends Context> getContexts();

	/**
	 * @param ice
	 *            the root {@link IndexedClassExpression} for which to find the
	 *            {@link Context}
	 * @return the {@link Context} in this {@link SaturationState} with the
	 *         given root {@link IndexedClassExpression}, or {@code null} if
	 *         there exists no such {@link Context}. There can be at most one
	 *         {@link Context} for any given root.
	 * @see Context#getRoot()
	 */
	public Context getContext(IndexedClassExpression ice);

	/**
	 * @return the {@link OntologyIndex} associated with this
	 *         {@link SaturationState}
	 */
	public OntologyIndex getOntologyIndex();

	/**
	 * @return the unmodifiable {@link Collection} of {@link Context}s in this
	 *         {@link SaturationState} that are not saturated, i.e., for which
	 *         {@link Context#isSaturated()} returns {@code false}
	 */
	public Collection<? extends Context> getNotSaturatedContexts();

	/**
	 * @return the total number of times a {@link Context} was marked as
	 *         non-saturated using this {@link SaturationState}, i.e., the
	 *         number of times a method
	 *         {@link SaturationStateWriter#markAsNotSaturated(IndexedClassExpression)}
	 *         returned {@code true}. It can only increase over time.
	 */
	int getContextMarkNonSaturatedCount();

	/**
	 * @return the total number of times a {@link Context} was set as saturated
	 *         using this {@link SaturationState}, i.e., the number of times a
	 *         method {@link setNextContextSaturated} returned not {@link null}.
	 *         It can only increase over time. This should always be smaller
	 *         than the value of #getContextMarkNonSaturatedCount().
	 */
	int getContextSetSaturatedCount();

	/**
	 * Sets one of the {@link Context}s as saturated and returns it. The
	 * {@link Context}s are set in the order in which they become non-saturated
	 * or created.
	 * 
	 * @return the {@link Context} that was set as saturated.
	 * 
	 * @see Context#isSaturated()
	 */
	public Context setNextContextSaturated();

	/**
	 * @param contextModificationListener
	 * @return a new {@link SaturationStateWriter} that can only modify
	 *         {@link Context}s in this {@link SaturationState}, but cannot
	 *         create new ones. When a {@link Context} is modified by the
	 *         {@link SaturationStateWriter}, it becomes not saturated according
	 *         to {@link Context#isSaturated()} if it was not already so.
	 *         Whenever a {@link Context} becomes not saturated using this
	 *         {@link SaturationStateWriter}, the provided
	 *         {@link ContextModificationListener} is called. The returned
	 *         {@link SaturationStateWriter} is not thread safe and should not
	 *         be used from more than one thread.
	 * 
	 * @see #getContextCreatingWriter(ContextCreationListener,
	 *      ContextModificationListener)
	 * @see Context#isSaturated()
	 */
	public SaturationStateWriter getContextModifyingWriter(
			ContextModificationListener contextModificationListener);

	/**
	 * @param contextCreationListener
	 * @param contextModificationListener
	 * @return a new {@link SaturationStateWriter} that can modify as well as
	 *         create new {@link Context}s in this {@link SaturationState}. When
	 *         a {@link Context} is modified by the
	 *         {@link SaturationStateWriter}, it becomes not saturated according
	 *         to {@link Context#isSaturated()} if it was not already so.
	 *         Whenever a {@link Context} is created using this
	 *         {@link SaturationStateWriter} the provided
	 *         {@link ContextCreationListener} is called. Whenever a
	 *         {@link Context} becomes not saturated using this
	 *         {@link SaturationStateWriter}, the provided
	 *         {@link ContextModificationListener} is called. The returned
	 *         {@link SaturationStateWriter} is not thread safe and should not
	 *         be used from more than one thread.
	 * 
	 * @see #getContextModifyingWriter(ContextModificationListener)
	 */
	public SaturationStateWriter getContextCreatingWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener);

}
