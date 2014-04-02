/**
 * 
 */
package org.semanticweb.elk.alc.indexing.hierarchy;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

/**
 * A collection of methods for indexing ELK objects
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ElkAxiomIndexer {

	public void indexSubClassOfAxiom(ElkClassExpression subClass,
			ElkClassExpression superClass);
	
	public void indexDisjointClassesAxiom(List<? extends ElkClassExpression> disjointClasses);

	public IndexedClass indexClassDeclaration(ElkClass ec);

	public IndexedObjectProperty indexObjectPropertyDeclaration(
			ElkObjectProperty eop);
	
	// do not support chains, only property hierarchies and transitivity
	
	public void indexSubObjectPropertyOfAxiom(ElkObjectProperty subProperty, ElkObjectProperty superProperty);
	
	public void indexTransitiveProperty(ElkObjectProperty property);

}
