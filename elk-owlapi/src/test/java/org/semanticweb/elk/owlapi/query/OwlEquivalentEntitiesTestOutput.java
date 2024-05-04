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

import java.util.Set;

import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.completeness.IncompleteTestOutput;
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
		extends IncompleteTestOutput<Set<? extends E>>
		implements DiffableOutput<E, O> {

	public OwlEquivalentEntitiesTestOutput(
			IncompleteResult<? extends Set<? extends E>> incompleteMembers) {
		super(incompleteMembers);
	}

	public OwlEquivalentEntitiesTestOutput(Set<? extends E> members) {
		super(members);
	}

	Set<? extends E> getMembers() {
		return this.getValue();
	}

	@Override
	public boolean containsAllElementsOf(O other) {
		if (!isComplete()) {
			return true;
		}
		for (E otherMember : other.getMembers()) {
			if (!getMembers().contains(otherMember)) {
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
			if (!getMembers().contains(otherMember)) {
				listener.missing(otherMember);
			}
		}
	}

}
