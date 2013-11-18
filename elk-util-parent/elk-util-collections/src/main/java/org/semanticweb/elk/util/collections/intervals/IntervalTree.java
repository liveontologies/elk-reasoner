package org.semanticweb.elk.util.collections.intervals;
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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Balanced binary tree data structure capable of storing {@link Interval} keys
 * and multiple arbitrary values for them. Insertion and deletion are O(log n)
 * operations. Query is O((k+1)log n) operation, where k is the number of
 * intervals that are included by the query interval.
 * 
 * TODO support partially ordered elements
 *
 * @author Pospishnyi Oleksandr
 */
public class IntervalTree<K extends Interval<T>, V, T> {

	private Entry<K, V, T> root = null;
	private int size = 0;
	private final Comparator<T> comparator_;

	public IntervalTree(Comparator<T> c) {
		comparator_ = c;
	}

	/**
	 * Get the size of the interval tree.
	 *
	 * @return number of entries in the tree
	 */
	public int size() {
		return size;
	}

	/**
	 * Prints the tree to the writer.
	 * @param writer
	 * @throws IOException
	 */
	public void print(Writer writer) throws IOException {
		print(root, writer, 0);
	}
	
	private void print(Entry<K, V, T> node, Writer writer, int indent) throws IOException {
		if (node != null) {
			for (int i = 0; i < indent; i++) {
				writer.write("  ");
			}
			
			writer.write(node.toString() + "\n");
			print(node.left, writer, indent + 1);
			print(node.right, writer, indent + 1);
		}
	}

	/**
	 * Add an interval and an associated data value to the tree. If the
	 * entry with the specified key already exists in the tree, then the
	 * value will be appended to it, without replacing the previously stored
	 * value.
	 *
	 * @param key an {@link Interval} key
	 * @param value arbitrary data value
	 */
	public void add(K key, V value) {
		
		if (key == null) {
			throw new IllegalArgumentException("Adding an element with the empty key into the interval tree " + value);
		}
		
		Entry<K, V, T> t = root;
		int cmp;
		Entry<K, V, T> parent;
		
		if (t == null) {
			root = new Entry<K, V, T>(key, value,comparator_);
			size = 1;
			return;
		}
		
		Entry<K, V, T> e = new Entry<K, V, T>(key, value, comparator_);
		
		size++;
		
		do {
			parent = t;
			t.updateMax(e);
			cmp = key.compareTo(t.key);
			
			if (cmp < 0) {
				t = t.left;
			} else if (cmp > 0) {
				t = t.right;
			} else {
				t.setValue(value);
				return;
			}
		} while (t != null);
		
		e.setParent(parent);
		
		if (cmp < 0) {
			parent.left = e;
		} else {
			parent.right = e;
		}
		
		fixAfterInsertion(e);
	}

	/**
	 * Remove an interval from the tree
	 *
	 * @param key an {@link Interval} key
	 * @param value value to be deleted
	 * @return boolean flag, indicating the successfulness of the operation
	 */
	public boolean remove(K key, V value) {
		Entry<K, V, T> e = getEntry(key, value);
		
		if (e == null) {
			return false;
		}
		size--;
		boolean ret = e.removeValue(value);
		if (e.hasEmptyValue()) {
			deleteEntry(e);
		}
		return ret;
	}

	protected Entry<K, V, T> getEntry(K key, V value) {
		Comparable<? super K> k = (Comparable<? super K>) key;
		Entry<K, V, T> p = root;
		
		while (p != null) {
			int cmp = k.compareTo(p.key);
			if (cmp < 0) {
				p = p.left;
			} else if (cmp > 0) {
				p = p.right;
			} else {
				return p;
			}
		}
		return null;
	}

	/**
	 * @return all values stored in this interval tree
	 */
	public Collection<V> values() {
		ArrayList<V> ret = new ArrayList<V>(size);
		Entry<K, V, T> e = getFirstEntry();
		if (e != null) {
			ret.addAll(e.value);
			while ((e = successor(e)) != null) {
				ret.addAll(e.value);
			}
		}
		return ret;
	}

	private Entry<K, V, T> getFirstEntry() {
		Entry<K, V, T> p = root;
		if (p != null) {
			while (p.left != null) {
				p = p.left;
			}
		}
		return p;
	}

	/**
	 * search for all {@link Interval}s that include the following
	 * {@link Interval} and return all their associated data values
	 *
	 * @param i search {@link Interval}
	 * @return all data values from {@link Interval}s that include the
	 * {@link Interval} i
	 */
	public Collection<V> searchIncludes(Interval<T> i) {
		Collection<V> result = new ArrayList<V>();
		if (root != null) {
			search(root, i, result);
		}
		return result;
	}

