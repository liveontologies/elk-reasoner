/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.taxonomy.impl;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyEqualator;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * @author Peter Skocovsky
 *
 * @param <T>
 * @param <I>
 */
public abstract class AbstractInstanceTaxonomy<T extends ElkEntity, I extends ElkEntity>
		extends AbstractTaxonomy<T> implements InstanceTaxonomy<T, I> {
	
	@Override
	public int hashCode() {
		return InstanceTaxonomyHasher.hash(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		
		if (!(obj instanceof Taxonomy<?>)) {
			return false;
		}
		
		try {
			return InstanceTaxonomyEqualator.equals(this, (Taxonomy<T>) obj);
		} catch (ClassCastException e) {
			return false;
		}
	}

}
