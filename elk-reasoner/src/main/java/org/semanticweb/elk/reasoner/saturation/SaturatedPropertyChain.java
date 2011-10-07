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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashListMultimap;

/**
 * 
 * This object is used for fast retrieval of property inclusions and compositions 
 * which are needed during ClassExpressionSaturation. 
 * 
 * @author Frantisek Simancik
 *
 */
public class SaturatedPropertyChain {
	protected final IndexedPropertyChain root;
	
	protected final Set<IndexedPropertyChain>
		derivedSubProperties;
	
	protected final Set<IndexedPropertyChain>
		derivedSuperProperties;
	
	protected HashListMultimap<IndexedPropertyChain,
			IndexedBinaryPropertyChain>
		propertyCompositionsByLeftSubProperty;
	
	protected HashListMultimap<IndexedPropertyChain,
			IndexedBinaryPropertyChain>
		propertyCompositionsByRightSubProperty;

	
	public SaturatedPropertyChain(IndexedPropertyChain iop) {
		this.root = iop;
		this.derivedSuperProperties =
			new ArrayHashSet<IndexedPropertyChain>();
		this.derivedSubProperties =
			new ArrayHashSet<IndexedPropertyChain>();
	}
	
	public IndexedPropertyChain getRoot() {
		return root;
	}

	public Set<IndexedPropertyChain> getSubProperties() {
		return derivedSubProperties;
	}

	public Set<IndexedPropertyChain> getSuperProperties() {
		return derivedSuperProperties;
	}

}
