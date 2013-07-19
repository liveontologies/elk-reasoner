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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Balanced binary tree data structure capable of storing {@link Interval} keys
 * and multiple arbitrary values for them. Insertion and deletion are O(log n)
 * operations. Query is O((k+1)log n) operation, where k is the number of intervals
 * that are included by the query interval.
 *
 * @author Pospishnyi Oleksandr
 */
public class IntervalTree<K extends Interval, V> {

	private Entry<K, V> root = null;
	private int size = 0;

	public IntervalTree() {
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
	 * Add an interval and an associated data value to the tree. If the
	 * entry with the specified key already exists in the tree, then the
	 * value will be appended to it, without replacing the previously stored
	 * value.
	 *
	 * @param key an {@link Interval} key
	 * @param value arbitrary data value
	 */
	public void add(K key, V value) {
		Entry<K, V> t = root;
		if (t == null) {
			root = new Entry<K, V>(key, value, null);
			size = 1;
			return;
		}
		int cmp;
		Entry<K, V> parent;
		if (key == null) {
			throw new NullPointerException();
		}
		Comparable<? super K> k = (Comparable<? super K>) key;
		do {
			parent = t;
			cmp = k.compareTo(t.key);
			if (cmp < 0) {
				t = t.left;
			} else if (cmp > 0) {
				t = t.right;
			} else {
				t.setValue(value);
				return;
			}
		} while (t != null);
		Entry<K, V> e = new Entry<K, V>(key, value, parent);
		if (cmp < 0) {
			parent.left = e;
		} else {
			parent.right = e;
		}
		fixAfterInsertion(e);
		size++;
	}

	/**
	 * Remove an interval from the tree
	 *
	 * @param key an {@link Interval} key
	 * @param value value to be deleted
	 * @return boolean flag, indicating the successfulness of the operation
	 */
	public boolean remove(K key, V value) {
		Entry<K, V> e = getEntry(key, value);
		if (e == null) {
			return false;
		}
		boolean ret = e.value.remove(value);
		if (e.value.isEmpty()) {
			deleteEntry(e);
		}
		return ret;
	}

	protected Entry<K, V> getEntry(K key, V value) {
		Comparable<? super K> k = (Comparable<? super K>) key;
		Entry<K, V> p = root;
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
	 * search for all {@link Interval}s that include the following
	 * {@link Interval} and return all their associated data values
	 *
	 * @param i search {@link Interval}
	 * @return all data values from {@link Interval}s that include the
	 * {@link Interval} i
	 */
	public Collection<V> searchIncludes(Interval i) {
		Collection<V> result = new ArrayList<V>();
		if (root != null) {
			search(root, i, result);
		}
		return result;
	}

	private boolean search(Entry<K, V> x, Interval i, Collection<V> result) {
		if (x.left != null && x.left.max.compareTo(i.getHigh()) >= 0) {
			boolean fnd = search(x.left, i, result);
			if (x.key.contains(i)) {
				result.addAll(x.value);
				fnd = true;
			}
			if (fnd) {
				if (x.right != null && x.right.max.compareTo(i.getHigh()) >= 0) {
					return search(x.right, i, result);
				} else {
					return true;
				}
			}
		} else {
			if (x.key.contains(i)) {
				result.addAll(x.value);

				if (x.right != null && x.right.max.compareTo(i.getHigh()) >= 0) {
					return search(x.right, i, result);
				} else {
					return true;
				}

			} else {
				if (x.right != null && x.right.max.compareTo(i.getHigh()) >= 0) {
					return search(x.right, i, result);
				}
			}
		}
		return false;
	}

	private void deleteEntry(Entry<K, V> z) {
		size--;

		boolean propagate = false;
		Entry<K, V> propagateStart = null;

		// If strictly internal, copy successor's element to p and then make p
		// point to successor.
		if (z.left != null && z.right != null) {
			Entry<K, V> s = successor(z);
			z.key = s.key;
			z.value = s.value;
			z = s;
		} // p has 2 children

		// Start fixup at replacement node, if it exists.
		Entry<K, V> replacement = (z.left != null ? z.left : z.right);

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
				propagate = z.parent.update();
				propagateStart = z.parent;
				z.parent = null;
			}
		}

		while (propagate && (propagateStart != root)) {
			propagateStart = parentOf(propagateStart);
			propagate = propagateStart.update();
		}
	}
	/*
	 * Red-black mechanics
	 */
	private static final boolean RED = false;
	private static final boolean BLACK = true;

	/*
	 * Internal Entry class
	 */
	protected static final class Entry<K extends Interval, V> {

		K key;
		Collection<V> value;
		Comparable max;
		Entry<K, V> left = null;
		Entry<K, V> right = null;
		Entry<K, V> parent;
		boolean color = BLACK;

		Entry(K key, V value, Entry<K, V> parent) {
			this.key = key;
			this.value = Arrays.asList(value);
			this.parent = parent;
		}

		public Collection<V> getValues() {
			return value;
		}

		public void setValue(V v) {
			try {
				value.add(v);
			} catch (UnsupportedOperationException e) {
				this.value = new ArrayList<V>(value);
				this.value.add(v);
			}
		}

		public boolean update() {
			Comparable newMax;

			newMax = maxOf(left != null ? left.max : null, right != null ? right.max : null, key.getHigh());

			if (max != newMax) {
				max = newMax;
				return true;
			}
			return false;
		}

