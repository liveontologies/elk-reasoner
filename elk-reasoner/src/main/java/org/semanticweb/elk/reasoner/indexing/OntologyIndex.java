/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, May 13, 2011
 */
package org.semanticweb.elk.reasoner.indexing;

import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkEntity;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.ElkSubObjectPropertyOfAxiom;

/**
 * Interface for public and protected methods of the index of the ontology.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 *
 */
public interface OntologyIndex  {
	
	IndexedEntity getIndexedEntity(ElkEntity representative);
	
	IndexedClassExpression getIndexedClassExpression(
			ElkClassExpression representative);
	
	IndexedObjectProperty getIndexedObjectProperty(
			ElkObjectPropertyExpression representative);
	
	IndexedPropertyComposition getIndexedPropertyChain(
			ElkSubObjectPropertyOfAxiom representative);
	
	/**
	 * @return All IndexedClasses occurring in the ontology
	 *  without repetitions.
	 */
	Iterable<IndexedClass> getIndexedClasses();		
	
	int getIndexedClassCount();
	
	/**
	 * @return All IndexedClassExpressions with possible repetitions.
	 */
	Iterable<IndexedClassExpression> getIndexedClassExpressions();		
	
	/**
	 * @return All IndexedObjectProperties occurring in the ontology
	 * without repetitions.
	 */
	Iterable<IndexedObjectProperty> getIndexedObjectProperties();
	
	/**
	 * @return All IndexedPropertyCompositions without repetitions.
	 */
	Iterable<IndexedPropertyComposition> getIndexedPropertyChains();
	
	ElkAxiomProcessor getAxiomInserter();
	
	ElkAxiomProcessor getAxiomDeleter();
}
