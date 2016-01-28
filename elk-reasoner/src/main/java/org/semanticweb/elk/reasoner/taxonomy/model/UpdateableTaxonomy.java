/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy.model;

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
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface UpdateableTaxonomy<T extends ElkEntity> extends Taxonomy<T> {

	public UpdateableTaxonomyNode<T> getCreateNode(Collection<T> members);

	public boolean addToBottomNode(T member);

	public boolean removeFromBottomNode(T member);

	public boolean removeNode(UpdateableTaxonomyNode<T> node);

	public UpdateableTaxonomyNode<T> getUpdateableNode(T elkEntity);

	public Set<? extends UpdateableTaxonomyNode<T>> getUpdateableNodes();

	/*
	 * FIXME replace by UpdateableTaxonomyNode getTopNode() when we switch to
	 * Java 1.7 which can handle covariant return types
	 */
	public UpdateableTaxonomyNode<T> getUpdateableTopNode();

	/*
	 * Handy since being able to update a taxonomy means being able to update
	 * super nodes of its bottom node
	 */
	public UpdateableBottomNode<T> getUpdateableBottomNode();

}
