/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, May 15, 2011
 */
package org.semanticweb.elk.reasoner.classification;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.elk.syntax.ElkClass;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class ClassNode {

	final ArrayList<ElkClass> members;
	ArrayList<ClassNode> parents;
	ArrayList<ClassNode> children;
	// to add children concurrently
	final Queue<ClassNode> childQueue;
	// true if childQueue is not empty
	private AtomicBoolean isActive;

	public ClassNode(final ArrayList<ElkClass> equivalent) {
		this.members = equivalent;
		this.children = new ArrayList<ClassNode>();
		this.parents = new ArrayList<ClassNode>();
		this.childQueue = new ConcurrentLinkedQueue<ClassNode>();
		this.isActive = new AtomicBoolean(false);
	}

	protected void enqueueChild(ClassNode child) {
		childQueue.add(child);
	}

	protected boolean tryActivate() {
		return isActive.compareAndSet(false, true);
	}

	protected boolean tryDeactivate() {
		return isActive.compareAndSet(true, false);
	}

	public synchronized void addParent(ClassNode parent) {
		this.parents.add(parent);
	}

	public synchronized void addChild(ClassNode child) {
		this.children.add(child);
	}

	public ArrayList<ElkClass> getMembers() {
		return members;
	}

	public ElkClass getCanonicalMember() {
		return members.get(0);
	}

	public ArrayList<ClassNode> getParents() {
		return parents;
	}

	public ArrayList<ClassNode> getChildren() {
		return children;
	}

}
