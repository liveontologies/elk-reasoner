package org.semanticweb.elk.reasoner.taxonomy.model;

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

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * Instance taxonomy with parameterized type of its nodes.
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of the type nodes in this taxonomy.
 * @param <I>
 *            The type of members of the instance nodes in this taxonomy.
 * @param <TN>
 *            The type of type nodes in this taxonomy.
 * @param <IN>
 *            The type of instance nodes in this taxonomy.
 */
public interface GenericInstanceTaxonomy<T extends ElkEntity, I extends ElkEntity, TN extends GenericTypeNode<T, I, TN, IN>, IN extends GenericInstanceNode<T, I, TN, IN>>
		extends InstanceTaxonomy<T, I>, GenericTaxonomy<T, TN> {

	@Override
	IN getInstanceNode(I elkEntity);

	@Override
	Set<? extends IN> getInstanceNodes();

	@Override
	TN getNode(T elkEntity);

	@Override
	Set<? extends TN> getNodes();

	@Override
	TN getTopNode();

	@Override
	TN getBottomNode();

}
