package org.semanticweb.elk.reasoner;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;

public class NodeEntailment<M extends ElkEntity, N extends Node<M>> {

	private final N node_;

	public NodeEntailment(N node) {
		this.node_ = node;
	}

	public N getNode() {
		return node_;
	}

	public boolean containsAllMembersOf(N other) {
		if (getNode().size() < other.size()) {
			return false;
		}
		for (M otherMember : other) {
			if (!getNode().contains(otherMember)) {
				return false;
			}
		}
		return true;
	}

	public void reportMissingMembersOf(N other, Listener<M> listener) {
		for (M otherMember : other) {
			if (!getNode().contains(otherMember)) {
				listener.reportMissingMember(otherMember);
			}
		}
	}

	public interface Listener<M extends ElkEntity> {

		public void reportMissingMember(M member);
	}

}
