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
package org.semanticweb.elk.reasoner.taxonomy;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;

/**
 * Implementation with all methods empty.
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of the type nodes in the taxonomy for which
 *            this listener is registered.
 * @param <I>
 *            The type of members of the instance nodes in the taxonomy for
 *            which this listener is registered.
 */
public class DummyInstanceTaxonomyListener<T extends ElkEntity, I extends ElkEntity>
		implements InstanceTaxonomy.Listener<T, I> {

	@Override
	public void directTypeNodesAppeared(final InstanceNode<T, I> instanceNode) {
		// Empty.
	}

	@Override
	public void directTypeNodesDisappeared(
			final InstanceNode<T, I> instanceNode) {
		// Empty.
	}

	@Override
	public void directInstanceNodesAppeared(final TypeNode<T, I> typeNode) {
		// Empty.
	}

	@Override
	public void directInstanceNodesDisappeared(final TypeNode<T, I> typeNode) {
		// Empty.
	}

}
