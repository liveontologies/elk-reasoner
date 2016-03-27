package org.semanticweb.elk.matching.conclusions;

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

public interface ClassConclusionMatch extends ConclusionMatch {

	<O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory extends BackwardLinkMatch1.Factory,
			BackwardLinkMatch2.Factory, SubClassInclusionComposedMatch1.Factory,
			SubClassInclusionDecomposedMatch1.Factory,
			SubClassInclusionDecomposedMatch2.Factory,
			ForwardLinkMatch1.Factory, ForwardLinkMatch2.Factory,
			PropagationMatch1.Factory, PropagationMatch2.Factory,
			PropagationMatch3.Factory {

		// combined interface

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> extends BackwardLinkMatch1.Visitor<O>,
			BackwardLinkMatch2.Visitor<O>,
			SubClassInclusionComposedMatch1.Visitor<O>,
			SubClassInclusionDecomposedMatch1.Visitor<O>,
			SubClassInclusionDecomposedMatch2.Visitor<O>,
			ForwardLinkMatch1.Visitor<O>, ForwardLinkMatch2.Visitor<O>,
			PropagationMatch1.Visitor<O>, PropagationMatch2.Visitor<O>,
			PropagationMatch3.Visitor<O> {

		// combined interface

	}

}
