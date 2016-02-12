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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.taxonomy.model.SimpleNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeUtils;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for storing information about a class in the context of classification.
 * It is the main data container for ClassTaxonomy objects. Like most such data
 * containers in ELK, it is read-only for public access but provides
 * package-private ways of modifying it. Modifications of this class happen in
 * implementations of ClassTaxonomy only.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Pavel Klinov
 * @author Peter Skocovsky
 */
class NonBottomClassNode extends SimpleNode<ElkClass>
		implements UpdateableTaxonomyNode<ElkClass> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(NonBottomClassNode.class);

	/**
	 * The link to the taxonomy to which this node belongs
	 */
	private final ConcurrentClassTaxonomy taxonomy_;

	/**
	 * ElkClass nodes whose members are direct super-classes of the members of
	 * this node.
	 */
	private final Set<UpdateableTaxonomyNode<ElkClass>> directSuperNodes_;
	/**
	 * ElkClass nodes, except for the bottom node, whose members are direct
	 * sub-classes of the members of this node.
	 */
	private final Set<UpdateableTaxonomyNode<ElkClass>> directSubNodes_;
	/**
	 * <tt>true</tt> if the direct super-nodes of this node need to be
	 * recomputed
	 */
	private final AtomicBoolean modified_ = new AtomicBoolean(true);

	/**
	 * Constructing the class node for a given taxonomy and the set of
	 * equivalent classes.
	 * 
	 * @param taxonomy
	 *            the taxonomy to which this node belongs
	 * @param members
	 *            non-empty list of equivalent ElkClass objects
	 */
	protected NonBottomClassNode(ConcurrentClassTaxonomy taxonomy,
			Collection<ElkClass> members) {
		super(members, members.size(), taxonomy.getKeyProvider());
		this.taxonomy_ = taxonomy;
		this.directSubNodes_ = new ArrayHashSet<UpdateableTaxonomyNode<ElkClass>>();
		this.directSuperNodes_ = new ArrayHashSet<UpdateableTaxonomyNode<ElkClass>>();
	}

	/**
	 * Add a direct super-class node. This method is not thread safe.
	 * 
	 * @param superNode
	 *            node to add
	 */
	@Override
	public synchronized void addDirectSuperNode(
			UpdateableTaxonomyNode<ElkClass> superNode) {
		LOGGER_.trace("{}: new direct super-node {}", this, superNode);

		directSuperNodes_.add(superNode);
	}

	/**
	 * Add a direct sub-class node. This method is not thread safe.
	 * 
	 * @param subNode
	 *            node to add
	 */
	@Override
	public synchronized void addDirectSubNode(
			UpdateableTaxonomyNode<ElkClass> subNode) {
		LOGGER_.trace("{}: new direct sub-node {}", this, subNode);

		if (directSubNodes_.isEmpty()) {
			this.taxonomy_.countNodesWithSubClasses.incrementAndGet();
		}

		directSubNodes_.add(subNode);
	}

	@Override
	public Set<UpdateableTaxonomyNode<ElkClass>> getDirectSuperNodes() {
		return Collections.unmodifiableSet(directSuperNodes_);
	}

	@Override
	public Set<? extends UpdateableTaxonomyNode<ElkClass>> getAllSuperNodes() {
		return TaxonomyNodeUtils.getAllSuperNodes(this);
	}

	@Override
	public Set<UpdateableTaxonomyNode<ElkClass>> getDirectSubNodes() {
		if (!directSubNodes_.isEmpty()) {
			return Collections
					.<UpdateableTaxonomyNode<ElkClass>> unmodifiableSet(directSubNodes_);
		}
		// else
		return Collections.singleton(this.taxonomy_.getBottomNode());
	}

	@Override
	public Set<? extends UpdateableTaxonomyNode<ElkClass>> getAllSubNodes() {
		return TaxonomyNodeUtils.getAllSubNodes(this);
	}

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	@Override
	public final int hashCode() {
		return hashCode_;
	}

	@Override
	public void setMembers(final Iterable<ElkClass> members) {
		members_.clear();
		for (final ElkClass elkClass : members) {
			members_.add(elkClass);
		}
		Collections.sort(this.members_, getKeyProvider().getComparator());
		LOGGER_.trace("updated members of {}", this);
	}
	
	@Override
	public boolean trySetModified(boolean modified) {
		boolean result = modified_.compareAndSet(!modified, modified);
		if (result && LOGGER_.isTraceEnabled())
			LOGGER_.trace("node " + this + ": set "
					+ (modified ? "modified" : "not modifiled"));
		return result;
	}

	@Override
	public boolean isModified() {
		return modified_.get();
	}

	@Override
	public boolean removeDirectSubNode(UpdateableTaxonomyNode<ElkClass> subNode) {
		boolean changed = directSubNodes_.remove(subNode);

		if (changed)
			LOGGER_.trace("{}: removed direct sub-node {}", this, subNode);

		if (directSubNodes_.isEmpty()) {
			taxonomy_.countNodesWithSubClasses.decrementAndGet();
		}

		return changed;
	}

	@Override
	public boolean removeDirectSuperNode(
			UpdateableTaxonomyNode<ElkClass> superNode) {
		boolean changed = directSuperNodes_.remove(superNode);

		LOGGER_.trace("{}: removed direct super-node {}", this, superNode);

		return changed;
	}
}
