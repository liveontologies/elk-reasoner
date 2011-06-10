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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.util.ArrayHashMap;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class SerialOntologyIndex extends OntologyIndex {

	protected final Map<ElkClassExpression, IndexedClassExpression> indexedClassExpressionLookup;
	protected final Map<ElkObjectPropertyExpression, IndexedObjectProperty> indexedObjectPropertyLookup;

	public SerialOntologyIndex() {
		indexedClassExpressionLookup = new ArrayHashMap<ElkClassExpression, IndexedClassExpression>(
				1024);
		indexedObjectPropertyLookup = new ArrayHashMap<ElkObjectPropertyExpression, IndexedObjectProperty>(
				128);
	}

	@Override
	public IndexedClassExpression getIndexedClassExpression(
			ElkClassExpression classExpression) {
		return indexedClassExpressionLookup.get(classExpression);
	}

	@Override
	public IndexedObjectProperty getIndexedObjectProperty(
			ElkObjectProperty objectProperty) {
		return indexedObjectPropertyLookup.get(objectProperty);
	}

	@Override
	protected IndexedClassExpression getCreateIndexedClassExpression(
			ElkClassExpression classExpression) {
		IndexedClassExpression indexedClassExpression = indexedClassExpressionLookup
				.get(classExpression);
		if (indexedClassExpression == null) {
			indexedClassExpression = IndexedClassExpression
					.create(classExpression);
			indexedClassExpressionLookup.put(classExpression,
					indexedClassExpression);
		}
		return indexedClassExpression;
	}

	@Override
	protected IndexedObjectProperty getCreateIndexedObjectProperty(
			ElkObjectProperty objectProperty) {
		IndexedObjectProperty indexedObjectProperty = indexedObjectPropertyLookup
				.get(objectProperty);
		if (indexedObjectProperty == null) {
			indexedObjectProperty = new IndexedObjectProperty(objectProperty);
			indexedObjectPropertyLookup.put(objectProperty,
					indexedObjectProperty);
		}
		return indexedObjectProperty;
	}
	
	@Override
	public ElkAxiomProcessor getAxiomIndexer() {
		return new AxiomIndexer(this);
	}

	@Override
	public Iterable<IndexedClass> getIndexedClasses() {
		return new Iterable<IndexedClass> () {

			public Iterator<IndexedClass> iterator() {
				return new Iterator<IndexedClass> () {
					Iterator<IndexedClassExpression> i = indexedClassExpressionLookup.values().iterator();
					IndexedClass next = find_next();
					
					public boolean hasNext() {
						return next != null;
					}

					public IndexedClass next() {
						if (next != null) {
							IndexedClass result = next;
							next = find_next();
							return result;
						}
						throw new NoSuchElementException();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
					
					private IndexedClass find_next() {
						while (i.hasNext()) {
							IndexedClassExpression ice = i.next();
							if (ice instanceof IndexedClass)
								return (IndexedClass) ice;
						}
						return null;
					}
					
				};
			}
		};
	}
	
	@Override
	public Iterable<IndexedClassExpression> getIndexedClassExpressions() {
		return Collections.unmodifiableCollection(indexedClassExpressionLookup
				.values());
	}

	@Override
	public Iterable<IndexedObjectProperty> getIndexedObjectProperties() {
		return Collections.unmodifiableCollection(indexedObjectPropertyLookup
				.values());
	}
}
