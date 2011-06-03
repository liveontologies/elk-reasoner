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

import java.util.Map;

import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.util.ArrayHashMap;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class SerialIndex implements Index {

	protected final Map<ElkClassExpression, IndexedClassExpression> indexedClassExpressionLookup;
	protected final Map<ElkObjectPropertyExpression, IndexedObjectProperty> indexedObjectPropertyLookup;

	public SerialIndex() {
		indexedClassExpressionLookup = new ArrayHashMap<ElkClassExpression, IndexedClassExpression>(
				1024);
		indexedObjectPropertyLookup = new ArrayHashMap<ElkObjectPropertyExpression, IndexedObjectProperty>(
				128);
	}

	public IndexedClassExpression getIndexed(ElkClassExpression classExpression) {
		IndexedClassExpression indexedClassExpression = indexedClassExpressionLookup
				.get(classExpression);
		if (indexedClassExpression == null) {
			indexedClassExpression = IndexedClassExpression.create(classExpression);
			indexedClassExpressionLookup.put(classExpression, indexedClassExpression);
		}
		return indexedClassExpression;
	}

	public IndexedObjectProperty getIndexed(ElkObjectProperty objectProperty) {
		IndexedObjectProperty indexedObjectProperty = indexedObjectPropertyLookup
				.get(objectProperty);
		if (indexedObjectProperty == null) {
			indexedObjectProperty = new IndexedObjectProperty(objectProperty);
			indexedObjectPropertyLookup.put(objectProperty, indexedObjectProperty);
		}
		return indexedObjectProperty;
	}

	public void computeRoleHierarchy() {
		for (IndexedObjectProperty iop : indexedObjectPropertyLookup.values()) 
			iop.computeSubAndSuperProperties();
	}

	public Iterable<IndexedClassExpression> getIndexedClassExpressions() {
		return indexedClassExpressionLookup.values();
	}
}
