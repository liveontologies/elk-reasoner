package org.semanticweb.elk.reasoner.saturation.context;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * A collection of {@link IndexedClassExpression}s that are roots of the given
 * collection of {@link Context}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ContextRootCollection extends
		AbstractCollection<IndexedClassExpression> {

	private final Collection<? extends Context> contexts_;

	public ContextRootCollection(Collection<? extends Context> contexts) {
		this.contexts_ = contexts;
	}

	@Override
	public Iterator<IndexedClassExpression> iterator() {
		return new Iterator<IndexedClassExpression>() {

			private final Iterator<? extends Context> iter_ = contexts_
					.iterator();

			@Override
			public boolean hasNext() {
				return iter_.hasNext();
			}

			@Override
			public IndexedClassExpression next() {
				return iter_.next().getRoot();
			}

			@Override
			public void remove() {
				iter_.remove();
			}

		};
	}

	@Override
	public int size() {
		return contexts_.size();
	}

}
