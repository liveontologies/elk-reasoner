package org.semanticweb.elk.reasoner;

import org.semanticweb.elk.testing.DiffableOutput;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2018 Department of Computer Science, University of Oxford
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

/**
 * A reasoning output with the information about completeness of the result
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 *            the type of the entailments of the reasoning result
 * @param <R>
 *            the type of the reasoning result
 */
public class ReasoningTestOutput<O, R extends DiffableOutput<O, R>> {

	private final R reasoningResult_;

	public ReasoningTestOutput(R reasoningResult) {
		this.reasoningResult_ = reasoningResult;
	}

	/**
	 * @return the reasoning result represented by this output
	 */
	public R getReasoningResult() {
		return reasoningResult_;
	}

}
