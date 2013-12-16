package org.semanticweb.elk.reasoner.taxonomy.nodes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Map;

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * A {@link Node} created for an anonymous objects that should not be listed
 * among its members
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 */
public class AnonymousNode<K extends ElkObject, M> extends SimpleNode<K, M>
		implements Node<K, M> {

	public AnonymousNode(K anonymousMember, Map<K, M> allMembers) {
		super(allMembers);
		this.membersLookup.remove(anonymousMember);
	}

}
