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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.taxonomy.impl.SimpleNode;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;

/**
 * A {@link Node} created for an anonymous {@link ElkObject} that should not be
 * listed among its members
 * 
 * @author Yevgeny Kazakov
 * @author Peter Skocovsky
 * 
 * @param <T>
 */
public class AnonymousNode<T extends ElkEntity> extends SimpleNode<T> implements
		Node<T> {

	public AnonymousNode(T anonymousMember, Iterable<T> allMembers, int size,
			final ComparatorKeyProvider<? super T> comparatorKeyProvider) {
		super(allMembers, size, comparatorKeyProvider);
		final int index = Collections.binarySearch(members_, anonymousMember,
				getKeyProvider().getComparator());
		if (index >= 0) {
			members_.remove(index);
		}
	}

}
