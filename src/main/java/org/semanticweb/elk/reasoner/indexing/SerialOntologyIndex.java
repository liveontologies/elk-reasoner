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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.util.ArrayHashMap;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class SerialOntologyIndex implements OntologyIndexComputation {

	// indexer for axioms
	private final AxiomIndexer axiomIndexer;

	protected final Map<ElkClassExpression, IndexedClassExpression> indexedClassExpressionLookup;
	protected final Map<ElkObjectPropertyExpression, IndexedObjectProperty> indexedObjectPropertyLookup;

	public SerialOntologyIndex() {
		this.axiomIndexer = new AxiomIndexer(this);

		indexedClassExpressionLookup = new ArrayHashMap<ElkClassExpression, IndexedClassExpression>(
				1024);
		indexedObjectPropertyLookup = new ArrayHashMap<ElkObjectPropertyExpression, IndexedObjectProperty>(
				128);
	}

	public void addTarget(Future<? extends ElkAxiom> futureAxiom) {
		try {
			futureAxiom.get().accept(axiomIndexer);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IndexedClassExpression getIndexedClassExpression(
			ElkClassExpression classExpression) {
		return indexedClassExpressionLookup.get(classExpression);
	}

	public IndexedObjectProperty getIndexedObjectProperty(
			ElkObjectProperty objectProperty) {
		return indexedObjectPropertyLookup.get(objectProperty);
	}

	public IndexedClassExpression getCreateIndexedClassExpression(
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

	public IndexedObjectProperty getCreateIndexedObjectProperty(
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

	public void computeRoleHierarchy() {
		for (IndexedObjectProperty iop : indexedObjectPropertyLookup.values())
			iop.computeSubAndSuperProperties();
	}

	public Iterable<IndexedClassExpression> getIndexedClassExpressions() {
		return Collections.unmodifiableCollection(indexedClassExpressionLookup
				.values());
	}
}
