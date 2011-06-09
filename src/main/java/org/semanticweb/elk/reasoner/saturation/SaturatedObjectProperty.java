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

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.util.ArrayHashSet;

public class SaturatedObjectProperty {
	protected final IndexedObjectProperty root;
	protected final Set<IndexedObjectProperty> derivedSubObjectProperties;
	protected final Set<IndexedObjectProperty> derivedSuperObjectProperties;
	protected Set<IndexedObjectProperty> transitiveSubObjectProperties;
	protected Set<IndexedObjectProperty> transitiveSuperObjectProperties;

	public SaturatedObjectProperty(IndexedObjectProperty iop) {
		this.root = iop;
		this.derivedSuperObjectProperties = new ArrayHashSet<IndexedObjectProperty>();
		this.derivedSubObjectProperties = new ArrayHashSet<IndexedObjectProperty>();
	}
	
	public IndexedObjectProperty getRoot() {
		return root;
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

}