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
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;

/**
 * Interface for public and protected methods of the index of the ontology.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 *
 */
public abstract class OntologyIndex  {

	public abstract IndexedClassExpression getIndexed(ElkClassExpression representative);
	
	public abstract IndexedObjectProperty getIndexed(ElkObjectPropertyExpression representative);
	
	
	protected abstract IndexedClassExpression createIndexed(ElkClassExpression representative);

	protected abstract IndexedObjectProperty createIndexed(ElkObjectPropertyExpression representative);
	
	protected abstract void removeIfNoOccurrence(IndexedClassExpression ice);
	
	protected abstract void removeIfNoOccurrence(IndexedObjectProperty iop);

	
	
	public abstract Iterable<IndexedClass> getIndexedClasses();
	
	public abstract Iterable<IndexedClassExpression> getIndexedClassExpressions();
	
	public abstract Iterable<IndexedObjectProperty> getIndexedObjectProperties();
	
	
	
	public abstract ElkAxiomProcessor getAxiomInserter();
	
	public abstract ElkAxiomProcessor getAxiomDeleter();

}
