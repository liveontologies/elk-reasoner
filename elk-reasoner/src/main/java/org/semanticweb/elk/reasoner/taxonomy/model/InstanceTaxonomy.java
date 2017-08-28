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
package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * Extended {@link Taxonomy} that also provides instances for each of its
 * members.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 * 
 * @param <T>
 *            the type of objects in the nodes of this taxonomy
 * @param <I>
 *            the type of instances of nodes of this taxonomy
 */
public interface InstanceTaxonomy<T extends ElkEntity, I extends ElkEntity>
		extends Taxonomy<T> {

	/**
	 * Returns the {@link ComparatorKeyProvider} that provides a key for each
	 * instance. These keys are used to compute hash codes and to compare the
	 * instances.
	 * 
	 * @return the {@link ComparatorKeyProvider} that provides a key for each
	 *         instance.
	 */
	ComparatorKeyProvider<? super I> getInstanceKeyProvider();

	/**
	 * @param elkEntity
	 *            {@link ElkEntity} for which the {@link InstanceNode} to be
	 *            computed
	 * @return the {@link InstanceNode} containing the given {@link ElkEntity}
	 *         as a member, or {@code null} if the input does not occur in the
	 *         taxonomy
	 */
	InstanceNode<T, I> getInstanceNode(I elkEntity);

	/**
	 * Obtain an unmodifiable Set of all instance nodes in this taxonomy.
	 * 
	 * @return an unmodifiable Set of all instance nodes in this taxonomy.
	 */
	Set<? extends InstanceNode<T, I>> getInstanceNodes();

	@Override
	TypeNode<T, I> getNode(T elkEntity);

	@Override
	Set<? extends TypeNode<T, I>> getNodes();

	@Override
	TypeNode<T, I> getTopNode();

	@Override
	TypeNode<T, I> getBottomNode();

	/**
	 * Registers the given {@link NodeStore.Listener} with this instance
	 * taxonomy. The registered listener will be notified about changes to
	 * members of instance nodes.
	 * 
	 * @param listener
	 *            The listener that should be registered.
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is return, the listener was not
	 *         registered
	 */
	boolean addInstanceListener(NodeStore.Listener<I> listener);

	/**
	 * Removes a given {@link NodeStore.Listener} from this instance taxonomy.
	 * 
	 * @param listener
	 *            The listener that should be removed.
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is return, the listener was not
	 *         removed
	 */
	boolean removeInstanceListener(NodeStore.Listener<I> listener);

	/**
	 * Registers the given {@link InstanceTaxonomy.Listener} with this instance
	 * taxonomy. The registered listener will be notified about changes in
	 * relations between instance and type nodes.
	 * 
	 * @param listener
	 *            The listener that should be registered.
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is return, the listener was not
	 *         registered
	 */
	boolean addInstanceListener(InstanceTaxonomy.Listener<T, I> listener);

	/**
	 * Removes the given {@link InstanceTaxonomy.Listener} from this instance
	 * taxonomy.
	 * 
	 * @param listener
	 *            The listener that should be removed.
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is return, the listener was not
	 *         removed
	 */
	boolean removeInstanceListener(InstanceTaxonomy.Listener<T, I> listener);

	/**
	 * Instances of this interface registered by
	 * {@link InstanceTaxonomy#addListener(InstanceTaxonomy.Listener)} will be
	 * notified about changes to the relations between the instance and type
	 * nodes.
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <T>
	 *            The type of members of the type nodes in the taxonomy for
	 *            which this listener is registered.
	 * @param <I>
	 *            The type of members of the instance nodes in the taxonomy for
	 *            which this listener is registered.
	 */
	interface Listener<T extends ElkEntity, I extends ElkEntity> {

		/**
		 * Called just after some type nodes of {@code instanceNode} appear.
		 * 
		 * @param instanceNode
		 *            The instance node whose type nodes appeared.
		 */
		void directTypeNodesAppeared(InstanceNode<T, I> instanceNode);

		/**
		 * Called just after some type nodes of {@code instanceNode} disappear.
		 * 
		 * @param instanceNode
		 *            The instance node whose type nodes disappeared.
		 */
		void directTypeNodesDisappeared(InstanceNode<T, I> instanceNode);

		/**
		 * Called just after some instance nodes of {@code typeNode} appear.
		 * 
		 * @param typeNode
		 *            The node whose instance nodes appeared.
		 */
		void directInstanceNodesAppeared(TypeNode<T, I> typeNode);

		/**
		 * Called just after some instance nodes of {@code typeNode} disappear.
		 * 
		 * @param typeNode
		 *            The node whose instance nodes disappeared.
		 */
		void directInstanceNodesDisappeared(TypeNode<T, I> typeNode);

	}

}
