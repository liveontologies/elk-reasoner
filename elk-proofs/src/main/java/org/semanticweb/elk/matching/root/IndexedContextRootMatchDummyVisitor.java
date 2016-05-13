package org.semanticweb.elk.matching.root;

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

/**
 * A {@link IndexedContextRootMatch.Visitor} that always returns {@code null}.
 * Can be used to prototype other visitors by overriding the defaultVisit
 * method.
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 *            the type of the output
 */
public class IndexedContextRootMatchDummyVisitor<O>
		implements IndexedContextRootMatch.Visitor<O> {

	protected O defaultVisit(IndexedContextRootMatch match) {
		return null;
	}

	protected O defaultVisit(IndexedContextRootRangeMatch match) {
		return defaultVisit((IndexedContextRootMatch) match);
	}

	@Override
	public O visit(IndexedContextRootClassExpressionMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedContextRootIndividualMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedContextRootRangeHasValueMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedContextRootRangeSomeValuesFromMatch match) {
		return defaultVisit(match);
	}

}
