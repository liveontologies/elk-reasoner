/*-
 * #%L
 * ELK Reasoner Core
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
package org.semanticweb.elk.reasoner.query;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.testing.DiffableOutput;

import com.google.common.collect.Sets;

/**
 * A possibly incomplete entailment results consisting of positive
 * 
 * The test output for possibly incomplete entailment results
 * 
 * @author Yevgeny Kazakov
 *
 * @param <E>
 *            the elements of the entailments
 * @param <O>
 *            the type of the output whose entailment can be compared
 */
public class IncompleteEntailmentTestOutput<E, O extends IncompleteEntailmentTestOutput<E, O>>
		implements DiffableOutput<E, O> {

	/**
	 * the elements of proved entailments
	 */
	private final Set<E> positiveEntailments_ = new HashSet<E>();

	/**
	 * the elements of disproved entailments
	 */
	private final Set<E> negativeEntailments_ = new HashSet<E>();

	public Set<? extends E> getPositiveEntailments() {
		return positiveEntailments_;
	}

	public Set<? extends E> getNegativeEntailments() {
		return negativeEntailments_;
	}

	protected void addPositiveEntailment(E positive) {
		positiveEntailments_.add(positive);
	}

	protected void addNegativeEntailment(E negative) {
		negativeEntailments_.add(negative);
	}

	@Override
	public boolean containsAllElementsOf(O other) {
		return Collections.disjoint(getNegativeEntailments(),
				other.getPositiveEntailments());
	}

	@Override
	public void reportMissingElementsOf(O other, Listener<E> listener) {
		for (E element : Sets.intersection(getNegativeEntailments(),
				other.getPositiveEntailments())) {
			listener.missing(element);
		}
	}

}
