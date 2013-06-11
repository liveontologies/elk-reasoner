/**
 * 
 */
package org.semanticweb.elk.util.collections;
/*
 * #%L
 * ELK Utilities Collections
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
import java.util.Set;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class LazyCollectionMinusSet<I> extends AbstractCollection<I> {

	private final Collection<I> collection_;
	private final Set<I> set_;
	
	public LazyCollectionMinusSet(final Collection<I> collection, final Set<I> set) {
		collection_ = collection;
		set_ = set;
	}
	
	@Override
	public boolean isEmpty() {
		return set_.containsAll(collection_);
	}
	
	@Override
	public boolean contains(Object o) {
		return collection_.contains(o) && !set_.contains(o);
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public Iterator<I> iterator() {
		return new Iterator<I>() {

			private final Iterator<I> iter_ = collection_.iterator();
			private I next_ = null;
			
			@Override
			public boolean hasNext() {
				
				while (next_ == null && iter_.hasNext()) {
					I elem = iter_.next();
					
					next_ = set_.contains(elem) ? null : elem;
				}
				
				return next_ != null;
			}

			@Override
			public I next() {
				if (next_ != null) {
					return giveAway();
				}
				else if (hasNext()) {
					return giveAway();
				}
				else {
					throw new NoSuchElementException();
				}
			}
			
			private I giveAway() {
				I elem = next_;
				
				next_ = null;
				
				return elem;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

	@Override
	public int size() {
		return collection_.size() - set_.size();
	}
}