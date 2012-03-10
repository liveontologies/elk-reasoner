/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.datatypes.intervals;

import java.util.Comparator;

/**
 * An interval is an immutable data structure defined by its two endpoints.
 */
public class Interval {

	private Number lowEndpoint;
	private Number highEndpoint;

	/**
	 * It is required that the low endpoint be less than or equal to the high
	 * endpoint according to the Comparator which will be passed into the
	 * overlaps() routines.
	 *
	 * @param lowEndpoint
	 * @param highEndpoint
	 */
	public Interval(Number lowEndpoint, Number highEndpoint) {
		this.lowEndpoint = lowEndpoint;
		this.highEndpoint = highEndpoint;
	}

	public Number getLowEndpoint() {
		return lowEndpoint;
	}

	public Number getHighEndpoint() {
		return highEndpoint;
	}

	/**
	 * This takes the Interval to compare against as well as a Comparator which
	 * will be applied to the low and high endpoints of the given intervals.
	 *
	 * @param arg
	 * @param endpointComparator
	 * @return
	 */
	public boolean overlaps(Interval arg, Comparator endpointComparator) {
		return overlaps(arg.getLowEndpoint(), arg.getHighEndpoint(), endpointComparator);
	}

	/**
	 * Routine which can be used instead of the one taking an interval, for the
	 * situation where the endpoints are being retrieved from different data
	 * structures
	 *
	 * @param otherLowEndpoint
	 * @param otherHighEndpoint
	 * @param endpointComparator
	 * @return
	 */
	public boolean overlaps(Number otherLowEndpoint,
			Number otherHighEndpoint,
			Comparator endpointComparator) {
		return endpointComparator.compare(highEndpoint, otherHighEndpoint) >= 0
				&& endpointComparator.compare(lowEndpoint, otherLowEndpoint) <= 0;
	}

	public String toString() {
		return "(" + getLowEndpoint().toString() + ", " + getHighEndpoint().toString() + ")";
	}
}
