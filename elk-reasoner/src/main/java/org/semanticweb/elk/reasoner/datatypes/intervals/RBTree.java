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
 * <P> This class implements a Red-Black tree as described in Cormen, Leiserson,
 * Rivest, <I>Introduction to Algorithms</I>, MIT Press: Cambridge, MA, 1990.
 * </P>
 *
 */
public class RBTree {

	private RBNode root;
	private Comparator comparator;

	public RBTree(Comparator comparator) {
		this.comparator = comparator;
	}

	public RBNode getRoot() {
		return root;
	}

	protected void insertNode(RBNode x) {
		treeInsert(x);

		x.setColor(RBColor.RED);
		boolean shouldPropagate = x.update();

		RBNode propagateStart = x;

		// Loop invariant: x has been updated.
		while ((x != root) && (x.getParent().getColor() == RBColor.RED)) {
			if (x.getParent() == x.getParent().getParent().getLeft()) {
				RBNode y = x.getParent().getParent().getRight();
				if ((y != null) && (y.getColor() == RBColor.RED)) {
					// Case 1
					x.getParent().setColor(RBColor.BLACK);
					y.setColor(RBColor.BLACK);
					x.getParent().getParent().setColor(RBColor.RED);
					x.getParent().update();
					x = x.getParent().getParent();
					shouldPropagate = x.update();
					propagateStart = x;
				} else {
					if (x == x.getParent().getRight()) {
						// Case 2
						x = x.getParent();
						leftRotate(x);
					}
					// Case 3
					x.getParent().setColor(RBColor.BLACK);
					x.getParent().getParent().setColor(RBColor.RED);
					shouldPropagate = rightRotate(x.getParent().getParent());
					propagateStart = x.getParent();
				}
			} else {
				// Same as then clause with "right" and "left" exchanged
				RBNode y = x.getParent().getParent().getLeft();
				if ((y != null) && (y.getColor() == RBColor.RED)) {
					// Case 1
					x.getParent().setColor(RBColor.BLACK);
					y.setColor(RBColor.BLACK);
					x.getParent().getParent().setColor(RBColor.RED);
					x.getParent().update();
					x = x.getParent().getParent();
					shouldPropagate = x.update();
					propagateStart = x;
				} else {
					if (x == x.getParent().getLeft()) {
						// Case 2
						x = x.getParent();
						rightRotate(x);
					}
					// Case 3
					x.getParent().setColor(RBColor.BLACK);
					x.getParent().getParent().setColor(RBColor.RED);
					shouldPropagate = leftRotate(x.getParent().getParent());
					propagateStart = x.getParent();
				}
			}
		}

		while (shouldPropagate && (propagateStart != root)) {
			propagateStart = propagateStart.getParent();
			shouldPropagate = propagateStart.update();
		}

		root.setColor(RBColor.BLACK);
	}

	protected Object getNodeValue(RBNode node) {
		return node.getData();
	}

	private void treeInsert(RBNode z) {
		RBNode y = null;
		RBNode x = root;

		while (x != null) {
			y = x;
			if (comparator.compare(getNodeValue(z), getNodeValue(x)) < 0) {
				x = x.getLeft();
			} else {
				x = x.getRight();
			}
		}
		z.setParent(y);
		if (y == null) {
			root = z;
		} else {
			if (comparator.compare(getNodeValue(z), getNodeValue(y)) < 0) {
				y.setLeft(z);
			} else {
				y.setRight(z);
			}
		}
	}

	private boolean leftRotate(RBNode x) {
		// Set y.
		RBNode y = x.getRight();
		// Turn y's left subtree into x's right subtree.
		x.setRight(y.getLeft());
		if (y.getLeft() != null) {
			y.getLeft().setParent(x);
		}
		// Link x's parent to y.
		y.setParent(x.getParent());
		if (x.getParent() == null) {
			root = y;
		} else {
			if (x == x.getParent().getLeft()) {
				x.getParent().setLeft(y);
			} else {
				x.getParent().setRight(y);
			}
		}
		// Put x on y's left.
		y.setLeft(x);
		x.setParent(y);
		// Update nodes in appropriate order (lowest to highest)
		boolean res = x.update();
		res = y.update() || res;
		return res;
	}

	private boolean rightRotate(RBNode y) {
		// Set x.
		RBNode x = y.getLeft();
		// Turn x's right subtree into y's left subtree.
		y.setLeft(x.getRight());
		if (x.getRight() != null) {
			x.getRight().setParent(y);
		}
		// Link y's parent into x.
		x.setParent(y.getParent());
		if (y.getParent() == null) {
			root = x;
		} else {
			if (y == y.getParent().getLeft()) {
				y.getParent().setLeft(x);
			} else {
				y.getParent().setRight(x);
			}
		}
		// Put y on x's right.
		x.setRight(y);
		y.setParent(x);
		// Update nodes in appropriate order (lowest to highest)
		boolean res = y.update();
		res = x.update() || res;
		return res;
	}
}
