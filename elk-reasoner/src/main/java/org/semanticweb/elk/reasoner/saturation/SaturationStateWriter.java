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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * An object that can modify a {@link SaturationState}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public interface SaturationStateWriter extends ConclusionProducer {

	/**
	 * @return the {@link SaturationState} modified by this
	 *         {@link SaturationStateWriter}
	 */
	public SaturationState getSaturationState();

	public Context pollForActiveContext();

	/**
	 * Marks the {@link Context} with the given root
	 * {@link IndexedClassExpression} as not saturated. That is, after calling
	 * of this method, {@code Context#isSaturated()} returns {@code true} for
	 * the {@link Context} returned by
	 * {@code SaturationState#getContext(IndexedClassExpression)} for the given
	 * root.
	 * 
	 * @param root
	 * @return {@code true} if the {@link Context} was marked as saturated or
	 *         {@code false} if the {@link Context} for the given
	 *         {@link IndexedClassExpression} does not exist or already marked
	 *         as saturated.
	 */
	public boolean markAsNotSaturated(IndexedClassExpression root);

	public void resetContexts();

	public void dispose();
}
