/*
 * #%L
 * ELK Utilities Collections
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owl.managers;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class WeakWrapper<T> extends WeakReference<T> {
	private final int hash_;
	
	protected int hashCode(T referent) {
		return referent.hashCode();
	}
	
	protected boolean equal(T referent, Object obj) {
		return referent.equals(obj);
	}
		
	public WeakWrapper(T referent, ReferenceQueue<? super T> q) {
		super(referent, q);
		assert referent != null;
		hash_ = hashCode(referent);
	}

	@Override
	public int hashCode() {
		return hash_;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj instanceof WeakWrapper<?>) {
			T a = this.get();
			Object b = ((WeakWrapper<?>) obj).get();
			
			if (a != null && b != null)
				return equal(a, b);
		}
		return false;
	}
}