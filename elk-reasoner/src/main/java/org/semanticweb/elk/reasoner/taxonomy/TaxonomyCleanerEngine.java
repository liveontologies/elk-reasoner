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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine to remove information from the taxonomy following saturation
 * update. The jobs submitted using the method {@link #submit(ElkClass)}
 * indicate that the saturation for the given {@link ElkClass} has changed. As
 * the result, the nodes for such classes are removed from the taxonomy, and
 * their direct sub-nodes (if not removed) are marked as modified, which means
 * that their direct super-nodes need to be recomputed.
 * 
 * @author "Yevgeny Kazakov"
 */
public class TaxonomyCleanerEngine implements InputProcessor<ElkClass> {

	/**
	 * The taxonomy to be cleaned by this cleaner
	 */
	final ConcurrentClassTaxonomy taxonomy;

	/**
	 * Temporary queue of nodes that should be removed from the taxonomy
	 */
	final Queue<SatisfiableClassNode> toRemove;

	public TaxonomyCleanerEngine(ConcurrentClassTaxonomy taxonomy) {
		this.taxonomy = taxonomy;
		this.toRemove = new ConcurrentLinkedQueue<SatisfiableClassNode>();
	}

	public void submit(ElkClass elkClass) throws InterruptedException {
		if (taxonomy.getUnsatisfiableClasses().remove(elkClass)) {
			return;
		}
		SatisfiableClassNode node = taxonomy.getSatisfiableClassNode(elkClass);
		if (node == null) {
			return;
		}
		if (node.trySetModified())
			toRemove.add(node);
		synchronized (node) {
			for (SatisfiableClassNode subNode : node
					.getDirectSatisfiableSubNodes()) {
				if (!subNode.trySetModified())
					continue;
				toRemove.add(subNode);
			}
		}
		if (node.equals(taxonomy.getTopNode())) {
			// removing node assignment for members except owl:Thing
			for (ElkClass member : node.getMembers()) {
				if (!member.equals(PredefinedElkClass.OWL_THING))
					taxonomy.removeSatisfiableClassNode(member);
			}
			taxonomy.getTopNode().clearMembers();
		} else {
			taxonomy.removeNode(node);
			// removing node assignment for members
			for (ElkClass member : node.getMembers()) {
				taxonomy.removeSatisfiableClassNode(member);
			}
		}
	}

	public void process() throws InterruptedException {
		for (;;) {
			SatisfiableClassNode node = toRemove.poll();
			if (node == null)
				return;
			List<SatisfiableClassNode> superNodes = new LinkedList<SatisfiableClassNode>();
			synchronized (node) {
				for (SatisfiableClassNode superNode : node
						.getDirectSuperNodes())
					superNodes.add(superNode);
				node.clearSuperNodes();
			}
			for (SatisfiableClassNode superNode : superNodes) {
				superNode.removeDirectSubNode(node);
			}
		}

	}

	public boolean canProcess() {
		return !toRemove.isEmpty();
	}

}
