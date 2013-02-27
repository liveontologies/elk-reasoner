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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface UpdateableTypeNode<T extends ElkObject, I extends ElkObject>
		extends TypeNode<T, I>, UpdateableTaxonomyNode<T> {

	@Override
	public Set<? extends UpdateableTypeNode<T, I>> getDirectUpdateableSubNodes();

	/*
	 * TODO Normally, this method should not exist, getDirectSuperNodes should
	 * suffice since all super nodes are updateable. However, the inability of
	 * Java 1.6 to deal with covariant return types in extended interfaces led
	 * to this method. Otherwise, javac 1.6 complains that this interface
	 * extends the same methods (getDirectSuperNodes) from TypeNode and
	 * UpdateableTaxonomyNode while their return types are unrelated and ignores
	 * the fact that UpdateableTypeNode is a sub-type of both, TypeNode and
	 * UpdateableTaxonomyNode. Eclipse, however, has no problem with it. It is
	 * allegedly fixed in Java 1.7
	 */
	@Override
	public Set<? extends UpdateableTypeNode<T, I>> getDirectUpdateableSuperNodes();
	
	@Override
	public Set<? extends UpdateableInstanceNode<T, I>> getDirectInstanceNodes();
	
	public void addDirectInstanceNode(
			UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode);
	
	public void removeDirectInstanceNode(
			UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode);

}