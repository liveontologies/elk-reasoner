/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/**
 * @author Frantisek Simancik
 * 
 */
public class ElkAxiomDeleterVisitor extends ElkAxiomIndexerVisitor {

	private final UpdateCacheFilter filter;
	private final ElkObjectIndexerVisitor elkObjectIndexer;


	public ElkAxiomDeleterVisitor(IndexedObjectCache objectCache) {
		this.filter = new UpdateCacheFilter(objectCache);
		this.elkObjectIndexer = new ElkObjectIndexerVisitor(filter);
	}

	@Override
	protected void indexSubClassOfAxiom(ElkClassExpression subElkClass,
			ElkClassExpression superElkClass) {
		
		filter.setIncrements(-1, 0, -1);
		IndexedClassExpression subIndexedClass = subElkClass
				.accept(elkObjectIndexer);
		
		filter.setIncrements(-1, -1, 0);
		IndexedClassExpression superIndexedClass = superElkClass
				.accept(elkObjectIndexer);

		subIndexedClass.removeToldSuperClassExpression(superIndexedClass);
	}

	@Override
	protected void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subElkProperty,
			ElkObjectPropertyExpression superElkProperty) {
		
		filter.setIncrements(-1, 0, -1);
		IndexedPropertyChain subIndexedProperty = subElkProperty
				.accept(elkObjectIndexer);
		
		filter.setIncrements(-1, -1, 0);
		IndexedObjectProperty superIndexedProperty = (IndexedObjectProperty) superElkProperty
				.accept(elkObjectIndexer);

		subIndexedProperty.removeToldSuperObjectProperty(superIndexedProperty);
		superIndexedProperty.removeToldSubObjectProperty(subIndexedProperty);
	}

	@Override
	protected void indexClassDeclaration(ElkClass ec) {
		filter.setIncrements(-1, 0, 0);
		ec.accept(elkObjectIndexer);
	}

	@Override
	protected void indexObjectPropertyDeclaration(ElkObjectProperty ep) {
		filter.setIncrements(-1, 0, 0);
		ep.accept(elkObjectIndexer);
	}

}
