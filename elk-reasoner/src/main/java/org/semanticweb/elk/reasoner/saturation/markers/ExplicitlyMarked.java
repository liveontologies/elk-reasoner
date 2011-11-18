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
	 * This method is used to compute the Markers of a result of a unary rule.
	 * 
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
	 * This method is used to compute the Markers of a result of a binary rule.
	 * 
	 * @return Marked<T> object that represent the key marked with the
	 *         intersection of markers from markerSource1 and markerSource2.
	 *         Returns null if the intersection is empty.
	 * 
	 */
	public static <T extends Marked<T>> Marked<T> markIntersection(T key,
			Marked<?> markerSource1, Marked<?> markerSource2) {
		if (markerSource1.isDefinite() && markerSource2.isDefinite())
			return key;

		if (markerSource1.isDefinite())
			return new ExplicitlyMarked<T>(key, markerSource2.getMarkers());

		if (markerSource2.isDefinite())
			return new ExplicitlyMarked<T>(key, markerSource1.getMarkers());

		// this is just an optimisation
		if (markerSource1.getMarkers() == markerSource2.getMarkers())
			return new ExplicitlyMarked<T>(key, markerSource1.getMarkers());

		ArraySet<Marker> markers = new ArraySet<Marker>(markerSource1
				.getMarkers().size());
		for (Marker m : markerSource1.getMarkers())
			if (markerSource2.getMarkers().contains(m))
				markers.add(m);

		if (markers.isEmpty())
			return null;

		markers.trimToSize();
		return new ExplicitlyMarked<T>(key, markers);
	}

	/**
	 * This method is used to decide if a newly derived axiom is stronger then
	 * an existing axiom, and if it is, then compute the resulting markers.
	 * 
	 * @return null if the oldMarked.getMarkers() is stronger than
	 *         newMarked.getMarkers(). Otherwise return newMarked.getKey()
	 *         marked by the union of the marks of oldMarked and newMarked.
	 */
	public static <T> Marked<T> markUnion(Marked<T> oldMarked,
			Marked<T> newMarked) {
		assert oldMarked.getKey() == newMarked.getKey();

		if (oldMarked.isDefinite()) {
			return null;
		}

		if (newMarked.isDefinite()) {
			return newMarked;
		}

		// this is just an optimisation
		if (oldMarked.getMarkers() == newMarked.getMarkers()) {
			return null;
		}

		ArraySet<Marker> markers = new ArraySet<Marker>(oldMarked.getMarkers());
		boolean newMarker = false;
		for (Marker m : newMarked.getMarkers()) {
			newMarker = newMarker || markers.add(m);
		}

		if (!newMarker)
			return null;

		markers.trimToSize();
		return new ExplicitlyMarked<T>(newMarked.getKey(), markers);
	}

	protected final Set<? extends Marker> markers;
	protected final T key;

	public ExplicitlyMarked(T key, Set<? extends Marker> markers) {
		this.markers = markers;
		this.key = key;
	}

	public T getKey() {
		return key;
	}

	public boolean isDefinite() {
		return false;
	}

	public Set<? extends Marker> getMarkers() {
		return Collections.unmodifiableSet(markers);
	}
}
