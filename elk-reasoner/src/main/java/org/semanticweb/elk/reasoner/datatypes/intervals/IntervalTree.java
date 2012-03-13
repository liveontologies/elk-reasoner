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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IntervalTree extends RBTree {

	private Comparator endpointComparator;

	public IntervalTree(Comparator endpointComparator) {
		super(new IntervalComparator(endpointComparator));
		this.endpointComparator = endpointComparator;
	}

	public void insert(Interval interval, Object data) {
		IntervalNode node = new IntervalNode(interval, endpointComparator, data);
		insertNode(node);
	}

	public void insertAll(List<Interval> intervals, Object data) {
		for (Interval i : intervals) {
			IntervalNode node = new IntervalNode(i, endpointComparator, data);
			insertNode(node);
		}
	}

	public List<IntervalNode> findAllNodesContaining(Interval interval) {
		List retList = new ArrayList();
		searchForIntersectingNodesFrom((IntervalNode) getRoot(), interval, retList);
		return retList;
	}

	protected Object getNodeValue(RBNode node) {
		return ((IntervalNode) node).getInterval();
	}

	static class IntervalComparator implements Comparator {

		private Comparator endpointComparator;

		public IntervalComparator(Comparator endpointComparator) {
			this.endpointComparator = endpointComparator;
		}

		public int compare(Object o1, Object o2) {
			Interval i1 = (Interval) o1;
			Interval i2 = (Interval) o2;
			return endpointComparator.compare(i1.getLowEndpoint(), i2.getLowEndpoint());
		}
	}

	private void searchForIntersectingNodesFrom(IntervalNode node,
			Interval searchInterval,
			List resultList) {

		if (node == null) {
			return;
		}

		// Check current node
		if (node.getInterval().overlays(searchInterval, endpointComparator)) {
			resultList.add(node);
		}

		// Check to see whether we have to traverse the left subtree
		IntervalNode left = (IntervalNode) node.getLeft();
		if (left != null
				&& endpointComparator.compare(left.getMinEndpoint(), searchInterval.getLowEndpoint()) <= 0
				&& endpointComparator.compare(left.getMaxEndpoint(), searchInterval.getHighEndpoint()) >= 0) {
			searchForIntersectingNodesFrom(left, searchInterval, resultList);
		}

		// Check to see whether we have to traverse the right subtree
		IntervalNode right = (IntervalNode) node.getRight();
		if (right != null
				&& endpointComparator.compare(right.getMinEndpoint(), searchInterval.getLowEndpoint()) <= 0
				&& endpointComparator.compare(right.getMaxEndpoint(), searchInterval.getHighEndpoint()) >= 0) {
			searchForIntersectingNodesFrom(right, searchInterval, resultList);
		}
	}
}
