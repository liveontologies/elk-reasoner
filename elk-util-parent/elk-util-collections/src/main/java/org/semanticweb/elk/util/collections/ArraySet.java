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
package org.semanticweb.elk.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class ArraySet<T> extends ArrayList<T> implements Set<T> {

	private static final long serialVersionUID = 4210562273973502066L;

	public ArraySet() {
		super();
	}
	
	public ArraySet(Collection<? extends T> c) {
		this(c.size());
		addAll(c);
	}
	
	public ArraySet(int initialCapacity) {
		super(initialCapacity);
	}
	
	@Override
	public boolean add(T element) {
		if (!contains(element)) 
			return super.add(element);
		return false;
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean change = false;
		for (T element : c)
			change = this.add(element) || change;
		return change;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}
}