	private boolean search(Entry<K, V, T> x, Interval<T> i, Collection<V> result) {
		if (x.left != null && IntervalUtils.compareUpperBounds(x.left.max, x.left.isMaxInclusive, i.getHigh(), i.isUpperInclusive(), comparator_) >= 0) { 
			//search the left subtree
			boolean fnd = search(x.left, i, result);
			if (x.key.subsumes(i)) {
				result.addAll(x.value);
				fnd = true;
			}
			if (fnd) {
				if (x.right != null && IntervalUtils.compareUpperBounds(x.right.max, x.right.isMaxInclusive, i.getHigh(), i.isUpperInclusive(), comparator_) >= 0) {
					return search(x.right, i, result);
				} else {
					return true;
				}
			}
		} else {
			if (x.key.subsumes(i)) {
				result.addAll(x.value);

				if (x.right != null && IntervalUtils.compareUpperBounds(x.right.max, x.right.isMaxInclusive, i.getHigh(), i.isUpperInclusive(), comparator_) >= 0) {
					//search the right subtree
					return search(x.right, i, result);
				} else {
					return true;
				}

			} else {
				if (x.right != null && IntervalUtils.compareUpperBounds(x.right.max, x.right.isMaxInclusive, i.getHigh(), i.isUpperInclusive(), comparator_) >= 0) {
					return search(x.right, i, result);
				}
			}
		}
		return false;
	}

	private void deleteEntry(Entry<K, V, T> z) {
		// If strictly internal, copy successor's element to z and then make z
		// point to successor.
		if (z.left != null && z.right != null) {
			Entry<K, V, T> s = successor(z);
			z.key = s.key;
			z.value = s.value;
			propagateMax(z);
			z = s;
		}

		// Start fixup at replacement node, if it exists.
		Entry<K, V, T> replacement = (z.left != null ? z.left : z.right);

		if (replacement != null) {
			// Link replacement to parent
			replacement.parent = z.parent;
			if (z.parent == null) {
				root = replacement;
			} else if (z == z.parent.left) {
				z.parent.left = replacement;
			} else {
				z.parent.right = replacement;
			}

			// Null out links so they are OK to use by fixAfterDeletion.
			z.left = z.right = z.parent = null;

			if (replacement.parent != null) {
				propagateMax(replacement.parent);
			}

			// Fix replacement
			if (z.color == BLACK) {
				fixAfterDeletion(replacement);
			}
		} else if (z.parent == null) { // return if we are the only node.
			root = null;
		} else { //  No children. Use self as phantom replacement and unlink.
			if (z.color == BLACK) {
				fixAfterDeletion(z);
			}

			if (z.parent != null) {
				if (z == z.parent.left) {
					z.parent.left = null;
				} else if (z == z.parent.right) {
					z.parent.right = null;
				}
				propagateMax(z.parent);
				z.parent = null;
			}
		}
	}

	private void propagateMax(Entry<K, V, T> z) {
		/*Endpoint<T> oldMax = z.max;
		
		z.updateMax();
		
		if (!oldMax.equals(z.max) && z.parent != null) {
			propagateMax(z.parent);
		}*/
		T oldMax = z.max;
		boolean oldIsMaxInclusive = z.isMaxInclusive;
		
		z.updateMax();
		
		if ((!oldMax.equals(z.max) || oldIsMaxInclusive != z.isMaxInclusive) && z.parent != null) {
			propagateMax(z.parent);
		}
	}
	/*
	 * Red-black mechanics
	 */
	private static final boolean RED = false;
	private static final boolean BLACK = true;

	/**
	 * Internal Entry class
	 * 
	 * TODO see if all fields are necessary. For example a more functional-style
	 * implementation may have the color as an immutable property and use
	 * sublclasses to avoid storing it.
	 */
	private static final class Entry<K extends Interval<T>, V, T> {

		K key;
		Collection<V> value;
		// this is always the upper bound
		// using a structured object here to contain both the value and whether
		// it's inclusive simplifies the code but is slower.
		T max;
		boolean isMaxInclusive;
		Entry<K, V, T> left = null;
		Entry<K, V, T> right = null;
		Entry<K, V, T> parent;
		boolean color = BLACK;
		final Comparator<T> comparator_;

		Entry(K key, V value, Comparator<T> c) {
			this.key = key;
			this.value = Collections.singletonList(value);
			this.max = key.getHigh();
			this.isMaxInclusive = key.isUpperInclusive();
			this.comparator_ = c;
		}

