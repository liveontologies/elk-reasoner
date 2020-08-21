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
package org.semanticweb.elk.owlapi.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.testing.DiffableOutput;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * A test output of a query for equivalent entities.
 * 
 * @author Yevgeny Kazakov
 *
 * @param <E>
 *            the type of entities.
 * @param <O>
 *            the type of the containers of entities that can be checked for
 *            inclusions
 */
public class OwlEquivalentEntitiesTestOutput<E extends OWLEntity, O extends OwlEquivalentEntitiesTestOutput<E, O>>
		implements DiffableOutput<E, O> {

	private final Set<? extends E> members_;

	private final boolean isComplete_;

	public OwlEquivalentEntitiesTestOutput(Set<? extends E> members,
			boolean isComplete) {
		this.members_ = members;
		this.isComplete_ = isComplete;
	}

	public OwlEquivalentEntitiesTestOutput(Iterable<? extends E> equivalent,
			int sizeEstimate, boolean isComplete) {
		HashSet<E> members = new HashSet<>(sizeEstimate);
		members_ = members;
		equivalent.forEach(m -> members.add(m));
		this.isComplete_ = isComplete;
	}

	public OwlEquivalentEntitiesTestOutput(Collection<? extends E> equivalent,
			boolean isComplete) {
		this(equivalent, equivalent.size(), isComplete);
	}

	Set<? extends E> getMembers() {
		return this.members_;
	}

	boolean isComplete() {
		return isComplete_;
	}

	@Override
	public boolean containsAllElementsOf(O other) {
		if (!isComplete()) {
			return true;
		}
		for (E otherMember : other.getMembers()) {
			if (!members_.contains(otherMember)) {
				return false;
			}
		}
		// else all tests passed
		return true;
	}

	@Override
	public void reportMissingElementsOf(O other, Listener<E> listener) {
		if (!isComplete()) {
			return;
		}
		for (E otherMember : other.getMembers()) {
			if (!members_.contains(otherMember)) {
				listener.missing(otherMember);
			}
		}
	}

}
