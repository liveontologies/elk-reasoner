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

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public interface UpdateableTaxonomyNode<T extends ElkEntity> extends
		UpdateableNode<T>, TaxonomyNode<T> {

	void addDirectSuperNode(UpdateableTaxonomyNode<T> superNode);

	void addDirectSubNode(UpdateableTaxonomyNode<T> subNode);

	boolean removeDirectSubNode(UpdateableTaxonomyNode<T> subNode);

	boolean removeDirectSuperNode(UpdateableTaxonomyNode<T> superNode);

	@Override
	Set<? extends UpdateableTaxonomyNode<T>> getDirectSuperNodes();

	@Override
	Set<? extends UpdateableTaxonomyNode<T>> getAllSuperNodes();

	@Override
	Set<? extends UpdateableTaxonomyNode<T>> getDirectSubNodes();

	@Override
	Set<? extends UpdateableTaxonomyNode<T>> getAllSubNodes();
}
