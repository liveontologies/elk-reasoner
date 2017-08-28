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
package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * A hierarchy of certain ElkEntities. For each such entity, the taxonomy holds
 * a {@link TaxonomyNode} object from which direct sub- and super- nodes can be
 * retrieved.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * @author Peter Skocovsky
 * @param <T>
 *            the type of objects stored in this taxonomy
 */
public interface Taxonomy<T extends ElkEntity>
		extends NodeStore<T, TaxonomyNode<T>> {

	@Override
	public TaxonomyNode<T> getNode(T elkEntity);

	@Override
	public Set<? extends TaxonomyNode<T>> getNodes();

	/**
	 * Returns the node of this taxonomy that has no parent nodes.
	 * 
	 * @return the node of this taxonomy that has no parent nodes.
	 */
	public TaxonomyNode<T> getTopNode();

	/**
	 * Returns the node of this taxonomy that has no child nodes.
	 * 
	 * @return the node of this taxonomy that has no child nodes.
	 */
	public TaxonomyNode<T> getBottomNode();

	/**
	 * Registers the given {@link Taxonomy.Listener} with this taxonomy.
	 * 
	 * @param listener
	 *            The listener that should be registered.
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is return, the listener was not
	 *         registered
	 */
	boolean addListener(Taxonomy.Listener<T> listener);

	/**
	 * Removes the given {@link Taxonomy.Listener} from this taxonomy.
	 * 
	 * @param listener
	 *            The listener that should be removed.
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is return, the listener was not
	 *         removed
	 */
	boolean removeListener(Taxonomy.Listener<T> listener);

	/**
	 * Instances of this interface registered by
	 * {@link Taxonomy#addListener(Taxonomy.Listener)} will be notified about
	 * changes to the relations between the taxonomy nodes.
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <T>
	 *            The type of members of the nodes in the taxonomy for which
	 *            this listener is registered.
	 */
	interface Listener<T extends ElkEntity> {

		/**
		 * Called just after some super-nodes of {@code subNode} appear.
		 * 
		 * @param subNode
		 *            The node whose super-nodes appeared.
		 */
		void directSuperNodesAppeared(TaxonomyNode<T> subNode);

		/**
		 * Called just after some super-nodes of {@code subNode} disappear.
		 * 
		 * @param subNode
		 *            The node whose super-nodes disappeared.
		 */
		void directSuperNodesDisappeared(TaxonomyNode<T> subNode);

		/**
		 * Called just after some sub-nodes of {@code superNode} appear.
		 * 
		 * @param superNode
		 *            The node whose sub-nodes appeared.
		 */
		void directSubNodesAppeared(TaxonomyNode<T> superNode);

		/**
		 * Called just after some sub-nodes of {@code superNode} disappear.
		 * 
		 * @param superNode
		 *            The node whose sub-nodes disappeared.
		 */
		void directSubNodesDisappeared(TaxonomyNode<T> superNode);

	}

}
