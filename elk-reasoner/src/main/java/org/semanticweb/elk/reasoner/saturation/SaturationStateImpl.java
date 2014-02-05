/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class SaturationStateImpl extends AbstractSaturationState {

	// logger for this class
	//private static final Logger LOGGER_ = LoggerFactory
	//		.getLogger(SaturationStateImpl.class);

	/**
	 * 
	 * @param index
	 */
	public SaturationStateImpl(OntologyIndex index) {
		super(index);
	}

	@Override
	public Collection<Context> getContexts() {
		return new AbstractCollection<Context>() {

			@Override
			public Iterator<Context> iterator() {
				return new Iterator<Context>() {

					Iterator<IndexedClassExpression> ices = ontologyIndex
							.getIndexedClassExpressions().iterator();

					Context next;

					{
						seekNext();
					}

					void seekNext() {
						while (ices.hasNext()) {
							next = ices.next().getContext();
							if (next != null)
								return;
						}
						// no next element
						next = null;
					}

					@Override
					public boolean hasNext() {
						return next != null;
					}

					@Override
					public Context next() {
						if (next == null)
							throw new NoSuchElementException("No next context");
						Context result = next;
						seekNext();
						return result;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException(
								"Removal not supported");
					}

				};
			}

			@Override
			public boolean isEmpty() {
				return !iterator().hasNext();
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

		};
	}

	@Override
	public Context getContext(IndexedClassExpression ice) {
		return ice.getContext();
	}

	@Override
	void resetContexts() {
		for (Context context : getContexts()) {
			context.getRoot().resetContext();
		}
	}

	@Override
	Context setIfAbsent(Context context) {
		return context.getRoot().setContextIfAbsent(context);
	}
}
