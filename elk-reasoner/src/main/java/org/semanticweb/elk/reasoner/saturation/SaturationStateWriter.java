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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;

/**
 * An object that can modify the respective {@link SaturationState}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public interface SaturationStateWriter<C extends Context> extends
		ClassConclusionProducer {

	/**
	 * @return the {@link SaturationState} modified by this
	 *         {@link SaturationStateWriter}
	 */
	public SaturationState<? extends C> getSaturationState();

	/**
	 * Removes and returns the next active {@link Context} of the
	 * {@link SaturationState}, i.e., the one which has at least one unprocessed
	 * {@link ClassConclusion}
	 * 
	 * @return the next {@link Context} of the {@link SaturationState} with
	 *         unprocessed {@link ClassConclusion} if there exists one, or
	 *         {@code null} if not
	 * 
	 * @see Context#takeToDo()
	 */
	public Context pollForActiveContext();

	/**
	 * Marks the {@link Context} with the given {@link IndexedContextRoot} as
	 * not saturated. That is, after calling of this method,
	 * {@code Context#isSaturated()} returns {@code true} for the
	 * {@link Context} returned by
	 * {@code SaturationState#getContext(IndexedContextRoot)} for the given
	 * root.
	 * 
	 * @param root
	 * @return {@code true} if the {@link Context} was marked as saturated or
	 *         {@code false} if the {@link Context} for the given
	 *         {@link IndexedContextRoot} does not exist or already marked as
	 *         saturated.
	 */
	public boolean markAsNotSaturated(IndexedContextRoot root);

	/**
	 * Removes all assignments of {@link Context}s to {@link IndexedContextRoot}
	 * s of this {@link SaturationState}. After that,
	 * {@link SaturationState#getContexts()} should be empty.
	 */
	public void resetContexts();

}
