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
package org.semanticweb.elk.reasoner.indexing.entries;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.util.collections.entryset.KeyEntry;

public class IndexedEntryConverter<T>
		implements
		IndexedClassExpressionVisitor<KeyEntry<T, ? extends IndexedClassExpression>>,
		IndexedPropertyChainVisitor<IndexedPropertyChainEntry<T, ? extends IndexedPropertyChain>> {

	public IndexedClassExpressionEntry<T, IndexedClass> visit(
			IndexedClass element) {
		return new IndexedClassEntry<T, IndexedClass>(element);
	}

	public IndexedClassExpressionEntry<T, IndexedObjectIntersectionOf> visit(
			IndexedObjectIntersectionOf element) {
		return new IndexedObjectIntersectionOfEntry<T, IndexedObjectIntersectionOf>(
				element);
	}

	public IndexedClassExpressionEntry<T, IndexedDataHasValue> visit(
			IndexedDataHasValue element) {
		return new IndexedDataHasValueEntry<T, IndexedDataHasValue>(element) {
		};
	}

	public IndexedClassExpressionEntry<T, IndexedObjectSomeValuesFrom> visit(
			IndexedObjectSomeValuesFrom element) {
		return new IndexedObjectSomeValuesFromEntry<T, IndexedObjectSomeValuesFrom>(
				element);
	}

	public IndexedPropertyChainEntry<T, IndexedObjectProperty> visit(
			IndexedObjectProperty element) {
		return new IndexedObjectPropertyEntry<T, IndexedObjectProperty>(element) {
		};
	}

	public IndexedPropertyChainEntry<T, IndexedBinaryPropertyChain> visit(
			IndexedBinaryPropertyChain element) {
		return new IndexedBinaryPropertyChainEntry<T, IndexedBinaryPropertyChain>(
				element);
	}

}
