package org.semanticweb.elk.reasoner.taxonomy;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.taxonomy.DepthFirstSearch.Direction;
import org.semanticweb.elk.reasoner.taxonomy.impl.AbstractInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Condition;
import org.semanticweb.elk.util.collections.Operations;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class MockInstanceTaxonomy<T extends ElkEntity, I extends ElkEntity>
		extends AbstractInstanceTaxonomy<T, I>
		implements InstanceTaxonomy<T, I> {

	protected final ExtremeNode<T, I> top;
	protected MockBottomNode bottom;
	protected MockInstanceNode bottomInstances = null; // will be created if taxonomy is inconsistent
	protected final Map<TypeNode<T, I>, Set<TypeNode<T, I>>> parentMap = new HashMap<TypeNode<T, I>, Set<TypeNode<T, I>>>();
	protected final Map<TypeNode<T, I>, Set<TypeNode<T, I>>> childrenMap = new HashMap<TypeNode<T, I>, Set<TypeNode<T, I>>>();
	protected final Map<Object, TypeNode<T, I>> typeIndex = new HashMap<Object, TypeNode<T, I>>();
	protected final Map<Object, MockInstanceNode> instanceIndex = new HashMap<Object, MockInstanceNode>();
	protected final Map<MockInstanceNode, Set<TypeNode<T, I>>> instanceTypeMap = new HashMap<MockInstanceNode, Set<TypeNode<T, I>>>();

	private int nodesWithoutParent = 0;
	private int nodesWithoutChildren = 0;
	private int instancesWithoutType = 0;

	/** provides keys that are used for hashing instead of the elkClasses */
	private final ComparatorKeyProvider<ElkEntity> typeKeyProvider_;
	/** provides keys that are used for hashing instead of the elkIndividuals */
	private final ComparatorKeyProvider<ElkEntity> instanceKeyProvider_;

	MockInstanceTaxonomy(T top, T bottom,
			final ComparatorKeyProvider<ElkEntity> typeKeyProvider,
			final ComparatorKeyProvider<ElkEntity> instanceKeyProvider) {
		this.typeKeyProvider_ = typeKeyProvider;
		this.instanceKeyProvider_ = instanceKeyProvider;
		this.top = new MockTopNode(top);
		this.bottom = new MockBottomNode(bottom);

		initTypeNode(this.top);
		initTypeNode(this.bottom);
	}

	private void initTypeNode(TypeNode<T, I> typeNode) {
		for (T member : typeNode) {
			typeIndex.put(typeKeyProvider_.getKey(member), typeNode);
		}

		parentMap.put(typeNode, new HashSet<TypeNode<T, I>>());
		childrenMap.put(typeNode, new HashSet<TypeNode<T, I>>());
	}

	@Override
	public ComparatorKeyProvider<ElkEntity> getKeyProvider() {
		return typeKeyProvider_;
	}
	
	@Override
	public ComparatorKeyProvider<ElkEntity> getInstanceKeyProvider() {
		return instanceKeyProvider_;
	}
	
	@Override
	public MockTypeNode getNode(T elkObject) {
		return (MockTypeNode) typeIndex.get(typeKeyProvider_.getKey(elkObject));
	}

	@Override
	public Set<? extends TypeNode<T, I>> getNodes() {
		return parentMap.keySet();
	}

	@Override
	public ExtremeNode<T, I> getTopNode() {		
		return top;
	}

	@Override
	public ExtremeNode<T, I> getBottomNode() {
		return bottom != null ? bottom : top;
	}

	@Override
	public MockInstanceNode getInstanceNode(I elkObject) {
		return instanceIndex.get(instanceKeyProvider_.getKey(elkObject));
	}

	@Override
	public Set<? extends InstanceNode<T, I>> getInstanceNodes() {
		return instanceTypeMap.keySet();
	}

	protected void makeInconsistent() {
		parentMap.clear();
		childrenMap.clear();
		instanceIndex.clear();
		typeIndex.clear();
		instancesWithoutType = 0;
		// merge all instances in one node
		Collection<I> instances = new HashSet<I>(instanceTypeMap.size());
		for (InstanceNode<T, I> node : getInstanceNodes()) {
			for (I member : node) {
				instances.add(member);
			}
		}
		instanceTypeMap.clear();		
		bottomInstances = new MockInstanceNode(instances, Collections.<TypeNode<T, I>> emptyList());
		for (I instance : instances) {
			instanceIndex.put(instanceKeyProvider_.getKey(instance), bottomInstances);
		}
		top.addMembers(bottom);
		bottom = null;
		// init the only node in the taxonomy
		initTypeNode(top);
	}

	public boolean isConsistent() {
		return top != bottom;
	}

	@SuppressWarnings("unchecked")
	public MutableTypeNode<T, I> getCreateTypeNode(Collection<T> types) {
		// check for inconsistency
		if (bottom != null && types.contains(top.getCanonicalMember())
				&& types.contains(bottom.getCanonicalMember())) {
			makeInconsistent();			
		}
		if (bottom == null) {
			// inconsistent
			top.addMembers(types);
			return top;			
		}
		// else nodes for some types may already exist
		MutableTypeNode<T, I> node = null;
		ExtremeNode<T, I> extreme = null;

		for (T type : types) {
			if (type.equals(top.getCanonicalMember())
					|| type.equals(bottom.getCanonicalMember())) {
				// this is an unchecked cast. could be avoided at the expense of
				// another LoC but i'm gonna punt on that
				extreme = (ExtremeNode<T, I>) getNode(type);
			} else {
				MockTypeNode typeNode = getNode(type);
				// raise an error since this class doesn't support node merging
				assert node == null || node == typeNode;

				node = typeNode;
			}
		}

		if (node == null) {
			if (extreme != null) {
				extreme.addMembers(types);
				node = extreme;
			} else {
				node = new MockTypeNode(types);
				initTypeNode(node);
				nodesWithoutParent++;
				nodesWithoutChildren++;
			}
		} else {
			if (extreme != null) {
				extreme.merge(node);
			} else {
				node.addMembers(types);
			}
		}

		return node;
	}

	public MockInstanceNode getCreateInstanceNode(Collection<I> instances,
			Collection<TypeNode<T, I>> types) {
		MockInstanceNode node;
		if (bottom == null) {			
			// inconsistent
			bottomInstances.addMembers(instances);
			node = bottomInstances;			
		} else {
			node = instanceIndex.get(
					instanceKeyProvider_.getKey(instances.iterator().next()));			
			if (node != null) {
				return node;							
			}
			// else
			node = new MockInstanceNode(instances, types);
		}

		for (I instance : instances) {
			instanceIndex.put(instanceKeyProvider_.getKey(instance), node);
		}
		return node;
	}
	
	public MockInstanceNode getCreateInstanceNode(Collection<I> instances) {
		return getCreateInstanceNode(instances,
				Collections.<TypeNode<T, I>> emptyList());
	}

	protected void remove(TypeNode<T, I> node) {
		// clean-up all indices
		for (TypeNode<T, I> subNode : node.getDirectSubNodes()) {
			parentMap.get(subNode).remove(node);
		}

		for (TypeNode<T, I> supNode : node.getDirectSuperNodes()) {
			childrenMap.get(supNode).remove(node);
		}

		for (InstanceNode<T, I> instanceNode : node.getDirectInstanceNodes()) {
			instanceTypeMap.get(instanceNode).remove(node);
		}
		// update counters
		if (parentMap.get(node).isEmpty()) {
			nodesWithoutParent--;
		}

		if (childrenMap.get(node).isEmpty()) {
			nodesWithoutChildren--;
		}
		// the final cleanup
		parentMap.remove(node);
		childrenMap.remove(node);
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	interface MutableTypeNode<T extends ElkEntity, I extends ElkEntity> extends
			TypeNode<T, I> {
		void addDirectInstance(InstanceNode<T, I> instNode);

		void addDirectParent(TypeNode<T, I> parent);

		void addMembers(Iterable<T> members);
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	protected abstract class MockNode<O extends ElkEntity> implements Node<O> {

		final SortedSet<O> members;

		MockNode(Collection<O> members, ComparatorKeyProvider<ElkEntity> keyProvider) {
			this.members = new TreeSet<O>(keyProvider.getComparator());
			this.members.addAll(members);
		}

		public Taxonomy<T> getTaxonomy() {
			return MockInstanceTaxonomy.this;
		}

		@Override
		public Iterator<O> iterator() {
			return members.iterator();
		}
		
		@Override
		public boolean contains(O member) {
			return members.contains(member);
		}
		
		@Override
		public int size() {
			return members.size();
		}
		
		@Override
		public O getCanonicalMember() {
			return members.isEmpty() ? null : members.iterator().next();
		}

		protected abstract void addMembers(Iterable<O> newMembers);

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();

			for (O member : members) {
				builder.append(OwlFunctionalStylePrinter.toString(member) + ",");
			}

			return builder.toString();
		}
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	protected class MockTypeNode extends MockNode<T> implements
			MutableTypeNode<T, I> {

		final Set<InstanceNode<T, I>> instances = new ArrayHashSet<InstanceNode<T, I>>();

		MockTypeNode(Collection<T> members) {
			super(members, typeKeyProvider_);
		}

		@Override
		public ComparatorKeyProvider<ElkEntity> getKeyProvider() {
			return typeKeyProvider_;
		}
		
		@Override
		public Set<? extends InstanceNode<T, I>> getDirectInstanceNodes() {
			return instances;
		}

		@Override
		public Set<? extends InstanceNode<T, I>> getAllInstanceNodes() {
			Set<TypeNode<T, I>> ancestors = getAllSubNodes();
			Set<InstanceNode<T, I>> result = new ArrayHashSet<InstanceNode<T, I>>();

			result.addAll(instances);

			for (TypeNode<T, I> ancestor : ancestors) {
				result.addAll(ancestor.getDirectInstanceNodes());
			}

			return result;
		}

		@Override
		public Set<TypeNode<T, I>> getDirectSuperNodes() {
			Set<TypeNode<T, I>> sup = MockInstanceTaxonomy.this.parentMap
					.get(this);

			return sup.isEmpty() ? Collections
					.<TypeNode<T, I>> singleton(getTopNode()) : Collections
					.unmodifiableSet(sup);
		}

		@Override
		public Set<TypeNode<T, I>> getAllSuperNodes() {
			Set<TypeNode<T, I>> sups = new HashSet<TypeNode<T, I>>();

			computeSuccessors(this, sups, Direction.UP);

			if (sups.size() > 2) {
				sups.remove(getTopNode());
				sups.remove(getBottomNode());
			}

			return sups;
		}

		@Override
		public Set<TypeNode<T, I>> getDirectSubNodes() {
			Set<TypeNode<T, I>> sub = MockInstanceTaxonomy.this.childrenMap
					.get(this);

			return sub.isEmpty() ? Collections
					.<TypeNode<T, I>> singleton(getBottomNode()) : Collections
					.unmodifiableSet(sub);
		}

		@Override
		public Set<TypeNode<T, I>> getAllSubNodes() {
			Set<TypeNode<T, I>> subs = new HashSet<TypeNode<T, I>>();

			computeSuccessors(this, subs, Direction.DOWN);

			if (subs.size() > 2) {
				subs.remove(getTopNode());
				subs.remove(getBottomNode());
			}

			return subs;
		}

		@Override
		public void addDirectParent(TypeNode<T, I> parent) {
			// assert parent.getTaxonomy() == getTaxonomy();

			if (this != getBottomNode() && parent != getTopNode()) {
				if (parent == getBottomNode()) {
					getBottomNode().merge(this);
				} else {
					if (childrenMap.get(parent).isEmpty()) {
						nodesWithoutChildren--;
					}

					if (parentMap.get(this).isEmpty()) {
						nodesWithoutParent--;
					}

					parentMap.get(this).add(parent);
					childrenMap.get(parent).add(this);
				}
			}
		}

		@Override
		public void addDirectInstance(InstanceNode<T, I> instance) {
			// assert instance.getInstanceTaxonomy() == getTaxonomy();
			assert this != getBottomNode();

			if (this != getTopNode()) {
				if (MockInstanceTaxonomy.this.instanceTypeMap.get(instance).isEmpty()) {
					instancesWithoutType--;
				}

				MockInstanceTaxonomy.this.instanceTypeMap.get(instance).add(
						this);
				instances.add(instance);
			}
		}

		private void computeSuccessors(TypeNode<T, I> node,
				Set<TypeNode<T, I>> successors, Direction dir) {

			for (TypeNode<T, I> succ : dir == Direction.UP ? node
					.getDirectSuperNodes() : node.getDirectSubNodes()) {
				successors.add(succ);
				computeSuccessors(succ, successors, dir);
			}
		}

		@Override
		public void addMembers(Iterable<T> newMembers) {
			for (T newMember : newMembers) {
				this.members.add(newMember);
				MockInstanceTaxonomy.this.typeIndex.put(typeKeyProvider_.getKey(newMember), this);
			}
		}
	}

	/**
	 * Only Top and Bot implement this since they're the only ones supporting
	 * the merge op so far
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	interface ExtremeNode<T extends ElkEntity, I extends ElkEntity> extends
			MutableTypeNode<T, I> {
		void merge(TypeNode<T, I> node);
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	class MockTopNode extends MockTypeNode implements ExtremeNode<T, I> {

		MockTopNode(T top) {
			super(Collections.singleton(top));
		}

		@Override
		public void merge(TypeNode<T, I> node) {
			// Merge that node into top
			// It's possible to have a generic "merge" method but it's
			// non-trivial
			addMembers(node);
			// No need to do anything with instances
			remove(node);
		}

		@Override
		public void addDirectParent(TypeNode<T, I> node) {
			if (node == getBottomNode()) {
				// this is the special case of an unsatisfiable taxonomy
				makeInconsistent();
			} else {
				merge(node);
			}
		}

		@Override
		public Set<? extends InstanceNode<T, I>> getDirectInstanceNodes() {
			// all instances that don't have a direct type
			return Operations.filter(
					MockInstanceTaxonomy.this.instanceTypeMap.keySet(),
					new Condition<InstanceNode<T, I>>() {
						@Override
						public boolean holds(InstanceNode<T, I> node) {
							return MockInstanceTaxonomy.this.instanceTypeMap
									.get(node).isEmpty();
						}
					}, instancesWithoutType);
		}

		@Override
		public Set<? extends InstanceNode<T, I>> getAllInstanceNodes() {
			// all instances in the taxonomy
			return MockInstanceTaxonomy.this.instanceTypeMap.keySet();
		}

		@Override
		public Set<TypeNode<T, I>> getDirectSuperNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<TypeNode<T, I>> getAllSuperNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<TypeNode<T, I>> getDirectSubNodes() {
			// all type nodes that have no non-top parent
			final boolean empty = MockInstanceTaxonomy.this.childrenMap.size() == 2;

			return Operations.filter(
					MockInstanceTaxonomy.this.parentMap.keySet(),
					new Condition<TaxonomyNode<T>>() {
						@Override
						public boolean holds(TaxonomyNode<T> node) {
							return node != getTopNode() // it's not Top
									&& MockInstanceTaxonomy.this.parentMap.get(
											node).isEmpty() // no direct parents
									&& (node != getBottomNode() || empty); // bottom
																			// but
																			// there're
																			// no
																			// intermediate
																			// nodes
						}
					}, empty ? 1 : nodesWithoutParent);
		}

		@Override
		public Set<TypeNode<T, I>> getAllSubNodes() {
			// all nodes
			return Operations.filter(
					MockInstanceTaxonomy.this.parentMap.keySet(),
					new Condition<TaxonomyNode<T>>() {
						@Override
						public boolean holds(TaxonomyNode<T> node) {
							return node != getTopNode();
						}
					}, MockInstanceTaxonomy.this.parentMap.keySet().size() - 1);
		}
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	class MockBottomNode extends MockTypeNode implements ExtremeNode<T, I> {

		MockBottomNode(T bot) {
			super(Collections.singleton(bot));
		}

		@Override
		public Set<? extends InstanceNode<T, I>> getDirectInstanceNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<? extends InstanceNode<T, I>> getAllInstanceNodes() {
			return Collections.emptySet();
		}

		@Override
		public void merge(TypeNode<T, I> node) {
			addMembers(node);

			if (!node.getDirectInstanceNodes().isEmpty()) {
				makeInconsistent();
			} else {
				remove(node);
			}
		}

		@Override
		public Set<TypeNode<T, I>> getDirectSuperNodes() {
			// all type nodes that have no non-bot children
			final boolean empty = MockInstanceTaxonomy.this.childrenMap.size() == 2;

			return Operations.filter(
					MockInstanceTaxonomy.this.childrenMap.keySet(),
					new Condition<TaxonomyNode<T>>() {
						@Override
						public boolean holds(TaxonomyNode<T> node) {
							return node != getBottomNode() && // not Bot
									MockInstanceTaxonomy.this.childrenMap.get(
											node).isEmpty() // no children
									&& (node != getTopNode() || empty); // Top
																		// but
																		// there're
																		// no
																		// intermediate
																		// nodes;
						}
					}, empty ? 1 : nodesWithoutChildren);
		}

		@Override
		public Set<TypeNode<T, I>> getAllSuperNodes() {
			return Operations.filter(
					MockInstanceTaxonomy.this.childrenMap.keySet(),
					new Condition<TaxonomyNode<T>>() {
						@Override
						public boolean holds(TaxonomyNode<T> node) {
							return node != getBottomNode();
						}
					}, MockInstanceTaxonomy.this.childrenMap.size() - 1);
		}

		@Override
		public Set<TypeNode<T, I>> getDirectSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<TypeNode<T, I>> getAllSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public void addDirectInstance(InstanceNode<T, I> instance) {
			makeInconsistent();
		}
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	protected class MockInstanceNode extends MockNode<I> implements
			InstanceNode<T, I> {

		MockInstanceNode(Collection<I> members, Collection<TypeNode<T, I>> types) {
			super(members, instanceKeyProvider_);

			instancesWithoutType++;
			MockInstanceTaxonomy.this.instanceTypeMap.put(this,
					new ArrayHashSet<TypeNode<T, I>>());

			for (TypeNode<T, I> type : types) {
				assert type != null;
				// assert type.getTaxonomy() == getInstanceTaxonomy();

				((MockTypeNode) type).addDirectInstance(this);
			}
		}

		@Override
		public ComparatorKeyProvider<ElkEntity> getKeyProvider() {
			return instanceKeyProvider_;
		}
		
		@Override
		public Set<? extends TypeNode<T, I>> getDirectTypeNodes() {
			Set<TypeNode<T, I>> typeNodes = MockInstanceTaxonomy.this.instanceTypeMap
					.get(this);

			return typeNodes.isEmpty() ? Collections.singleton(getTopNode())
					: typeNodes;
		}

		@Override
		public Set<? extends TypeNode<T, I>> getAllTypeNodes() {
			Set<TypeNode<T, I>> allTypes = new HashSet<TypeNode<T, I>>();

			for (TypeNode<T, I> aType : getDirectTypeNodes()) {
				allTypes.addAll(aType.getAllSuperNodes());
				allTypes.add(aType);
			}

			if (allTypes.size() > 1) {
				allTypes.remove(getTopNode());
			}

			return allTypes;
		}

		@Override
		protected void addMembers(Iterable<I> newMembers) {
			for (I newMember : newMembers) {
				this.members.add(newMember);
				MockInstanceTaxonomy.this.instanceIndex.put(newMember, this);
			}
		}
	}

	@Override
	public boolean addListener(final Taxonomy.Listener<T> listener) {
		// Ignore
		return false;
	}

	@Override
	public boolean removeListener(final Taxonomy.Listener<T> listener) {
		// Ignore
		return false;
	}

	@Override
	public boolean addListener(final NodeStore.Listener<T> listener) {
		// Ignore
		return false;
	}

	@Override
	public boolean removeListener(final NodeStore.Listener<T> listener) {
		// Ignore
		return false;
	}

	@Override
	public boolean addInstanceListener(final NodeStore.Listener<I> listener) {
		// Ignore
		return false;
	}

	@Override
	public boolean removeInstanceListener(
			final NodeStore.Listener<I> listener) {
		// Ignore
		return false;
	}

	@Override
	public boolean addInstanceListener(
			final InstanceTaxonomy.Listener<T, I> listener) {
		// Ignore
		return false;
	}

	@Override
	public boolean removeInstanceListener(
			final InstanceTaxonomy.Listener<T, I> listener) {
		// Ignore
		return false;
	}

}