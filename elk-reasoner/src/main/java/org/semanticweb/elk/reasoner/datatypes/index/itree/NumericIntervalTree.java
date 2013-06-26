package org.semanticweb.elk.reasoner.datatypes.index.itree;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.reasoner.datatypes.numbers.NumberComparator;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.NumericIntervalValueSpace;

/**
 * A <em>dynamic interval tree</em> is a balanced binary search tree which
 * stores intervals so that point queries (queries that return intervals from
 * the set which contain a query point) can be completed in time O(k*log(n)),
 * where n is the number of intervals stored in the tree and k is the size of
 * the result set from the query.
 * <p>
 * Insertion and deletion of intervals to and from this tree completes in time
 * O(log(n)) where n is the number of intervals stored in the tree.
 * <p>
 * This tree consumes linear space in the number of intervals stored in the
 * tree.
 */
public class NumericIntervalTree<T extends NumericIntervalValueSpace> {

	protected RedBlackTree<T> binarySearchTree = new RedBlackTree<T>(
		new Comparator<T>() {
		@Override
		public int compare(T o1, T o2) {
			int result = NumberComparator.INSTANCE.compare(o1.lowerBound, o2.lowerBound);
			if (result == 0) {
				if (o1.lowerInclusive != o2.lowerInclusive) {
					result = o1.lowerInclusive ? -1 : 1;
				} else {
					result = NumberComparator.INSTANCE.compare(
						o1.upperBound, o2.upperBound);
					if (result == 0) {
						if (o1.upperInclusive != o2.upperInclusive) {
							result = o1.upperInclusive ? -1 : 1;
						}
					}
				}
			}
			return result;
		}
	}) {
		@Override
		protected RedBlackTree.Node<T> createNewNode(T value) {
			return new NumericIntervalTree.Node<T>(value);
		}

		@Override
		public RedBlackTree.Node<T> delete(T value) {
			RedBlackTree.Node<T> node = super.delete(value);
			if (node != null && node.getColor() != NodeColor.BLACK) {
				NumericIntervalTree.Node<T> temp = (NumericIntervalTree.Node<T>) node
					.getParent();
				while (temp != null) {
					temp.computeSubtreeSpan();
					temp = temp.getParent();
				}
			}
			return node;
		}

		@Override
		protected void fixAfterDeletion(RedBlackTree.Node<T> node) {
			NumericIntervalTree.Node<T> temp = (NumericIntervalTree.Node<T>) node
				.getParent();
			while (temp != null) {
				temp.computeSubtreeSpan();
				temp = temp.getParent();
			}
			super.fixAfterDeletion(node);
		}

		@Override
		protected void fixAfterInsertion(RedBlackTree.Node<T> node) {
			NumericIntervalTree.Node<T> temp = (NumericIntervalTree.Node<T>) node
				.getParent();
			while (temp != null) {
				temp.computeSubtreeSpan();
				temp = temp.getParent();
			}
			super.fixAfterInsertion(node);
		}

		@Override
		protected void leftRotate(RedBlackTree.Node<T> node) {
			super.leftRotate(node);
			NumericIntervalTree.Node<T> temp = (NumericIntervalTree.Node<T>) node;
			temp.computeSubtreeSpan();
			temp.getParent().computeSubtreeSpan();
		}

		@Override
		protected void rightRotate(RedBlackTree.Node<T> node) {
			super.rightRotate(node);
			NumericIntervalTree.Node<T> temp = (NumericIntervalTree.Node<T>) node;
			temp.computeSubtreeSpan();
			temp.getParent().computeSubtreeSpan();
		}
	};

	/**
	 * Clear the contents of the tree.
	 */
	public void clear() {
		binarySearchTree.clear();
	}

	/**
	 * Delete the specified interval from this tree.
	 *
	 * @param interval the interval to delete.
	 * @return true if an element was deleted as a result of this call,
	 * false otherwise.
	 */
	public boolean delete(T interval) {
		return binarySearchTree.delete(interval) != null;
	}

	/**
	 * Fetch intervals containing the specified point.
	 *
	 * @param queryPoint the query point.
	 * @return a Collection of all intervals containing the specified point.
	 */
	public Set<T> fetchContainingIntervals(Number queryPoint, ElkDatatype.ELDatatype datatype) {
		if (queryPoint == null) {
			throw new IllegalArgumentException();
		}
		Set<T> result = new HashSet<T>();
		Node<T> node = (Node<T>) binarySearchTree.getRoot();
		List<Node<T>> queue = new ArrayList<Node<T>>();
		if (node != null) {
			queue.add(node);
		}
		while (!queue.isEmpty()) {
			node = queue.remove(queue.size() - 1);
			if (node.getValue().contains(queryPoint, datatype)) {
				result.add(node.getValue());
			}
			if (node.hasTail()) {
				for (T t : node.getTail()) {
					if (t.contains(queryPoint, datatype)) {
						result.add(t);
					}
				}
			}
			Node<T> child = node.getLeft();
			if (child != null) {
				int cmp = NumberComparator.INSTANCE.compare(child.getSubtreeSpanHigh(), queryPoint);
				if (cmp > 0 || cmp == 0 && child.isClosedOnSubtreeSpanHigh()) {
					queue.add(child);
				}
			}
			child = node.getRight();
			if (child != null) {
				int cmp = NumberComparator.INSTANCE.compare(child.getSubtreeSpanLow(), queryPoint);
				if (cmp < 0 || cmp == 0 && child.isClosedOnSubtreeSpanLow()) {
					queue.add(child);
				}
			}
		}
		return result;
	}

