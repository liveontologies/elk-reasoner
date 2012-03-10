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

public class IntervalNode extends RBNode {

	private Interval interval;
	private Comparator endpointComparator;
	private Number minEndpoint;
	private Number maxEndpoint;

	public IntervalNode(Interval interval, Comparator endpointComparator, Object data) {
		super(data);
		this.interval = interval;
		this.endpointComparator = endpointComparator;
	}

	public void copyFrom(RBNode arg) {
		IntervalNode argNode = (IntervalNode) arg;
		this.interval = argNode.interval;
	}

	public Interval getInterval() {
		return interval;
	}

	public Number getMinEndpoint() {
		return minEndpoint;
	}

	public Number getMaxEndpoint() {
		return maxEndpoint;
	}

	public boolean update() {
		Number newMaxEndpoint = computeMaxEndpoint();
		Number newMinEndpoint = computeMinEndpoint();

		if ((maxEndpoint != newMaxEndpoint) || (minEndpoint != newMinEndpoint)) {
			maxEndpoint = newMaxEndpoint;
			minEndpoint = newMinEndpoint;
			return true;
		}

		return false;
	}

	// Computes maximum endpoint without setting it in this node
	public Number computeMinEndpoint() {
		IntervalNode left = (IntervalNode) getLeft();
		if (left != null) {
			return left.getMinEndpoint();
		}
		return interval.getLowEndpoint();
	}

	public Number computeMaxEndpoint() {
		Number curMax = interval.getHighEndpoint();
		if (getLeft() != null) {
			IntervalNode left = (IntervalNode) getLeft();
			if (endpointComparator.compare(left.getMaxEndpoint(), curMax) > 0) {
				curMax = left.getMaxEndpoint();
			}
		}

		if (getRight() != null) {
			IntervalNode right = (IntervalNode) getRight();
			if (endpointComparator.compare(right.getMaxEndpoint(), curMax) > 0) {
				curMax = right.getMaxEndpoint();
			}
		}
		return curMax;
	}

	public String toString() {
		String res = interval.toString();
		Object d = getData();
		if (d != null) {
			res += " " + d;
		}
		return res;
	}
}
