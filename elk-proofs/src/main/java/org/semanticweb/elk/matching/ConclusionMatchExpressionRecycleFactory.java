package org.semanticweb.elk.matching;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.matching.conclusions.ConclusionMatch;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionDelegatingFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConclusionMatchExpressionRecycleFactory
		extends ConclusionMatchExpressionDelegatingFactory {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ConclusionMatchExpressionRecycleFactory.class);

	private final Map<Object, Object> cache_ = new HashMap<Object, Object>();

	private final ConclusionMatch.Visitor<?> newMatchVisitor_;

	private final Collection<ConclusionMatch> newConclusions_;

	ConclusionMatchExpressionRecycleFactory(ElkObjectFactory elkObjectFactory,
			Collection<ConclusionMatch> newMatches,
			ConclusionMatch.Visitor<?> newMatchVisitor) {
		super(elkObjectFactory);
		this.newMatchVisitor_ = newMatchVisitor;
		this.newConclusions_ = newMatches;
	}

	@Override
	protected <C extends ConclusionMatch> C filter(C candidate) {
		@SuppressWarnings("unchecked")
		C previous = (C) cache_.get(candidate);
		if (previous != null) {
			return previous;
		}
		// else
		cache_.put(candidate, candidate);
		newConclusions_.add(candidate);
		candidate.accept(newMatchVisitor_);
		return candidate;
	}

}
