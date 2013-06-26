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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A <em>red-black tree</em> is a binary search tree guaranteeing that no path
 * from root to leaf is more than twice as long as any other such path. This
 * property provides an assurance that the height of a red-black tree is
 * logarithmic in the number of nodes in the tree.
 * <p>
 *
 * @see Introduction to Algorithms Cormen, Leiserson, Rivest, and Stein.
 * Introduction to Algorithms. 2nd ed. Cambridge, MA: MIT Press, 2001. ISBN:
 * 0262032937.
 */
public class RedBlackTree<T> implements Iterable<T> {

	private Comparator<? super T> comparator;
	private Node<T> root;
	private int size;

	/**
	 * Default constructor. Tree uses the natural ordering of elements
	 * defined by {@link Comparable#compareTo(Object)}; for custom ordering
	 * see {@link RedBlackTree#RedBlackTree(Comparator)}.
	 */
	public RedBlackTree() {
		this(null);
	}

	/**
	 * Construct a new tree which uses the specified custom comparator.
	 *
	 * @param comparator the comparator to use when ordering elements.
	 */
	public RedBlackTree(Comparator<? super T> comparator) {
		this.comparator = comparator;
		this.root = null;
		this.size = 0;
	}

	/**
	 * Clear all entries from this tree.
	 */
	public void clear() {
		root = null;
		size = 0;
	}

	/**
	 * Convenience method to compare two values either by use of the
	 * Comparator, if not null, or by casting to Comparable otherwise. This
	 * method may result in a ClassCastException if the tree holds
	 * non-Comparable values and no Comparator was specified upon
	 * construction.
	 *
	 * @param val1 the lhs of the compare operation.
	 * @param val2 the rhs of the compare operation.
	 * @return a negative integer, zero, or a positive integer depending
	 * upon whether val1 is less than, equal to, or greater than val2,
	 * respectively.
	 */
	private int compare(T val1, T val2) {
		return comparator == null ? ((Comparable) val1).compareTo(val2)
			: comparator.compare(val1, val2);
	}

	/**
	 * Test whether or not the specified value is an element of this tree.
	 *
	 * @param value the query value.
	 * @return true if the specified value is an element of this tree, false
	 * otherwise.
	 */
	public boolean contains(T value) {
		return getNode(value) != null;
	}

	/**
	 * Create a new node with the specified value. This method allows a
	 * subclass to assert control over the type of the nodes held by this
	 * tree.
	 *
	 * @param value the value to apply to the new node.
	 * @return a new node holding the specified value.
	 */
	protected Node<T> createNewNode(T value) {
		return new Node<T>(value);
	}

	/**
	 * Delete the specified value from this tree.
	 *
	 * @param value the value to delete.
	 * @return the node which contained the value before it was removed from
	 * this tree, null if the value is not an element of this tree.
	 */
	public Node<T> delete(T value) {
		if (value == null) {
			return null;
		}
		Node<T> node = getNode(value);
		if (node == null) {
			return null;
		}
		Node<T> swap;
		if (!(node.getLeft() == null || node.getRight() == null)) {
			Node<T> successor = getSuccessor(node);
			exchangeValues(node, successor);
			node = successor;
		}
		if (node.getLeft() != null) {
			swap = node.getLeft();
		} else {
			swap = node.getRight();
		}
		if (swap != null) {
			swap.setParent(node.getParent());
		}
		if (node.getParent() == null) {
			root = swap;
		} else if (node == node.getParent().getLeft()) {
			node.getParent().setLeft(swap);
		} else {
			node.getParent().setRight(swap);
		}
		if (node.getColor() == NodeColor.BLACK) {
			if (root != null) {
				fixAfterDeletion(swap == null ? node : swap);
			}
		}

		size--;

		return node;
	}

	/**
	 * Called by {@link #delete(Node)} when the node to be removed is a
	 * leaf. In this case, the node's value is exchanged with its successor
	 * as per the typical binary tree node removal operation. This method
	 * allows a subclass to influence value exchange behavior (e.g. if
	 * additional node information needs to be exchanged).
	 *
	 * @param node the node whose value is to be removed.
	 * @param successor the node to actually be removed.
	 */
	protected void exchangeValues(Node<T> node, Node<T> successor) {
		T tempValue = successor.getValue();
		successor.setValue(node.getValue());
		node.setValue(tempValue);
	}

