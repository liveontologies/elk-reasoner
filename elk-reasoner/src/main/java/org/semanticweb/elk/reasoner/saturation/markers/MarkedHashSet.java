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

import org.semanticweb.elk.util.collections.ArraySet;

/**
 * A set of Marked<T> that takes into account relationships between markers to
 * remove redundancy. It overrides add(Marked<T> element) so that it if an
 * element with stronger markers than element.getMarkers() is already present,
 * then the element is not inserted.
 * 
 * @author Frantisek Simancik
 * 
 */
public class MarkedHashSet<T> extends EntryHashSet<T, Marked<T>> {

	public MarkedHashSet() {
		super();
	}

	public MarkedHashSet(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public boolean add(Marked<T> element) {
		Marked<T> old = get(element.getKey());

		if (old == null) {
			return super.add(element);
		}

		if (old.isDefinite()) {
			return false;
		}

		if (element.isDefinite()) {
			return replace(element);
		}

		ArraySet<Marker> markers = new ArraySet<Marker>(old.getMarkers());
		boolean newMarker = false;
		for (Marker m : element.getMarkers()) {
			newMarker = newMarker || markers.add(m);
		}

		if (!newMarker)
			return false;

		markers.trimToSize();
		return replace(new ExplicitlyMarked<T>(element.getKey(), markers));

	}
}