	/**
	 * Get the number of intervals being stored in the tree.
	 *
	 * @return the number of intervals being stored in the tree.
	 */
	public int getSize() {
		return binarySearchTree.getSize();
	}

	/**
	 * Insert the specified interval into this tree.
	 *
	 * @param interval the interval to insert.
	 * @return true if an element was inserted as a result of this call,
	 * false otherwise.
	 */
	public boolean insert(T interval) {
		return binarySearchTree.insert(interval) != null;
	}

	/**
	 * A <em>node</em> for a dynamic interval tree is a red-black tree node
	 * augmented to store the maximum high and minimum low endpoints among
	 * intervals stored within the subtree rooted at the node.
	 */
	protected static class Node<T extends NumericIntervalValueSpace>
		extends RedBlackTree.Node<T> {

		private Number subtreeSpanLow, subtreeSpanHigh;
		private boolean isClosedOnSubtreeSpanLow, isClosedOnSubtreeSpanHigh;

		/**
		 * Construct a new node associated with the specified interval.
		 *
		 * @param interval the interval with which this node is
		 * associated.
		 */
		public Node(T interval) {
			super(interval);
			subtreeSpanLow = interval.lowerBound;
			subtreeSpanHigh = interval.upperBound;
			isClosedOnSubtreeSpanLow = interval.lowerInclusive;
			isClosedOnSubtreeSpanHigh = interval.upperInclusive;
		}

		/**
		 * Compute the maximum high and minimum low endpoints among
		 * intervals stored within the subtree rooted at this node and
		 * correct values up the tree.
		 */
		protected void computeSubtreeSpan() {
			Number _subtreeSpanLow = getValue().lowerBound;
			Number _subtreeSpanHigh = getValue().upperBound;
			boolean _isClosedOnSubtreeSpanLow = getValue().lowerInclusive;
			boolean _isClosedOnSubtreeSpanHigh = getValue().upperInclusive;
			Node<T> child;
			child = getLeft();
			if (child != null) {
				int cmp = NumberComparator.INSTANCE.compare(child.subtreeSpanLow, _subtreeSpanLow);
				if (cmp < 0 || cmp == 0 && child.isClosedOnSubtreeSpanLow) {
					_subtreeSpanLow = child.subtreeSpanLow;
					_isClosedOnSubtreeSpanLow = child.isClosedOnSubtreeSpanLow;
				}
				cmp = NumberComparator.INSTANCE.compare(child.subtreeSpanHigh, _subtreeSpanHigh);
				if (cmp > 0 || cmp == 0 && child.isClosedOnSubtreeSpanHigh) {
					_subtreeSpanHigh = child.subtreeSpanHigh;
					_isClosedOnSubtreeSpanHigh = child.isClosedOnSubtreeSpanHigh;
				}
			}
			child = getRight();
			if (child != null) {
				int cmp = NumberComparator.INSTANCE.compare(child.subtreeSpanLow, _subtreeSpanLow);
				if (cmp < 0 || cmp == 0 && child.isClosedOnSubtreeSpanLow) {
					_subtreeSpanLow = child.subtreeSpanLow;
					_isClosedOnSubtreeSpanLow = child.isClosedOnSubtreeSpanLow;
				}
				cmp = NumberComparator.INSTANCE.compare(child.subtreeSpanHigh, _subtreeSpanHigh);
				if (cmp > 0 || cmp == 0 && child.isClosedOnSubtreeSpanHigh) {
					_subtreeSpanHigh = child.subtreeSpanHigh;
					_isClosedOnSubtreeSpanHigh = child.isClosedOnSubtreeSpanHigh;
				}
			}
			this.subtreeSpanLow = _subtreeSpanLow;
			this.isClosedOnSubtreeSpanLow = _isClosedOnSubtreeSpanLow;
			this.subtreeSpanHigh = _subtreeSpanHigh;
			this.isClosedOnSubtreeSpanHigh = _isClosedOnSubtreeSpanHigh;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node<T> getLeft() {
			return (Node<T>) super.getLeft();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node<T> getParent() {
			return (Node<T>) super.getParent();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node<T> getRight() {
			return (Node<T>) super.getRight();
		}

		public Number getSubtreeSpanHigh() {
			return subtreeSpanHigh;
		}

		public Number getSubtreeSpanLow() {
			return subtreeSpanLow;
		}

		public boolean isClosedOnSubtreeSpanHigh() {
			return isClosedOnSubtreeSpanHigh;
		}

		public boolean isClosedOnSubtreeSpanLow() {
			return isClosedOnSubtreeSpanLow;
		}
	}
}
