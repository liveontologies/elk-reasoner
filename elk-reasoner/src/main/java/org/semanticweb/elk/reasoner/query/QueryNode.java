/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.impl.SimpleNode;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * A {@link Node} created for queried class expressions. It keeps track of
 * atomic classes equivalent to the queried class expressions and atomic direct
 * super classes of the queried class expressions.
 * 
 * @author Yevgeny Kazakov
 * @author Peter Skocovsky
 * 
 * @param <T>
 */
public class QueryNode<T extends ElkEntity> extends SimpleNode<T>
		implements Node<T> {

	protected final Set<Node<T>> directSuperNodes_;

	public QueryNode(final Iterable<T> allMembers, final int size,
			final ComparatorKeyProvider<? super T> comparatorKeyProvider) {
		super(allMembers, size, comparatorKeyProvider);
		this.directSuperNodes_ = new ArrayHashSet<Node<T>>();
	}

	public QueryNode(
			final ComparatorKeyProvider<? super T> comparatorKeyProvider) {
		super(comparatorKeyProvider);
		this.directSuperNodes_ = new ArrayHashSet<Node<T>>();
	}

	public Set<? extends Node<T>> getDirectSuperNodes() {
		return Collections.unmodifiableSet(directSuperNodes_);
	}

	public void addDirectSuperNode(final Node<T> superNode) {
		directSuperNodes_.add(superNode);
	}

}