	/**
	 * Re-balance the tree after a delete operation.
	 *
	 * @param node the deleted node or the swap node.
	 *
	 * @see CLRS Introduction to Algorithms
	 */
	protected void fixAfterDeletion(Node<T> node) {
		while (node != root && getColor(node) == NodeColor.BLACK) {
			if (node == node.getParent().getLeft()
				|| (node.getParent().getRight() != null && node != node
				.getParent().getRight())) {
				Node<T> temp = node.getParent().getRight();
				if (getColor(temp) == NodeColor.RED) {
					setColor(temp, NodeColor.BLACK);
					setColor(node.getParent(), NodeColor.RED);
					leftRotate(node.getParent());
					temp = node.getParent().getRight();
				}
				if (getColor(temp.getLeft()) == NodeColor.BLACK
					&& getColor(temp.getRight()) == NodeColor.BLACK) {
					setColor(temp, NodeColor.RED);
					node = node.getParent();
				} else {
					if (getColor(temp.getRight()) == NodeColor.BLACK) {
						setColor(temp.getLeft(), NodeColor.BLACK);
						setColor(temp, NodeColor.RED);
						rightRotate(temp);
						temp = node.getParent().getRight();
					}
					setColor(temp, getColor(node.getParent()));
					setColor(node.getParent(), NodeColor.BLACK);
					setColor(temp.getRight(), NodeColor.BLACK);
					leftRotate(node.getParent());
					node = root;
				}
			} else {
				Node<T> temp = node.getParent().getLeft();
				if (getColor(temp) == NodeColor.RED) {
					setColor(temp, NodeColor.BLACK);
					setColor(node.getParent(), NodeColor.RED);
					rightRotate(node.getParent());
					temp = node.getParent().getLeft();
				}
				if (getColor(temp.getRight()) == NodeColor.BLACK
					&& getColor(temp.getLeft()) == NodeColor.BLACK) {
					setColor(temp, NodeColor.RED);
					node = node.getParent();
				} else {
					if (getColor(temp.getLeft()) == NodeColor.BLACK) {
						setColor(temp.getRight(), NodeColor.BLACK);
						setColor(temp, NodeColor.RED);
						leftRotate(temp);
						temp = node.getParent().getLeft();
					}
					setColor(temp, getColor(node.getParent()));
					setColor(node.getParent(), NodeColor.BLACK);
					setColor(temp.getLeft(), NodeColor.BLACK);
					rightRotate(node.getParent());
					node = root;
				}
			}
		}
		setColor(node, NodeColor.BLACK);
	}

	/**
	 * Re-balance the tree after an insert operation.
	 *
	 * @param node the inserted node.
	 *
	 * @see CLRS Introduction to Algorithms
	 */
	protected void fixAfterInsertion(Node<T> node) {
		while (getColor(node.getParent()) == NodeColor.RED) {
			if (node.getParent() == node.getParent().getParent().getLeft()) {
				Node<T> temp = node.getParent().getParent().getRight();
				if (getColor(temp) == NodeColor.RED) {
					setColor(node.getParent(), (NodeColor.BLACK));
					setColor(temp, NodeColor.BLACK);
					setColor(node.getParent().getParent(), NodeColor.RED);
					node = node.getParent().getParent();
				} else {
					if (node == node.getParent().getRight()) {
						node = node.getParent();
						leftRotate(node);
					}
					setColor(node.getParent(), NodeColor.BLACK);
					setColor(node.getParent().getParent(), NodeColor.RED);
					rightRotate(node.getParent().getParent());
				}
			} else {
				Node<T> temp = node.getParent().getParent().getLeft();
				if (getColor(temp) == NodeColor.RED) {
					setColor(node.getParent(), NodeColor.BLACK);
					setColor(temp, NodeColor.BLACK);
					setColor(node.getParent().getParent(), NodeColor.RED);
					node = node.getParent().getParent();
				} else {
					if (node == node.getParent().getLeft()) {
						node = node.getParent();
						rightRotate(node);
					}
					setColor(node.getParent(), NodeColor.BLACK);
					setColor(node.getParent().getParent(), NodeColor.RED);
					leftRotate(node.getParent().getParent());
				}
			}
		}
		setColor(root, NodeColor.BLACK);
	}

	/**
	 * Convenience method implementing the concept of a null-node leaf being
	 * black.
	 *
	 * @param node the node whose color is to be determined, null is
	 * interpreted as a null leaf and is assigned the color black.
	 * @return the color of the specified node.
	 */
	private NodeColor getColor(Node<T> node) {
		return (node == null) ? NodeColor.BLACK : node.getColor();
	}

