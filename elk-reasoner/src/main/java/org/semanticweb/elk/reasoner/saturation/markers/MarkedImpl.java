/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.saturation.markers;

import java.util.Set;

import org.semanticweb.elk.util.collections.ArraySet;

public class MarkedImpl<T> implements Marked<T> {
	
	public static <T extends Marked<T>> Marked<T> create(T key, Marked<?>... markers) {

		boolean allDefinite = true;
		for (Marked<?> m : markers)
			if (!m.isDefinite())
				allDefinite = false;
		
		if (allDefinite)
			return key;
		
		ArraySet<Marker> s = null;
		for (Marked<?> m : markers)
			if (!m.isDefinite()) {
				if (s == null) {
					s = new ArraySet<Marker> (m.getMarkers().size());
					s.addAll(m.getMarkers());
				}
				else {
					s.retainAll(m.getMarkers());
				}
			}
		
		return new MarkedImpl<T> (key, s);
	}
	
	protected final Set<Marker> markers;
	protected final T key;
	
	public MarkedImpl(T key, Set<Marker> markers) {
		this.markers = markers;
		this.key = key;
	}

	public T getKey() {
		return key;
	}

	public boolean isDefinite() {
		return false;
	}

	public Set<Marker> getMarkers() {
		return markers;
	}
}
