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
package org.semanticweb.elk.reasoner.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.testing.DiffableOutput;

/**
 * A test output of a query for equivalent entities.
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 *
 * @param <E>
 *            the type of entities.
 * @param <O>
 *            the type of the containers of entities that can be checked for
 *            inclusions
 */
public class EquivalentEntitiesTestOutput<E extends ElkEntity, O extends EquivalentEntitiesTestOutput<E, O>>
		implements DiffableOutput<E, O> {

	private final Map<ElkIri, ? extends E> members_;

	private final boolean isComplete_;

	public EquivalentEntitiesTestOutput(Map<ElkIri, ? extends E> members,
			boolean isComplete) {
		this.members_ = members;
		this.isComplete_ = isComplete;
	}

	public EquivalentEntitiesTestOutput(Iterable<? extends E> equivalent,
			int sizeEstimate, boolean isComplete) {
		Map<ElkIri, E> members = new HashMap<>(sizeEstimate);
		members_ = members;
		for (E member : equivalent) {
			members.put(member.getIri(), member);
		}
		this.isComplete_ = isComplete;
	}

	public EquivalentEntitiesTestOutput(Collection<? extends E> equivalent,
			boolean isComplete) {
		this(equivalent, equivalent.size(), isComplete);
	}

	public EquivalentEntitiesTestOutput(
			IncompleteResult<? extends Node<? extends E>> equivalent) {
		this(equivalent.getValue(), equivalent.getValue().size(),
				equivalent.isComplete());
	}

	Map<ElkIri, ? extends E> getMembers() {
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
		for (ElkIri otherMember : other.getMembers().keySet()) {
			if (!members_.containsKey(otherMember)) {
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
		for (ElkIri otherMember : other.getMembers().keySet()) {
			if (!members_.containsKey(otherMember)) {
				listener.missing(other.getMembers().get(otherMember));
			}
		}
	}

}
