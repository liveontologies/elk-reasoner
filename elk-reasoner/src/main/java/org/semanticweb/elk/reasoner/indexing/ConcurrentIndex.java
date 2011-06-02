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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class ConcurrentIndex implements Index {

	protected final ConcurrentMap<ElkClassExpression, IndexedClassExpression> indexedClassExpressionLookup;
	protected final ConcurrentMap<ElkObjectPropertyExpression, IndexedObjectProperty> indexedObjectPropertyLookup;

	public ConcurrentIndex() {
		indexedClassExpressionLookup = new ConcurrentHashMap<ElkClassExpression, IndexedClassExpression>();
		indexedObjectPropertyLookup = new ConcurrentHashMap<ElkObjectPropertyExpression, IndexedObjectProperty>();
	}


	public IndexedClassExpression getIndexed(ElkClassExpression classExpression) {
		IndexedClassExpression indexedClassExpression = indexedClassExpressionLookup
				.get(classExpression);
		if (indexedClassExpression == null) {
			indexedClassExpression = new IndexedClassExpression(classExpression);
			IndexedClassExpression previous = indexedClassExpressionLookup
					.putIfAbsent(classExpression, indexedClassExpression);
			if (previous != null)
				return (previous);
		}
		return indexedClassExpression;
	}

	public IndexedObjectProperty getIndexed(ElkObjectProperty ope) {
		IndexedObjectProperty indexedObjectProperty = indexedObjectPropertyLookup
				.get(ope);
		if (indexedObjectProperty == null) {
			indexedObjectProperty = new IndexedObjectProperty(ope);
			IndexedObjectProperty previous = indexedObjectPropertyLookup
					.putIfAbsent(ope, indexedObjectProperty);
			if (previous != null)
				return (previous);
		}
		return indexedObjectProperty;
	}

	public void computeRoleHierarchy() {
		for (IndexedObjectProperty iop : indexedObjectPropertyLookup.values()) {
			iop.computeSubObjectProperties();
			iop.computeSuperObjectProperties();
		}
	}

	
	public Iterable<IndexedClassExpression> getIndexedClassExpressions() {
		return indexedClassExpressionLookup.values();
	}
}
