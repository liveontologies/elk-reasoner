package org.semanticweb.elk.reasoner.datatypes.index.itree;


import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author Pospishnyi Oleksandr
 */
public class RedBlackTree<K, V> {

	private final Comparator<? super K> comparator;
	private transient Entry<K, V> root = null;
	private transient int size = 0;

	public RedBlackTree() {
		comparator = null;
	}

	public RedBlackTree(Comparator<? super K> comparator) {
		this.comparator = comparator;
	}

	public int size() {
		return size;
	}

	public V put(K key, V value) {
		Entry<K, V> t = root;
		if (t == null) {
			compare(key, key); // type (and possibly null) check

			root = new Entry<K, V>(key, value, null);
			size = 1;
			return null;
		}
		int cmp;
		Entry<K, V> parent;
		// split comparator and comparable paths
		Comparator<? super K> cpr = comparator;
		if (cpr != null) {
			do {
				parent = t;
				cmp = cpr.compare(key, t.key);
				if (cmp < 0) {
					t = t.left;
				} else if (cmp > 0) {
					t = t.right;
				} else {
					return t.setValue(value);
				}
			} while (t != null);
		} else {
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
					return t.setValue(value);
				}
			} while (t != null);
		}
		Entry<K, V> e = new Entry<K, V>(key, value, parent);
		if (cmp < 0) {
			parent.left = e;
		} else {
			parent.right = e;
		}
		fixAfterInsertion(e);
		size++;
		return null;
	}

	public V remove(Object key) {
		Entry<K, V> p = getEntry(key);
		if (p == null) {
			return null;
		}

		V oldValue = p.value;
		deleteEntry(p);
		return oldValue;
	}

	final Entry<K, V> getEntry(Object key) {
		if (comparator != null) {
			return getEntryUsingComparator(key);
		}
		if (key == null) {
			throw new NullPointerException();
		}
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

	final Entry<K, V> getEntryUsingComparator(Object key) {
		K k = (K) key;
		Comparator<? super K> cpr = comparator;
		if (cpr != null) {
			Entry<K, V> p = root;
			while (p != null) {
				int cmp = cpr.compare(k, p.key);
				if (cmp < 0) {
					p = p.left;
				} else if (cmp > 0) {
					p = p.right;
				} else {
					return p;
				}
			}
		}
		return null;
	}

	private void deleteEntry(Entry<K, V> p) {
		size--;

		// If strictly internal, copy successor's element to p and then make p
		// point to successor.
		if (p.left != null && p.right != null) {
			Entry<K, V> s = successor(p);
			p.key = s.key;
			p.value = s.value;
			p = s;
		} // p has 2 children

		// Start fixup at replacement node, if it exists.
		Entry<K, V> replacement = (p.left != null ? p.left : p.right);

		if (replacement != null) {
			// Link replacement to parent
			replacement.parent = p.parent;
			if (p.parent == null) {
				root = replacement;
			} else if (p == p.parent.left) {
				p.parent.left = replacement;
			} else {
				p.parent.right = replacement;
			}

			// Null out links so they are OK to use by fixAfterDeletion.
			p.left = p.right = p.parent = null;

			// Fix replacement
			if (p.color == BLACK) {
				fixAfterDeletion(replacement);
			}
		} else if (p.parent == null) { // return if we are the only node.
			root = null;
		} else { //  No children. Use self as phantom replacement and unlink.
			if (p.color == BLACK) {
				fixAfterDeletion(p);
			}

			if (p.parent != null) {
				if (p == p.parent.left) {
					p.parent.left = null;
				} else if (p == p.parent.right) {
					p.parent.right = null;
				}
				p.parent = null;
			}
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
	static final class Entry<K, V> implements Map.Entry<K, V> {

		K key;
		V value;
		Entry<K, V> left = null;
		Entry<K, V> right = null;
		Entry<K, V> parent;
		boolean color = BLACK;

		Entry(K key, V value, Entry<K, V> parent) {
			this.key = key;
			this.value = value;
			this.parent = parent;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;

			return valEquals(key, e.getKey()) && valEquals(value, e.getValue());
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

	private void fixAfterInsertion(Entry<K, V> x) {
		x.color = RED;

		while (x != null && x != root && x.parent.color == RED) {
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				Entry<K, V> y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == rightOf(parentOf(x))) {
						x = parentOf(x);
						rotateLeft(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateRight(parentOf(parentOf(x)));
				}
			} else {
				Entry<K, V> y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateLeft(parentOf(parentOf(x)));
				}
			}
		}
		root.color = BLACK;
	}

	private void fixAfterDeletion(Entry<K, V> x) {
		while (x != root && colorOf(x) == BLACK) {
			if (x == leftOf(parentOf(x))) {
				Entry<K, V> sib = rightOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x));
				}

				if (colorOf(leftOf(sib)) == BLACK
					&& colorOf(rightOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						setColor(leftOf(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = rightOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(rightOf(sib), BLACK);
					rotateLeft(parentOf(x));
					x = root;
				}
			} else { // symmetric
				Entry<K, V> sib = leftOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateRight(parentOf(x));
					sib = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(sib)) == BLACK
					&& colorOf(leftOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(leftOf(sib)) == BLACK) {
						setColor(rightOf(sib), BLACK);
						setColor(sib, RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(leftOf(sib), BLACK);
					rotateRight(parentOf(x));
					x = root;
				}
			}
		}

		setColor(x, BLACK);
	}

	/**
	 * Balancing operations.
	 */
	private static <K, V> boolean colorOf(Entry<K, V> p) {
		return (p == null ? BLACK : p.color);
	}

	private static <K, V> Entry<K, V> parentOf(Entry<K, V> p) {
		return (p == null ? null : p.parent);
	}

	private static <K, V> void setColor(Entry<K, V> p, boolean c) {
		if (p != null) {
			p.color = c;
		}
	}

	private static <K, V> Entry<K, V> leftOf(Entry<K, V> p) {
		return (p == null) ? null : p.left;
	}

	private static <K, V> Entry<K, V> rightOf(Entry<K, V> p) {
		return (p == null) ? null : p.right;
	}

	private void rotateLeft(Entry<K, V> p) {
		if (p != null) {
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
		}
	}

	private void rotateRight(Entry<K, V> p) {
		if (p != null) {
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
		}
	}

	static <K, V> Entry<K, V> successor(Entry<K, V> t) {
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

	/*
	 * Utility methods
	 */
	final int compare(Object k1, Object k2) {
		return comparator == null ? ((Comparable<? super K>) k1).compareTo((K) k2)
			: comparator.compare((K) k1, (K) k2);
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
			printHelper(n.right, indent + spread, spread );
		}
		for (int i = 0; i < indent; i++) {
			System.out.print("  ");
		}
		if (n.color == BLACK) {
			System.out.println("\u001B[30m" + n.key);
		} else {
			System.out.println("\u001B[31m" + n.key);
		}
		if (n.left != null) {
			printHelper(n.left, indent + spread, spread);
		}
	}
}
