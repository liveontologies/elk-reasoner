/**
 * 
 */
package org.semanticweb.elk.util.collections;

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