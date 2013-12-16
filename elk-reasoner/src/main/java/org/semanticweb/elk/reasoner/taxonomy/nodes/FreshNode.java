/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Collections;
import java.util.Map;

/**
 * A fresh {@link Node} containing an assignment for one member that do not
 * occur in a taxonomy. Such nodes are returned to queries when
 * {@link Reasoner#getAllowFreshEntities()} is set to {@code true}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * 
 * @see Reasoner#getAllowFreshEntities()
 */
public class FreshNode<K, M> implements Node<K, M> {

	final Map.Entry<K, M> member;

	public FreshNode(Map.Entry<K, M> member) {
		this.member = member;
	}

	@Override
	public Map<K, M> getMembersLookup() {
		return Collections.singletonMap(member.getKey(), member.getValue());
	}

}
