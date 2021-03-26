/**
 * 
 */
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * A simple implementation of the depth-first search
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 *         
 * @param <T> 
 */
public class DepthFirstSearch<T extends ElkEntity> {
	// The search direction, up or down the taxonomy
	public enum Direction {
		UP {
			@Override
			public Direction reverse() {
				return DOWN;
			}
		},
		DOWN {
			@Override
			public Direction reverse() {
				return UP;
			}
		};

		public abstract Direction reverse();
	}

	public void run(TaxonomyNode<T> start, Direction dir,
			TaxonomyNodeVisitor<T> visitor) {
		LinkedList<TaxonomyNode<T>> path = new LinkedList<TaxonomyNode<T>>();
		Set<TaxonomyNode<T>> pathSet = new ArrayHashSet<TaxonomyNode<T>>();

		run(start, dir, visitor, path, pathSet);
	}

	private void run(TaxonomyNode<T> node, Direction dir,
			TaxonomyNodeVisitor<T> visitor, LinkedList<TaxonomyNode<T>> path,
			Set<TaxonomyNode<T>> pathSet) {
		visitor.visit(node, path);

		if (!pathSet.add(node)) {
			return;
		}

		path.add(node);
		// now go depth-first
		for (TaxonomyNode<T> subNode : getSuccessors(node, dir)) {
			run(subNode, dir, visitor, path, pathSet);
		}

		path.removeLast(/* path.size() - 1 */);
		pathSet.remove(node);
	}

	protected static <U extends ElkEntity> Set<? extends TaxonomyNode<U>> getSuccessors(
			TaxonomyNode<U> node, Direction dir) {
		switch (dir) {
		case UP:
			return node.getDirectSuperNodes();
		case DOWN:
			return node.getDirectSubNodes();
		default:
			return Collections.<TaxonomyNode<U>> emptySet();
		}
	}
}