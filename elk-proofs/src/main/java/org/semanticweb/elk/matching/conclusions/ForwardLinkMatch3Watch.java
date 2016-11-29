package org.semanticweb.elk.matching.conclusions;

import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch7;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedExpandedMatch4;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch6;
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

public interface ForwardLinkMatch3Watch extends InferenceMatch {

	<O> O accept(Visitor<O> visitor);

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> extends BackwardLinkCompositionMatch7.Visitor<O>,
			BackwardLinkReversedExpandedMatch4.Visitor<O>,
			ForwardLinkCompositionMatch6.Visitor<O> {

		// combined interface

	}

}