	/**
	 * Get the node containing the smallest value held by this tree.
	 *
	 * @return the node containing the smallest value held by this tree.
	 */
	public Node<T> getFirstNode() {
		Node<T> result = root;

		if (result != null) {
			while (result.getLeft() != null) {
				result = result.getLeft();
			}
		}

		return result;
	}

	/**
	 * Get the node that holds the specified value.
	 *
	 * @param value the query value.
	 * @return the node that holds the specified value, null if none.
	 */
	public Node<T> getNode(T value) {
		if (value == null) {
			return null;
		}
		Node<T> node = root;
		while (node != null) {
			int delta = compare(node.getValue(), value);
			if (delta < 0) {
				node = node.getRight();
			} else if (delta > 0) {
				node = node.getLeft();
			} else {
				break;
			}
		}
		return node;
	}

	/**
	 * Get the predecessor of the specified node. The predecessor of a node
	 * n is the node with the largest value in the tree smaller than the
	 * value held by n.
	 *
	 * @see CLRS
	 */
	public Node<T> getPredecessor(Node<T> node) {
		if (node.getLeft() != null) {
			node = node.getLeft();
			while (node.getRight() != null) {
				node = node.getRight();
			}
			return node;
		}
		Node<T> temp = node.getParent();
		while (temp != null && node == temp.getLeft()) {
			node = temp;
			temp = temp.getParent();
		}
		return temp;
	}

	/**
	 * Get the root of this tree.
	 *
	 * @return the root of this tree.
	 */
	public Node<T> getRoot() {
		return root;
	}

	/**
	 * Get the number of elements contained within this tree.
	 *
	 * @return the number of elements contained within this tree.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Get the successor of the specified node. The successor of a node n is
	 * the node with the smallest value in the tree larger than the value
	 * held by n.
	 *
	 * @see CLRS
	 */
	public Node<T> getSuccessor(Node<T> node) {
		if (node.getRight() != null) {
			node = node.getRight();
			while (node.getLeft() != null) {
				node = node.getLeft();
			}
			return node;
		}
		Node<T> temp = node.getParent();
		while (temp != null && node == temp.getRight()) {
			node = temp;
			temp = temp.getParent();
		}
		return temp;
	}

	/**
	 * Insert the specified value into this tree.
	 *
	 * @param value the value to insert.
	 * @return the new node containing the specified value if the value was
	 * not already present in the tree, null otherwise.
	 */
	public Node<T> insert(T value) {
		Node<T> node = null;
		Node<T> parent = root;
		while (parent != null) {
			int delta = compare(parent.getValue(), value);
			if (delta < 0) {
				if (parent.getRight() == null) {
					node = createNewNode(value);
					parent.setRight(node);
					node.setParent(parent);
					parent = null;
				} else {
					parent = parent.getRight();
				}
			} else if (delta > 0) {
				if (parent.getLeft() == null) {
					node = createNewNode(value);
					parent.setLeft(node);
					node.setParent(parent);
					parent = null;
				} else {
					parent = parent.getLeft();
				}
			} else {
				parent.appendTail(value);
				return null;
			}
		}
		if (node == null) {
			node = createNewNode(value);
			root = node;
		}

		setColor(node, NodeColor.RED);
		fixAfterInsertion(node);
		size++;

		return node;
	}

	/**
	 * @return true if there are no items in this tree, false otherwise.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns an Iterator over the elements of this tree.
	 *
	 * @return an Iterator over the elements of this tree.
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private Node<T> cursor = getFirstNode();
			private T lastReturn;

			@Override
			public boolean hasNext() {
				return cursor != null;
			}

			@Override
			public T next() {
				if (cursor == null) {
					throw new NoSuchElementException();
				}
				lastReturn = cursor.getValue();
				cursor = getSuccessor(cursor);
				return lastReturn;
			}

			@Override
			public void remove() {
				if (lastReturn == null) {
					throw new NoSuchElementException();
				}
				T currentValue = cursor == null ? null : cursor.getValue();
				delete(lastReturn);
				cursor = currentValue == null ? null : getNode(currentValue);
				lastReturn = null;
			}
		};
	}

	/**
	 * Perform a left rotate operation on the specified node.
	 *
	 * @param node the node on which the left rotate operation will be
	 * performed.
	 *
	 * @see CLRS Introduction to Algorithms
	 */
	protected void leftRotate(Node<T> node) {
		Node<T> temp = node.getRight();
		node.setRight(temp.getLeft());
		if (temp.getLeft() != null) {
			temp.getLeft().setParent(node);
		}
		temp.setParent(node.getParent());
		if (node.getParent() == null) {
			root = temp;
		} else if (node == node.getParent().getLeft()) {
			node.getParent().setLeft(temp);
		} else {
			node.getParent().setRight(temp);
		}
		temp.setLeft(node);
		node.setParent(temp);
	}

