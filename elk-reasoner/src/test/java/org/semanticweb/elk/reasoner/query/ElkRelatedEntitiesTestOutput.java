/*
 * #%L
 * ELK OWL API Binding
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
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;

/**
 * A {@link RelatedEntitiesTestOutput} that returns {@link ElkEntity}s
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 * 
 * @param <E>
 *            the type of the entities to return
 */
public abstract class ElkRelatedEntitiesTestOutput<E extends ElkEntity>
		implements RelatedEntitiesTestOutput<E> {

	private final Iterable<? extends Iterable<E>> related_;

	private final boolean isComplete_;

	public ElkRelatedEntitiesTestOutput(
			final Collection<? extends Collection<E>> related,
			final ComparatorKeyProvider<? super E> keyProvider,
			boolean isComplete) {
		this.related_ = QueryTestUtils.related2Equalable(related,
				keyProvider.getComparator());
		this.isComplete_ = isComplete;
	}

	public ElkRelatedEntitiesTestOutput(final Set<? extends Node<E>> related,
			final ComparatorKeyProvider<? super E> keyProvider,
			boolean isComplete) {
		this.related_ = QueryTestUtils.relatedNodes2Equalable(related,
				keyProvider.getComparator());
		this.isComplete_ = isComplete;
	}

	@Override
	public Iterable<? extends Iterable<E>> getResult() {
		return related_;
	}

	@Override
	public boolean isComplete() {
		return isComplete_;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + related_
				+ (isComplete_ ? "" : "...") + ")";
	}

}