		protected static <T extends Comparable<T>> T maxOf(T... ts) {
			T max = null;
			for (T t : ts) {
				if (t != null && (max == null || t.compareTo(max) > 0)) {
					max = t;
				}
			}
			return max;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Entry)) {
				return false;
			}
			Entry<?, ?> e = (Entry<?, ?>) o;

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
			return key + "=" + value;
		}
	}

	private void fixAfterInsertion(Entry<K, V> z) {
		z.color = RED;

		boolean propagate = z.update();
		Entry<K, V> propagateStart = z;

		while (z != null && z != root && z.parent.color == RED) {
			if (parentOf(z) == leftOf(parentOf(parentOf(z)))) {
				Entry<K, V> y = rightOf(parentOf(parentOf(z)));
				if (colorOf(y) == RED) {
					setColor(parentOf(z), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(z)), RED);
					parentOf(z).update();
					z = parentOf(parentOf(z));
					propagate = z.update();
					propagateStart = z;
				} else {
					if (z == rightOf(parentOf(z))) {
						z = parentOf(z);
						rotateLeft(z);
					}
					setColor(parentOf(z), BLACK);
					setColor(parentOf(parentOf(z)), RED);
					propagate = rotateRight(parentOf(parentOf(z)));
					propagateStart = parentOf(z);
				}
			} else {
				Entry<K, V> y = leftOf(parentOf(parentOf(z)));
				if (colorOf(y) == RED) {
					setColor(parentOf(z), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(z)), RED);
					parentOf(z).update();
					z = parentOf(parentOf(z));
					propagate = z.update();
					propagateStart = z;
				} else {
					if (z == leftOf(parentOf(z))) {
						z = parentOf(z);
						rotateRight(z);
					}
					setColor(parentOf(z), BLACK);
					setColor(parentOf(parentOf(z)), RED);
					propagate = rotateLeft(parentOf(parentOf(z)));
					propagateStart = parentOf(z);
				}
			}
		}

		while (propagate && (propagateStart != root)) {
			propagateStart = parentOf(propagateStart);
			propagate = propagateStart.update();
		}

		root.color = BLACK;
	}

	private void fixAfterDeletion(Entry<K, V> z) {

		boolean propagate = z.update();
		Entry<K, V> propagateStart = z;

		while (z != root && colorOf(z) == BLACK) {
			if (z == leftOf(parentOf(z))) {
				Entry<K, V> sib = rightOf(parentOf(z));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(z), RED);
					propagate = rotateLeft(parentOf(z));
					propagateStart = parentOf(z);
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
					propagate = rotateLeft(parentOf(z));
					propagateStart = parentOf(z);
					z = root;
				}
			} else { // symmetric
				Entry<K, V> sib = leftOf(parentOf(z));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(z), RED);
					propagate = rotateRight(parentOf(z));
					propagateStart = parentOf(z);
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
					propagate = rotateRight(parentOf(z));
					propagateStart = parentOf(z);
					z = root;
				}
			}
		}

		while (propagate && (propagateStart != root)) {
			propagateStart = parentOf(propagateStart);
			propagate = propagateStart.update();
		}

		setColor(z, BLACK);
	}

	/**
	 * Balancing operations.
	 */
	private boolean colorOf(Entry<K, V> p) {
		return (p == null ? BLACK : p.color);
	}

	private Entry<K, V> parentOf(Entry<K, V> p) {
		return (p == null ? null : p.parent);
	}

	private void setColor(Entry<K, V> p, boolean c) {
		if (p != null) {
			p.color = c;
		}
	}

	private Entry<K, V> leftOf(Entry<K, V> p) {
		return (p == null) ? null : p.left;
	}

	private Entry<K, V> rightOf(Entry<K, V> p) {
		return (p == null) ? null : p.right;
	}

	private boolean rotateLeft(Entry<K, V> p) {
		Entry<K, V> r = p.right;
		p.right = r.left;
		if (r.left != null) {
			r.left.parent = p;
		}
		r.parent = p.parent;
		if (p.parent == null) {
			root = r;
		} else if (p.parent.left == p) {
			p.parent.left = r;
		} else {
			p.parent.right = r;
		}
		r.left = p;
		p.parent = r;
		boolean res = p.update();
		res = r.update() || res;
		return res;
	}

	private boolean rotateRight(Entry<K, V> p) {
		Entry<K, V> l = p.left;
		p.left = l.right;
		if (l.right != null) {
			l.right.parent = p;
		}
		l.parent = p.parent;
		if (p.parent == null) {
			root = l;
		} else if (p.parent.right == p) {
			p.parent.right = l;
		} else {
			p.parent.left = l;
		}
		l.right = p;
		p.parent = l;
		boolean res = p.update();
		res = l.update() || res;
		return res;
	}

	Entry<K, V> successor(Entry<K, V> t) {
		if (t == null) {
			return null;
		} else if (t.right != null) {
			Entry<K, V> p = t.right;
			while (p.left != null) {
				p = p.left;
			}
			return p;
		} else {
			Entry<K, V> p = t.parent;
			Entry<K, V> ch = t;
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

	private void printHelper(Entry<K, V> n, int indent, int spread) {
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
			System.out.println("\u001B[30m" + n.key + n.value);
		} else {
			System.out.println("\u001B[31m" + n.key + n.value);
		}
		if (n.left != null) {
			printHelper(n.left, indent + spread, spread);
		}
	}
}