		public void setParent(Entry<K, V, T> parent) {
			this.parent = parent;
		}

		public void setValue(V v) {
			try {
				value.add(v);
			} catch (UnsupportedOperationException e) {
				this.value = new ArrayList<V>(value);
				this.value.add(v);
			}
		}

		public boolean removeValue(V v) {
			try {
				return value.remove(v);
			} catch (UnsupportedOperationException e) {
				this.value = null;
				return true;
			}
		}

		public boolean hasEmptyValue() {
			return this.value == null || this.value.isEmpty();
		}

		private static int LEFT = -1;
		private static int RIGHT = 1;
		
		protected void updateMax(Entry<K, V, T> e) {
			//this.max = maxOf(e.max, this.max);
			
			int cmp = maxOf(e.max, e.isMaxInclusive, max, isMaxInclusive);
			
			if (cmp == LEFT && e != null) {
				max = e.max;
				isMaxInclusive = e.isMaxInclusive;
			}
		}

		/*
		 * this function updates the max value and whether it's inclusive based on the corresponding values in the children nodes.
		 * it's ugly because we do not use any structured objects to return both the max value and its isInclusive flag from the maxOf method.
		 */
		protected void updateMax() {
			T leftMax = left != null ? left.max : null;
			boolean leftMaxInclusive = left != null ? left.isMaxInclusive : false; 
			T rightMax = right != null ? right.max : null;
			boolean rightMaxInclusive = right != null ? right.isMaxInclusive : false;
			// compare children first
			int leftRightCmp = maxOf(leftMax, leftMaxInclusive, rightMax, rightMaxInclusive);
			
			if (leftRightCmp == LEFT) {
				//the left is bigger, compare it to the key
				int cmp = maxOf(leftMax, leftMaxInclusive, key.getHigh(), key.isUpperInclusive());
				
				if (cmp == LEFT) {
					max = leftMax;
					isMaxInclusive = leftMaxInclusive;
				}
				else {
					max = key.getHigh();
					isMaxInclusive = key.isUpperInclusive();
				}
			}
			else {
				//the right is bigger, compare it to the key
				int cmp = maxOf(rightMax, rightMaxInclusive, key.getHigh(), key.isUpperInclusive());
				
				if (cmp == LEFT) {
					max = rightMax;
					isMaxInclusive = rightMaxInclusive;
				}
				else {
					max = key.getHigh();
					isMaxInclusive = key.isUpperInclusive();
				}
			}
		}
		
		protected int maxOf(T t1, boolean t1maxInclusive, T t2, boolean t2maxInclusive) {
			if (t1 == null) {
				return RIGHT;
			}
			else if (t2 == null) {
				return LEFT;
			}
			else {
				return IntervalUtils.compareUpperBounds(t1, t1maxInclusive, t2, t2maxInclusive, comparator_) > 0 ? LEFT : RIGHT;
			}
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Entry)) {
				return false;
			}
			Entry<?, ?, ?> e = (Entry<?, ?, ?>) o;

