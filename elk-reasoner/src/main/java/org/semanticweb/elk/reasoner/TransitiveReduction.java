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

package org.semanticweb.elk.reasoner;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import org.semanticweb.elk.util.ArraySet;

/**
 * Assigns 
 * 
 * @author Frantisek Simancik
 *
 * @param <T>
 */
public class TransitiveReduction<T> implements Taxonomy<T> {
	protected Map<T, Integer>  mapObjectToInteger;
	protected Vector<EquivalenceClass<T>> equivalenceClasses;
	protected boolean[] canonical;
	protected int n;
		
	/**
	 * Computes the transitive closure of the relation represented by
	 * transitiveRelation
	 * 
	 * @param transitiveRelation
	 */
	public TransitiveReduction(TransitiveRelation<T> transitiveRelation) {

		// set up a mapping to integers
		
		mapObjectToInteger = new HashMap<T, Integer> ();
		n = 0;
		for (T object : transitiveRelation.getAllObjects()) {
			if (mapObjectToInteger.get(object) == null)
				mapObjectToInteger.put(object, n++);
		}

		
		// internalize sub-super relations
		
		Vector<ArraySet<Integer>> superObjects = new Vector<ArraySet<Integer>> (n);
		for (int i = 0; i < n; i++)
			superObjects.add(new ArraySet<Integer> ());
		
		for (T object : transitiveRelation.getAllObjects()) {
			Integer x = mapObjectToInteger.get(object);
			switch (transitiveRelation.getImplementedDirection()) {
			case SUB_OBJECTS :
				for (T subObject : transitiveRelation.getAllSubObjects(object)) {
					Integer y = mapObjectToInteger.get(subObject);
					if (!x.equals(y)) 
						superObjects.get(y).add(x);
				}
				break;
			case SUPER_OBJECTS :
				for (T superObject : transitiveRelation.getAllSuperObjects(object)) {
					Integer y = mapObjectToInteger.get(superObject);
					if (!x.equals(y)) 
						superObjects.get(x).add(y);
					
				}
				break;
			}
		}

		//compute equivalence classes
		
		equivalenceClasses = new Vector<EquivalenceClass<T>> (n);
		equivalenceClasses.setSize(n);
		canonical = new boolean[n];
		for (int i = 0; i < n; i++)
			canonical[i] = true;
		
		for (int i = 0; i < n; i++)
			if (canonical[i]) {
				EquivalenceClass<T> eqClass = new EquivalenceClass<T> ();
				equivalenceClasses.set(i, eqClass);
				for (int j : superObjects.get(i))
					if (superObjects.get(j).contains(i)) {
						equivalenceClasses.set(j, eqClass);
						canonical[j] = false;
					}
			}
		
		//transitive reduction
		
		int[] t = new int[n];
		for (int i = 0; i < n; i++)
			t[i] = -1;
		
		for (int i = 0; i < n; i++)
			if (canonical[i]) {
				for (int j : superObjects.get(i))
					if (canonical[j] && t[j] != i)
						for (int k : superObjects.get(j))
							t[k] = i;
				for (int j : superObjects.get(i))
					if (canonical[j] && t[j] != i) {
						equivalenceClasses.get(j).getDirectSubClasses().add(equivalenceClasses.get(i));
						equivalenceClasses.get(i).getDirectSuperClasses().add(equivalenceClasses.get(j));
					}
			}
		
		
		// register objects to equivalence classes
				 
		for (T object : transitiveRelation.getAllObjects())
			getEquivalenceClass(object).getMembers().add(object);
	}
	
	/**
	 * @param object
	 * @return the EquivalenceClass of object
	 */
	public EquivalenceClass<T> getEquivalenceClass(T object) {
		return equivalenceClasses.get(mapObjectToInteger.get(object));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<EquivalenceClass<T>> iterator() {
		return new TaxonomyIterator();
	}

	protected class TaxonomyIterator implements Iterator<EquivalenceClass<T>> {
		int i;
		
		public TaxonomyIterator() {
			i = 0;
			while (i != n && !canonical[i])
				i++;
		}

		public boolean hasNext() {
			return i != n;
		}

		public EquivalenceClass<T> next() {
			if (i == n)
				throw new NoSuchElementException();
			
			EquivalenceClass<T> result = equivalenceClasses.get(i++);
			while (i != n && !canonical[i])
				i++;
			
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}