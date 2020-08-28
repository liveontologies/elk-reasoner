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

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.completeness.IncompleteTestOutput;
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
		extends IncompleteTestOutput<Map<ElkIri, ? extends E>>
		implements DiffableOutput<E, O> {

	public EquivalentEntitiesTestOutput(
			IncompleteResult<? extends Map<ElkIri, ? extends E>> incompleteMembers) {
		super(incompleteMembers);
	}

	public EquivalentEntitiesTestOutput(Map<ElkIri, ? extends E> members) {
		super(members);
	}

	static <E extends ElkEntity> Map<ElkIri, E> toMap(Node<? extends E> node) {
		Map<ElkIri, E> result = new HashMap<>(node.size());
		for (E member : node) {
			result.put(member.getIri(), member);
		}
		return result;
	}

	@Override
	public boolean containsAllElementsOf(O other) {
		if (!isComplete()) {
			return true;
		}
		for (ElkIri otherMember : other.getValue().keySet()) {
			if (!getValue().containsKey(otherMember)) {
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
		for (ElkIri otherMember : other.getValue().keySet()) {
			if (!getValue().containsKey(otherMember)) {
				listener.missing(other.getValue().get(otherMember));
			}
		}
	}

}