			return valEquals(key, e.key) && valEquals(value, e.value);
		}

		@Override
		public int hashCode() {
			int keyHash = (key == null ? 0 : key.hashCode());
			int valueHash = (value == null ? 0 : value.hashCode());
			return keyHash ^ valueHash;
		}

		@Override
		public String toString() {
			return key + "=" + value;// + ", max = " + max;
		}
	}

	private void fixAfterInsertion(Entry<K, V, T> z) {
		z.color = RED;

		while (z != null && z != root && z.parent.color == RED) {
			if (parentOf(z) == leftOf(parentOf(parentOf(z)))) {
				Entry<K, V, T> y = rightOf(parentOf(parentOf(z)));
				
				if (colorOf(y) == RED) {
					setColor(parentOf(z), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(z)), RED);
					z = parentOf(parentOf(z));
				} else {
					if (z == rightOf(parentOf(z))) {
						z = parentOf(z);
						rotateLeft(z);
					}
					setColor(parentOf(z), BLACK);
					setColor(parentOf(parentOf(z)), RED);
					rotateRight(parentOf(parentOf(z)));
				}
			} else {
				Entry<K, V, T> y = leftOf(parentOf(parentOf(z)));
				
				if (colorOf(y) == RED) {
					setColor(parentOf(z), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(z)), RED);
					z = parentOf(parentOf(z));
				} else {
					if (z == leftOf(parentOf(z))) {
						z = parentOf(z);
						rotateRight(z);
					}
					setColor(parentOf(z), BLACK);
					setColor(parentOf(parentOf(z)), RED);
					rotateLeft(parentOf(parentOf(z)));
				}
			}
		}
		root.color = BLACK;
	}

	private void fixAfterDeletion(Entry<K, V, T> z) {
		while (z != root && colorOf(z) == BLACK) {
			if (z == leftOf(parentOf(z))) {
				Entry<K, V, T> sib = rightOf(parentOf(z));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(z), RED);
					rotateLeft(parentOf(z));
					sib = rightOf(parentOf(z));
				}

				if (colorOf(leftOf(sib)) == BLACK
					&& colorOf(rightOf(sib)) == BLACK) {
					setColor(sib, RED);
					z = parentOf(z);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						setColor(leftOf(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = rightOf(parentOf(z));
					}
					setColor(sib, colorOf(parentOf(z)));
					setColor(parentOf(z), BLACK);
					setColor(rightOf(sib), BLACK);
					rotateLeft(parentOf(z));
					z = root;
				}
			} else { // symmetric
				Entry<K, V, T> sib = leftOf(parentOf(z));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(z), RED);
					rotateRight(parentOf(z));
					sib = leftOf(parentOf(z));
				}

				if (colorOf(rightOf(sib)) == BLACK
					&& colorOf(leftOf(sib)) == BLACK) {
					setColor(sib, RED);
					z = parentOf(z);
				} else {
					if (colorOf(leftOf(sib)) == BLACK) {
						setColor(rightOf(sib), BLACK);
						setColor(sib, RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(z));
					}
					setColor(sib, colorOf(parentOf(z)));
					setColor(parentOf(z), BLACK);
					setColor(leftOf(sib), BLACK);
					rotateRight(parentOf(z));
					z = root;
				}
			}
		}

		setColor(z, BLACK);
	}

	/**
	 * Balancing operations.
	 */
	private boolean colorOf(Entry<K, V, T> p) {
		return (p == null ? BLACK : p.color);
	}

	private Entry<K, V, T> parentOf(Entry<K, V, T> p) {
		return (p == null ? null : p.parent);
	}

	private void setColor(Entry<K, V, T> p, boolean c) {
		if (p != null) {
			p.color = c;
		}
	}

	private Entry<K, V, T> leftOf(Entry<K, V, T> p) {
		return (p == null) ? null : p.left;
	}

	private Entry<K, V, T> rightOf(Entry<K, V, T> p) {
		return (p == null) ? null : p.right;
	}

	private void rotateLeft(Entry<K, V, T> x) {
		if (x != null) {
			Entry<K, V, T> y = x.right;
			x.right = y.left;
			if (y.left != null) {
				y.left.parent = x;
			}
			y.parent = x.parent;
			if (x.parent == null) {
				root = y;
			} else if (x.parent.left == x) {
				x.parent.left = y;
			} else {
				x.parent.right = y;
			}
			y.left = x;
			x.parent = y;
			x.updateMax();
			y.updateMax();
		}
	}

	private void rotateRight(Entry<K, V, T> x) {
		if (x != null) {
			Entry<K, V, T> y = x.left;
			x.left = y.right;
			if (y.right != null) {
				y.right.parent = x;
			}
			y.parent = x.parent;
			if (x.parent == null) {
				root = y;
			} else if (x.parent.right == x) {
				x.parent.right = y;
			} else {
				x.parent.left = y;
			}
			y.right = x;
			x.parent = y;
			y.updateMax();
			x.updateMax();
		}
	}

	Entry<K, V, T> successor(Entry<K, V, T> t) {
		if (t == null) {
			return null;
		} else if (t.right != null) {
			Entry<K, V, T> p = t.right;
			while (p.left != null) {
				p = p.left;
			}
			return p;
		} else {
			Entry<K, V, T> p = t.parent;
			Entry<K, V, T> ch = t;
			
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	static boolean valEquals(Object o1, Object o2) {
		return (o1 == null ? o2 == null : o1.equals(o2));
	}

	/*
	 * Printing operations
	 */
	public void print(int spread) {
		printHelper(root, 0, spread);
	}

	private void printHelper(Entry<K, V, T> n, int indent, int spread) {
		if (n == null) {
			System.out.print("<nil>");
			return;
		}
		if (n.right != null) {
			printHelper(n.right, indent + spread, spread);
		}
		for (int i = 0; i < indent; i++) {
			System.out.print("  ");
		}
		if (n.color == BLACK) {
			System.out.println("\u001B[30m" + n.key + ':' + n.max);
		} else {
			System.out.println("\u001B[31m" + n.key + ':' + n.max);
		}
		if (n.left != null) {
			printHelper(n.left, indent + spread, spread);
		}
	}
}
