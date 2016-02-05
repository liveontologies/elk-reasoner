/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link SaturationInference} that produce {@link SubClassConclusion}s
 * 
 * @author Yevgeny Kazakov
 */
public interface SubClassInference extends ClassInference {

	/**
	 * @return the {@link IndexedObjectProperty} of the {@link Context} in which
	 *         the conclusion of this {@link SubClassInference} should
	 *         participate in inferences. It cannot be {@code null}.
	 */
	public IndexedObjectProperty getSubDestination();

	/**
	 * @return the {@link IndexedObjectProperty}, which is the same as
	 *         {@link SubClassConclusion#getTraceSuRoot()} for the conclusion of
	 *         this {@link SubClassInference} and for some of the premises of
	 *         this {@link SubClassInference}, if it has any (i.e., it is not an
	 *         {@link SubContextInitializationInference}). This value is used
	 *         for tracing of inferences. It cannot be {@code null}.
	 * 
	 * @see ClassConclusion#getTraceRoot()
	 */
	public IndexedObjectProperty getTraceSubRoot();

	public <O> O accept(Visitor<O> visitor);

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O>
			extends
				BackwardLinkInference.Visitor<O>,
				PropagationInference.Visitor<O>,
				SubContextInitializationInference.Visitor<O> {

		// combined interface

	}

}
