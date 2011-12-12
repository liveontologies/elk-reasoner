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
import org.semanticweb.elk.util.collections.LazySetIntersection;

public class MarkerOperations {

	/**
	 * This method is used to create Marked objects
	 * 
	 * @return Marked<T> object that represents the key marked with the markers
	 */
	public static <T extends Marked<T>> Marked<T> mark(T key,
			Markers markers) {
		
		if (markers.isDefinite())
			return key;
		return new ExplicitlyMarked<T> (key, markers);
	}

	/**
	 * This method is used to compute intersection of two Markers
	 * 
	 * @return Marked<T> object that represent the key marked with the
	 *         intersection of markers1 and markers2.
	 *         Returns null if the intersection is empty.
	 * 
	 */
	public static Markers markersIntersection(Markers markers1, Markers markers2) {
		if (markers1.isDefinite())
			return markers2;
	
		if (markers2.isDefinite())
			return markers1;
	
		// this is just an optimisation
		if (markers1 == markers2)
			return markers1;
	
		ArraySet<Marker> markers = new ArraySet<Marker>(markers1.getMarkers().size());
		for (Marker m : new LazySetIntersection<Marker> (markers1.getMarkers(), markers2.getMarkers()))
				markers.add(m);
	
		if (markers.isEmpty())
			return null;
		
		return new NonDefiniteMarkers(markers);
	}

	/**
	 * This method is used to compute union of two Markers.
	 * 
	 * @return null if oldMarkes is stronger than
	 *         newMarkers. Otherwise return the union oldMarkers and newMarkers.
	 */
	public static Markers markersUnion(Markers oldMarkers,
			Markers newMarkers) {
	
		if (oldMarkers.isDefinite()) {
			return null;
		}
	
		if (newMarkers.isDefinite()) {
			return newMarkers;
		}
	
		// this is just an optimisation
		if (oldMarkers == newMarkers) {
			return null;
		}
	
		ArraySet<Marker> markers = new ArraySet<Marker> (oldMarkers.getMarkers());
		boolean newMarker = false;
		for (Marker m : newMarkers.getMarkers()) {
			newMarker = newMarker || markers.add(m);
		}
	
		if (!newMarker)
			return null;
		
		return new NonDefiniteMarkers(markers);
	}
	
	/**
	 * This is similar to marekrsUnion but returns only the new markers.
	 * 
	 * @return null if oldMarkes is stronger than
	 *         newMarkers. Otherwise return the difference newMarkers and oldMarkers.
	 */
	public static Markers markersDifference(Markers oldMarkers,
			Markers newMarkers) {
	
		if (oldMarkers.isDefinite()) {
			return null;
		}
	
		if (newMarkers.isDefinite()) {
			return newMarkers;
		}
	
		// this is just an optimisation
		if (oldMarkers == newMarkers) {
			return null;
		}
	
		ArraySet<Marker> markers = new ArraySet<Marker> ();
		for (Marker m : newMarkers.getMarkers())
			if (!oldMarkers.getMarkers().contains(m)) {
				markers.add(m);
		}
	
		if (markers.isEmpty())
			return null;
		
		return new NonDefiniteMarkers(markers);
	}

}