	/**
	 * Perform a right rotate operation on the specified node.
	 *
	 * @param node the node on which a right rotate operation is to be
	 * performed.
	 *
	 * @see CLRS Introduction to Algorithms
	 */
	protected void rightRotate(Node<T> node) {
		Node<T> temp = node.getLeft();
		node.setLeft(temp.getRight());
		if (temp.getRight() != null) {
			temp.getRight().setParent(node);
		}
		temp.setParent(node.getParent());
		if (node.getParent() == null) {
			root = temp;
		} else if (node == node.getParent().getRight()) {
			node.getParent().setRight(temp);
		} else {
			node.getParent().setLeft(temp);
		}
		temp.setRight(node);
		node.setParent(temp);
	}

	/**
	 * Convenience method to set the color of the specified node to the
	 * specified color if the node is non-null.
	 *
	 * @param node the target node, possibly null.
	 * @param color the target color, non-null.
	 */
	private void setColor(Node<T> node, NodeColor color) {
		if (node != null) {
			node.setColor(color);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		if (!isEmpty()) {
			for (Iterator<T> i = iterator();;) {
				builder.append(i.next());
				if (i.hasNext()) {
					builder.append(", ");
				} else {
					break;
				}
			}
		}
		builder.append("}");
		return builder.toString();
	}

	/**
	 * A <em>red-black tree node</em> is a binary tree node augmented to
	 * hold an additional piece of information called the node's
	 * <em>color</em>. The domain of values from which a red-black tree
	 * node's color is assigned comprises <em>red</em> and <em>black</em>. A
	 * red-black tree requires that its nodes provide the ability to set the
	 * parent and children and to set the value stored by the node. This
	 * class encapsulates the concept of a red-black tree node.
	 */
	public static class Node<T> {

		private NodeColor color;
		private Node<T> left, right, parent;
		private T value;
		private List<T> tail;

		/**
		 * Construct a new node with the specified value.
		 *
		 * @param value the value to store in this node.
		 */
		public Node(T value) {
			if (value == null) {
				throw new NullPointerException("value is null");
			}

			this.value = value;
		}

		/**
		 * Get the color.
		 *
		 * @return the color, never null.
		 */
		public NodeColor getColor() {
			return color;
		}

		/**
		 * Get the left child.
		 *
		 * @return the left child, possibly null.
		 */
		public Node<T> getLeft() {
			return left;
		}

		/**
		 * Get the parent.
		 *
		 * @return the parent, possibly null.
		 */
		public Node<T> getParent() {
			return parent;
		}

		/**
		 * Get the right child.
		 *
		 * @return the right child, possibly null.
		 */
		public Node<T> getRight() {
			return right;
		}

		/**
		 * Get the value.
		 *
		 * @return the value.
		 */
		public T getValue() {
			return value;
		}

		/**
		 * Test whether or not this node is a leaf node.
		 *
		 * @return true if this node is a leaf node, false otherwise.
		 */
		public boolean isLeaf() {
			return left == null && right == null;
		}

		/**
		 * Set the color.
		 *
		 * @param color the color to set to this node.
		 */
		protected void setColor(NodeColor color) {
			this.color = color;
		}

		/**
		 * Set the left child.
		 *
		 * @param node the node to set as the left child of this node.
		 */
		protected void setLeft(Node<T> node) {
			this.left = node;
		}

		/**
		 * Set the parent.
		 *
		 * @param node the node to set as the parent of this node.
		 */
		protected void setParent(Node<T> node) {
			this.parent = node;
		}

		/**
		 * Set the right child.
		 *
		 * @param node the node to set as the right child of this node.
		 */
		protected void setRight(Node<T> node) {
			this.right = node;
		}

		/**
		 * Set the value.
		 *
		 * @param value the value to store in this node, must be
		 * non-null.
		 */
		protected void setValue(T value) {
			if (value == null) {
				throw new IllegalArgumentException("value is null");
			}
			this.value = value;
		}
		
		public void appendTail(T node) {
			if (tail == null) {
				tail = new ArrayList<T>(1);
			}
			tail.add(node);
		}
		
		public boolean hasTail() {
			return tail != null && !tail.isEmpty();
		}

		public List<T> getTail() {
			return tail;
		}
	}

	/**
	 * The domain of values from which a node's color is assigned.
	 */
	public static enum NodeColor {

		BLACK, RED
	}
}