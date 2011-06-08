/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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

package org.semanticweb.elk.reasoner.saturation;

import java.util.ArrayDeque;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.util.ArrayHashSet;

public class SaturatedObjectProperty {
	
	protected final IndexedObjectProperty root;
	protected Set<IndexedObjectProperty> derivedSubObjectProperties;
	protected Set<IndexedObjectProperty> derivedSuperObjectProperties;
	protected Set<IndexedObjectProperty> transitiveSubObjectProperties;
	protected Set<IndexedObjectProperty> transitiveSuperObjectProperties;

	public SaturatedObjectProperty(IndexedObjectProperty root) {
		this.root = root;
		compute();
	}

	public Set<IndexedObjectProperty> getSubObjectProperties() {
		return derivedSubObjectProperties;
	}


	public Set<IndexedObjectProperty> getSuperObjectProperties() {
		return derivedSuperObjectProperties;
	}


	public Set<IndexedObjectProperty> getTransitiveSubObjectProperties() {
		return transitiveSubObjectProperties;
	}


	public Set<IndexedObjectProperty> getTransitiveSuperObjectProperties() {
		return transitiveSuperObjectProperties;
	}


	protected void compute() {		
		//compute all subproperties

		derivedSubObjectProperties = new ArrayHashSet<IndexedObjectProperty>();
		ArrayDeque<IndexedObjectProperty> queue = new ArrayDeque<IndexedObjectProperty>();
		derivedSubObjectProperties.add(root);
		queue.addLast(root);
		while (!queue.isEmpty()) {
			IndexedObjectProperty r = queue.removeLast();
			if (r.getToldSubObjectProperties() != null)
				for (IndexedObjectProperty s : r.getToldSubObjectProperties())
					if (derivedSubObjectProperties.add(s))
						queue.addLast(s);
		}

		//find transitive subproperties
		transitiveSubObjectProperties = null;
		for (IndexedObjectProperty r : derivedSubObjectProperties)
			if (r.isTransitive()) {
				if (transitiveSubObjectProperties == null)
					transitiveSubObjectProperties = new ArrayHashSet<IndexedObjectProperty> ();
				transitiveSubObjectProperties.add(r);
			}

		//compute all superproperties
		derivedSuperObjectProperties = new ArrayHashSet<IndexedObjectProperty>();
		queue.clear();
		derivedSuperObjectProperties.add(root);
		queue.addLast(root);
		while (!queue.isEmpty()) {
			IndexedObjectProperty r = queue.removeLast();
			if (r.getToldSuperObjectProperties() != null)
				for (IndexedObjectProperty s : r.getToldSuperObjectProperties())
					if (derivedSuperObjectProperties.add(s))
						queue.addLast(s);
		}

		//find transitive superproperties
		transitiveSuperObjectProperties = null;
		for (IndexedObjectProperty r : derivedSuperObjectProperties)
			if (r.isTransitive()) {
				if (transitiveSuperObjectProperties == null)
					transitiveSuperObjectProperties = new ArrayHashSet<IndexedObjectProperty> ();
				transitiveSuperObjectProperties.add(r);
			}
	}
}