package org.semanticweb.elk.matching.inferences;

import org.semanticweb.elk.exceptions.ElkRuntimeException;

/*
 * #%L
 * ELK Proofs Package
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

import org.semanticweb.elk.matching.AbstractMatch;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionDelegatingFactory;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;

public abstract class AbstractInferenceMatch<P> extends AbstractMatch<P>
		implements InferenceMatch {

	private static final boolean DEBUG_ = true;

	static final ConclusionMatchExpressionFactory DEBUG_FACTORY = new ConclusionMatchExpressionDelegatingFactory();

	void checkEquals(Object first, Object second) {
		if (DEBUG_ && !first.equals(second)) {
			throw new ElkRuntimeException(
					"Equality assertion failure: " + first + " <> " + second);
		}
	}

	AbstractInferenceMatch(P parent) {
		super(parent);
	}

	@Override
	public String toString() {
		return InferenceMatchPrinter.toString(this);
	}

}
