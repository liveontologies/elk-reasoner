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

/**
 * This class implements a node in a red-black tree. It provides accessors for
 * the left and right children as well as the color of the node.
 */
public class RBNode {

	private Object data;
	private RBNode left;
	private RBNode right;
	private RBNode parent;
	private RBColor color;

	public RBNode(Object data) {
		this.data = data;
		color = RBColor.RED;
	}

	public Object getData() {
		return data;
	}

	public void copyFrom(RBNode arg) {
		this.data = arg.data;
	}

	public boolean update() {
		return false;
	}

	public RBColor getColor() {
		return color;
	}

	public void setColor(RBColor color) {
		this.color = color;
	}

	public RBNode getParent() {
		return parent;
	}

	public void setParent(RBNode parent) {
		this.parent = parent;
	}

	public RBNode getLeft() {
		return left;
	}

	public void setLeft(RBNode left) {
		this.left = left;
	}

	public RBNode getRight() {
		return right;
	}

	public void setRight(RBNode right) {
		this.right = right;
	}
}
