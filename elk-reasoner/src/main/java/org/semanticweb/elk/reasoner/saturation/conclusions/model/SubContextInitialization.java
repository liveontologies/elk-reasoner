package org.semanticweb.elk.reasoner.saturation.conclusions.model;
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;

/**
 * A special {@link ClassConclusion} that is used to initialize inferences for
 * {@link ClassConclusion}s associated with the concept expression represented
 * by {@link #getDestination()} and object property expression represented by
 * {@link #getSubDestination()}.<br>
 * 
 * Notation:
 * 
 * <pre>
 * ![C:R]
 * </pre>
 * 
 * The axiom has no logical meaning (equivalent to a tautology)<br>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getDestination()}<br>
 * R = {@link #getSubDestination()}<br>
 * 
 * @author "Yevgeny Kazakov"
 */
public interface SubContextInitialization
		extends
			SubClassConclusion,
			InitializationConclusion {

	public static final String NAME = "Sub-Context Initialization";

	public <O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		SubContextInitialization getSubContextInitialization(
				IndexedContextRoot root, IndexedObjectProperty subRoot);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		public O visit(SubContextInitialization conclusion);

	}

}
