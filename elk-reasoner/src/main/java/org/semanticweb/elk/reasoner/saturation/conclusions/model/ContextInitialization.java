package org.semanticweb.elk.reasoner.saturation.conclusions.model;

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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;

/**
 * A special {@link ClassConclusion} that is used to initialize inferences for
 * {@link ClassConclusion}s associated with the concept represented by
 * {@link #getDestination()}.<br>
 * 
 * Notation:
 * 
 * <pre>
 * ![C]
 * </pre>
 * 
 * The axiom has no logical meaning (equivalent to a tautology)<br>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getDestination()}<br>
 * 
 * @author "Yevgeny Kazakov"
 */
public interface ContextInitialization
		extends
			ClassConclusion,
			InitializationConclusion {

	public static final String NAME = "Context Initialization";

	public <O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		ContextInitialization getContextInitialization(IndexedContextRoot root);

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

		public O visit(ContextInitialization conclusion);

	}

}
