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

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.util.collections.ArraySet;

/**
 * Represents a non-definite element marked by at least one marker.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ExplicitlyMarked<T> implements Marked<T> {

	/**
	 * @return Marked<T> object that represents the key marked with the same
	 *         markers as markerSource.
	 */
	public static <T extends Marked<T>> Marked<T> mark(T key,
			Marked<?> markerSource) {
		if (markerSource.isDefinite())
			return key;
		return new ExplicitlyMarked<T>(key, markerSource.getMarkers());
	}

	/**
	 * @return Marked<T> object that represent the key marked with the
	 *         intersection of markers from markerSource1 and markerSource2.
	 *         Returns null if the intersection if empty.
	 * 
	 */
	public static <T extends Marked<T>> Marked<T> mark(T key,
			Marked<?> markerSource1, Marked<?> markerSource2) {
		if (markerSource1.isDefinite() && markerSource2.isDefinite())
			return key;
		
		if (markerSource1.isDefinite())
			return new ExplicitlyMarked<T> (key, markerSource2.getMarkers());
		
		if (markerSource2.isDefinite())
			return new ExplicitlyMarked<T> (key, markerSource1.getMarkers());
		
		ArraySet<Marker> markers = new ArraySet<Marker> (markerSource1.getMarkers().size());
		for (Marker m : markerSource1.getMarkers())
			if (markerSource2.getMarkers().contains(m))
				markers.add(m);
		
		if (markers.isEmpty())
			return null;
		
		markers.trimToSize();
		return new ExplicitlyMarked<T> (key, markers);
	}

	protected final Set<Marker> markers;
	protected final T key;

	public ExplicitlyMarked(T key, Set<Marker> markers) {
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
		return Collections.unmodifiableSet(markers);
	}
}
