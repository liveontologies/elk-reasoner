package org.semanticweb.elk.matching.conclusions;

import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch6;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectHasSelfMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectSomeValuesFromMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedExpandedMatch3;
import org.semanticweb.elk.matching.inferences.InferenceMatch;

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

public interface BackwardLinkMatch2Watch extends InferenceMatch {

	<O> O accept(Visitor<O> visitor);

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> extends BackwardLinkCompositionMatch6.Visitor<O>,
			BackwardLinkOfObjectHasSelfMatch2.Visitor<O>,
			BackwardLinkOfObjectSomeValuesFromMatch2.Visitor<O>,
			BackwardLinkReversedExpandedMatch3.Visitor<O> {

		// combined interface

	}

}
